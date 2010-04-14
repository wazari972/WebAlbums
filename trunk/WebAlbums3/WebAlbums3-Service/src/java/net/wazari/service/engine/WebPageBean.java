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
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.*;

import net.wazari.service.UserLocal;
import net.wazari.service.WebPageLocal;

import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Type;
import net.wazari.service.util.google.GooglePoint;
import net.wazari.service.util.google.GooglePoint.Point;

import net.wazari.util.XmlBuilder;

@Stateless
public class WebPageBean implements WebPageLocal {

    @EJB
    private TagThemeFacadeLocal tagThemeDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private UserLocal userService;
    @EJB
    private TagPhotoFacadeLocal tagPhotoDAO;
    @EJB
    private TagFacadeLocal tagDAO;
    
    private static final long serialVersionUID = -8157612278920872716L;
    private static final Logger log = Logger.getLogger(WebPageBean.class.getName());
    
    public static final int USER_CHEAT = 0 ;
    public static final int TAILLE_PHOTO = 10;
    public static final int TAILLE_ALBUM = 15;

    static {
        WebPageBean.log.info("WebAlbums v4 is loading ... ");

        WebPageBean.log.info("Starting up Hibernate ...");
        WebPageBean.log.info("Hibernate is ready !");
    }

    //look for the theme which ID is themeID in the DB and log it if possible
    //return themeID if OK or null in case of error
    protected Integer tryToSaveTheme(ViewSession vSession)
            throws WebAlbumsServiceException {

        boolean autologging = false;
        Integer themeID = vSession.getThemeId();


        if (themeID == null) {
            themeID = vSession.getConfiguration().autoLogin();
            if (themeID == null) {
                return null;
            }
            autologging = true;
        }

        Integer currentThemeID = vSession.getThemeId();
        if (themeID.equals(currentThemeID)) {
            return themeID;
        }


        //memoriser le nom de l'auteur

        Theme enrTheme = themeDAO.find(themeID);

        if (enrTheme == null) {
            //WebPage.log.warn("Ce theme (" + themeID + ") n'existe pas ...");
            return null;
        }

        Integer savedID = saveTheme(vSession, enrTheme);

        if (autologging) {
            userService.saveUser(vSession, null, null, true);
        }
        return savedID;

    }

    protected Integer saveTheme(ViewSession vSession,
            Theme enrTheme) {
        Integer oldID = vSession.getThemeId();
        Integer newID = enrTheme.getId();

        if (!newID.equals(oldID) && vSession.isSessionManager()) {
            vSession.setUserId(null);
        }

        log.info("saveTheme (" + enrTheme.getNom() + "-" + newID + ")");
        vSession.setThemeName(enrTheme.getNom());
        vSession.setThemeID(newID);
        vSession.setEditionMode(EditMode.NORMAL);

        return newID;
    }

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

    //[begin][end][nb pages][page]
    public Integer[] calculBornes(Type type,
            Integer page,
            Integer asked,
            int size) {
        int ipage;
        int taille = (type == Type.ALBUM ? TAILLE_ALBUM : TAILLE_PHOTO);
        Integer[] bornes = new Integer[4];
        try {
            if (asked != null) {
                //compute the page into wich the element asked is in
                ipage = (int) Math.floor(asked / taille);
                bornes[0] = ipage * taille;
            } else if (page == null) {
                bornes[0] = 0;
                ipage = 0;
            } else {
                bornes[0] = Math.min(page * taille, size);
            }
        } catch (NumberFormatException e) {
            //cannot parse the page
            bornes[0] = 0;
            ipage = 0;
        }
        bornes[0] = (bornes[0] < 0 ? 0 : bornes[0]);
        bornes[1] = Math.min(bornes[0] + taille, size);
        bornes[2] = (int) Math.ceil(((double) size) / ((double) taille));
        bornes[3] = page;
        return bornes;
    }

    public XmlBuilder xmlPage(XmlBuilder from, Integer[] bornes) {
        XmlBuilder page = new XmlBuilder("page");
        page.addComment("Page 0 .. " + bornes[3] + " .." + bornes[2]);
        page.add("url", from);

        if (bornes[2] > 1) {
            int ipage = bornes[3];

            for (int i = 0; i < bornes[2]; i++) {
                if (i == ipage || ipage == -1 && i == 0) {
                    page.add("current", i);
                } else if (i < ipage) {
                    page.add("prev", i);
                } else {
                    page.add("next", i);
                }
            }
            page.validate();
        } else {
            page.addComment("1 page only");
        }
        return page;
    }

