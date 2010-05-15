package net.wazari.service.engine;

import java.util.List;
import java.util.NoSuchElementException;

import java.util.logging.Logger;
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
import net.wazari.service.PhotoLocal;
import net.wazari.service.SystemToolsLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.WebPageLocal.Bornes;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSession.Type;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.Turn;

import net.wazari.util.system.FilesFinder;
import net.wazari.util.StringUtil;
import net.wazari.util.XmlBuilder;

@Stateless
public class PhotoBean implements PhotoLocal {

    private static final Logger log = Logger.getLogger(PhotoBean.class.toString());
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
    private FilesFinder finder = new FilesFinder();
    @EJB private SystemToolsLocal sysTools ;

    @Override
    public XmlBuilder treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException {
        Action action = vSession.getAction();
        XmlBuilder output;
        XmlBuilder submit = null;
        Boolean correct = new Boolean(true);

        if (Action.SUBMIT == action && vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            submit = treatPhotoSUBMIT(vSession, correct);
        }

        if ((Action.EDIT == action || !correct) && vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            output = treatPhotoEDIT(vSession, submit);

            XmlBuilder return_to = new XmlBuilder("return_to");
            return_to.add("name", "Photos");
            return_to.add("count", vSession.getCount());
            return_to.add("album", vSession.getAlbum());
            return_to.add("albmCount", vSession.getAlbmCount());
            output.add(return_to);
        } else {
            output = new XmlBuilder("photos");
            output.add(treatPhotoDISPLAY(vSession, submit));
        }


        return output.validate();
    }

