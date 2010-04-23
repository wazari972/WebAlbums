package net.wazari.util.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import javax.ejb.EJB;

import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.*;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.dao.exception.WebAlbumsDaoException;

import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;

import net.wazari.util.system.ImageResizer;
import net.wazari.util.StringUtil;
import net.wazari.util.XmlBuilder;

public class FilesFinder {

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

    private static final int DEFAULT_USER = 3;
    public static final SimpleDateFormat DATE_STANDARD = new SimpleDateFormat("yyyy-MM-dd");
    private String themeName;
    private int annee;
    private String dossier;
    private static ImageResizer resizer = null;

    public boolean importAuthor(ViewSession vSession,
            String themeName,
            String passwrd,
            XmlBuilder out, Configuration conf) {
        if (resizer == null) {
            new ImageResizer(conf, sysTools);
        }
        String rq = null;
        boolean correct = false;

        if (!resizer.isDone()) {
            warn(out, "The previous resize stack is not empty " +
                    "(" + resizer.getStackSize() + "), " +
                    "please wait a second ");
            return false;
        }
        try {
            info(out, "Importing for theme : " + themeName);

            Theme enrTheme = themeDAO.loadByName(themeName);

            //si l'auteur n'est pas encore dans la base de données,
            //on l'ajoute
            if (enrTheme == null) {
                if (String.CASE_INSENSITIVE_ORDER.compare("root", themeName) == 0) {
                    out.addException("root is a reserved keyword");
                    out.validate();
                    return false;
                }

                if (themeName.indexOf(' ') != -1) {
                    out.addException("pas d'espace dans le nom du theme");
                    out.validate();
                    return false;
                }

                info(out, "Le theme n'est pas dans la table");
                if (passwrd != null && !passwrd.equals("")) {
                    enrTheme = new Theme();
                    enrTheme.setNom(themeName);
                    enrTheme.setPassword(passwrd);

                    themeDAO.create(enrTheme);
                    info(out, "Le theme a correctement été ajouté");
                    correct = true;
                } else {
                    warn(out, "Nouveau theme sans mot de passe...");
                    enrTheme = null;
                }
            } //if theme already exists
            else {
                int enrThemeId = enrTheme.getId();
                int logThemeId = vSession.getThemeId();
                info(out, "Le theme est dans la table");
                info(out, "logged: " + enrThemeId + ", " + logThemeId);
                info(out, "logged: " + (enrThemeId == logThemeId));
                //pas besoin de mot de passe si on importe le theme courant
                if (enrThemeId != logThemeId) {
                    if (passwrd == null ||
                            !passwrd.equals(enrTheme.getPassword())) {
                        warn(out, "Le mot de passe est incorrect...");
                    } else {
                        info(out, "Mot de passe correct");
                        correct = true;
                    }
                } else {
                    info(out, "Pas besoin de mot de passe");
                    correct = true;
                }
            }

            //if init was performed correctly
            if (correct) {
                File dirTheme = null;

                dirTheme = new File(conf.getSourcePath() + conf.getFTP() + "/" + themeName + "/");
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
                    Thread th = new Thread(resizer);
                    th.setName("Resizer stack");
                    th.start();


                    this.themeName = themeName;
                    int myID = enrTheme.getId();
                    info(out, "ID du theme : " + myID + "");
                    File[] subfiles = dirTheme.listFiles();

                    warn(out, "Le dossier '" + themeName + "' contient " +
                            subfiles.length + " fichier" + (subfiles.length > 1 ? "s" : ""));

                    correct = true;
                    int err = 0;
                    for (int i = 0; i < subfiles.length; i++) {
                        if (subfiles[i].isDirectory()) {
                            info(out, "Important de l'album " + subfiles[i] + "");
                            if (!importAlbum(subfiles[i], myID, out)) {
                                warn(out, "An error occured during " +
                                        "importation of album (" + subfiles[i] + ")...");
                                correct = false;
                                err++;
                            }
                            subfiles[i].delete();
                        }
                    }

                    info(out, "## Import of theme " + themeName + " completed");
                    if (err != 0) {
                        warn(out, "## with " + err + " errors");
                    }
                }
            }

            if (!correct) {
                warn(out, "An error occured during initialization process ...");
            }

        } catch (WebAlbumsDaoException e) {
            e.printStackTrace();
            warn(out, "Erreur de requete ... " + rq);
            correct = false;

        }
        info(out, "Say to the Resizer that we are done");
        resizer.terminate();
        return correct;
    }

    private boolean importAlbum(File album, int authorID, XmlBuilder out)
            throws WebAlbumsDaoException {
        info(out, "##");
        info(out, "## Import of : " + album.getName());

        String rq = null;
        try {
            if (!album.exists() || !album.isDirectory()) {
                info(out, "## Le dossier Album '" + album.getName() + "' n'existe pas");

                return false;
            } else {
                String nom, strDate = null;

                try {
                    nom = StringUtil.escapeHTML(album.getName().substring(11));
                    info(out, "## NOM  : " + nom);

                    strDate = album.getName().substring(0, 10);
                    Date date = DATE_STANDARD.parse(strDate);
                    info(out, "## DATE : " + date);

                } catch (StringIndexOutOfBoundsException e) {
                    warn(out, "## Erreur dans le format du nom de l'album " +
                            "(" + album + "), on skip");
                    return false;
                } catch (ParseException e) {
                    warn(out, "## Erreur dans le format de la date " +
                            "(" + strDate + "), on skip");
                    return false;
                }

                //rechercher s'il est deja dans la liste

                Album enrAlbum = albumDAO.loadByNameDate(nom, strDate);
                Theme enrTheme = themeDAO.find(authorID);
                if (enrAlbum == null) {
                    //si il n'y est pas, on l'ajoute
                    info(out, "## L'album n'est pas dans la table");
                    enrAlbum = new Album();

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
                    enrAlbum.setTheme(enrTheme);
                    albumDAO.edit(enrAlbum);
                }
                int err = 0;

                //definition des attributs de l'album pour la prochaine procedure
                dossier = album.getName();
                annee = Integer.parseInt(dossier.substring(0, 4));
                info(out, "## Année : " + annee);

                File[] subfiles = album.listFiles();
                if (subfiles != null) {

                    info(out, "## Le répertoire '" + dossier +
                            "' contient " + subfiles.length +
                            " fichier" + (subfiles.length > 1 ? "s" : ""));

                    for (int i = 0; i < subfiles.length; i++) {
                        info(out, "## Traitement de " + subfiles[i].getName());
                        if (!importPhoto(subfiles[i], enrAlbum, out)) {
                            err++;
                        }
                    }
                    info(out, "## Import of : " + album.getName() + " completed");
                    if (err != 0) {
                        info(out, "## with " + err + " errors");
                    }

                } else {
                    warn(out, "Impossible de connaitre le nombre de fichiers ..." +
                            "(dossier ? " + album.isDirectory() + ")");
                }
                return true;
            }
        } catch (WebAlbumsDaoException e) {
            e.printStackTrace();
            warn(out, rq);

            return false;
        }
    }

    private boolean importPhoto(File photo,
            Album enrAlbum,
            XmlBuilder out)
            throws WebAlbumsDaoException {
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
        if (!sysTools.support(type, ext)) {
            warn(out, "### " + photo + " n'est pas supportée ... (" + type + ")");
            return true;
        }

        String path = annee + "/" + dossier + "/" + photo.getName();

        Photo enrPhoto = photoDAO.loadByPath(path);

        //si l'image (son path) n'est pas encore dans la base
        if (enrPhoto == null) {
            info(out, "### Creation d'un nouvel enregistrement");
            //on crée la nouvelle photo
            enrPhoto = new Photo();
            enrPhoto.setDescription("");
            enrPhoto.setPath(path);
            photoUtil.retreiveExif(enrPhoto, "file://" + photo.getAbsolutePath());
            enrPhoto.setAlbum(enrAlbum);
            enrPhoto.setType(type);

            photoDAO.create(enrPhoto);
        } else /* sinon on update son nom d'album*/ {
            info(out, "### Mise à jour de l'enregistrement");
            enrPhoto.setAlbum(enrAlbum);

            photoDAO.edit(enrPhoto);
        }

        ImageResizer.Element elt =
                new ImageResizer.Element(this.themeName + "/" + path, photo, type);
        resizer.push(elt);

        info(out, "### Import of : " + photo.getName() + " : completed");
        return true;

    }

    public boolean deleteAlbum(Album enrAlbum, XmlBuilder out, Configuration conf) {

        List<Photo> lstP = photoDAO.loadFromAlbum(null, enrAlbum.getId()) ;

        boolean correct = true;
        for (Photo enrPhoto : lstP) {
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

        String source = conf.getSourceURL();
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
            tagPhotoDAO.deleteByPhoto(enrPhoto.getId());

            //suppression des photos physiquement
            url = source + conf.getImages() + conf.getSep() + enrTheme.getNom() + conf.getSep() + enrPhoto.getPath();

            fichier = new File(new URL(StringUtil.escapeURL(url)).toURI());
            info(out, "On supprime sa photo :" + url);
            if (!fichier.delete()) {
                warn(out, "mais ça marche pas ...");
            }
            //pas de rep vide
            fichier.getParentFile().delete();

            //miniature
            url = source + conf.getMini() + conf.getSep() + enrTheme.getNom() + conf.getSep() + enrPhoto.getPath() + ".png";
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
            //WebPage.other.warn (msg.toString());
        }
    }

    private static void info(XmlBuilder output, Object msg) {
        output.add("message", msg.toString());
        //WebPage.other.info (msg.toString());
    }
}
