package net.wazari.service.entity.util;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import net.wazari.dao.entity.Theme;
import javax.swing.ImageIcon;
import java.awt.Image;

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.exception.WebAlbumsDaoException;
import net.wazari.service.SystemToolsLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.StringUtil;
import net.wazari.util.XmlBuilder;

/**
 * This is the object class that relates to the Photo table.
 * Any customizations belong here.
 */
@Stateless
public class PhotoUtil {

    private static final Logger log = Logger.getLogger(PhotoUtil.class.toString());
    private static final long serialVersionUID = 1L;
    @EJB SystemToolsLocal sysTools ;
    @EJB
    TagPhotoFacadeLocal tagPhotoDAO;
    @EJB
    TagFacadeLocal tagDAO;
    @EJB
    ThemeFacadeLocal themeDAO;
    @EJB
    UtilisateurFacadeLocal userDAO;

    public void setTags(Photo p, Integer[] tags)
            throws WebAlbumsServiceException {
        removeExtraTags(p, tags);
        addTags(p, tags);
    }

    public void addTags(Photo p, Integer[] tags)
            throws WebAlbumsServiceException {

        log.info("photo " + p.getId());
        if (tags == null) {
            return;
        }

        List<TagPhoto> list = p.getTagPhotoList();
        //ajouter les nouveaux tags
        //qui ne sont pas encore dans la liste existante
        for (int i = 0; i < tags.length; i++) {
            log.info("add tag " + tags[i]);
            boolean already = false;

            //verifier que le tag est bien dans la base
            net.wazari.dao.entity.Tag enrTag = tagDAO.find(tags[i]);
            if (enrTag != null) {
                //regarder si le tag est déjà dans la liste
                for (TagPhoto enrTp : list) {
                    //si le tag est déjà dans la liste
                    if (enrTag.equals(enrTp.getTag())) {
                        already = true;
                        break ;
                    }
                }

                if (!already) {
                    //alors on l'ajoute
                    TagPhoto nouveau = tagPhotoDAO.newTagPhoto();
                    nouveau.setPhoto(p);
                    nouveau.setTag(enrTag);
                    log.info("Ajout du tag : " + enrTag.getNom());
                    tagPhotoDAO.create(nouveau);
  
                } else {
                    log.info("already: " +enrTag.getNom());
                }
            } else {
                log.warning("Erreur dans l'id du Tag : " + tags[i] + ": introuvable !");
            }
        }
    }

    public void removeTag(Photo p, int tag) throws WebAlbumsServiceException {
        TagPhoto toRemove = null ;
        for (TagPhoto enrTp : p.getTagPhotoList()) {
            if (enrTp.getTag().getId() == tag) {
                toRemove = enrTp ;
                break ;
            }
        }
        if (toRemove != null) {
            tagPhotoDAO.remove(toRemove);
        }

    }

    public void removeExtraTags(Photo p, Integer[] tags) throws WebAlbumsServiceException {
        if (tags == null) {
            for (TagPhoto enrTp : p.getTagPhotoList()) {
                tagPhotoDAO.remove(enrTp);
            }
            return;
        }

        Arrays.sort(tags);

        //enlever les tags existants qui ne sont pas dans la nouvelle liste
        for (TagPhoto enrTp : p.getTagPhotoList()) {
            //si la liste des nouveaux tags ne contient pas le tag courant
            if (Arrays.binarySearch(tags, enrTp.getTag().getId()) < 0) {
                //alors enlever ce tag des tags existants
                tagPhotoDAO.remove(enrTp);
            }
        }

    }

    public XmlBuilder getXmlExif(Photo p) {
        XmlBuilder output = new XmlBuilder("exif");

        String[] exifs = new String[]{
            p.getModel(), p.getDate(), p.getIso(), p.getExposure(),
            p.getFocal(), p.getHeight(), p.getWidth(), p.getFlash()};

        for (int i = 0; i < exifs.length; i++) {
            if (exifs[i] == null) {
                continue;
            }
            String[] values = exifs[i].split(" - ");
            XmlBuilder data = new XmlBuilder("data", values[1]);
            data.addAttribut("name", values[0]);
            output.add(data);
        }
        return output.validate();
    }

    public void retreiveExif(ViewSession vSession, Photo p) {
        String path = vSession.getConfiguration().getSourceURL() + "/" + vSession.getConfiguration().getImages() + "/" + p.getPath();
        retreiveExif(p, path);
    }

