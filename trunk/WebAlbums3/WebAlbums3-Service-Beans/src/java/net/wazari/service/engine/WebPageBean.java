package net.wazari.service.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.*;

import net.wazari.dao.entity.facades.PhotoOrAlbum;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.WebPageLocal;

import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.util.google.GooglePoint;
import net.wazari.service.util.google.GooglePoint.Point;

import net.wazari.util.XmlBuilder;

@Stateless
public class WebPageBean implements WebPageLocal {

    @EJB
    private TagThemeFacadeLocal tagThemeDAO;
    @EJB
    private UtilisateurFacadeLocal userDAO ;
    @EJB
    private TagPhotoFacadeLocal tagPhotoDAO;
    @EJB
    private TagFacadeLocal tagDAO;
    
    private static final long serialVersionUID = -8157612278920872716L;
    private static final Logger log = Logger.getLogger(WebPageBean.class.getName());
    
    public static final int USER_CHEAT = 0 ;

    static {
        WebPageBean.log.info("WebAlbums v4 is loading ... ");

        WebPageBean.log.info("Starting up Hibernate ...");
        WebPageBean.log.info("Hibernate is ready !");
    }

    @Override
    public EditMode getNextEditionMode(ViewSession vSession) {
        EditMode editionMode = vSession.getEditionMode();
        EditMode next;
        if (editionMode == EditMode.VISITE) {
            next = EditMode.NORMAL;
        } else if (editionMode == EditMode.NORMAL) {
            next = EditMode.EDITION;
        } else if (editionMode == EditMode.EDITION) {
            next = EditMode.VISITE;
        } else {
            next = EditMode.NORMAL;
        }
        return next;
    }
    
    //try to get the 'asked' element
    //or the page 'page' if asked is null
    //go to the first page otherwise
    @Override
    public Bornes calculBornes(Integer page,
            Integer eltAsked,
            int taille) 
    {
        Bornes bornes;

        if (eltAsked != null) {
            //compute the page into which the element asked is
            int first = (int) Math.floor(eltAsked / taille) ;
            bornes = new Bornes(taille, first, first * taille);
        } else if (page != null) {
            bornes = new Bornes(taille, page * taille, page);
        } else {
            bornes = new Bornes(taille, 0,0) ;
        }
        
        return bornes;
    }

    @Override
    public XmlBuilder xmlPage(XmlBuilder from, Bornes bornes) {
        XmlBuilder page = new XmlBuilder("page");
        page.addComment("Page 0 .. " + bornes.getCurrentPage() + " .."+bornes.getLastPage());
        page.add("url", from);
        int start = Math.max(0, bornes.getCurrentPage() - 5) ;
        int stop = Math.min(bornes.getCurrentPage() + 5, bornes.getLastPage()) ;
        if (start != 0) page.add("first", 0);
        if (stop != bornes.getLastPage()) page.add("last", 0);
        for (int i = start; i < stop; i++) {
            if (i == bornes.getCurrentPage() || bornes.getCurrentPage() == -1 && i == 0) {
                page.add("current", i);
            } else if (i < bornes.getCurrentPage()) {
                page.add("prev", i);
            } else {
                page.add("next", i);
            }
        }
        page.validate();
       
        return page;
    }

    @Override
    public XmlBuilder xmlLogin(ViewSession vSession) {
        XmlBuilder login = new XmlBuilder("login");
        String theme = "Not logged in" ;
        Theme enrTheme = vSession.getTheme() ;
        if (enrTheme != null) {
            theme = enrTheme.getNom() ;
        }
        login.add("theme", theme);
        login.add("user", vSession.getUserName());
        log.info("logged as manager? "+vSession.isSessionManager());
        if (vSession.isSessionManager()) {
            login.add("admin");
            if (vSession.getEditionMode() == EditMode.EDITION) {
                login.add("edit");
            }
        }
        log.info("logged as root? "+vSession.isRootSession());
        if (vSession.isRootSession()) {
            login.add("root");
        }

        return login.validate();
    }