    public XmlBuilder xmlLogin(ViewSession vSession) {
        XmlBuilder login = new XmlBuilder("login");
        login.add("theme", vSession.getThemeName());
        login.add("user", vSession.getUserName());

        if (vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            login.add("admin");
            if (vSession.getEditionMode() == EditMode.EDITION) {
                login.add("edit");
            }
        }

        if (vSession.isRootSession()) {
            login.add("root");
        }

        return login.validate();
    }

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
    @SuppressWarnings("unchecked")
    public XmlBuilder displayListIBTNI(Mode mode,
            ViewSession vSession,
            int id,
            Box box,
            Type type,
            String name,
            String info)
            throws WebAlbumsServiceException {

        List<Integer> ids = null;
        List<TagPhoto> list = null;
        if (type == Type.PHOTO) {
            list = tagPhotoDAO.queryByPhoto(id);

        } else if (type == Type.ALBUM) {
            list = tagPhotoDAO.queryByAlbum(id);
        }
        ids = new ArrayList<Integer>(list.size());
        for (TagPhoto enrTagPhoto : list) {
            ids.add(enrTagPhoto.getTag().getId());
        }
        return displayListLBNI(mode, vSession, ids, box, name, info).addAttribut("box", box).addAttribut("type", type).addAttribut("id", id).addAttribut("mode", mode);

    }

    @SuppressWarnings("unchecked")
    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is named NAME
    //Only IDS are added to the list
    //Mode specific information can be provide throug info (null otherwise)
    //(used by Mode.MAP for the link to the relevant address)
    public XmlBuilder displayListLBNI(Mode mode,
            ViewSession vSession,
            List<Integer> ids,
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
            int id = -1;
            String nom = null;
            Point p = null;
            Integer photo = null;

            //first, prepare the information (type, id, nom)
            if (box == Box.MAP_SCRIPT) {
                if (enrTag.getTagType() == 3) {
                    //ensure that this tag is displayed in this theme
                    //(in root theme, diplay all of theme
                    TagTheme enrTagTh = tagThemeDAO.loadByTagTheme(enrTag.getId(), vSession.getThemeId());
                    if (enrTagTh != null && !enrTagTh.getIsVisible()) {
                        continue;
                    }

                    //get its geoloc
                    Geolocalisation enrGeo = enrTag.getGeolocalisation();
                    if (enrGeo != null) {
                        id = enrTag.getId();

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
                id = enrTag.getId();
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
                if (nom != null && (ids == null || ids.contains(id))) {
                    String msg;
                    msg = String.format("<center><a href='Tags?tagAsked=%s'>%s</a></center>",
                            id, p.name);
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
                        if (ids.contains(id)) {
                            selected = "checked";
                        }
                    } else if (!ids.contains(id)) {
                        written = false;
                    }
                }
                if (written) {
                    xmlResult.add(new XmlBuilder(type, nom).addAttribut("id", id).addAttribut("checked", selected));
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

    @SuppressWarnings("unchecked")
    public XmlBuilder displayListDroit(Integer right, Integer albmRight)
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

                if (right == null || right.equals(0)) {
                    selected = true;
                }
            } else if (right != null && right.equals(enrUser.getId())) {
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
            throw new NoSuchElementException("Right problem: " + right + ", " + albmRight);
        }

        return output.validate();
    }

    public XmlBuilder displayMapInBody(ViewSession vSession,
            String name, String info)
            throws WebAlbumsServiceException {
        return displayListLBNI(Mode.TAG_USED, vSession, null, Box.MAP, name, info);
    }

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
    public XmlBuilder displayListIBT(Mode mode,
            ViewSession vSession,
            int id,
            Box box,
            Type type)
            throws WebAlbumsServiceException {
        return displayListIBTNI(mode, vSession, id, box, type,
                null,
                null);
    }

    //display a list into STR
    //according to the MODE
    //and the information found in REQUEST.
    //List is made up of BOX items
    //and is filled with the IDs
    public XmlBuilder displayListLB(Mode mode,
            ViewSession vSession,
            List<Integer> ids,
            Box box)
            throws WebAlbumsServiceException {
        return displayListLBNI(mode, vSession, ids, box,
                "tags", null);
    }
}
