package net.wazari.service.engine;


import net.wazari.service.exchange.xml.album.XmlAlbumYear;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.common.XmlDetails;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Gpx;
import net.wazari.dao.entity.Photo;

import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.AlbumLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.AlbumUtil;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exchange.xml.album.XmlAlbumAbout;
import net.wazari.service.exchange.xml.album.XmlAlbumDisplay;
import net.wazari.service.exchange.xml.album.XmlAlbumEdit;
import net.wazari.service.exchange.xml.album.XmlAlbumGraph;
import net.wazari.service.exchange.xml.album.XmlAlbumList;
import net.wazari.service.exchange.xml.album.XmlAlbumSelect;
import net.wazari.service.exchange.xml.album.XmlAlbumSubmit;
import net.wazari.service.exchange.xml.album.XmlAlbumTop;
import net.wazari.service.exchange.xml.album.XmlAlbumYears;
import net.wazari.service.exchange.xml.album.XmlGpx;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.common.XmlPhotoAlbumUser;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;
import net.wazari.util.system.FilesFinder;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

@Stateless
public class AlbumBean implements AlbumLocal {
    private static final Logger log = LoggerFactory.getLogger(AlbumBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    public static final int TOP = 5;

    @EJB
    private AlbumFacadeLocal albumDAO;
    @EJB
    private AlbumUtil albumUtil;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private WebPageLocal webPageService;
    @EJB 
    private FilesFinder finder;
    @EJB
    private TagFacadeLocal tagDAO;
    
    @Override
    public XmlAlbumEdit treatAlbmEDIT(ViewSessionAlbumEdit vSession,
            XmlAlbumSubmit submit)
            throws WebAlbumsServiceException {

        XmlAlbumEdit output = new XmlAlbumEdit();

        if (submit != null) {
            output.submit = submit ;
        }

        Integer albumId = vSession.getId();
        Integer page = vSession.getPage();
        page = (page == null ? 0 : page);

        Album enrAlbum = albumDAO.find(albumId);

        if (enrAlbum == null) {
            output.exception = "Impossible de trouver l'album (" + albumId + ")";
            return output ;
        }

        if (enrAlbum.getPicture() != null) {
            output.picture = new XmlPhotoId(enrAlbum.getPicture().getId());
            if (vSession.directFileAccess())
                output.picture.path = enrAlbum.getPicture().getPath(true);
        }
        output.name = enrAlbum.getNom();
        output.id = enrAlbum.getId();
        output.description = enrAlbum.getDescription();
        output.description = "";
        
        output.date = enrAlbum.getDate();

        output.tag_used = webPageService.displayListLB(Mode.TAG_USED, vSession, null,
                Box.MULTIPLE);
        output.tag_nused = webPageService.displayListLB(Mode.TAG_NUSED, vSession, null,
                Box.MULTIPLE);
        output.tag_never = webPageService.displayListLB(Mode.TAG_NEVER, vSession, null,
                Box.MULTIPLE);
        output.rights = webPageService.displayListDroit(enrAlbum.getDroit(), null);

        return output;
    }

    @Override
    public XmlAlbumList displayAlbum(ViewSessionAlbumDisplay vSession, 
                                        XmlAlbumSubmit submit, XmlFrom fromPage)
            throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;

        Integer albumId = vSession.getId();
        Integer page = vSession.getPage();
        Bornes bornes = null;
        SubsetOf<Album> albums = null;
        boolean found = false;
        if (page == null && albumId != null) {
            int ipage = 0;
            while (!found) {
                log.info("check on page "+ipage);
                bornes = webPageService.calculBornes(ipage, vSession.getPhotoAlbumSize());
        
                albums = albumDAO.queryAlbums(vSession, Restriction.THEME_ONLY,
                                       AlbumFacadeLocal.TopFirst.FIRST, bornes);
                for (Album enrAlbum : albums.subset) {
                    if (enrAlbum.getId() == albumId) {
                        found = true;
                        page = ipage;
                        break;   
                    }
                }
                    
                if (albums.setSize == 0) {
                    break;
                }
                ipage++;
            }
        } 
        
        if (!found) {
            bornes = webPageService.calculBornes(page, vSession.getPhotoAlbumSize());
        
            albums = albumDAO.queryAlbums(vSession, 
               Restriction.THEME_ONLY, AlbumFacadeLocal.TopFirst.FIRST, bornes);
        }
        
        XmlAlbumList output = new XmlAlbumList(albums.subset.size()) ;
        for(Album enrAlbum : albums.subset) {
            XmlAlbum album = new XmlAlbum();

            if (enrAlbum.getId() == albumId) {
                album.submit = submit ;
            }

            album.date = webPageService.xmlDate(enrAlbum.getDate());
            album.id = enrAlbum.getId();
            album.title = enrAlbum.getNom();

            XmlDetails details = new XmlDetails();

            if (enrAlbum.getPicture() != null) {
                details.photoId = new XmlPhotoId(enrAlbum.getPicture().getId());
                if (vSession.directFileAccess()) {
                    details.photoId.path = enrAlbum.getPicture().getPath(true) ;
                }
            }
            for (Carnet enrCarnet: enrAlbum.getCarnetList()) {
                if (album.carnet == null)
                    album.carnet = new ArrayList(enrAlbum.getCarnetList().size()) ;
                
                XmlCarnet carnet = new XmlCarnet();
                carnet.date = webPageService.xmlDate(enrCarnet.getDate());
                carnet.id = enrCarnet.getId();
                carnet.name = enrCarnet.getNom();
                if (enrCarnet.getPicture() != null) {
                    carnet.picture = new XmlPhotoId(enrCarnet.getPicture().getId());
                    if (vSession.directFileAccess())
                        carnet.picture.path = enrCarnet.getPicture().getPath(true);
                }
                album.carnet.add(carnet);
            }
            details.description = enrAlbum.getDescription();
            
            for (Gpx enrGpx : enrAlbum.getGpxList()) {
                if (album.gpx == null)
                    album.gpx = new ArrayList(enrAlbum.getGpxList().size()) ;
                XmlGpx gpx = new XmlGpx();
                gpx.id = enrGpx.getId();
                gpx.description = enrGpx.getDescription();
                
                album.gpx.add(gpx);
            }
            //tags de l'album
            details.tag_used = webPageService.displayListIBTD(Mode.TAG_USED, 
                              vSession, enrAlbum, Box.NONE, enrAlbum.getDate());
            //utilisateur ayant le droit à l'album
            //ou a l'une des photos qu'il contient
            if (vSession.isSessionManager()) {
                details.user = new XmlPhotoAlbumUser(enrAlbum.getDroit().getNom(), null);
                details.userInside = new LinkedList<String>() ;
                for (Utilisateur user : userDAO.loadUserInside(enrAlbum.getId())) {
                    details.userInside.add(user.getNom()) ;
                }
            }
            album.details = details ;

            album.photoCount.put("album", new XmlAlbum.Counter(enrAlbum.getPhotoList().size()));

            output.album.add(album);
        }

        output.page = webPageService.xmlPage(fromPage, bornes);
        stopWatch.stop("Service.displayAlbum") ;
        return output ;
    }

