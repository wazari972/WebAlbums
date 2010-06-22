package net.wazari.service.engine;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;

import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.AlbumLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.AlbumUtil;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;

import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.common.util.StringUtil;
import net.wazari.util.system.FilesFinder;
import net.wazari.common.util.XmlBuilder;

@Stateless
public class AlbumBean implements AlbumLocal {
    private static final Logger log = Logger.getLogger(AlbumBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    private static final int TOP = 5;

    @EJB
    AlbumFacadeLocal albumDAO;
    @EJB
    AlbumUtil albumUtil;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private WebPageLocal webPageService;
    private FilesFinder finder;

    @Override
    public XmlBuilder treatAlbmEDIT(ViewSessionAlbumEdit vSession,
            XmlBuilder submit)
            throws WebAlbumsServiceException {

        XmlBuilder output = new XmlBuilder("albm_edit");

        if (submit != null) {
            output.add(submit);
        }

        Integer albumId = vSession.getId();
        Integer page = vSession.getPage();
        Integer count = vSession.getCount();
        page = (page == null ? 0 : page);

        Album enrAlbum = albumDAO.find(albumId);

        if (enrAlbum == null) {
            output.cancel();
            output.addException("Impossible de trouver l'album (" + albumId + ")");
            return output.validate();
        }

        output.add("picture", enrAlbum.getPicture());
        output.add("name", enrAlbum.getNom());
        output.add("count", count);
        output.add("id", enrAlbum.getId());
        output.add("description", enrAlbum.getDescription());
        output.add("date", enrAlbum.getDate());

        output.add(webPageService.displayListLB(Mode.TAG_USED, vSession, null,
                Box.MULTIPLE));
        output.add(webPageService.displayListLB(Mode.TAG_NUSED, vSession, null,
                Box.MULTIPLE));
        output.add(webPageService.displayListLB(Mode.TAG_NEVER, vSession, null,
                Box.MULTIPLE));
        output.add(webPageService.displayListDroit(enrAlbum.getDroit(), null));

        output.validate();
        return output.validate();
    }

    @Override
    public XmlBuilder displayAlbum(XmlBuilder output,
            ViewSessionAlbumDisplay vSession,
            XmlBuilder submit,
            XmlBuilder thisPage) throws WebAlbumsServiceException {

        EditMode inEditionMode = vSession.getEditionMode();
        Integer albumId = vSession.getId();
        Integer page = vSession.getPage();
        Integer eltAsked = vSession.getCount();

        Bornes bornes = webPageService.calculBornes(page, eltAsked, vSession.getConfiguration().getAlbumSize());

        SubsetOf<Album> albums = albumDAO.queryAlbums(vSession, Restriction.ALLOWED_AND_THEME, AlbumFacadeLocal.TopFirst.FIRST, bornes);

        int count = bornes.getFirstElement();
        String oldDate = null ;
        for(Album enrAlbum : albums.subset) {
            XmlBuilder album = new XmlBuilder("album");

            if (enrAlbum.getId() == albumId) {
                album.add(submit);
            }

            album.add(StringUtil.xmlDate(enrAlbum.getDate(), oldDate));
            oldDate = enrAlbum.getDate() ;
            album.add("id", enrAlbum.getId());
            album.add("count", count);
            album.add("title", enrAlbum.getNom());

            XmlBuilder details = new XmlBuilder("details");

            Integer iPhoto = enrAlbum.getPicture();
            if (iPhoto != null) {
                Photo enrPhoto = photoDAO.find(iPhoto) ;
                details.add("photoID", enrPhoto.getId());
                details.add("miniWidth", enrPhoto.getWidth());
                details.add("miniHeight", enrPhoto.getHeight());
            }
            
            details.add("description", enrAlbum.getDescription());

            //tags de l'album
            details.add(webPageService.displayListIBT(Mode.TAG_USED, vSession, enrAlbum, Box.NONE));
            //utilisateur ayant le droit à l'album
            //ou a l'une des photos qu'il contient
            if (vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
                if (inEditionMode != EditMode.VISITE) {
                    details.add(enrAlbum.getDroit().getNom());
                    details.add("userInside", userDAO.loadUserInside(enrAlbum.getId()));
                }
            }
            album.add(details);

            count++;

            output.add(album);
        }
        if (submit != null) {
            output.add(submit);
        }

        output.add(webPageService.xmlPage(thisPage, bornes));

        return output.validate();
    }

    @Override
    public XmlBuilder treatALBM(ViewSessionAlbum vSession)
            throws WebAlbumsServiceException {

        XmlBuilder output = new XmlBuilder("albums");
        XmlBuilder submit = null;

        Special special = vSession.getSpecial();
        if (special == Special.TOP5) {
            XmlBuilder top5 = new XmlBuilder("top5");

            SubsetOf<Album> albums = albumDAO.queryAlbums(vSession, Restriction.ALLOWED_AND_THEME, TopFirst.TOP, new Bornes(TOP));
            int i = 0;
            for (Album enrAlbum : albums.subset) {
                XmlBuilder album = new XmlBuilder("album");
                album.add("id", enrAlbum.getId());
                album.add("count", i);
                album.add("nom", enrAlbum.getNom());
                if (enrAlbum.getPicture() != null) {
                    album.add("photo", enrAlbum.getPicture());
                }
                top5.add(album);
            }
            output.add(top5);
            return output.validate();
        }

        Action action = vSession.getAction();
        if (action == Action.SUBMIT && vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            submit = treatAlbmSUBMIT((ViewSessionAlbumSubmit) vSession);
        }

        if (action == Action.EDIT && vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            output = treatAlbmEDIT((ViewSessionAlbumEdit) vSession, submit);

        } else {
            //sinon afficher la liste des albums de ce theme
            output.add(treatAlbmDISPLAY((ViewSessionAlbumDisplay)vSession, submit));
        }

        return output.validate();
    }

    @Override
    public XmlBuilder treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession)
            throws WebAlbumsServiceException {

        XmlBuilder output = new XmlBuilder(null);
        Integer albumId = vSession.getId();


        Album enrAlbum = albumDAO.loadIfAllowed(vSession, albumId);
        if (enrAlbum == null) {
            return null;
        }

        Boolean supprParam = vSession.getSuppr();
        if (supprParam) {
            XmlBuilder suppr = new XmlBuilder("suppr_msg");
            if (finder.deleteAlbum(enrAlbum, suppr, vSession.getConfiguration())) {
                output.add(suppr);
                output.add("message", "Album correctement  supprimé !");
            } else {
                output.addException(suppr);
                output.addException("Exception", "an error occured ...");
            }
            return output.validate();
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

        output.add("message", "Album (" + enrAlbum.getId() + ") correctement mise à jour !");

        return output.validate();
    }


    @Override
    public XmlBuilder treatAlbmDISPLAY(ViewSessionAlbumDisplay vSession,
            XmlBuilder submit) throws WebAlbumsServiceException {

        XmlBuilder output = new XmlBuilder(null);
        XmlBuilder thisPage = new XmlBuilder("name", "Albums");

        displayAlbum(output, vSession, submit, thisPage);

        return output.validate();
    }
}
