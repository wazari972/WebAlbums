package net.wazari.service.engine;


import net.wazari.service.exchange.xml.common.XmlDetails;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;

import net.wazari.dao.CarnetFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Carnet;

import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.EditMode;
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
    private UtilisateurFacadeLocal userDAO;
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

        Integer carnetId = vSession.getCarnet();
        Integer page = vSession.getPage();
        Integer count = vSession.getCount();
        page = (page == null ? 0 : page);

        Carnet enrCarnet = carnetDAO.find(carnetId);

        if (enrCarnet == null) {
            output.exception = "Impossible de trouver le carnet (" + carnetId + ")";
            return output ;
        }

        output.picture = enrCarnet.getPicture();
        output.name = enrCarnet.getNom();
        output.count = count;
        output.id = enrCarnet.getId();
        output.description = enrCarnet.getDescription();
        output.text = enrCarnet.getText();
        output.date = enrCarnet.getDate();

        output.rights = webPageService.displayListDroit(enrCarnet.getDroit(), null);

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
                carnet.text = enrCarnet.getText();
            }
            
            XmlDetails details = new XmlDetails();

            details.photoId = enrCarnet.getPicture();
            
            details.description = enrCarnet.getDescription();

            //tags de l'album
            //details.tag_used = webPageService.displayListIBT(Mode.TAG_USED, vSession, enrAlbum, Box.NONE) ;
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

        Integer carnetId = vSession.getId();
        Carnet enrCarnet = carnetDAO.find(carnetId);
        if (enrCarnet == null) {
            return null;
        }

        Boolean supprParam = vSession.getSuppr();
        if (supprParam) {
            if (finder.deleteCarnet(enrCarnet, vSession.getConfiguration())) {
                output.message = "Carnet correctement  supprimé !";
            } else {
                output.exception = "an error occured ...";
            }
            return output;
        }

        Integer user = vSession.getUserAllowed();
        String desc = vSession.getDesc();
        String nom = vSession.getNom();
        String date = vSession.getDate();
        String text = vSession.getCarnetText();
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
            }

        }
        //albumDAO.edit(enrAlbum);

        output.message = "Carnet (" + enrCarnet.getId() + ") correctement mise à jour !";
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