    @Override
    public XmlBuilder treatPhotoSUBMIT(ViewSessionPhoto vSession,
            Boolean correct) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null);
        Integer photoID = vSession.getId();
        int theme = vSession.getThemeId();
        String rq = null;
        try {
            Photo enrPhoto = photoDAO.loadIfAllowed(vSession, photoID);

            if (enrPhoto == null) {
                correct = false;
                output.addException("Impossible de trouver cette photo " +
                        "(" + photoID + (vSession.isRootSession() ? "" : "/" + theme) + ")");
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
            String user = vSession.getUser();
            String desc = vSession.getDesc();
            Integer[] tags = vSession.getNewTag();

            enrPhoto.setDescription(desc);
            photoUtil.updateDroit(enrPhoto, "null".equals(user) ? null : new Integer("0" + user));
            photoDAO.edit(enrPhoto);

            photoUtil.setTags(enrPhoto, tags);
            
            //utiliser cette photo comme representante de l'album ?
            Boolean represent = vSession.getRepresent();
            if (represent) {
                Album enrAlbum = enrPhoto.getAlbum();
                if (enrAlbum == null) {
                    output.addException("Exception", "Impossible d'acceder l'album à representer " +
                            "(" + enrPhoto.getAlbum() + ")");
                    return output.validate();
                }

                enrAlbum.setPicture(enrPhoto.getId());
                albumDAO.edit(enrAlbum);
            }

            //utiliser cette photo pour representer le tag de ce theme
            Integer tagPhoto = vSession.getTagPhoto();
            if (tagPhoto != null && -1 != tagPhoto) {
                List<TagTheme> lstTT = tagThemeDAO.queryByTag(vSession, tagPhoto);
                Theme actualTheme;
                TagTheme enrTagTh = null;
                if (!vSession.isRootSession()) {
                    actualTheme = themeDAO.find(vSession.getThemeId());

                    if (!lstTT.isEmpty()) {
                        enrTagTh = lstTT.get(0);
                    }
                } else {

                    Album enrAlbum = enrPhoto.getAlbum();
                    if (enrAlbum == null) {
                        throw new NoSuchElementException("Album with ID=" + enrPhoto.getAlbum());
                    }
                    actualTheme = enrAlbum.getTheme();

                    for (TagTheme enrTagThCurrent : lstTT) {
                        log.info("tag th" + enrTagThCurrent.getTheme() + " ta" + enrTagThCurrent.getTag());
                        if (enrTagTh.getTheme() == enrAlbum.getTheme()) {
                            enrTagTh = enrTagThCurrent;
                            break;
                        }
                    }

                    if (enrTagTh.getTheme() != enrAlbum.getTheme()) {
                        enrTagTh = null;
                    }
                }

                if (enrTagTh == null) {
                    Tag enrTag = tagDAO.find(tagPhoto);

                    //creer un tagTheme pour cette photo/tag/theme
                    enrTagTh = new TagTheme();

                    enrTagTh.setTheme(actualTheme);
                    enrTagTh.setTag(enrTag);
                    //par défaut le tag est visible
                    enrTagTh.setIsVisible(true);
                }
                //changer la photo representant ce tag/theme
                enrTagTh.setPhoto(enrPhoto.getId());

                log.info("saveOrUpdate " + enrTagTh);
                tagThemeDAO.edit(enrTagTh);
            }

            output.add("message", " Photo (" + enrPhoto.getId() + ") " +
                    "correctement mise à jour !");
        } catch (NoSuchElementException e) {
            e.printStackTrace();

            output.cancel();
            output.addException("NoSuchElementException", "Impossible d'accerder à la photo à modifier (" + photoID + ")");
            output.addException("NoSuchElementException", rq);


            correct = false;
        }
        return output.validate();
    }

    @Override
    public XmlBuilder treatPhotoDISPLAY(ViewSessionPhoto vSession, XmlBuilder submit) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null);
        //afficher les photos
        //afficher la liste des albums de cet theme
        Integer albumId = vSession.getAlbum();
        Integer page = vSession.getPage();
        Integer albmCount = vSession.getAlbmCount();
        Special special = vSession.getSpecial();
        page = (page == null ? 0 : page);

        Album enrAlbum = null;
        try {
            enrAlbum = albumDAO.loadIfAllowed(vSession, albumId);

            if (enrAlbum == null) {
                output.addException("L'album (" + albumId + ") n'existe pas " +
                        "ou n'est pas accessible...");
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
            
            PhotoRequest rq = new PhotoRequest(TypeRequest.PHOTO, albumId) ;
            if (Special.FULLSCREEN == special) {
                sysTools.fullscreen(vSession, rq, "Albums", enrAlbum.getId(), page);
            }

            XmlBuilder thisPage = new XmlBuilder(null);
            thisPage.add("name", "Photos");
            thisPage.add("album", albumId);
            thisPage.add("albmCount", albmCount);
            output.add(displayPhoto(rq, vSession, thisPage, submit));

        } catch (NullPointerException e) {
            e.printStackTrace();
            output.cancel();
            output.addException("NullPointerException", "Quelque chose n'existe pas ... " + e);
        }
        return output.validate();
    }

    @Override
    public XmlBuilder treatPhotoEDIT(ViewSessionPhoto vSession, XmlBuilder submit) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("photo_edit");
        if (submit != null) {
            output.add(submit);
        }

        Integer photoID = vSession.getId();
        Integer page = vSession.getPage();
        page = (page == null ? 0 : page);

        Photo enrPhoto = photoDAO.find(photoID);

        if (enrPhoto == null) {
            output.addException("Impossible de trouver la photo (" + photoID + ")");
            return output.validate();
        }
        Album enrAlbum = enrPhoto.getAlbum();

        output.add("id", enrPhoto.getId());
        output.add("description", enrPhoto.getDescription());
        output.add(webService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto.getId(),
                Box.MULTIPLE, Type.PHOTO));

        output.add(webService.displayListIBT(Mode.TAG_NUSED, vSession, enrPhoto.getId(),
                Box.MULTIPLE, Type.PHOTO));

        output.add(webService.displayListIBT(Mode.TAG_NEVER, vSession, enrPhoto.getId(),
                Box.MULTIPLE, Type.PHOTO));

        output.add(webService.displayListIBTNI(Mode.TAG_USED, vSession, enrPhoto.getId(),
                Box.LIST, Type.PHOTO,
                null, null));
        output.add(webService.displayListDroit(enrPhoto.getDroit(), enrAlbum.getDroit().getId()));
        output.validate();

        return output.validate();
    }
    
    @Override
    public XmlBuilder displayPhoto(PhotoRequest rq,
            ViewSessionPhoto vSession,
            XmlBuilder thisPage,
            XmlBuilder submit)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null);

        EditMode inEditionMode = vSession.getEditionMode();
        Integer page = vSession.getPage();
        Integer photoID = vSession.getId();
        Integer scount = vSession.getCount();
        Bornes bornes = webService.calculBornes(page, scount, vSession.getPhotoSize());
        List<Photo> lstP ;
        if (rq.type == TypeRequest.PHOTO) {
            lstP = photoDAO.loadFromAlbum(vSession, rq.albumId, bornes.first);
        } else {
            lstP = photoDAO.loadByTags(vSession, rq.listTagId, bornes.first);
        }
        
        String degrees = "0";
        Integer tag = null;
        int countME = 0;
        boolean massEditParam = false;
        boolean reSelect = false;
        boolean current = false;

        Turn turn = vSession.getTurn();
        if (inEditionMode == EditMode.EDITION) {
            try {
                Action action = vSession.getAction();
                if (Action.MASSEDIT == action) {
                    if (turn == Turn.LEFT) {
                        degrees = "270";
                    } else if (turn == Turn.RIGHT) {
                        degrees = "90";
                    } else if (turn == Turn.TAG || turn == Turn.UNTAG || turn == Turn.MVTAG) {
                        tag = vSession.getAddTag();
                    }
                    massEditParam = true;
                }
            } catch (NoSuchElementException e) {
                output.addException("NoSuchElementException", tag);
                reSelect = true;
            }
        }

        int count = bornes.first;
        for (Photo enrPhoto : lstP) {
            XmlBuilder photo = new XmlBuilder("photo");
            boolean reSelectThis = false;
            if (massEditParam) {
                Boolean chkbox = vSession.getChk(enrPhoto.getId());
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
                            Integer rmTag = vSession.getRmTag();
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
                    submit = null;
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
            details.add(webService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto.getId(),
                    Box.NONE, Type.PHOTO));
            details.add("albumID", enrPhoto.getAlbum().getId());
            //liste des utilisateurs pouvant voir cette photo
            if (vSession.isSessionManager() &&
                    inEditionMode != EditMode.VISITE) {
                Integer right = enrPhoto.getDroit();
                if (right != null && !right.equals(0)) {
                    details.add("user", userDAO.find(enrPhoto.getDroit()).getNom());
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

        if (submit != null) {
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
                    msg = "" + countME + " photo" +
                            (countME == 1 ? " a été modifiée" : "s ont été modifées");
                }
                massEdit.add("message", msg);
            }
            output.add(massEdit);
        }
        output.add(webPageService.xmlPage(thisPage, bornes));
        return output.validate();
    }
}
