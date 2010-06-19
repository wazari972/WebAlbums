package net.wazari.service.engine;

import java.util.List;
import java.util.NoSuchElementException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.GeolocalisationFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.util.system.FilesFinder;
import net.wazari.util.XmlBuilder;

import net.wazari.dao.entity.*;

import net.wazari.service.ConfigLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;

@Stateless
public class ConfigBean implements ConfigLocal {

    private static final long serialVersionUID = -628341734743684910L;
    
    @EJB private TagFacadeLocal tagDAO ;
    @EJB private GeolocalisationFacadeLocal geoDAO ;
    @EJB private TagThemeFacadeLocal tagThemeDAO ;
    @EJB private TagPhotoFacadeLocal tagPhotoDAO ;
    @EJB private ThemeFacadeLocal themeDAO ;
    @EJB private WebPageLocal webPageService ;

    @Override
    public XmlBuilder treatCONFIG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {

        Special special = vSession.getSpecial();
        if (special != null) {
            return new XmlBuilder("updated");
        }
        return displayCONFIG(vSession);
    }

    @Override
    public XmlBuilder treatIMPORT(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("import");
        String theme = vSession.getImportTheme();
        String passwrd = vSession.getPassword();

            output.add("message", "Begining ...");
            boolean correct = new FilesFinder().importAuthor(vSession, theme, passwrd, output, vSession.getConfiguration());

            if (correct) {
                output.add("message", "Well done !");
            } else {
                output.addException("An error occured ...");
            }
        
        return output.validate();
    }

    @Override
    public XmlBuilder treatMODTAG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("modTag");

        String nouveau = vSession.getNouveau();
        Integer tag = vSession.getTag();

