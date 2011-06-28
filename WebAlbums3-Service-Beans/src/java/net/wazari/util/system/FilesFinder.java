package net.wazari.util.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.common.plugins.Importer.Capability;

import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.*;

import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;

import net.wazari.common.util.StringUtil;
import net.wazari.util.system.ImageResizer.Element;

@Stateless
public class FilesFinder {

    private static final String SEP = File.separator;
    private static final int DEFAULT_USER = 3;
    
    private static final Logger log = LoggerFactory.getLogger(FilesFinder.class.getCanonicalName());
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private AlbumFacadeLocal albumDAO;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private TagPhotoFacadeLocal tagPhotoDAO;
    @EJB
    private SystemTools sysTools;
    @EJB
    private ImageResizer resizer;

    public boolean importAuthor(ViewSession vSession,
            String themeName, Configuration conf) {
        if (String.CASE_INSENSITIVE_ORDER.compare("root", themeName) == 0) {
            log.info("root is a reserved keyword");
            return false;
        }

        boolean correct = false;
        Stack<Element> stack = new Stack<Element>();

        log.info("Importing for theme : {}", themeName);

        Theme enrTheme = themeDAO.loadByName(themeName);
        File dirTheme = null;

        //si l'auteur n'est pas encore dans la base de données,
        //on l'ajoute
        if (enrTheme == null) {
            if (themeName.contains(" ")) {
                log.warn("pas d'espace dans le nom du theme");
                return false;
            }

            log.info("Le theme n'est pas dans la table");
            enrTheme = themeDAO.newTheme(themeName);
            enrTheme = themeDAO.loadByName(enrTheme.getNom()) ;
            log.info("Le theme a correctement été ajouté");
            correct = true;

        } //if theme already exists
        else {
            log.info("Le theme est dans la table");

            correct = true;
        }

        //if init was performed correctly
        if (correct) {
            dirTheme = new File(conf.getFtpPath() + themeName + SEP);
            log.info("Dossier source : " + dirTheme);
            //creer le dossier d'import s'il n'existe pas encore
            if (!dirTheme.isDirectory()) {
                log.info("Creation du dossier d'import (" + dirTheme + ")");
                dirTheme.mkdirs();
            }

            if (!dirTheme.isDirectory()) {
                log.warn(dirTheme.getAbsolutePath() + " n'est pas un dossier/impossible de le creer  ... ");
                correct = false;
            } else {
                log.info("ID du theme : " + enrTheme + "");
                File[] subfiles = dirTheme.listFiles();

                log.warn("Le dossier '" + themeName + "' contient "
                        + subfiles.length + " fichier" + (subfiles.length > 1 ? "s" : ""));

                correct = true;
                int err = 0;
                for (int i = 0; i < subfiles.length; i++) {
                    if (subfiles[i].isDirectory()) {
                        log.info("Important de l'album " + subfiles[i] + "");
                        if (!importAlbum(stack, subfiles[i], enrTheme)) {
                            log.warn("An error occured during importation of album (" + subfiles[i] + ")...");
                            correct = false;
                            err++;
                        }
                        subfiles[i].deleteOnExit();
                    }
                }

                log.info( "## Import of theme " + themeName + " completed");
                if (err != 0) {
                    log.warn("## with " + err + " errors");
                }
            }
        }

        if (dirTheme != null) resizer.resize(conf, stack, dirTheme);

        if (!correct) {
            log.warn("An error occured during initialization process ...");
        }


        return correct;
    }
    private static String sansAccents(String source) {
            return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }
    private boolean importAlbum(Stack<Element> stack, File album, Theme enrTheme) {
        log.info("##");
        log.info("## Import of : " + album.getName());
        int annee;
        String dossier;

        if (!album.exists() || !album.isDirectory()) {
            log.info("## Le dossier Album '" + album.getName() + "' n'existe pas");
            return false;
        } else {
            String strDate = null;
            String dirName = album.getName();
            Album enrAlbum;
            // split album name into YYYY-MM-DD NAME
            if (dirName != null && dirName.length() > 11) {
                String nom = dirName.substring(11);
                nom = sansAccents(nom) ;
                log.info("## NOM  : " + nom);
                try {
                    strDate = album.getName().substring(0, 10);
                    Date date = Album.DATE_STANDARD.parse(strDate);
                    log.info("## DATE : " + date);

                } catch (ParseException e) {
                    log.warn("## Erreur dans le format de la date "
                            + "(" + strDate + "), on skip");
                    return false;
                }
                enrAlbum = albumDAO.loadByNameDate(nom, strDate);
                if (enrAlbum == null) {
                    //si il n'y est pas, on l'ajoute
                    log.info("## L'album n'est pas dans la table");
                    enrAlbum = albumDAO.newAlbum();

                    enrAlbum.setNom(nom);
                    enrAlbum.setDescription("");
                    enrAlbum.setTheme(enrTheme);
                    enrAlbum.setDate(strDate);
                    enrAlbum.setDroit(userDAO.find(DEFAULT_USER));

                    log.info("## On tente d'ajouter l'album dans la base");
                    albumDAO.create(enrAlbum);
                    log.info("## On vient de lui donner l'ID " + enrAlbum.getId());

                } else {
                    log.info("## L'album est dans la table : ID " + enrAlbum.getId());
                }
            } else {
                try {
                    int albumId = Integer.parseInt(dirName);
                    enrAlbum = albumDAO.find(albumId);
                    if (enrAlbum == null) {
                        log.info("## Can't find an album with id=" + albumId);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    log.warn("## Format of the album folder (" + dirName + ") wrong; "
                            + "expected YYYY-MM-DD Title");
                    return false;
                }
            }

            if (enrAlbum.getTheme().getId() != enrTheme.getId()) {
                log.warn("## L'album est dans la table (" + enrAlbum.getId() + "),"
                        + " mais le theme n'est pas bon: " + enrAlbum.getTheme());
                return false;
            }
            //rechercher s'il est deja dans la liste
            int err = 0;

            //definition des attributs de l'album pour la prochaine procedure
            dossier = enrAlbum.getDate() + " " + enrAlbum.getNom();
            annee = Integer.parseInt(dossier.substring(0, 4));
            log.info("## Année : " + annee);

            String albumPath = annee + SEP + dossier + SEP ;
            File[] subfiles = album.listFiles();
            if (subfiles != null) {

                log.info("## Le répertoire '" + dossier
                        + "' contient " + subfiles.length
                        + " fichier" + (subfiles.length > 1 ? "s" : ""));

                for (int i = 0; i < subfiles.length; i++) {
                    log.info("## Traitement de " + subfiles[i].getName());
                    if (!importPhoto(stack, albumPath, subfiles[i], enrAlbum)) {
                        err++;
                    }
                }
                log.info("## Import of : " + album.getName() + " completed");
                if (err != 0) {
                    log.info("## with " + err + " errors");
                }

            } else {
                log.warn("Impossible de connaitre le nombre de fichiers ..."
                        + "(dossier ? " + album.isDirectory() + ")");
            }
            return true;
        }
    }

    private boolean importPhoto(Stack<Element> stack, String albumPath,
            File photo,
            Album enrAlbum) {
        log.info("### Import of : " + photo.getName() + "");

        if ("Thumbs.db".equals(photo.getName())) {
            log.info("### Supression de " + photo);
            photo.delete();
            return true;
        }
        //verification du type du fichier
        String type = null;
        try {
            URL url = photo.toURI().toURL();
            URLConnection connection = url.openConnection();
            type = connection.getContentType();

            log.info("### Type : " + type);
        } catch (MalformedURLException e) {
            log.warn("### URL mal formée ..." + e);
            return false;
        } catch (IOException e) {
            log.warn("### Erreur d'IO ..." + e);
            return false;
        }

        String ext = null;
        int idx = photo.getName().lastIndexOf('.');
        if (idx != -1) {
            ext = photo.getName().substring(idx + 1);
        }
        if (!sysTools.supports(type, ext, Capability.THUMBNAIL)) {
            log.warn("### " + photo + " n'est pas supportée ... (" + type + ")");
            return false;
        }

        String photoPath = albumPath + sansAccents(photo.getName());

        Photo enrPhoto = photoDAO.loadByPath(photoPath);

        //si l'image (son path) n'est pas encore dans la base
        if (enrPhoto == null) {
            log.info("### Creation d'un nouvel enregistrement");
            //on crée la nouvelle photo
            enrPhoto = photoDAO.newPhoto();
            enrPhoto.setDescription("");
            enrPhoto.setPath(photoPath);

            sysTools.retrieveMetadata(type, null, enrPhoto, photo.getAbsolutePath());
            enrPhoto.setAlbum(enrAlbum);
            enrPhoto.setType(type);
            log.info("### Album " + enrPhoto.getAlbum());
            photoDAO.create(enrPhoto);
        } else /* sinon on update son nom d'album*/ {
            log.info("### Mise à jour de l'enregistrement");
            enrPhoto.setAlbum(enrAlbum);

            photoDAO.edit(enrPhoto);
        }

        ImageResizer.Element elt = new ImageResizer.Element(enrAlbum.getTheme().getNom()+SEP+photoPath, photo, type);
        stack.push(elt);

        log.info("### Import of : " + photo.getName() + " : completed");
        return true;

    }

    public boolean deleteAlbum(Album enrAlbum, Configuration conf) {
        boolean correct = true;
        for (Iterator<Photo> iter = enrAlbum.getPhotoList().iterator(); iter.hasNext();) {
            Photo enrPhoto = iter.next() ;
             iter.remove();
            if (!deletePhoto(enrPhoto, conf)) {
                log.warn("Problem during the deletion ...");
                correct = false;
            }
        }
        if (correct) {
            albumDAO.remove(enrAlbum);
            return true;
        }

        return false;
    }

    public boolean deletePhoto(Photo enrPhoto, Configuration conf) {

        String url = null;
        try {
            File fichier;

            Theme enrTheme = enrPhoto.getAlbum().getTheme();
            if (enrTheme == null) {
                log.warn("Theme impossible à trouver ... (photo={})", enrPhoto.getId());
                return false;
            }
            log.info("Traitement de la photo {}", enrPhoto.getId());
            //suppression des tags de cette photo
            tagPhotoDAO.deleteByPhoto(enrPhoto);

            //suppression des photos physiquement
            url = "file://" + conf.getImagesPath() + SEP + enrTheme.getNom() + SEP + enrPhoto.getPath();

            fichier = new File(new URL(StringUtil.escapeURL(url)).toURI());
            log.info("On supprime sa photo : {}");
            if (!fichier.delete()) {
                log.warn("Mais ça marche pas ...");
            }
            //pas de rep vide
            fichier.getParentFile().delete();

            //miniature
            url = "file://" + conf.getMiniPath() + SEP + enrTheme.getNom() + SEP + enrPhoto.getPath() + ".png";
            fichier = new File(new URL(StringUtil.escapeURL(url)).toURI());
            log.info("On supprime sa miniature : {}");
            if (!fichier.delete()) {
                log.warn("mais ça marche pas ...");
            }
            //pas de rep vide
            fichier.getParentFile().delete();
            //suppression du champs dans la table Photo
            photoDAO.remove(enrPhoto);

            log.info("Photo correctement supprimée !");
            return true;
        } catch (MalformedURLException e) {
            log.warn("MalformedURLException {}", url, e);
        } catch (URISyntaxException e) {
            log.warn("URISyntaxException {}", url);
        }
        return false;
    }

    public boolean deleteCarnet(Carnet enrCarnet, Configuration configuration) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