    @Override
    public XmlAlbumTop treatTOP(ViewSessionAlbum vSession) {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlAlbumTop top5 = new XmlAlbumTop();

        SubsetOf<Album> albums = albumDAO.queryAlbums(vSession, 
                         Restriction.THEME_ONLY, TopFirst.TOP, new Bornes(TOP));
        for (Album enrAlbum : albums.subset) {
            XmlAlbum album = new XmlAlbum();
            album.id = enrAlbum.getId();
            album.name = enrAlbum.getNom();
            if (enrAlbum.getPicture() != null) {
                album.picture = new XmlPhotoId(enrAlbum.getPicture().getId());
                if (vSession.directFileAccess())
                    album.picture.path = enrAlbum.getPicture().getPath(true);
            }
            top5.album.add(album);
        }
        stopWatch.stop("Service.treatTOP") ;
        return top5;
    }

    @Override
    public XmlAlbumGraph treatGRAPH(ViewSessionAlbum vSession) {
        XmlAlbumGraph graph = new XmlAlbumGraph();
        
        graph.album.addAll(treatSELECT(vSession).album);
        
        return graph;
    }
    
    @Override
    public XmlAlbumSelect treatSELECT(ViewSessionAlbum vSession) {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlAlbumSelect select = new XmlAlbumSelect();

        SubsetOf<Album> albums = albumDAO.queryAlbums(vSession, 
                                    Restriction.THEME_ONLY, TopFirst.ALL, null);
        
        List<Tag> tagList = new LinkedList<Tag>();
        for (int tagId : vSession.getTagAsked()) {
            Tag enrTag = tagDAO.find(tagId);
            if (enrTag != null) 
                tagList.add(enrTag);
        }
        
        for (Album enrAlbum : albums.subset) {
            XmlAlbum album = new XmlAlbum();
            album.id = enrAlbum.getId();
            album.name = enrAlbum.getNom();
            album.albmDate = enrAlbum.getDate();
            try {
                album.time = new SimpleDateFormat("yyyy-MM-dd").parse(enrAlbum.getDate()).getTime();
            } catch (ParseException ex) {
                album.time = new Date().getTime() ;
            }

            if (enrAlbum.getPicture() != null) {
                album.picture = new XmlPhotoId(enrAlbum.getPicture().getId());
                if (vSession.directFileAccess())
                    album.picture.path = enrAlbum.getPicture().getPath(true);
            }
            
            if (tagList.isEmpty()) {
                album.photoCount.put("album", new XmlAlbum.Counter(enrAlbum.getPhotoList().size()));
            } else {
                for (Tag enrTag : tagList) {
                    album.photoCount.put("tag__"+enrTag.getId()+"__"+enrTag.getNom(), new XmlAlbum.Counter()) ;
                }
                for (Photo enrPhoto : enrAlbum.getPhotoList()) {
                    for (TagPhoto enrTp : enrPhoto.getTagPhotoList()) {
                        if (tagList.contains(enrTp.getTag())) {
                            Tag enrTag = enrTp.getTag();
                            String key = "tag__"+enrTag.getId()+"__"+enrTag.getNom();
                            album.photoCount.get(key).inc();
                        }
                    }
                }
            }
            
            select.album.add(album);
        }

        stopWatch.stop("Service.treatSELECT") ;
        return select;
    }

    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy") ;
    @Override
    public XmlAlbumYears treatYEARS(ViewSessionAlbum vSession) {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlAlbumYears years = new XmlAlbumYears();

        Album enrFirstAlbum = albumDAO.loadFirstAlbum(vSession, Restriction.THEME_ONLY);
        Album enrLastAlbum = albumDAO.loadLastAlbum(vSession, Restriction.THEME_ONLY);

        if (enrFirstAlbum == null || enrLastAlbum == null) return years ;
        int firstYear = 2011 ;
        int lastYear = 2011 ;
        try {
            log.info(enrFirstAlbum.toString()) ;
            firstYear = Integer.parseInt(YEAR.format(Album.DATE_STANDARD.parse(enrFirstAlbum.getDate())));
            lastYear =  Integer.parseInt(YEAR.format(Album.DATE_STANDARD.parse(enrLastAlbum.getDate()))) ;
        } catch (ParseException ex) {
            log.warn("ParseException", ex);
        }
        
        Integer nbPerYear = vSession.getNbPerYear();
        if (nbPerYear == null)
            nbPerYear = TOP;
        for (Integer currentYear = lastYear; currentYear >= firstYear; currentYear--) {
            XmlAlbumYear year = new XmlAlbumYear() ;
            year.year = currentYear;
            SubsetOf<Album> albums = albumDAO.queryRandomFromYear(vSession, Restriction.THEME_ONLY, 
                    new Bornes(nbPerYear), currentYear.toString()) ;
            for (Album enrAlbum : albums.subset) {
                XmlAlbum album = new XmlAlbum();
                album.id = enrAlbum.getId();
                album.name = enrAlbum.getNom();
                if (enrAlbum.getPicture() != null) {
                    album.picture = new XmlPhotoId(enrAlbum.getPicture().getId());
                    if (vSession.directFileAccess())
                        album.picture.path = enrAlbum.getPicture().getPath(true);
                }
                year.album.add(album) ;
            }
            years.year.add(year);
        }

        stopWatch.stop("Service.treatYEARS") ;
        return years ;
    }

