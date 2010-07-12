package net.wazari.util.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.common.plugins.Importer.Capability;

import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.*;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.service.entity.util.PhotoUtil;

import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;

import net.wazari.common.util.StringUtil;
import net.wazari.common.util.XmlBuilder;
import net.wazari.util.system.ImageResizer.Element;

@Stateless
public class FilesFinder {

    private static final String SEP = File.separator;
    private static final int DEFAULT_USER = 3;
    
    private static final Logger log = Logger.getLogger(FilesFinder.class.getCanonicalName());
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
    private PhotoUtil photoUtil;
    @EJB
    private SystemTools sysTools;
    @EJB
    private ImageResizer resizer;

    public boolean importAuthor(ViewSession vSession,
            String themeName,
            XmlBuilder out, Configuration conf) {
        if (String.CASE_INSENSITIVE_ORDER.compare("root", themeName) == 0) {
            info(out, "root is a reserved keyword");
            out.addException("root is a reserved keyword");
            out.validate();
            return false;
        }

        boolean correct = false;
        Stack<Element> stack = new Stack<Element>();

        info(out, "Importing for theme : " + themeName);

        Theme enrTheme = themeDAO.loadByName(themeName);
        File dirTheme = null;

        //si l'auteur n'est pas encore dans la base de données,
        //on l'ajoute
        if (enrTheme == null) {
            if (themeName.contains(" ")) {
                out.addException("pas d'espace dans le nom du theme");
                out.validate();
                return false;
            }

            info(out, "Le theme n'est pas dans la table");
            enrTheme = themeDAO.newTheme(themeName);
            enrTheme = themeDAO.loadByName(enrTheme.getNom()) ;
            info(out, "Le theme a correctement été ajouté");
            correct = true;

        } //if theme already exists
        else {
            info(out, "Le theme est dans la table");

            correct = true;
        }

        //if init was performed correctly
        if (correct) {
            

            dirTheme = new File(conf.getFtpPath() + themeName + SEP);
            info(out, "Dossier source : " + dirTheme);
            //creer le dossier d'import s'il n'existe pas encore
            if (!dirTheme.isDirectory()) {
                info(out, "Creation du dossier d'import (" + dirTheme + ")");
                dirTheme.mkdirs();
            }

            if (!dirTheme.isDirectory()) {
                warn(out, dirTheme.getAbsolutePath() + " n'est pas un dossier/impossible de le creer  ... ");
                correct = false;
            } else {
                info(out, "ID du theme : " + enrTheme + "");
                File[] subfiles = dirTheme.listFiles();

                warn(out, "Le dossier '" + themeName + "' contient "
                        + subfiles.length + " fichier" + (subfiles.length > 1 ? "s" : ""));

                correct = true;
                int err = 0;
                for (int i = 0; i < subfiles.length; i++) {
                    if (subfiles[i].isDirectory()) {
                        info(out, "Important de l'album " + subfiles[i] + "");
                        if (!importAlbum(stack, subfiles[i], enrTheme, out)) {
                            warn(out, "An error occured during "
                                    + "importation of album (" + subfiles[i] + ")...");
                            correct = false;
                            err++;
                        }
                        subfiles[i].deleteOnExit();
                    }
                }

                info(out, "## Import of theme " + themeName + " completed");
                if (err != 0) {
                    warn(out, "## with " + err + " errors");
                }
            }
        }

        if (dirTheme != null) resizer.resize(conf, stack, dirTheme);

        if (!correct) {
            warn(out, "An error occured during initialization process ...");
        }


        return correct;
    }

