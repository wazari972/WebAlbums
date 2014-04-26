package net.wazari.service.engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.common.exception.WebAlbumsException;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.*;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.AlbumLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.AlbumUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Tag_Mode;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSelect;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSimple;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumYear;
import net.wazari.service.exchange.xml.album.*;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.common.XmlPhotoAlbumUser;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;
import net.wazari.util.system.FilesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AlbumBean implements AlbumLocal {
    private static final Logger log = LoggerFactory.getLogger(AlbumBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    public static final int TOP = 5;

    @EJB private AlbumFacadeLocal albumDAO;
    @EJB private AlbumUtil albumUtil;
    @EJB private UtilisateurFacadeLocal userDAO;
    @EJB private WebPageLocal webPageService;
    @EJB private FilesFinder finder;
    @EJB private TagFacadeLocal tagDAO;
    @EJB private ThemeFacadeLocal themeDAO;
    @EJB private DaoToXmlBean daoToXmlService;
    
    @Override
    public XmlAlbum treatAlbmEDIT(ViewSessionAlbumEdit vSession)
            throws WebAlbumsServiceException {
        Integer albumId = vSession.getId();
        
        Album enrAlbum = albumDAO.find(albumId);
        if (enrAlbum == null) {
            throw new WebAlbumsServiceException(WebAlbumsException.ErrorType.ServiceException, 
                                                "Impossible de trouver l'album (" + albumId + ")");
        }
        
        XmlAlbum album = new XmlAlbum();
        daoToXmlService.convertAlbum(vSession.getVSession(), enrAlbum, album, true);
        daoToXmlService.addAlbumRight(vSession.getVSession(), enrAlbum, album);
        return album;
    }

    @Override
    public XmlAlbumList displayAlbum(ViewSessionAlbumDisplay vSession, 
                                        XmlAlbumSubmit submit, XmlFrom fromPage)
            throws WebAlbumsServiceException {

        Integer albumId = vSession.getId();
        Integer page = vSession.getPage();
        Bornes bornes = null;
        SubsetOf<Album> albums = null;
        boolean found = false;
        if (page == null && albumId != null) {
            int ipage = 0;
            while (!found) {
                bornes = webPageService.calculBornes(ipage, vSession.getVSession().getPhotoAlbumSize());
        
                albums = albumDAO.queryAlbums(vSession.getVSession(), Restriction.THEME_ONLY,
                                       AlbumFacadeLocal.TopFirst.FIRST, bornes);
                for (Album enrAlbum : albums.subset) {
                    if (Objects.equals(enrAlbum.getId(), albumId)) {
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
            bornes = webPageService.calculBornes(page, vSession.getVSession().getPhotoAlbumSize());
        
            albums = albumDAO.queryAlbums(vSession.getVSession(), 
               Restriction.THEME_ONLY, AlbumFacadeLocal.TopFirst.FIRST, bornes);
        }
        
        XmlAlbumList output = new XmlAlbumList(albums.subset.size()) ;
        for(Album enrAlbum : albums.subset) {
            XmlAlbum album = new XmlAlbum();

            if (Objects.equals(enrAlbum.getId(), albumId)) {
                album.submit = submit ;
            }

            album.date = webPageService.xmlDate(enrAlbum.getDate());
            album.id = enrAlbum.getId();
            album.name = enrAlbum.getNom();

            if (enrAlbum.getPicture() != null) {
                album.details.photoId = new XmlPhotoId(enrAlbum.getPicture().getId());
                if (vSession.getVSession().directFileAccess()) {
                    album.details.photoId.path = enrAlbum.getPicture().getPath(true) ;
                }
                
                if (enrAlbum.getPicture().isGpx()) {
                    //keep null if false
                    album.details.isGpx = true;
                }
            }
            for (Carnet enrCarnet: enrAlbum.getCarnetList()) {
                if (album.carnet == null) {
                    album.carnet = new ArrayList(enrAlbum.getCarnetList().size()) ;
                }
                
                XmlCarnet carnet = new XmlCarnet();
                carnet.date = webPageService.xmlDate(enrCarnet.getDate());
                carnet.id = enrCarnet.getId();
                carnet.name = enrCarnet.getNom();
                if (enrCarnet.getPicture() != null) {
                    carnet.picture = new XmlPhotoId(enrCarnet.getPicture().getId());
                    if (vSession.getVSession().directFileAccess()) {
                        carnet.picture.path = enrCarnet.getPicture().getPath(true);
                    }
                }
                album.carnet.add(carnet);
            }
            album.details.setDescription(enrAlbum.getDescription());
            
            for (Photo enrGpx : enrAlbum.getGpxList()) {
                if (album.gpx == null) {
                    album.gpx = new ArrayList(enrAlbum.getGpxList().size()) ;
                }
                XmlGpx gpx = new XmlGpx();
                gpx.id = enrGpx.getId();
                gpx.setDescription(enrGpx.getDescription());
                
                album.gpx.add(gpx);
            }
            //tags de l'album
            album.details.tag_used = webPageService.displayListIBTD(Tag_Mode.TAG_USED, 
                              vSession.getVSession(), enrAlbum, null, enrAlbum.getDate());
            //utilisateur ayant le droit à l'album
            //ou a l'une des photos qu'il contient
            if (vSession.getVSession().isSessionManager()) {
                album.details.user = new XmlPhotoAlbumUser(enrAlbum.getDroit().getNom(), null);
                album.details.userInside = new LinkedList<String>() ;
                for (Utilisateur user : userDAO.loadUserInside(enrAlbum.getId())) {
                    album.details.userInside.add(user.getNom()) ;
                }
            }

            album.photoCount.put("album", new XmlAlbum.Counter(enrAlbum.getPhotoList().size()));

            output.album.add(album);
        }

        output.page = webPageService.xmlPage(fromPage, bornes);
        return output ;
    }

    @Override
    public XmlAlbumGpx treatGPX(ViewSession vSession) throws WebAlbumsServiceException {
        XmlAlbumGpx gpxList = new XmlAlbumGpx();
        
        SubsetOf<Album> albums = albumDAO.queryAlbums(vSession, 
                         Restriction.THEME_ONLY, TopFirst.ALL, null);
        for (Album enrAlbum : albums.subset) {
            for (Photo enrGpx : enrAlbum.getGpxList()) {
                XmlGpx gpx = new XmlGpx();
                
                gpx.setDescription(enrGpx.getDescription());
                gpx.id = enrGpx.getId();
                gpx.albumId = enrGpx.getAlbum().getId();
                gpx.albumName = enrGpx.getAlbum().getNom();
                gpx.tag_used = webPageService.displayListIBTD(ViewSession.Tag_Mode.TAG_USED,  
                        vSession, enrGpx, ViewSession.Box.NONE,
                        enrGpx.getAlbum().getDate());
                
                gpxList.gpx.add(gpx);
            }
        }
        return gpxList;
    }
    
    @Override
    public XmlAlbumTop treatTOP(ViewSession vSession) throws WebAlbumsServiceException {
        XmlAlbumTop top5 = new XmlAlbumTop();

        SubsetOf<Album> albums = albumDAO.queryAlbums(vSession, 
                         Restriction.THEME_ONLY, TopFirst.TOP, new Bornes(TOP));
        for (Album enrAlbum : albums.subset) {
            XmlAlbum album = new XmlAlbum();
            daoToXmlService.convertAlbum(vSession, enrAlbum, album, false);
            
            top5.album.add(album);
        }
        
        return top5;
    }

    @Override
    public XmlAlbumGraph treatGRAPH(ViewSessionAlbumSelect vSession) throws WebAlbumsServiceException {
        XmlAlbumGraph graph = new XmlAlbumGraph();
        
        graph.album.addAll(treatSELECT(vSession).album);
        
        return graph;
    }
    
    @Override
    public XmlAlbumSelect treatSELECT(ViewSessionAlbumSelect vSession) throws WebAlbumsServiceException {
        XmlAlbumSelect select = new XmlAlbumSelect();

        SubsetOf<Album> albums = albumDAO.queryAlbums(vSession.getVSession(), 
                                    Restriction.THEME_ONLY, TopFirst.ALL, null);
        
        boolean wantTags = vSession.getWantTags();
        
        List<Tag> tagList = new LinkedList<Tag>();
        for (int tagId : vSession.getTagAsked()) {
            Tag enrTag = tagDAO.find(tagId);
            if (enrTag != null) {
                tagList.add(enrTag);
            }
        }
        
        for (Album enrAlbum : albums.subset) {
            XmlAlbum album = new XmlAlbum();
            daoToXmlService.convertAlbum(vSession.getVSession(), enrAlbum, album, wantTags);
            daoToXmlService.addAlbumGpx(vSession.getVSession(), enrAlbum, album);
            
            try {
                album.time = new SimpleDateFormat("yyyy-MM-dd").parse(enrAlbum.getDate()).getTime();
            } catch (ParseException ex) {
                album.time = new Date().getTime() ;
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

        return select;
    }

    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy") ;
    @Override
    public XmlAlbumYears treatYEARS(ViewSessionAlbumYear vSession) throws WebAlbumsServiceException {
        XmlAlbumYears years = new XmlAlbumYears();

        Album enrFirstAlbum = albumDAO.loadFirstAlbum(vSession.getVSession(), Restriction.THEME_ONLY);
        Album enrLastAlbum = albumDAO.loadLastAlbum(vSession.getVSession(), Restriction.THEME_ONLY);

        if (enrFirstAlbum == null || enrLastAlbum == null) {
            return years ;
        }
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
        if (nbPerYear == null) {
            nbPerYear = TOP;
        }
        for (Integer currentYear = lastYear; currentYear >= firstYear; currentYear--) {
            XmlAlbumYear year = new XmlAlbumYear() ;
            year.year = currentYear;
            SubsetOf<Album> albums = albumDAO.queryRandomFromYear(vSession.getVSession(), Restriction.THEME_ONLY, 
                    new Bornes(nbPerYear), currentYear.toString()) ;
            for (Album enrAlbum : albums.subset) {
                XmlAlbum album = new XmlAlbum();
                daoToXmlService.convertAlbum(vSession.getVSession(), enrAlbum, album, false);
                year.album.add(album) ;
            }
            years.year.add(year);
        }

        return years ;
    }

    @Override
    public XmlAlbumAgo treatAGO(ViewSessionAlbumAgo vSession) throws WebAlbumsServiceException {
        XmlAlbumAgo ago = new XmlAlbumAgo();
        
        Integer year = vSession.getYear();
        Integer month = vSession.getMonth();
        Integer day = vSession.getDay();
        boolean all = vSession.getAll();
        
        if (year == null && month == null && day == null) {
            //Note that Calendar.MONTH is for no apparent reasons ZERO based
            month = Calendar.getInstance().get(Calendar.MONTH)+1;
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        
        List<Album> albums = albumDAO.loadTimesAgoAlbums(vSession.getVSession(), year, month, day,
                                                         (all ? Restriction.NONE : Restriction.THEME_ONLY));
        for (Album enrAlbum : albums) {
            XmlAlbum album = new XmlAlbum();
            daoToXmlService.convertAlbum(vSession.getVSession(), enrAlbum, album, false);
            ago.album.add(album);
        }
        
        return ago;
    }
    
    @Override
    public XmlAlbumSubmit treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession)
            throws WebAlbumsServiceException {
        XmlAlbumSubmit output = new XmlAlbumSubmit();
        Integer albumId = vSession.getId();

        Album enrAlbum = albumDAO.find(albumId);
        if (enrAlbum == null) {
            return null;
        }

        Boolean supprParam = vSession.getSuppr();
        if (supprParam) {
            if (finder.deleteAlbum(enrAlbum, vSession.getVSession().getConfiguration())) {
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
                log.warn("Date format incorrect: {}", date);
            }
        }
        
        albumDAO.edit(enrAlbum);

        int newThemeId = vSession.getNewTheme();
        if (newThemeId != enrAlbum.getTheme().getId()) {
            Theme enrNewTheme = themeDAO.find(newThemeId);
            finder.moveAlbum(enrAlbum, enrNewTheme, vSession.getVSession().getConfiguration());
        }
        
        output.message = "Album (" + enrAlbum.getId() + ") correctement mise à jour !";
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
    public XmlAlbumAbout treatABOUT(ViewSessionAlbumSimple vSession) throws WebAlbumsServiceException {
        Integer albumId = vSession.getId() ;
        if (albumId == null) {
            return null;
        }
        Album enrAlbum = albumDAO.loadIfAllowed(vSession.getVSession(), albumId);
        if (enrAlbum == null) {
            return null;
        }

        XmlAlbumAbout about = new XmlAlbumAbout() ;
        about.album = new XmlAlbum() ;
        daoToXmlService.convertAlbum(vSession.getVSession(), enrAlbum, about.album, true);
        
        return about ;
    }
}