    @Override
    public XmlBuilder xmlAffichage(ViewSession vSession) {
        XmlBuilder affichage = new XmlBuilder("affichage");
        if (vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            if (vSession.getEditionMode() == EditMode.EDITION) {
                affichage.add("edit");
            }
            affichage.add("edition", vSession.getEditionMode());
        }
        affichage.add("maps", "Avec Carte");
        affichage.add("details", vSession.getDetails());

        return affichage;
    }

    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is named NAME
    //if type is PHOTO, info (MODE) related to the photo #ID are put in the list
    //if type is ALBUM, info (MODE) related to the album #ID are put in the list
    @Override
    public XmlBuilder displayListIBTNI(Mode mode,
            ViewSession vSession,
            PhotoOrAlbum entity,
            Box box,
            String name,
            String info)
            throws WebAlbumsServiceException {

        String type = "unknown" ;
        List<TagPhoto> list = null;
        if (entity instanceof Photo) {
            list = ((Photo) entity).getTagPhotoList();
            type = "PHOTO" ;
        } else if (entity instanceof Album) {
            list = tagPhotoDAO.queryByAlbum((Album) entity);
            type = "ALBUMS" ;
        }
        List<Tag> tags = new ArrayList<Tag>(list.size());
        for (TagPhoto enrTagPhoto : list) {
            tags.add(enrTagPhoto.getTag());
        }
        return displayListLBNI(mode, vSession, tags, box, name, info)
                .addAttribut("box", box)
                .addAttribut("type", type)
                .addAttribut("id", entity.getId())
                .addAttribut("mode", mode);

    }

    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is named NAME
    //Only IDS are added to the list
    //Mode specific information can be provide throug info (null otherwise)
    //(used by Mode.MAP for the link to the relevant address)
    @Override
    public XmlBuilder displayListLBNI(Mode mode,
            ViewSession vSession,
            List<Tag> ids,
            Box box,
            String name,
            String info)
            throws WebAlbumsServiceException {
        XmlBuilder xmlResult = null;
        List<Tag> tags = null;

        //affichage de la liste des tags où il y aura des photos
        if (!vSession.isSessionManager()) {
            if (mode != Mode.TAG_USED && mode != Mode.TAG_GEO) {
                throw new RuntimeException("Don't want to process mode " + mode + " when not logged at manager");
            }

            tags = tagDAO.loadVisibleTags(vSession, mode == Mode.TAG_GEO) ;
        } else /* current manager*/ {

            if (mode == Mode.TAG_USED || mode == Mode.TAG_GEO) {
               tags = tagDAO.loadVisibleTags(vSession, mode == Mode.TAG_GEO) ;
            } else if (mode == Mode.TAG_ALL) {

                //afficher tous les tags
               tags = tagDAO.findAll() ;
            } else if (mode == Mode.TAG_NUSED || mode == Mode.TAG_NEVER) {
                List<Tag> notWantedTags = null ;
                //select the tags not used [in this theme]
                if (mode == Mode.TAG_NEVER || vSession.isRootSession()) {
                    //select all the tags used
                    
                    notWantedTags = tagPhotoDAO.selectDistinctTags() ;

                } else /* TAG_NUSED*/ {
                    //select all the tags used in photo of this theme
                    notWantedTags = tagPhotoDAO.selectUnusedTags(vSession) ;
                }

                tags = tagDAO.getNoSuchTags(vSession, notWantedTags) ;

            } else /* not handled mode*/ {
                xmlResult = new XmlBuilder("list");

                xmlResult.addException("Unknown handled mode :" + mode);
                return xmlResult.validate();
            }
        } /* isManagerSession */

        xmlResult = new XmlBuilder("tags");
        xmlResult.addAttribut("mode", mode);

        GooglePoint map = null;
        if (box == Box.MAP_SCRIPT) {
            map = new GooglePoint(name);
        }

        xmlResult.addComment("Mode: " + mode);
        xmlResult.addComment("Box:" + box);
        xmlResult.addComment("List: " + ids);

        for(Tag enrTag: tags) {
            String type = "nop";
            Tag tagId =  null ;
            String nom = null;
            Point p = null;
            Integer photo = null;

            //first, prepare the information (type, id, nom)
            if (box == Box.MAP_SCRIPT) {
                if (enrTag.getTagType() == 3) {
                    //ensure that this tag is displayed in this theme
                    //(in root theme, diplay all of theme
                    TagTheme enrTagTh = tagThemeDAO.loadByTagTheme(enrTag.getId(), vSession.getTheme().getId());
                    if (enrTagTh != null && !enrTagTh.getIsVisible()) {
                        continue;
                    }

                    //get its geoloc
                    Geolocalisation enrGeo = enrTag.getGeolocalisation();
                    if (enrGeo != null) {
                        tagId = enrTag;

                        p = new Point(enrGeo.getLat(),
                                enrGeo.getLongitude(),
                                enrTag.getNom());
                        nom = enrTag.getNom();

                        //Get the photo to display, if any
                        if (enrTagTh != null) {
                            photo = enrTagTh.getPhoto();
                        }
                    }
                }
            } else if (box == Box.MAP) {
            } else {
                tagId = enrTag;
                nom = enrTag.getNom();

                switch (enrTag.getTagType()) {
                    case 1:
                        type = "who";
                        break;
                    case 2:
                        type = "what";
                        break;
                    case 3:
                        type = "where";
                        break;
                    default:
                        type = "unknown";
                        break;
                }
            }
            //display the value [if in ids][select if in ids]
            if (box == Box.MAP_SCRIPT) {
                if (nom != null && (ids == null || ids.contains(tagId))) {
                    String msg;
                    msg = String.format("<center><a href='Tags?tagAsked=%s'>%s</a></center>",
                            tagId.getId(), p.name);
                    if (photo != null) {
                        msg += String.format("<br/><img height='150px' alt='%s' " +
                                "src='Images?" +
                                "id=%d&amp;mode=PETIT' />",
                                p.name,
                                photo);
                    }

                    p.setMsg(msg);
                    map.addPoint(p);
                }
            } else if (box == Box.MAP) {
            } else {
                String selected = "";
                boolean written = true;
                if (ids != null) {
                    if (box == Box.MULTIPLE) {
                        if (ids.contains(tagId)) {
                            selected = "checked";
                        }
                    } else if (!ids.contains(tagId)) {
                        written = false;
                    }
                }
                if (written) {
                    xmlResult.add(new XmlBuilder(type, nom).addAttribut("id", tagId.getId()).addAttribut("checked", selected));
                }
            }
        } /* while loop*/

        if (box == Box.MAP) {
            xmlResult = new XmlBuilder("map");
            xmlResult.add("name", name);
            xmlResult.add(GooglePoint.getBody());
        } else if (box == Box.MAP_SCRIPT) {
            xmlResult = XmlBuilder.newText();
            xmlResult.addText(map.getInitFunction(vSession.getConfiguration()));
        }


        return xmlResult.validate();
    }