    public void retreiveExif(Photo p, String path) {
        try {
            File photo = new File(new URI(StringUtil.escapeURL(path)));
            ExifReader ex = new ExifReader(photo);
            Iterator it = ex.extract().getDirectoryIterator();
            while (it.hasNext()) {
                Directory dir = (Directory) it.next();
                Iterator it2 = dir.getTagIterator();
                while (it2.hasNext()) {
                    Tag t = (Tag) it2.next();
                    boolean model = false,
                            date = false,
                            iso = false,
                            expo = false,
                            focal = false,
                            height = false,
                            width = false,
                            flash = false;

                    if (!model && t.getTagName().equals("Model")) {
                        p.setModel(escapeBracket(t.toString()));

                        model = true;
                    } else if (!date && t.getTagName().equals("Date/Time")) {
                        p.setDate(escapeBracket(t.toString()));
                        date = true;

                    } else if (!iso && t.getTagName().equals("ISO Speed Ratings")) {
                        p.setIso(escapeBracket(t.toString()));
                        iso = true;

                    } else if (!expo && t.getTagName().equals("Exposure Time")) {
                        p.setExposure(escapeBracket(t.toString()));
                        expo = true;

                    } else if (!focal && t.getTagName().equals("Focal Length")) {
                        p.setFocal(escapeBracket(t.toString()));
                        focal = true;

                    } else if (!height && t.getTagName().equals("Exif Image Height")) {
                        p.setHeight(escapeBracket(t.toString()));
                        height = true;

                    } else if (!width && t.getTagName().equals("Exif Image Width")) {
                        p.setWidth(escapeBracket(t.toString()));
                        width = true;

                    } else if (!flash && t.getTagName().equals("Flash")) {
                        p.setFlash(escapeBracket(t.toString()));
                        flash = true;
                    }
                }
            }
        } catch (JpegProcessingException e) {
            log.warning("Exception JPEG durant le traitement exif : " + e);
            log.warning(path);
        } catch (URISyntaxException e) {
            log.warning("URISyntaxException durant le traitement exif : " + e);
            log.warning(path);
        }
    }

    private String escapeBracket(String str) {
        int pos = str.indexOf("]");
        return str.substring(pos + 2);
    }

    public boolean rotate(ViewSession vSession, Photo p, String degrees)
            throws WebAlbumsServiceException {

        if (p.getType() != null && !p.getType().contains("image")) {
            return true;
        }

        String themeName = null;
        try {
            Theme enrTh = p.getAlbum().getTheme();

            if (enrTh == null) {
                throw new WebAlbumsDaoException(WebAlbumsDaoException.JDBCException,
                        "Erreur dans Photo.rotate(), " +
                        "Impossible to find the photo's theme " +
                        "(" + p.getId() + ")");
            } else {
                themeName = enrTh.getNom();
            }
        } catch (WebAlbumsDaoException e) {
            e.printStackTrace();
            throw new WebAlbumsServiceException(WebAlbumsDaoException.JDBCException,
                    "Erreur dans Photo.rotate()");
        }

        String path = p.getPath();
        String mini = vSession.getConfiguration().getSourcePath() + vSession.getConfiguration().getMini() + "/" + themeName + "/" + path;
        String image = vSession.getConfiguration().getSourcePath() + vSession.getConfiguration().getImages() + "/" + themeName + "/" + path;
        log.info("Rotation de " + degrees + " de " + path);
        if (sysTools.rotate(null, null, degrees, mini + ".png", mini + ".png")) {
            if (!sysTools.rotate(null, null, degrees, image, image)) {
                sysTools.rotate(null, null, "-" + degrees, mini + ".png", mini + ".png");
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /***/
    public String getThemedPath(Photo p) {
        Theme enrTheme = p.getAlbum().getTheme();
        if (enrTheme == null) {
            return null;
        }
        return enrTheme.getNom() + "/" + p.getPath();
    }

    public String getImagePath(ViewSession vSession, Photo p) {
        return vSession.getConfiguration().getSourcePath() + vSession.getConfiguration().getImages() + "/" + getThemedPath(p);
    }

    public String getMiniPath(ViewSession vSession, Photo p) {
        return vSession.getConfiguration().getSourcePath() + vSession.getConfiguration().getMini() + "/" + getThemedPath(p) + ".png";
    }

    public String getExtention(ViewSession vSession, Photo p) {
        int idx = getImagePath(vSession, p).lastIndexOf('.');
        if (idx == -1) {
            return null;
        } else {
            return getImagePath(vSession, p).substring(idx + 1);
        }
    }

    /***/
    public int getWidth(ViewSession vSession, Photo p, boolean large) {
        return getImage(vSession, p, large).getWidth(null);
    }

    public int getHeight(ViewSession vSession, Photo p, boolean large) {
        return getImage(vSession, p, large).getHeight(null);
    }

    private Image getImage(ViewSession vSession, Photo p, boolean large) {
        String path;
        if (large) {
            path = getImagePath(vSession, p);
        } else {
            path = getMiniPath(vSession, p);
        }

        return new ImageIcon(path).getImage();
    }
}