    private boolean importAlbum(Stack<Element> stack, File album, Theme enrTheme, XmlBuilder out) {
        info(out, "##");
        info(out, "## Import of : " + album.getName());
        int annee;
        String dossier;

        if (!album.exists() || !album.isDirectory()) {
            info(out, "## Le dossier Album '" + album.getName() + "' n'existe pas");
            return false;
        } else {
            String strDate = null;
            String dirName = album.getName();
            Album enrAlbum;

            if (dirName != null && dirName.length() > 11) {
                String nom = StringUtil.escapeHTML(dirName.substring(11));
                info(out, "## NOM  : " + nom);
                try {
                    strDate = album.getName().substring(0, 10);
                    Date date = Album.DATE_STANDARD.parse(strDate);
                    info(out, "## DATE : " + date);

                } catch (ParseException e) {
                    warn(out, "## Erreur dans le format de la date "
                            + "(" + strDate + "), on skip");
                    return false;
                }
                enrAlbum = albumDAO.loadByNameDate(nom, strDate);
                if (enrAlbum == null) {
                    //si il n'y est pas, on l'ajoute
                    info(out, "## L'album n'est pas dans la table");
                    enrAlbum = albumDAO.newAlbum();

                    enrAlbum.setNom(nom);
                    enrAlbum.setDescription("");
                    enrAlbum.setTheme(enrTheme);
                    enrAlbum.setDate(strDate);
                    enrAlbum.setDroit(userDAO.find(DEFAULT_USER));

                    info(out, "## On tente d'ajouter l'album dans la base");
                    albumDAO.create(enrAlbum);
                    info(out, "## On vient de lui donner l'ID " + enrAlbum.getId());

                } else {
                    info(out, "## L'album est dans la table : ID " + enrAlbum.getId());
                }
            } else {
                try {
                    int albumId = Integer.parseInt(dirName);
                    enrAlbum = albumDAO.find(albumId);
                    if (enrAlbum == null) {
                        info(out, "## Can't find an album with id=" + albumId);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    warn(out, "## Format of the album folder (" + dirName + ") wrong; "
                            + "expected YYYY-MM-DD Title");
                    return false;
                }
            }

            if (enrAlbum.getTheme().getId() != enrTheme.getId()) {
                warn(out, "## L'album est dans la table (" + enrAlbum.getId() + "),"
                        + " mais le theme n'est pas bon: " + enrAlbum.getTheme());
                return false;
            }
            //rechercher s'il est deja dans la liste


            int err = 0;

            //definition des attributs de l'album pour la prochaine procedure
            dossier = enrAlbum.getDate() + " " + enrAlbum.getNom();
            annee = Integer.parseInt(dossier.substring(0, 4));
            info(out, "## Année : " + annee);

            String albumPath = annee + SEP + dossier + SEP ;
            File[] subfiles = album.listFiles();
            if (subfiles != null) {

                info(out, "## Le répertoire '" + dossier
                        + "' contient " + subfiles.length
                        + " fichier" + (subfiles.length > 1 ? "s" : ""));

                for (int i = 0; i < subfiles.length; i++) {
                    info(out, "## Traitement de " + subfiles[i].getName());
                    if (!importPhoto(stack, albumPath, subfiles[i], enrAlbum, out)) {
                        err++;
                    }
                }
                info(out, "## Import of : " + album.getName() + " completed");
                if (err != 0) {
                    info(out, "## with " + err + " errors");
                }

            } else {
                warn(out, "Impossible de connaitre le nombre de fichiers ..."
                        + "(dossier ? " + album.isDirectory() + ")");
            }
            return true;
        }
    }

    private boolean importPhoto(Stack<Element> stack, String albumPath,
            File photo,
            Album enrAlbum,
            XmlBuilder out) {
        info(out, "### Import of : " + photo.getName() + "");

        if ("Thumbs.db".equals(photo.getName())) {
            info(out, "### Supression de " + photo);
            photo.delete();
            return true;
        }
        //verification du type du fichier
        String type = null;
        try {
            URL url = photo.toURI().toURL();
            URLConnection connection = url.openConnection();
            type = connection.getContentType();

            info(out, "### Type : " + type);

        } catch (MalformedURLException e) {
            warn(out, "### URL mal formée ..." + e);
            return false;
        } catch (IOException e) {
            warn(out, "### Erreur d'IO ..." + e);
            return false;
        }

        String ext = null;
        int idx = photo.getName().lastIndexOf('.');
        if (idx != -1) {
            ext = photo.getName().substring(idx + 1);
        }
        if (!sysTools.supports(type, ext, Capability.THUMBNAIL)) {
            warn(out, "### " + photo + " n'est pas supportée ... (" + type + ")");
            return false;
        }

        String photoPath = albumPath + photo.getName();

        Photo enrPhoto = photoDAO.loadByPath(photoPath);

        //si l'image (son path) n'est pas encore dans la base
        if (enrPhoto == null) {
            info(out, "### Creation d'un nouvel enregistrement");
            //on crée la nouvelle photo
            enrPhoto = photoDAO.newPhoto();
            enrPhoto.setDescription("");
            enrPhoto.setPath(photoPath);

            sysTools.retreiveMetadata(type, null, enrPhoto, photo.getAbsolutePath());
            enrPhoto.setAlbum(enrAlbum);
            enrPhoto.setType(type);
            info(out, "### Album " + enrPhoto.getAlbum());
            photoDAO.create(enrPhoto);
        } else /* sinon on update son nom d'album*/ {
            info(out, "### Mise à jour de l'enregistrement");
            enrPhoto.setAlbum(enrAlbum);

            photoDAO.edit(enrPhoto);
        }

        ImageResizer.Element elt = new ImageResizer.Element(enrAlbum.getTheme().getNom()+SEP+photoPath, photo, type);
        stack.push(elt);

        info(out, "### Import of : " + photo.getName() + " : completed");
        return true;

    }

    public boolean deleteAlbum(Album enrAlbum, XmlBuilder out, Configuration conf) {

        SubsetOf<Photo> lstP = photoDAO.loadFromAlbum(null, enrAlbum.getId(), null);

        boolean correct = true;
        for (Photo enrPhoto : lstP.subset) {
            if (!deletePhoto(enrPhoto, out, conf)) {
                warn(out, "Problem during the deletion ...");
                correct = false;
            }
        }
        if (correct) {
            albumDAO.remove(enrAlbum);
            return true;
        }

        return false;
    }

    public boolean deletePhoto(Photo enrPhoto,
            XmlBuilder out, Configuration conf) {

        String url = null;
        try {
            File fichier;

            Theme enrTheme = enrPhoto.getAlbum().getTheme();
            if (enrTheme == null) {
                warn(out, "theme impossible à trouver ... (photo=" + enrPhoto.getId() + ")");
                return false;
            }
            info(out, "Traitement de la photo " + enrPhoto.getId());
            //suppression des tags de cette photo
            tagPhotoDAO.deleteByPhoto(enrPhoto);

            //suppression des photos physiquement
            url = "file://" + conf.getImagesPath() + SEP + enrTheme.getNom() + SEP + enrPhoto.getPath();

            fichier = new File(new URL(StringUtil.escapeURL(url)).toURI());
            info(out, "On supprime sa photo :" + url);
            if (!fichier.delete()) {
                warn(out, "mais ça marche pas ...");
            }
            //pas de rep vide
            fichier.getParentFile().delete();

            //miniature
            url = "file://" + conf.getMiniPath() + SEP + enrTheme.getNom() + SEP + enrPhoto.getPath() + ".png";
            fichier = new File(new URL(StringUtil.escapeURL(url)).toURI());
            info(out, "On supprime sa miniature :" + url);
            if (!fichier.delete()) {
                warn(out, "mais ça marche pas ...");
            }
            //pas de rep vide
            fichier.getParentFile().delete();
            //suppression du champs dans la table Photo
            photoDAO.remove(enrPhoto);

            info(out, "Photo correctement supprimée !");
            out.validate();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            warn(out, "MalformedURLException " + url);
            warn(out, e.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            warn(out, "URISyntaxException " + url);
            warn(out, e.toString());
        }
        return false;
    }

    private static void warn(XmlBuilder output, Object msg) {
        if (msg != null) {
            output.add("Exception", msg.toString());
        }
        log.log(Level.WARNING, "FilesFinder: {0}", msg);
    }

    private static void info(XmlBuilder output, Object msg) {
        if (msg != null) {
            output.add("message", msg.toString());
        }
        log.log(Level.INFO, "FilesFinder: {0}", msg);
    }
}
