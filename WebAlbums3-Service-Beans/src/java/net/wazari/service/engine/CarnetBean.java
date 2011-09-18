package net.wazari.service.engine;


import net.wazari.service.exchange.xml.common.XmlDetails;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import net.wazari.common.util.StringUtil;
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
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.CarnetLocal;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
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
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private AlbumFacadeLocal albumDAO;
    @EJB
    private WebPageLocal webPageService;
    @EJB private FilesFinder finder;

    @Override
    public XmlCarnetEdit treatEDIT(ViewSessionCarnetEdit vSession,
            XmlCarnetSubmit submit)
            throws WebAlbumsServiceException {

        XmlCarnetEdit output = new XmlCarnetEdit();
        Integer carnetId = vSession.getCarnet();
        Integer page = vSession.getPage();
        Integer count = vSession.getCount();
        page = (page == null ? 0 : page);
        
        if (submit != null) {
            output.submit = submit ;
        }
        
        /*Reuse the Carnet from SUBMIT if any, otherwise lookup this one.  */
        Carnet enrCarnet = (submit != null && submit.carnet != null) ?
                submit.carnet : carnetDAO.find(carnetId);
        
        if (enrCarnet == null) {
            output.rights = webPageService.displayListDroit(null, null);
            return output ;
        }
        output.rights = webPageService.displayListDroit(enrCarnet.getDroit(), null);
        output.picture = enrCarnet.getPicture();
        output.name = enrCarnet.getNom();
        output.count = count;
        output.id = enrCarnet.getId();
        output.description = enrCarnet.getDescription();
        output.text = enrCarnet.getText();
        output.date = enrCarnet.getDate();

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

        int count = 0;
        List<Carnet> carnets = null;
        Integer carnetId = vSession.getCarnet();
        if (carnetId != null) {
            Carnet enrCarnet = carnetDAO.loadIfAllowed(vSession, carnetId);
            if (enrCarnet != null) {
                carnets = new ArrayList<Carnet>(1);
                carnets.add(enrCarnet);
            } else {
                output.message = "Couldn't load carnet #"+carnetId;
                carnetId = null;
            }
        }
        
        if (carnetId == null) {
            Bornes bornes = webPageService.calculBornes(page, eltAsked, vSession.getConfiguration().getAlbumSize());
            count = bornes.getFirstElement();
            carnets = carnetDAO.queryCarnets(vSession, Restriction.ALLOWED_AND_THEME, AlbumFacadeLocal.TopFirst.FIRST, bornes).subset;
            output.page = webPageService.xmlPage(thisPage, bornes);
        }
        
        String oldDate = null ;
        for(Carnet enrCarnet : carnets) {
            XmlCarnet carnet = new XmlCarnet();

            carnet.date = webPageService.xmlDate(enrCarnet.getDate(), oldDate);
            oldDate = enrCarnet.getDate() ;
            carnet.id = enrCarnet.getId();
            carnet.count = count;
            carnet.name = enrCarnet.getNom();
            if (carnetId != null) {
                carnet.text = StringUtil.escapeXML(enrCarnet.getText());
            }
            carnet.photo = new ArrayList<Integer>(enrCarnet.getPhotoList().size());
            for (Photo p : enrCarnet.getPhotoList())
                carnet.photo.add(p.getId());
            
            XmlDetails details = new XmlDetails();

            details.photoId = enrCarnet.getPicture();
            
            details.description = enrCarnet.getDescription();

            //tags du carnet
            details.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrCarnet, Box.NONE) ;
            //utilisateur ayant le droit à l'album
            //ou a l'une des photos qu'il contient
            if (vSession.isSessionManager()) {
                if (inEditionMode != EditMode.VISITE) {
                    details.user = enrCarnet.getDroit().getNom();
                }
            }
            carnet.details = details ;

            count++;

            output.carnet.add(carnet);
        }

        
        stopWatch.stop("Service.displayCarnet") ;
        return output ;
    }

    @Override
    public XmlCarnetSubmit treatSUBMIT(ViewSessionCarnetSubmit vSession)
            throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        XmlCarnetSubmit output = new XmlCarnetSubmit();
        Carnet enrCarnet;
        
        Integer carnetId = vSession.getCarnet();
        if (carnetId == null) {
            enrCarnet = carnetDAO.newCarnet();
            enrCarnet.setTheme(vSession.getTheme());
        } else {
            enrCarnet = carnetDAO.find(carnetId);
        }
        if (enrCarnet == null) {
            output.exception = "Couldn't find the carnet #"+carnetId;
            return output;
        }
        output.carnet = enrCarnet;
        Boolean supprParam = vSession.getSuppr();
        if (supprParam) {
            if (finder.deleteCarnet(enrCarnet, vSession.getConfiguration())) {
                output.message = "Carnet correctement  supprimé !";
            } else {
                output.exception = "an error occured ...";
            }
            return output;
        }
        Integer repr = vSession.getCarnetRepr();
        Integer user = vSession.getUserAllowed();
        String desc = vSession.getDesc();
        String nom = vSession.getNom();
        String date = vSession.getDate();
        String text = vSession.getCarnetText();
        Set<Integer> photos = vSession.getCarnetPhoto();
        Set<Integer> albums = vSession.getCarnetAlbum();
        
        if (user != null) {
            Utilisateur enrDroit = userDAO.find(user);
            if (enrDroit != null) {
                enrCarnet.setDroit(enrDroit);
            }
        }
        enrCarnet.setText(text);
        enrCarnet.setNom(nom);
        enrCarnet.setDescription(desc);
        if (date != null) {
            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(date);
                enrCarnet.setDate(date);
            } catch (ParseException ex) {
                log.info("Date format incorrect: "+date);
                output.valid = false;
            }
        }
        
        if (repr != null) {
            try {
                Photo enrRepr = photoDAO.find(repr);
                enrCarnet.setPicture(enrRepr.getId());
                photos.add(repr);
            } catch (Exception e) {}
        }
        
        List<Photo> enrPhotos = new ArrayList<Photo>(photos.size());
        for (Integer photo : photos) {
            try {
                enrPhotos.add(photoDAO.find(photo));
            } catch (Exception e) {}
        }
        
        if (!enrPhotos.isEmpty())
            enrCarnet.setPhotoList(enrPhotos);
        
        List<Album> enrAlbums = new ArrayList<Album>(albums.size());
        for (Integer album : albums) {
            try {
                enrAlbums.add(albumDAO.find(album));
            } catch (Exception e) {}
        }
        if (!enrAlbums.isEmpty())
            enrCarnet.setAlbumList(enrAlbums);
        
        try {
            if (carnetId == null)
                carnetDAO.create(enrCarnet);

            carnetDAO.edit(enrCarnet);
            output.message = "Carnet (" + enrCarnet.getId() + ") correctement mise à jour !";
        } catch (ConstraintViolationException e) {
            output.exception = "La mise a jour de (" + enrCarnet.getId() + ") a échouée ... "+e.getMessage();
            output.valid = false;
        }
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