        if (tag == null || tag == -1) {
            output.addException("Pas de tag selectionné ...");
            return output.validate();
        }
        try {
            Tag enrTag = tagDAO.find(tag);
            if (tag == null) {
                output.addException("Le Tag #" + tag + " n'est pas dans la base ...");
                return output.validate();
            }

            output.add("oldName", enrTag.getNom());

            enrTag.setNom(nouveau);
            tagDAO.edit(enrTag);
            output.add("newName", nouveau);

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            output.cancel();
            output.addException("NoSuchElementException", e);
        }
        return output.validate();
    }

    @Override
    public XmlBuilder treatMODGEO(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("modGeo");

        String lng = vSession.getLng();
        String lat = vSession.getLat();
        Integer tag = vSession.getTag();

        if (tag == null || tag == -1) {
            output.addException("Pas de tag selectionné ...");
            return output.validate();
        }

        Tag enrTag = tagDAO.find(tag);
        if (enrTag == null) {
            output.addException("La localisation " + tag + " ne correspond à aucun tag ...");
            return output.validate();
        }


        if (lng == null || lat == null) {
            output.addException("La geoloc " + lng + "/" + lat + " n'est pas correcte...");

            return output.validate();
        }
        Geolocalisation enrGeo = enrTag.getGeolocalisation() ;
        enrGeo.setLongitude(lng);
        enrGeo.setLat(lat);
        geoDAO.edit(enrGeo);

        output.add("newLngLat", lng + "/" + lat);

        return output.validate();
    }

    @Override
    public XmlBuilder treatMODVIS(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("modVis");
        String rq = null;

        if (vSession.isRootSession()) {
            output.addException("impossible to change visibility on the root session");
            return output.validate();
        }

        Integer tag = vSession.getTag();
        Boolean visible = vSession.getVisible();

        if (tag == null || tag == -1) {
            output.addException("Pas de tag selectionné ...");
            return output.validate();
        }
            TagTheme enrTagTheme = tagThemeDAO.loadByTagTheme(tag, vSession.getTheme().getId());

            if (enrTagTheme == null) {

                if (tagDAO.find(tag) == null) {
                    output.addException("Impossible de trouver ce tag (" + tag + ") ...");
                    return output.validate();
                }
                //le tag existe
                rq = "done";

                enrTagTheme = tagThemeDAO.newTagTheme();
                enrTagTheme.setTheme(vSession.getTheme());
                enrTagTheme.setTag(tagDAO.find(tag));

                tagThemeDAO.create(enrTagTheme);
            }
            if (visible != null && visible) {
                enrTagTheme.setIsVisible(true);
            } else {
                enrTagTheme.setIsVisible(false);
            }
            tagThemeDAO.edit(enrTagTheme);
            output.add("message", "Le tag " + tag + " est maintenant : " + (visible ? "visible" : "invisible"));

        return output.validate();
    }

    @Override
    public XmlBuilder treatNEWTAG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("newTag");
        String rq = null;
        String nom = vSession.getNom();
        Integer type = vSession.getType();

        if (type == null || type == -1) {
            output.addException("Pas de type selectionné ...");
            return output.validate();
        }

        try {
            if (nom != null && !nom.equals("")) {
                String msg = "";
                String liste = "";

                Tag enrTag = tagDAO.loadByName(nom);
                if (enrTag == null) {
                    if (0 > type || type > 3) {
                        output.addException("Type incorrect (" + type + ") ...");
                        return output.validate();
                    }
                    enrTag = tagDAO.newTag();

                    enrTag.setNom(nom);
                    enrTag.setTagType(type);
                    tagDAO.create(enrTag);
                    output.add("message", "TAG == " + enrTag.getId() + " ==");
                    if (type == 3) {
                            String longit = vSession.getLng() ;
                            String lat = vSession.getLat() ;
                            msg = " (" + longit + "/" + lat + ")";
                            if (longit == null || lat == null) {
                                output.cancel();
                                output.addException("La geoloc " + msg + " n'est pas correcte...");
                                tagDAO.remove(enrTag);
       
                                return output.validate();
                            }

                            Geolocalisation geo = geoDAO.newGeolocalisation();
                            geo.setTag(enrTag.getId());
                            geo.setLongitude(longit);
                            geo.setLat(lat);

                            geoDAO.create(geo);

               
                    }

                    switch (type) {
                        case 1:
                            liste = "Who";
                            break;
                        case 2:
                            liste = "What";
                            break;
                        case 3:
                            liste = "Where";
                            break;
                    }

                    output.add("message", "Tag '" + nom + msg + "' correctement ajouté à la liste " + liste);
                } else {
                    output.cancel();
                    output.addException("Le Tag " + nom + " est déjà présent dans la base ...");
                    output.addException(enrTag.getId() + " - " + enrTag.getNom());
                    
                    return output.validate();
                }
            } else {
                output.cancel();
                output.addException("Le nom du tag est vide ...");

                return output.validate();
            }
       

        } catch (NumberFormatException e) {
            e.printStackTrace();
            output.cancel();
            output.addException("NumberFormatException", "Erreur dans le cast de l'un des nombres");
            
        }
        return output.validate();
    }

    @Override
    public XmlBuilder treatDELTAG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("delTag");

        String rq = null;
        int tagID = vSession.getTag();

        try {
            Tag enrTag = tagDAO.find(tagID) ;
            //liens Tag->Photos
            List<TagPhoto> lstTP = enrTag.getTagPhotoList() ;
            int i = 0;
            for (TagPhoto enrTagPhoto : lstTP)  {
                tagPhotoDAO.remove(enrTagPhoto);
                i++;
            }
            output.add("message", "Suppression de " + i + " Tags Photo");

            //liens Tag->Localisation
            i = 0;
            Geolocalisation enrGeo = enrTag.getGeolocalisation() ;
            if (enrGeo != null) {
                geoDAO.remove(enrGeo);
                i = 1;
            }
            output.add("message", "Suppression de " + i + " Geolocalisation");

            //liens Tag->Theme
            List<TagTheme> lstTT = enrTag.getTagThemeList() ;

            i = 0;
            for(TagTheme enrTagTheme : lstTT) {
                tagThemeDAO.remove(enrTagTheme);
                i++;
            }
            output.add("message", "Suppression de " + i + " TagThemes");

            //tag
            i = 0;
            if (enrTag != null) {
                tagDAO.remove(enrTag);
                i++;
            }
            output.add("message", "Suppression de " + i + " Tag");

            return output.validate();
        } catch (NumberFormatException e) {
            output.addException("Aucun tag selectionné ...");
            return output.validate();

        } 
    }

    @Override
    public XmlBuilder displayCONFIG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("config");

        Action action = vSession.getAction();
        if (vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {

            output.add("map");
            if (action == Action.IMPORT) {
                output.add(treatIMPORT(vSession));
            }

            //ajout d'un nouveau tag
            if (Action.NEWTAG == action) {
                output.add(treatNEWTAG(vSession));
            }

            //Renommage d'un tag tag
            if (Action.MODTAG == action) {
                output.add(treatMODTAG(vSession));
            }

            //Changement de visibilité d'un tag
            if (Action.MODVIS == action) {
                output.add(treatMODVIS(vSession));
            }

            //modification d'une geolocalisation
            if (Action.MODGEO == action) {
                output.add(treatMODGEO(vSession));
            }

            //suppression d'un tag
            if (Action.DELTAG == action) {
                output.add(treatDELTAG(vSession));
            }
            output.add(webPageService.displayListLB(Mode.TAG_USED, vSession, null,
                    Box.MULTIPLE));
            output.add(webPageService.displayListLB(Mode.TAG_GEO, vSession, null,
                    Box.MULTIPLE));
            output.add(webPageService.displayListLB(Mode.TAG_NEVER, vSession, null,
                    Box.MULTIPLE));

        } else {
            output.addException("Vous n'avez pas crée ce theme ...");
        }

        return output.validate();
    }
}
