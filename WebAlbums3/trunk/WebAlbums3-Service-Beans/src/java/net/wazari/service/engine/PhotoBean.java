package net.wazari.service.engine;

import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import java.io.File;
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
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.photo.XmlPhotoAbout;
import net.wazari.service.exchange.xml.photo.XmlPhotoDisplay;
import net.wazari.service.exchange.xml.photo.XmlPhotoEdit;
import net.wazari.service.exchange.xml.photo.XmlPhotoList;
import net.wazari.service.exchange.xml.photo.XmlPhotoMassEdit;
import net.wazari.service.exchange.xml.photo.XmlPhotoRandom;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.util.system.SystemTools;
import org.apache.commons.lang.StringEscapeUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

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
    private FilesFinder finder;
    @EJB
    private SystemTools sysTools;

    @Override
    public XmlPhotoSubmit treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,
            Boolean correct) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatPhotoSUBMIT", log) ;
        XmlPhotoSubmit output = new XmlPhotoSubmit();
        Integer photoID = vSession.getId();

        if (photoID == null) {
            output.exception = "Pas de photo demandé ... (id=null)" ;
            return output ;
        }
        Photo enrPhoto = photoDAO.loadIfAllowed(vSession, photoID);

        if (enrPhoto == null) {
            output.exception = "Impossible de trouver cette photo "
                    + "(" + photoID + (vSession.isRootSession() ? "" : "/" + vSession.getTheme()) + ")" ;
            return output ;
        }

        //supprimer ?
        if (vSession.getSuppr()) {
            if (finder.deletePhoto(enrPhoto, vSession.getConfiguration())) {
                output.message = "Photo correctement  supprimé !";
            } else {
                output.exception = "Impossible de supprimer correctement la photo ...";
            }
            return output ;
        }

        //mise à jour des tag/description
        String desc = vSession.getDesc();
        enrPhoto.setDescription(StringEscapeUtils.escapeXml(desc));

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
                    log.warn( "Unknown userId:{}", user);
                }

                if (valid) {
                    log.info("Set Droit to:{}", userId);
                    enrPhoto.setDroit(userId);
                }

            } catch (NumberFormatException e) {
                log.warn( "Cannot parse userId:{}", user);
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
                output.exception = "Impossible d'acceder l'album à representer "
                        + "(" + enrPhoto.getAlbum() + ")" ;
                return output ;
            }

            enrAlbum.setPicture(enrPhoto.getId());
            albumDAO.edit(enrAlbum);
        }

        //utiliser cette photo comme representante de l'album ?
        Boolean themeBackground = vSession.getThemeBackground();
        if (themeBackground) {
            Theme enrTheme = enrPhoto.getAlbum().getTheme();
            log.warn("Assign theme background {}",enrPhoto) ;
            themeDAO.setPicture(enrTheme, enrPhoto.getId());
            vSession.getTheme().setPicture(enrPhoto.getId());
            File backgroundDir = new File(vSession.getConfiguration()
                    .getTempPath()+vSession.getTheme().getNom()) ;
            log.info("Delete and create background dir: {}", backgroundDir) ;
            if (backgroundDir.listFiles() != null) {
                for (File child : backgroundDir.listFiles()) {
                    if (!child.delete()) {
                        log.warn("Could not remove background file: {}",child);
                    }
                }
            }
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

            log.info( "edit {}", enrTagTh);
            tagThemeDAO.edit(enrTagTh);
        }

        output.message = " Photo (" + enrPhoto.getId() + ") "
                + "correctement mise à jour !" ;

        stopWatch.stop() ;
        return output ;
    }

    @Override
    public XmlPhotoDisplay treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatPhotoDISPLAY", log) ;

        XmlPhotoDisplay output = new XmlPhotoDisplay();
        //afficher les photos
        //afficher la liste des albums de cet theme
        Integer page = vSession.getPage();
        Integer albmCount = vSession.getAlbmCount();
        Special special = vSession.getSpecial();
        page = (page == null ? 0 : page);

        Integer albumId = vSession.getAlbum();
        if (albumId == null) {
            output.exception = "No album asked ... (id=null)" ;
            return output ;
        }


        Album enrAlbum = null;
        enrAlbum = albumDAO.loadIfAllowed(vSession, albumId);
        if (enrAlbum == null) {
            output.exception = "L'album (" + albumId + ") n'existe pas "
                    + "ou n'est pas accessible..." ;
            return output ;
        }
        XmlAlbum album = new XmlAlbum();
        output.album = album ;

        album.id = enrAlbum.getId();
        album.count = albmCount;
        album.title = enrAlbum.getNom();
        album.droit = enrAlbum.getDroit().getNom();
        album.date = webPageService.xmlDate(enrAlbum.getDate(), null);
        
        XmlDetails details = new XmlDetails() ;
        details.description = enrAlbum.getDescription() ;
        details.photoId = enrAlbum.getPicture() ;

        PhotoRequest rq = new PhotoRequest(TypeRequest.PHOTO, albumDAO.find(albumId));
        if (Special.FULLSCREEN == special) {
            sysTools.fullscreenMultiple(vSession, rq, enrAlbum.getId(), page, "Albums");
        }

        XmlFrom thisPage = new XmlFrom();
        thisPage.name = "Photos";
        thisPage.album = albumId ;
        thisPage.albmCount = albmCount ;
        output.photoList = displayPhoto(rq, vSession, submit, thisPage);

        stopWatch.stop() ;
        return output ;
    }

    @Override
    public XmlPhotoEdit treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException {
        XmlPhotoEdit output = new XmlPhotoEdit();
        if (submit != null) {
            output.submit = submit ;
        }

        Integer photoID = vSession.getId();

        Photo enrPhoto = photoDAO.find(photoID);

        if (enrPhoto == null) {
            output.exception = "Impossible de trouver la photo (" + photoID + ")" ;
            return output ;
        }
        Album enrAlbum = enrPhoto.getAlbum();

        output.id = enrPhoto.getId() ;
        output.description = enrPhoto.getDescription();
        output.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto,
                Box.MULTIPLE);

        output.tag_nused = webPageService.displayListIBT(Mode.TAG_NUSED, vSession, enrPhoto,
                Box.MULTIPLE);

        output.tag_never = webPageService.displayListIBT(Mode.TAG_NEVER, vSession, enrPhoto,
                Box.MULTIPLE);

        output.tag_used_lst = webPageService.displayListIBTNI(Mode.TAG_USED, vSession, enrPhoto,
                Box.LIST,
                null, null);
        Utilisateur enrUtil = userDAO.find(enrPhoto.getDroit());
        output.rights = webPageService.displayListDroit(enrUtil, enrAlbum.getDroit().getId());

        return output ;
    }

    @Override
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    public XmlPhotoList displayPhoto(PhotoRequest rq,
            ViewSessionPhotoDisplay vSession,
            XmlPhotoSubmit submit,
            XmlFrom thisPage)
            throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Service.displayPhoto."+rq.type, log) ;

        EditMode inEditionMode = vSession.getEditionMode();
        Integer page = vSession.getPage();
        Integer photoID = vSession.getId();
        Integer scount = vSession.getCount();
        Bornes bornes = webPageService.calculBornes(page, scount, vSession.getPhotoSize());
        SubsetOf<Photo> lstP;
        if (rq.type == TypeRequest.PHOTO) {
            lstP = photoDAO.loadFromAlbum(vSession, rq.albumId, bornes, ListOrder.ASC);
        } else {
            lstP = photoDAO.loadByTags(vSession, rq.listTagId, bornes, ListOrder.ASC);
        }

        String degrees = "0";
        Integer tag = null;
        int countME = 0;
        boolean massEditParam = false;
        boolean reSelect = false;
        boolean current = false;

        XmlPhotoList output = new XmlPhotoList(lstP.subset.size()) ;
        Turn turn = null;
        if (vSession.isSessionManager()
                && inEditionMode == EditMode.EDITION) {
            try {
                Action action = vSession.getAction();
                if (Action.MASSEDIT == action) {
                    turn = vSession.getMassEdit().getTurn();
                    stopWatch.setTag(stopWatch.getTag()+".MASSEDIT."+turn) ;
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
                output.exception = "NoSuchElementException: "+ tag;
                reSelect = true;
            }
        }

        boolean submitted = false ;
        int count = bornes.getFirstElement();
        for (Photo enrPhoto : lstP.subset) {
            XmlPhoto photo = new XmlPhoto();
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
                        photo.message = "Tag " + tag + " " + verb + " to photo #" + enrPhoto.getId();
                    } else if (turn == Turn.LEFT || turn == Turn.RIGHT) {
                        if (!photoUtil.rotate(vSession, enrPhoto, degrees)) {
                            photo.exception = "Erreur dans le ConvertWrapper ...";
                            reSelectThis = true;
                        }
                    }

                    countME++;

                }
            }

            if (enrPhoto.getId().equals(photoID)) {
                if (submit != null) {
                    photo.submit = submit ;
                    submitted = true ;
                }
            }
            if (vSession.isSessionManager()
                    && inEditionMode == EditMode.EDITION) {
                if ((reSelect || reSelectThis) && current) {
                    photo.checked = true ;
                }
            }
            XmlDetails details = new XmlDetails();
            details.photoId = enrPhoto.getId();
            details.description = enrPhoto.getDescription();

            //TODO: improve here, EXIF tags contain text
            details.miniWidth = Integer.toString(photoUtil.getWidth(vSession, enrPhoto, false));
            details.miniHeight =  Integer.toString(photoUtil.getHeight(vSession, enrPhoto, false));

            //tags de cette photo
            details.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto,
                    Box.NONE);
            details.albumId = enrPhoto.getAlbum().getId();
            //liste des utilisateurs pouvant voir cette photo
            if (vSession.isSessionManager()
                    && inEditionMode != EditMode.VISITE) {
                Utilisateur enrUser = userDAO.find(enrPhoto.getDroit());
                if (enrUser != null) {
                    details.user = enrUser.getNom();
                } else {
                    details.user = "o."+userDAO.loadUserOutside(enrPhoto.getAlbum().getId()).getNom() +".o";
                }
            }
            photo.details = details ;
            photo.count = count ;
            if (vSession.wantsDetails()) {
                photo.exif = photoUtil.getXmlExif(enrPhoto) ;
            }
            output.photo.add(photo);
            current = false;
            count++;
        }

        if (!submitted) {
            output.submit = submit ;
        }

        if (vSession.isSessionManager()
                && inEditionMode == EditMode.EDITION) {
            XmlPhotoMassEdit massEdit = new XmlPhotoMassEdit();
            massEdit.tag_used = webPageService.displayListBN(Mode.TAG_USED, vSession,
                    Box.LIST, "newTag");
            massEdit.tag_never = webPageService.displayListBN(Mode.TAG_NEVER, vSession,
                    Box.LIST, "newTag");
            if (massEditParam) {
                String msg;
                if (countME == 0 || Turn.RIEN == turn) {
                    msg = "Aucune modification faite ... ?";
                } else {
                    msg = "" + countME + " photo"
                            + (countME == 1 ? " a été modifiée" : "s ont été modifées");
                }
                massEdit.message = msg ;
            }
            output.massEdit = massEdit ;
        }
        output.page = webPageService.xmlPage(thisPage, bornes);
        if (countME == 0) {
            stopWatch.stop() ;
        } else {
            stopWatch.stop(stopWatch.getTag(), ""+countME+" photos modified") ;

        }
        return output ;
    }

    @Override
    public XmlPhotoRandom treatRANDOM(ViewSession vSession) throws WebAlbumsServiceException {

        Photo enrPhoto = photoDAO.loadRandom(vSession);
        if (enrPhoto == null) return null ;
        XmlPhotoRandom output = new XmlPhotoRandom() ;
        
        output.details = transformToDetails(vSession, enrPhoto) ;
        return output ;
    }

    private XmlDetails transformToDetails(ViewSession vSession, Photo enrPhoto) throws WebAlbumsServiceException {
        XmlDetails details = new XmlDetails();
        details.photoId = enrPhoto.getId();
        details.description = enrPhoto.getDescription();
        details.miniWidth = Integer.toString(photoUtil.getWidth(vSession, enrPhoto, false));
        details.miniHeight = Integer.toString(photoUtil.getHeight(vSession, enrPhoto, false));
        //tags de cette photo
        details.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrPhoto, Box.NONE) ;
        details.albumId = enrPhoto.getAlbum().getId();

        return details ;
    }

    public XmlPhotoAbout treatAbout(ViewSessionPhoto vSession) throws WebAlbumsServiceException {
        Photo enrPhoto = photoDAO.loadIfAllowed(vSession, vSession.getId());
        if(enrPhoto == null) return null ;

        XmlPhotoAbout output = new XmlPhotoAbout() ;
        output.details = transformToDetails(vSession, enrPhoto) ;
        
        return output ;
    }

}