    @Override
    public XmlAlbumSubmit treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession)
            throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlAlbumSubmit output = new XmlAlbumSubmit();
        Integer albumId = vSession.getId();


        Album enrAlbum = albumDAO.find(albumId);
        if (enrAlbum == null) {
            return null;
        }

        Boolean supprParam = vSession.getSuppr();
        if (supprParam) {
            if (finder.deleteAlbum(enrAlbum, vSession.getConfiguration())) {
                output.message = "Album correctement  supprimé !";
            } else {
                output.exception = "an error occured ...";
            }
            return output;
        }

        Integer user = vSession.getUserAllowed();
        String desc = vSession.getDesc();
        String nom = vSession.getNom();
        String date = vSession.getDate();
        Integer[] tags = vSession.getTags();
        Boolean force = vSession.getForce();
        if (user != null) {
            albumUtil.updateDroit(enrAlbum, user);
        }
        albumUtil.setTagsToPhoto(enrAlbum, tags, force);
        enrAlbum.setNom(nom);
        enrAlbum.setDescription(desc);
        if (date != null) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                enrAlbum.setDate(date);
            } catch (ParseException ex) {
                log.info("Date format incorrect: "+date);
            }

        }
        albumDAO.edit(enrAlbum);

        output.message = "Album (" + enrAlbum.getId() + ") correctement mise à jour !";
        stopWatch.stop("Service.treatSUBMIT") ;
        return output ;
    }

    @Override
    public XmlAlbumDisplay treatAlbmDISPLAY(ViewSessionAlbumDisplay vSession,
            XmlAlbumSubmit submit) throws WebAlbumsServiceException {

        XmlAlbumDisplay output = new XmlAlbumDisplay() ;
        XmlFrom thisPage = new XmlFrom();
        thisPage.name = "Albums" ;

        output.albumList = displayAlbum(vSession, submit, thisPage) ;
        return output ;
    }
    
    @Override
    public XmlAlbumAbout treatABOUT(ViewSessionAlbum vSession) throws WebAlbumsServiceException {
        Integer albumId = vSession.getId() ;
        if (albumId == null) return null;
        Album enrAlbum = albumDAO.loadIfAllowed(vSession, albumId);
        if (enrAlbum == null) return null;

        XmlAlbumAbout about = new XmlAlbumAbout() ;
        about.album = new XmlAlbum() ;
        about.album.id = enrAlbum.getId() ;
        about.album.title = enrAlbum.getNom() ;
        about.album.date = webPageService.xmlDate(enrAlbum.getDate());
        about.album.details = new XmlDetails() ;
        if (enrAlbum.getPicture() != null) {
            about.album.details.photoId = new XmlPhotoId(enrAlbum.getPicture().getId()) ;
            if (vSession.directFileAccess())
                about.album.details.photoId.path = enrAlbum.getPicture().getPath(true) ;
        }

        about.album.details.description = enrAlbum.getDescription();

        //tags de l'album
        about.album.details.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrAlbum, Box.NONE) ;

        return about ;
    }
}
