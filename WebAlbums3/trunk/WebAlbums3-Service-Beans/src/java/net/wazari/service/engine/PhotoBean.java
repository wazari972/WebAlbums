package net.wazari.service.engine;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.PhotoLocal;
import net.wazari.service.UserLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay.ViewSessionPhotoDisplayMassEdit.Turn;
import net.wazari.util.system.FilesFinder;
import net.wazari.common.util.StringUtil;
import net.wazari.common.util.XmlBuilder;
import net.wazari.util.system.SystemTools;

@Stateless
public class PhotoBean implements PhotoLocal {

    private static final Logger log = LoggerFactory.getLogger(PhotoBean.class.toString());
    private static final long serialVersionUID = 1L;
    @EJB
    PhotoUtil photoUtil;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private AlbumFacadeLocal albumDAO;
    @EJB
    private TagThemeFacadeLocal tagThemeDAO;
    @EJB
    private TagFacadeLocal tagDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private WebPageLocal webPageService;
    @EJB
    private WebPageLocal webService;
    @EJB
    private FilesFinder finder;
    @EJB
    private SystemTools sysTools;

    @Override
    public XmlBuilder treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,
            Boolean correct) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null);
        Integer photoID = vSession.getId();

        Photo enrPhoto = photoDAO.loadIfAllowed(vSession, photoID);

        if (enrPhoto == null) {
            output.addException("Impossible de trouver cette photo "
                    + "(" + photoID + (vSession.isRootSession() ? "" : "/" + vSession.getTheme()) + ")");
            return output.validate();
        }

        //supprimer ?
        if (vSession.getSuppr()) {
            XmlBuilder suppr_msg = new XmlBuilder("suppr_msg");
            finder.deletePhoto(enrPhoto, suppr_msg, vSession.getConfiguration());
            output.add(suppr_msg);
            return output.validate();
        }

        //mise à jour des tag/description
        String desc = vSession.getDesc();
        enrPhoto.setDescription(desc);

        String user = vSession.getDroit();
        if (user != null) {
            boolean valid = false;
            try {
                Integer userId = null;

                if ("null".equals(user)) {
                    valid = true;
                } else if (userDAO.find(Integer.parseInt(user)) != null) {
                    valid = true;
                    userId = Integer.parseInt(user);
                } else {
                    log.warn( "Unknown userId:{0}", user);
                }

                if (valid) {
                    log.info("Set Droit to:{0}", userId);
                    enrPhoto.setDroit(userId);
                }

            } catch (NumberFormatException e) {
                log.warn( "Cannot parse userId:{0}", user);
            }
        }

        Integer[] tags = vSession.getNewTag();
        photoUtil.setTags(enrPhoto, tags);

        photoDAO.edit(enrPhoto);

        //utiliser cette photo comme representante de l'album ?
        Boolean represent = vSession.getRepresent();
        if (represent) {
            Album enrAlbum = enrPhoto.getAlbum();
            if (enrAlbum == null) {
                output.addException("Exception", "Impossible d'acceder l'album à representer "
                        + "(" + enrPhoto.getAlbum() + ")");
                return output.validate();
            }

            enrAlbum.setPicture(enrPhoto.getId());
            albumDAO.edit(enrAlbum);
        }

        //utiliser cette photo pour representer le tag de ce theme
        Tag enrTag = tagDAO.find(vSession.getTagPhoto());
        if (enrTag != null) {
            Theme actualTheme = enrPhoto.getAlbum().getTheme();

            TagTheme enrTagTh = null;
            for (TagTheme enrTTCurrent : enrTag.getTagThemeList()) {
                if (enrTTCurrent.getTheme().getId().equals(actualTheme.getId())) {
                    enrTagTh = enrTTCurrent;
                    break;
                }
            }

            if (enrTagTh == null) {
                log.warn("CREATE TAG");
                //creer un tagTheme pour cette photo/tag/theme
                enrTagTh = tagThemeDAO.newTagTheme();

                enrTagTh.setTheme(actualTheme);
                enrTagTh.setTag(enrTag);
                //par défaut le tag est visible
                enrTagTh.setIsVisible(true);

                tagThemeDAO.create(enrTagTh);
            } else {
                log.warn("EDIT TAG");
            }
            //changer la photo representant ce tag/theme
            enrTagTh.setPhoto(enrPhoto.getId());

            log.info( "edit {0}", enrTagTh);
            tagThemeDAO.edit(enrTagTh);
        }

        output.add("message", " Photo (" + enrPhoto.getId() + ") "
                + "correctement mise à jour !");

        return output.validate();
    }

    @Override
    public XmlBuilder treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlBuilder submit) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null);
        //afficher les photos
        //afficher la liste des albums de cet theme
        Integer albumId = vSession.getAlbum();
        Integer page = vSession.getPage();
        Integer albmCount = vSession.getAlbmCount();
        Special special = vSession.getSpecial();
        page = (page == null ? 0 : page);

        Album enrAlbum = null;

        enrAlbum = albumDAO.loadIfAllowed(vSession, albumId);

        if (enrAlbum == null) {
            output.addException("L'album (" + albumId + ") n'existe pas "
                    + "ou n'est pas accessible...");
            return output.validate();
        }
        XmlBuilder album = new XmlBuilder("album");
        output.add(album);

        album.add("id", enrAlbum.getId());
        album.add("count", albmCount);
        album.add("title", enrAlbum.getNom());
        album.add(enrAlbum.getDroit().getNom());
        album.add(StringUtil.xmlDate(enrAlbum.getDate(), null));
        album.add(new XmlBuilder("details").add("description", enrAlbum.getDescription()).add("photoID", enrAlbum.getPicture()));

        PhotoRequest rq = new PhotoRequest(TypeRequest.PHOTO, albumId);
        if (Special.FULLSCREEN == special) {
            sysTools.fullscreenMultiple(vSession, rq, "Albums", enrAlbum.getId(), page);
        }

        XmlBuilder thisPage = new XmlBuilder(null);
        thisPage.add("name", "Photos");
        thisPage.add("album", albumId);
        thisPage.add("albmCount", albmCount);
        output.add(displayPhoto(rq, vSession, thisPage, submit));


        return output.validate();
    }

    @Override
    public XmlBuilder treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlBuilder submit) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("photo_edit");
        if (submit != null) {
            output.add(submit);
        }

        Integer photoID = vSession.getId();

        Photo enrPhoto = photoDAO.find(photoID);

        if (enrPhoto == null) {
            output.addException("Impossible de trouver la photo (" + photoID + ")");
            return output.validate();
        }
        Album enrAlbum = enrPhoto.getAlbum();

        output.add("id", enrPhoto.getId());
        output.add("description", enrPhoto.getDescription());
        output.add(webService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto,
                Box.MULTIPLE));

        output.add(webService.displayListIBT(Mode.TAG_NUSED, vSession, enrPhoto,
                Box.MULTIPLE));

        output.add(webService.displayListIBT(Mode.TAG_NEVER, vSession, enrPhoto,
                Box.MULTIPLE));

        output.add(webService.displayListIBTNI(Mode.TAG_USED, vSession, enrPhoto,
                Box.LIST,
                null, null));
        Utilisateur enrUtil = userDAO.find(enrPhoto.getDroit());
        output.add(webService.displayListDroit(enrUtil, enrAlbum.getDroit().getId()));
        output.validate();

        return output.validate();
    }

    @Override
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    public XmlBuilder displayPhoto(PhotoRequest rq,
            ViewSessionPhotoDisplay vSession,
            XmlBuilder thisPage,
            XmlBuilder submit)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null);

        EditMode inEditionMode = vSession.getEditionMode();
        Integer page = vSession.getPage();
        Integer photoID = vSession.getId();
        Integer scount = vSession.getCount();
        Bornes bornes = webService.calculBornes(page, scount, vSession.getPhotoSize());
        SubsetOf<Photo> lstP;
        if (rq.type == TypeRequest.PHOTO) {
            lstP = photoDAO.loadFromAlbum(vSession, rq.albumId, bornes);
        } else {
            lstP = photoDAO.loadByTags(vSession, rq.listTagId, bornes);
        }

        String degrees = "0";
        Integer tag = null;
        int countME = 0;
        boolean massEditParam = false;
        boolean reSelect = false;
        boolean current = false;

        Turn turn = null;
        if (inEditionMode == EditMode.EDITION) {
            try {
                Action action = vSession.getAction();
                if (Action.MASSEDIT == action) {
                    turn = vSession.getMassEdit().getTurn();
                    if (turn == Turn.LEFT) {
                        degrees = "270";
                    } else if (turn == Turn.RIGHT) {
                        degrees = "90";
                    } else if (turn == Turn.TAG || turn == Turn.UNTAG || turn == Turn.MVTAG) {
                        tag = vSession.getMassEdit().getAddTag();
                    }
                    massEditParam = true;
                }
            } catch (NoSuchElementException e) {
                output.addException("NoSuchElementException", tag);
                reSelect = true;
            }
        }

        boolean submitted = false ;
        int count = bornes.getFirstElement();
        for (Photo enrPhoto : lstP.subset) {
            XmlBuilder photo = new XmlBuilder("photo");
            boolean reSelectThis = false;
            if (massEditParam) {
                Boolean chkbox = vSession.getMassEdit().getChk(enrPhoto.getId());
                if (chkbox) {
                    current = true;

                    if (turn == Turn.TAG || turn == Turn.UNTAG || turn == Turn.MVTAG) {
                        String verb;
                        if (turn == Turn.TAG) {
                            photoUtil.addTags(enrPhoto, new Integer[]{tag});
                            verb = "added";
                        } else if (turn == Turn.UNTAG) {
                            photoUtil.removeTag(enrPhoto, tag);
                            verb = "removed";
                        } else if (turn == Turn.MVTAG) {
                            Integer rmTag = vSession.getMassEdit().getRmTag();
                            photoUtil.removeTag(enrPhoto, rmTag);
                            photoUtil.addTags(enrPhoto, new Integer[]{tag});
                            verb = "added and tag " + rmTag + " removed";
                        } else {
                            verb = "nothinged";
                        }
                        photo.add("message", "Tag " + tag + " " + verb + " to photo #" + enrPhoto.getId());
                    } else if (turn == Turn.LEFT || turn == Turn.RIGHT) {
                        if (!photoUtil.rotate(vSession, enrPhoto, degrees)) {
                            photo.addException("Erreur dans le ConvertWrapper ...");
                            reSelectThis = true;
                        }
                    }

                    countME++;

                }
            }

            if (enrPhoto.getId().equals(photoID)) {
                if (submit != null) {
                    photo.add(submit);
                    submitted = true ;
                }
            }
            if (inEditionMode == EditMode.EDITION) {
                if ((reSelect || reSelectThis) && current) {
                    photo.add("checked");
                }
            }
            XmlBuilder details = new XmlBuilder("details");
            details.add("photoID", enrPhoto.getId());
            details.add("description", enrPhoto.getDescription());

            details.add("miniWidth", photoUtil.getWidth(vSession, enrPhoto, false));
            details.add("miniHeight", photoUtil.getHeight(vSession, enrPhoto, false));

            //tags de cette photo
            details.add(webService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto,
                    Box.NONE));
            details.add("albumID", enrPhoto.getAlbum().getId());
            //liste des utilisateurs pouvant voir cette photo
            if (vSession.isSessionManager()
                    && inEditionMode != EditMode.VISITE) {
                Utilisateur enrUser = userDAO.find(enrPhoto.getDroit());
                if (enrUser != null) {
                    details.add("user", enrUser.getNom());
                } else {
                    details.add("user", userDAO.loadUserOutside(enrPhoto.getAlbum().getId()).getNom());
                }
            }
            photo.add(details);
            photo.add("count", count);
            if (vSession.wantsDetails()) {
                photo.add(photoUtil.getXmlExif(enrPhoto));
            }
            output.add(photo);
            current = false;
            count++;
        }

        if (!submitted && submit != null) {
            output.add(submit);
        }

        if (inEditionMode == EditMode.EDITION) {
            XmlBuilder massEdit = new XmlBuilder("massEdit");
            massEdit.add(webService.displayListBN(Mode.TAG_USED, vSession,
                    Box.LIST, "newTag"));
            if (massEditParam) {
                String msg;
                if (countME == 0 || Turn.RIEN == turn) {
                    msg = "Aucune modification faite ... ?";
                } else {
                    msg = "" + countME + " photo"
                            + (countME == 1 ? " a été modifiée" : "s ont été modifées");
                }
                massEdit.add("message", msg);
            }
            output.add(massEdit);
        }
        output.add(webPageService.xmlPage(thisPage, bornes));
        return output.validate();
    }
}
