package net.wazari.service.entity.util;

import java.awt.Image;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.swing.ImageIcon;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Gpx;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.entity.Theme;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.photo.XmlPhotoExif;
import net.wazari.service.exchange.xml.photo.XmlPhotoExif.XmlPhotoExifEntry;
import net.wazari.util.system.SystemTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the object class that relates to the Photo table.
 * Any customizations belong here.
 */
@Stateless
public class PhotoUtil {

    private static final Logger log = LoggerFactory.getLogger(PhotoUtil.class.toString());
    private static final long serialVersionUID = 1L;
    @EJB SystemTools sysTools ;
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

        log.info( "add tags to photo {}", p);
        if (tags == null) {
            return;
        }

        List<TagPhoto> list = p.getTagPhotoList();
        //ajouter les nouveaux tags
        //qui ne sont pas encore dans la liste existante
        for (int i = 0; i < tags.length; i++) {
            log.info( "add tag {}", tags[i]);
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
                    log.info( "Ajout du tag : {}", enrTag.getNom());
                    tagPhotoDAO.create(nouveau);
                } else {
                    log.info( "already: {}", enrTag.getNom());
                }
            } else {
                log.warn( "Erreur dans l''id du Tag : {}: introuvable !", tags[i]);
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
        List<TagPhoto> toRemove = new LinkedList<TagPhoto>() ;
        for (TagPhoto enrTp : p.getTagPhotoList()) {
            //si la liste des nouveaux tags ne contient pas le tag courant
            if (Arrays.binarySearch(tags, enrTp.getTag().getId()) < 0) {
                //alors enlever ce tag des tags existants
                toRemove.add(enrTp);
            }
        }
        for (TagPhoto enrTp : toRemove) {
            tagPhotoDAO.remove(enrTp);
        }

    }

    public XmlPhotoExif getXmlExif(Photo p) {
        XmlPhotoExif output = new XmlPhotoExif();

        String[] exifs = new String[]{
            p.getModel(), p.getDate(), p.getIso(), p.getExposure(),
            p.getFocal(), p.getHeight(), p.getWidth(), p.getFlash()};

        for (int i = 0; i < exifs.length; i++) {
            if (exifs[i] == null) {
                continue;
            }
            String[] values = exifs[i].split(" - ");
            output.entry.add(new XmlPhotoExifEntry(values[0], values[1])) ;
        }
        return output ;
    }

    public boolean rotate(ViewSession vSession, Photo p, String degrees)
            throws WebAlbumsServiceException {

        if (p.getType() != null && !p.getType().contains("image")) {
            return true;
        }

        String sep = vSession.getConfiguration().getSep() ;

        String path = p.getPath(true);
        String mini = vSession.getConfiguration().getMiniPath(true) + sep + path;
        String image = vSession.getConfiguration().getImagesPath(true) + sep + path;
        log.info( "Rotation de {}degres de {}", new Object[]{degrees, path});
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
    public String getThemedPath(Gpx g) {
        Theme enrTheme = g.getAlbum().getTheme();
        if (enrTheme == null) {
            return null;
        }
        return enrTheme.getNom() + "/" + g.getGpxPath();
    }

    public String getGpxPath(ViewSession vSession, Gpx g) {
        String sep = vSession.getConfiguration().getSep() ;
        return vSession.getConfiguration().getImagesPath(true) + getThemedPath(g);
    }
    
    public String getImagePath(ViewSession vSession, Photo p) {
        String sep = vSession.getConfiguration().getSep() ;
        return vSession.getConfiguration().getImagesPath(true) + p.getPath(true);
    }

    public String getMiniPath(ViewSession vSession, Photo p) {
        String sep = vSession.getConfiguration().getSep() ;
        return  vSession.getConfiguration().getMiniPath(true) +  p.getPath(true) + ".png";
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
