package net.wazari.service.engine;

import java.io.File;
import java.util.ArrayList;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.*;
import net.wazari.dao.entity.*;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.service.PhotoLocal;
import net.wazari.service.UserLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Tag_Mode;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplayMassEdit.Turn;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit.TagAction;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSimple;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlGpx;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.photo.*;
import net.wazari.util.system.FilesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private DaoToXmlBean daoToXml;
    @EJB
    private FilesFinder systemFacade;
    
    @Override
    public XmlPhotoSubmit treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,
            Boolean correct) throws WebAlbumsServiceException {
        XmlPhotoSubmit output = new XmlPhotoSubmit();
        Integer photoId = vSession.getId();
        output.photoId = photoId;
        
        if (photoId == null) {
            output.exception = "Pas de photo demandé ... (id=null)" ;
            return output ;
        }
        Photo enrPhoto = photoDAO.loadIfAllowed(vSession.getVSession(), photoId);

        if (enrPhoto == null) {
            output.exception = "Impossible de trouver cette photo "
                    + "(" + photoId + (vSession.getVSession().isRootSession() ? "" : "/" + vSession.getVSession().getTheme()) + ")" ;
            return output ;
        }

        //supprimer ?
        if (vSession.getSuppr()) {
            if (finder.deletePhoto(enrPhoto, vSession.getVSession().getConfiguration())) {
                output.message = "Photo correctement  supprimé !";
            } else {
                output.exception = "Impossible de supprimer correctement la photo ...";
            }
            return output ;
        }

        //mise à jour des tag/description
        enrPhoto.setDescription(vSession.getDesc());

        String user = vSession.getDroit();
        if (user != null && !user.isEmpty()) {
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

            enrAlbum.setPicture(enrPhoto);
            albumDAO.edit(enrAlbum);
        }

        //utiliser cette photo comme representante de l'album ?
        Boolean themeBackground = vSession.getThemeBackground();
        if (themeBackground) {
            Theme enrTheme = enrPhoto.getAlbum().getTheme();
            log.warn("Assign theme background {}",enrPhoto) ;
            themeDAO.setBackground(enrTheme, enrPhoto);
            vSession.getVSession().getTheme().setBackground(enrPhoto);
            
            File backgroundDir = new File(vSession.getVSession().getConfiguration()
                    .getTempPath()+vSession.getVSession().getTheme().getNom()) ;
            
            log.info("Delete and create background dir: {}", backgroundDir) ;
            if (backgroundDir.listFiles() != null) {
                for (File child : backgroundDir.listFiles()) {
                    if (!child.delete()) {
                        log.warn("Could not remove background file: {}",child);
                    }
                }
            }
        }
        //utiliser cette photo comme representante de l'album ?
        Boolean themePicture = vSession.getThemePicture();
        if (themePicture) {
            Theme enrTheme = enrPhoto.getAlbum().getTheme();
            log.warn("Assign theme picture {}",enrPhoto) ;
            themeDAO.setPicture(enrTheme, enrPhoto);
            vSession.getVSession().getTheme().setPicture(enrPhoto);
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
                //creer un tagTheme pour cette photo/tag/theme
                enrTagTh = tagThemeDAO.newTagTheme();

                enrTagTh.setTheme(actualTheme);
                enrTagTh.setTag(enrTag);
                //par défaut le tag est visible
                enrTagTh.setVisible(true);

                tagThemeDAO.create(enrTagTh);
            }
            //changer la photo representant ce tag/theme
            enrTagTh.setPhoto(enrPhoto);

            log.info( "edit {}", enrTagTh);
            tagThemeDAO.edit(enrTagTh);
        }

        output.message = " Photo (" + enrPhoto.getId() + ") "
                + "correctement mise à jour !" ;

        return output ;
    }

    @Override
    public XmlPhotoDisplay treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException {
        XmlPhotoDisplay output = new XmlPhotoDisplay();
        //afficher les photos
        //afficher la liste des albums de cet theme
        Integer albmPage = vSession.getAlbmPage();

        Integer albumId = vSession.getAlbum();
        if (albumId == null) {
            output.exception = "No album asked ... (album=null)" ;
            return output ;
        }

        Album enrAlbum = albumDAO.loadIfAllowed(vSession.getVSession(), albumId);
        if (enrAlbum == null) {
            output.exception = "L'album (" + albumId + ") n'existe pas ou n'est pas accessible..." ;
            return output ;
        }
        XmlAlbum album = output.album = new XmlAlbum();
        
        daoToXml.convertAlbum(vSession.getVSession(), enrAlbum, album, true);

        for (Carnet enrCarnet: enrAlbum.getCarnetList()) {
            if (album.carnet == null) {
                album.carnet = new ArrayList(enrAlbum.getCarnetList().size()) ;
            }
            
            XmlCarnet carnet = new XmlCarnet();
            daoToXml.convertCarnet(vSession.getVSession(), enrCarnet, carnet);
            album.carnet.add(carnet);
        }
        
        for (Photo enrGpx : enrAlbum.getGpxList()) {
            if (album.gpx == null) {
                album.gpx = new ArrayList(enrAlbum.getGpxList().size()) ;
            }
            
            XmlGpx gpx = new XmlGpx();
            daoToXml.convertGpx(vSession.getVSession(), enrGpx, gpx);
            album.gpx.add(gpx);
        }
        
        XmlFrom thisPage = new XmlFrom();
        thisPage.name = "Photos";
        thisPage.album = albumId ;
        thisPage.albmPage = albmPage ;
        
        PhotoRequest rq = new PhotoRequest(TypeRequest.PHOTO, enrAlbum);
        output.photoList = displayPhoto(rq, vSession, submit, thisPage);
        
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

        daoToXml.convertPhotoDetails(vSession.getVSession(), enrPhoto, output.details, false);
                
        output.tag_used = webPageService.displayListIBT(Tag_Mode.TAG_USED, vSession.getVSession(), enrPhoto,
                Box.MULTIPLE);

        output.tag_nused = webPageService.displayListIBT(Tag_Mode.TAG_NUSED, vSession.getVSession(), enrPhoto,
                Box.MULTIPLE);

        output.tag_never = webPageService.displayListIBT(Tag_Mode.TAG_NEVER, vSession.getVSession(), enrPhoto,
                Box.MULTIPLE);

        output.tag_used_lst = webPageService.displayListIBT(Tag_Mode.TAG_USED, vSession.getVSession(), enrPhoto,
                Box.LIST);
        
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

        Integer page = vSession.getPage();
        Integer photoId = null;
        if (submit != null) photoId = submit.photoId;
        
        Bornes bornes = null;
        SubsetOf<Photo> lstP = null;
        boolean found = false;
        if (page == null && submit != null) {
            int ipage = 0;
            while (!found) {
                bornes = webPageService.calculBornes(ipage, vSession.getVSession().getPhotoAlbumSize());
        
                if (rq.type == TypeRequest.PHOTO) {
                    lstP = photoDAO.loadFromAlbum(vSession.getVSession(), rq.albumId, bornes, 
                                                  ListOrder.ASC);
                } else {
                    lstP = photoDAO.loadByTags(vSession.getVSession(), rq.listTagId, bornes, 
                                               ListOrder.DESC);
                }
                for (Photo enrPhoto : lstP.subset) {
                    if (enrPhoto.getId() == photoId) {
                        found = true;
                        break;   
                    }
                }
                    
                if (lstP.setSize == 0) {
                    break;
                }
                ipage++;
            }
        } 
        if (!found) {
            bornes = webPageService.calculBornes(page, vSession.getVSession().getPhotoAlbumSize());
        
            if (rq.type == TypeRequest.PHOTO) {
                lstP = photoDAO.loadFromAlbum(vSession.getVSession(), rq.albumId, bornes, 
                                          ListOrder.ASC);
            } else {
                lstP = photoDAO.loadByTags(vSession.getVSession(), rq.listTagId, bornes, 
                                           ListOrder.DESC);
            }
        }        
        
        String degrees = "0";
        Integer[] tags = null;
        int countME = 0;
        boolean massEditParam = false;
        boolean reSelect = false;
        boolean current = false;

        XmlPhotoList output = new XmlPhotoList(lstP.subset.size()) ;
        Turn turn = null;
        if (vSession.getVSession().isSessionManager()) {
            if (vSession.getWantMassedit()) {
                turn = vSession.getMassEdit().getTurn();
                
                if (turn == Turn.LEFT) {
                    degrees = "270";
                } else if (turn == Turn.RIGHT) {
                    degrees = "90";
                } else if (turn == Turn.TAG || turn == Turn.UNTAG || turn == Turn.MVTAG) {
                    tags = vSession.getMassEdit().getAddTags();
                } else if (turn == Turn.AUTHOR) {
                    tags = vSession.getMassEdit().getAddTags();
                }
                massEditParam = true;
                if ((turn == Turn.MVTAG || turn == Turn.AUTHOR) && tags.length != 1) {
                    massEditParam = false;
                    output.exception = "Please select only one tag for this action: "+ turn;
                    turn = null;
                }
            }
        }

        boolean submitted = false ;
        for (Photo enrPhoto : lstP.subset) {
            XmlPhoto photo = new XmlPhoto();
            boolean reSelectThis = false;
            if (massEditParam) {
                Boolean chkbox = vSession.getMassEdit().getChk(enrPhoto.getId());
                if (chkbox) {
                    current = true;
                    if (turn == Turn.LEFT || turn == Turn.RIGHT) {
                        if (!photoUtil.rotate(vSession.getVSession(), enrPhoto, degrees)) {
                            photo.exception = "Erreur dans le ConvertWrapper ...";
                            reSelectThis = true;
                        }
                    
                    } else if (turn != null) {
                        String verb;
                        if (turn == Turn.TAG) {
                            photoUtil.addTags(enrPhoto, tags);
                            verb = "added";
                        } else if (turn == Turn.UNTAG) {
                            for (Integer tag : tags) {
                                log.warn("remove {}", tag);
                                photoUtil.removeTag(enrPhoto, tag);
                            }
                            verb = "removed";
                        } else if (turn == Turn.MVTAG) {
                            Integer rmTag = vSession.getMassEdit().getRmTag();
                            photoUtil.removeTag(enrPhoto, rmTag);
                            photoUtil.addTags(enrPhoto, tags);
                            verb = "added and tag " + rmTag + " removed";
                        } else if (turn == Turn.AUTHOR) {
                            enrPhoto.setTagAuthor(tagDAO.find(tags[0]));
                            photoDAO.edit(enrPhoto);
                            verb = "set as author";
                        } else {
                            verb = "nothinged";
                        }
                        String str = " ";
                        for (Integer tag : tags) {
                            str += tag + " ";
                        }
                                
                        photo.message = "Tag" + str + " " + verb + " to photo #" + enrPhoto.getId();
                    }
                    countME++;
                }
            }

            if (enrPhoto.getId().equals(photoId)) {
                if (submit != null) {
                    photo.submit = submit ;
                    submitted = true ;
                }
            }
            if (vSession.getVSession().isSessionManager()) {
                if ((reSelect || reSelectThis) && current) {
                    photo.checked = true ;
                }
            }
            
            daoToXml.convertPhotoDetails(vSession.getVSession(), enrPhoto, photo.details, rq.type == TypeRequest.TAG);
            
            //liste des utilisateurs pouvant voir cette photo
            if (vSession.getVSession().isSessionManager()) {
                daoToXml.addUserOutside(vSession.getVSession(), enrPhoto, photo.details);
            }
            
            daoToXml.addAuthorDetails(vSession.getVSession(), enrPhoto, photo);
            daoToXml.addExifDetails(vSession, enrPhoto, photo);
            
            output.photo.add(photo);
            current = false;
        }

        if (!submitted) {
            output.submit = submit ;
        }

        if (vSession.getVSession().isSessionManager()) {
            XmlPhotoMassEdit massEdit = new XmlPhotoMassEdit();
            massEdit.tag_used = webPageService.displayListBN(Tag_Mode.TAG_USED, vSession.getVSession(), Box.LIST);
            massEdit.tag_never = webPageService.displayListBN(Tag_Mode.TAG_NEVER_EVER, vSession.getVSession(), Box.LIST);
            if (massEditParam) {
                String msg;
                if (countME == 0 || Turn.NOTHING == turn) {
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
        return output ;
    }

    @Override
    public XmlPhotoRandom treatRANDOM(ViewSession vSession) throws WebAlbumsServiceException {
        Photo enrPhoto = null;
        while (true) {
            enrPhoto = photoDAO.loadRandom(vSession);
            if (enrPhoto == null) {
                //this means there is NO photo at all, get out of here!
                return null ;
            }
            // wait for a valid photo // what about videos ... ?
            if (!enrPhoto.isGpx()) {
                break;
            }
        }
        
        XmlPhotoRandom output = new XmlPhotoRandom() ;
        daoToXml.convertPhotoDetails(vSession, enrPhoto, output.details, true);
        
        return output ;
    }

    public XmlPhotoAbout treatABOUT(ViewSessionPhotoSimple vSession) throws WebAlbumsServiceException {
        Photo enrPhoto = photoDAO.loadIfAllowed(vSession.getVSession(), vSession.getId());
        if(enrPhoto == null) {
            return null ;
        }

        XmlPhotoAbout output = new XmlPhotoAbout() ;
        output.details = new XmlDetails();
        daoToXml.convertPhotoDetails(vSession.getVSession(), enrPhoto, output.details, true); 
        
        return output ;
    }

    @Override
    public XmlPhotoFastEdit treatFASTEDIT(ViewSessionPhotoFastEdit vSession) {
        XmlPhotoFastEdit output = new XmlPhotoFastEdit();
        
        Integer starLevel = vSession.getNewStarLevel();
        if (starLevel != null) {
            vSession.setStarLevel(starLevel);
            
            return output;
        }
        
        Integer id = vSession.getId();
        Photo enrPhoto = photoDAO.find(id);
        if (enrPhoto == null) {
            output.desc_msg = "No photo found for id="+id;
            output.desc_status = XmlPhotoFastEdit.Status.ERROR;
            output.tag_status = XmlPhotoFastEdit.Status.ERROR;
            output.delete_status = XmlPhotoFastEdit.Status.ERROR;
            return output;
        }

        if (vSession.getSuppr()) {
            if (finder.deletePhoto(enrPhoto, vSession.getVSession().getConfiguration())) {
                output.delete_status = XmlPhotoFastEdit.Status.OK;
            } else {
                output.delete_status = XmlPhotoFastEdit.Status.ERROR;
            }
            return output;
        }
        
        String desc = vSession.getDesc();
        if (desc != null) {
            enrPhoto.setDescription(desc);
            output.desc_status = XmlPhotoFastEdit.Status.OK;
        }

        TagAction action = vSession.getTagAction();
        if (action != null) {
            try {
                Integer[] tagSet = vSession.getTagSet();

                output.tag_status = XmlPhotoFastEdit.Status.OK;

                if (tagSet == null || tagSet.length == 0) {
                    output.tag_status = XmlPhotoFastEdit.Status.ERROR;
                    output.tag_msg = "Not tag selected";
                } else if (action == TagAction.SET) {
                    photoUtil.setTags(enrPhoto, tagSet);
                } else if (action == TagAction.ADD) {
                    photoUtil.addTags(enrPhoto, tagSet);
                } else if (action == TagAction.RM) {
                    photoUtil.removeTag(enrPhoto, tagSet[0]);
                } else {
                    output.tag_status = XmlPhotoFastEdit.Status.ERROR;
                    output.tag_msg = "Not tag action selected";
                }
            } catch (WebAlbumsServiceException ex) {
                output.tag_msg = ex.getMessage();
                output.tag_status = XmlPhotoFastEdit.Status.ERROR;
            }
        }
        
        Integer stars = vSession.getStars();
        if (stars != null) {
            enrPhoto.setStars(stars);
            output.stars_status = XmlPhotoFastEdit.Status.OK;
        }
        
        photoDAO.pleaseFlush();
        
        return output;
    }
    
    public XmlDetails getPhotoByPath(ViewSession vSession, String path) throws WebAlbumsServiceException {
        XmlDetails ret = new XmlDetails();
        Photo enrPhoto = photoDAO.loadByPath(path);
        daoToXml.convertPhotoDetails(vSession, enrPhoto, ret, true);
        
        return ret;
    }
}
