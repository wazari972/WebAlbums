package net.wazari.service.engine;


import net.wazari.service.exchange.xml.common.XmlDetails;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;

import net.wazari.dao.CarnetFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Photo;

import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.AlbumUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.CarnetLocal;
import net.wazari.service.exchange.ViewSessionCarnet;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetDisplay;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetEdit;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSubmit;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.carnet.XmlCarnetEdit;
import net.wazari.service.exchange.xml.carnet.XmlCarnetSubmit;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsDisplay;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsTop;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.util.system.FilesFinder;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

@Stateless
public class CarnetBean implements CarnetLocal {
    private static final Logger log = LoggerFactory.getLogger(CarnetBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;

    @EJB
    private CarnetFacadeLocal carnetDAO;
    @EJB
    private AlbumUtil albumUtil;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private WebPageLocal webPageService;
    @EJB private FilesFinder finder;

    @Override
    public XmlCarnetEdit treatEDIT(ViewSessionCarnetEdit vSession,
            XmlCarnetSubmit submit)
            throws WebAlbumsServiceException {

        XmlCarnetEdit output = new XmlCarnetEdit();

        if (submit != null) {
            output.submit = submit ;
        }

        Integer albumId = vSession.getId();
        Integer page = vSession.getPage();
        Integer count = vSession.getCount();
        page = (page == null ? 0 : page);

        Album enrAlbum = null;//= albumDAO.find(albumId);

        if (enrAlbum == null) {
            output.exception = "Impossible de trouver l'album (" + albumId + ")";
            return output ;
        }

        output.picture = enrAlbum.getPicture();
        output.name = enrAlbum.getNom();
        output.count = count;
        output.id = enrAlbum.getId();
        output.description = enrAlbum.getDescription();
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

    public XmlCarnetsDisplay treatDISPLAY(ViewSessionCarnetDisplay vSession,
            XmlCarnetSubmit submit) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        
        XmlCarnetsDisplay output = new XmlCarnetsDisplay() ;
        XmlFrom thisPage = new XmlFrom();
        thisPage.name = "Carnets" ;

        EditMode inEditionMode = vSession.getEditionMode();
        Integer page = vSession.getPage();
        Integer eltAsked = vSession.getCount();

        Bornes bornes = webPageService.calculBornes(page, eltAsked, vSession.getConfiguration().getAlbumSize());

        SubsetOf<Carnet> carnets = carnetDAO.queryCarnets(vSession, Restriction.ALLOWED_AND_THEME, AlbumFacadeLocal.TopFirst.FIRST, bornes);

        int count = bornes.getFirstElement();
        String oldDate = null ;
        
        for(Carnet enrCarnet : carnets.subset) {
            XmlCarnet carnet = new XmlCarnet();

            carnet.date = webPageService.xmlDate(enrCarnet.getDate(), oldDate);
            oldDate = enrCarnet.getDate() ;
            carnet.id = enrCarnet.getId();
            carnet.count = count;
            carnet.name = enrCarnet.getNom();

            XmlDetails details = new XmlDetails();

            Integer iPhoto = enrCarnet.getPicture();
            if (iPhoto != null) {
                Photo enrPhoto = photoDAO.find(iPhoto) ;
                if (enrPhoto != null) {
                    details.photoId = enrPhoto.getId() ;
                } else {
                    log.warn("Invalid photo ({}) for album {}", new Object[]{iPhoto, enrCarnet.getId()});
                }
            }
            
            details.description = enrCarnet.getDescription();

            //tags de l'album
            //details.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrAlbum, Box.NONE) ;
            //utilisateur ayant le droit à l'album
            //ou a l'une des photos qu'il contient
            if (vSession.isSessionManager()) {
                if (inEditionMode != EditMode.VISITE) {
                    details.user = enrCarnet.getDroit().getNom();
                    details.userInside = new LinkedList<String>() ;
                    for (Utilisateur user : userDAO.loadUserInside(enrCarnet.getId())) {
                        details.userInside.add(user.getNom()) ;
                    }
                }
            }
            carnet.details = details ;

            count++;

            output.carnet.add(carnet);
        }

        output.page = webPageService.xmlPage(thisPage, bornes);
        stopWatch.stop("Service.displayAlbum") ;
        return output ;
    }

    @Override
    public XmlCarnetSubmit treatSUBMIT(ViewSessionCarnetSubmit vSession)
            throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlCarnetSubmit output = new XmlCarnetSubmit();


        Album enrAlbum = null;// = albumDAO.loadIfAllowed(vSession, albumId);
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
        //albumDAO.edit(enrAlbum);

        output.message = "Album (" + enrAlbum.getId() + ") correctement mise à jour !";
        stopWatch.stop("Service.treatSUBMIT") ;
        return output ;
    }
    
    @Override
    public XmlCarnetsTop treatTOP(ViewSessionCarnet vSession) {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlCarnetsTop top5 = new XmlCarnetsTop();

        SubsetOf<Carnet> carnets = carnetDAO.queryCarnets(vSession, Restriction.ALLOWED_AND_THEME, TopFirst.TOP, new Bornes(AlbumBean.TOP));
        int i = 0;
        for (Carnet enrCarnet : carnets.subset) {
            XmlCarnet carnet = new XmlCarnet();
            carnet.id = enrCarnet.getId();
            carnet.count = i;
            carnet.name = enrCarnet.getNom();
            carnet.picture = enrCarnet.getPicture();
            
            top5.carnet.add(carnet);
            i++ ;
        }
        stopWatch.stop("Service.treatTOP") ;
        return top5;
    }
}