    @Override
    public XmlBuilder displayListDroit(Utilisateur right, Integer albmRight)
            throws WebAlbumsServiceException {
        if (right == null && albmRight == null) {
            throw new NullPointerException("Droit and Album cannot be null");
        }

        XmlBuilder output = new XmlBuilder("userList");
        boolean hasSelected = false;

        List<Utilisateur> lstUsr = userDAO.findAll() ;
        for (Utilisateur enrUser : lstUsr) {

            String name = enrUser.getNom();
            Integer id = enrUser.getId();
            boolean selected = false;

            if (albmRight != null && albmRight.equals(enrUser.getId())) {
                name = "[" + name + "]";
                id = null;

                if (right == null) {
                    selected = true;
                }
            } else if (right != null && right.getId().equals(enrUser.getId())) {
                selected = true;
            }

            XmlBuilder util = new XmlBuilder("user", name);
            util.addAttribut("id", id == null ? "null" : id);
            if (selected) {
                util.addAttribut("selected", true);
                hasSelected = true;
            }
            output.add(util);
        }

        if (!hasSelected) {
            throw new NoSuchElementException("Right problem: " + right + ", " + albmRight + " ("+lstUsr+")");
        }

        return output.validate();
    }

    @Override
    public XmlBuilder displayMapInBody(ViewSession vSession,
            String name, String info)
            throws WebAlbumsServiceException {
        return displayListLBNI(Mode.TAG_USED, vSession, null, Box.MAP, name, info);
    }

    @Override
    public XmlBuilder displayMapInScript(ViewSession vSession,
            String name, String info)
            throws WebAlbumsServiceException {
        XmlBuilder output = displayListLBNI(Mode.TAG_USED, vSession, null, Box.MAP_SCRIPT, name, info);
        return output;
    }

    //display a list into STR
    //according to the MODE
    //and the information found in the REQUEST.
    //List made up of BOX items,
    //and is named NAME
    @Override
    public XmlBuilder displayListBN(Mode mode,
            ViewSession vSession,
            Box box,
            String name)
            throws WebAlbumsServiceException {
        return displayListLBNI(mode, vSession, null, box, name, null);
    }

    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is named with the default name for this MODE
    @Override
    public XmlBuilder displayListB(Mode mode,
            ViewSession vSession,
            Box box)
            throws WebAlbumsServiceException {
        return displayListLBNI(mode, vSession, null, box,
                "tags", null);
    }

    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is named with the default name for this MODE
    //if type is PHOTO, info (MODE) related to the photo #ID are put in the list
    //if type is ALBUM, info (MODE) related to the album #ID are put in the list
    @Override
    public XmlBuilder displayListIBT(Mode mode,
            ViewSession vSession,
            PhotoOrAlbum entity,
            Box box)
            throws WebAlbumsServiceException {
        return displayListIBTNI(mode, vSession, entity, box,
                null,
                null);
    }

    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is filled with the IDs
    @Override
    public XmlBuilder displayListLB(Mode mode,
            ViewSession vSession,
            List<Tag> ids,
            Box box)
            throws WebAlbumsServiceException {
        return displayListLBNI(mode, vSession, ids, box,
                "tags", null);
    }
}
