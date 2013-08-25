package net.wazari.util.system;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.Importer.Capability;
import net.wazari.common.plugins.Importer.Metadata;
import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.ProcessCallbackImpl;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.service.PluginManagerLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.ViewSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SystemTools {

    private static final Logger log = LoggerFactory.getLogger(SystemTools.class.getCanonicalName());

    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private PhotoUtil photoUtil;
    @EJB
    private PluginManagerLocal plugins ;

    static final ProcessCallback cb = ProcessCallbackImpl.getProcessCallBack();

    public static boolean initate() {
        return true;
    }

    @PostConstruct
    public void init() {
         plugins.reloadPlugins(null);
    }

    private Importer getWrapper(String type, String ext, Importer.Capability cap) {
        Importer wrap = null;
        String typeSafe = type == null ? "image" : type ;
        for (Importer util : plugins.getWorkingPlugins()) {
            log.warn( "Test {}: {}", new Object[]{util.getName(), Arrays.asList(util.supports())});
            if (util.supports(typeSafe, ext, cap)) {
                wrap = util;
                break ;
            }
        }
        log.warn( "Wrapper for {}@{}-{}: {}", new Object[]{typeSafe, ext, wrap, cap});
        return wrap;
    }

    private File buildTempDir(ViewSession vSession, Integer id, String ... dirs) {
        File root = vSession.getTempDir();
        if (!root.isDirectory() && !root.mkdir()) {
            return null;
        }

        //build temp/USER
        File dir = new File(root, vSession.getUser().getNom());
        if (!dir.isDirectory() && !dir.mkdir()) {
            log.warn("COULD NOT CREATE TMEP DIRE");
            return null;
        }
        dir.deleteOnExit();

        //build temp/user/THEME
        dir = new File(dir, vSession.getTheme().getNom());
        if (!dir.isDirectory() && !dir.mkdir()) {
            return null;
        }
        dir.deleteOnExit();

        //build temp/user/theme/DIRS...
        for (String dirName : dirs) {
            dir = new File(dir, dirName);
            if (!dir.isDirectory() && !dir.mkdir()) {
                return null;
            }
            dir.deleteOnExit();
        }

        if (id != null) {
            //build temp/user/theme/dirs/idID
            File unique = new File(dir, "id" + id.toString());
            if (!unique.isDirectory() && !unique.mkdir()) {
                try {
                    unique = File.createTempFile("uid.", ".tags", dir);
                    unique.delete();
                    if (!unique.mkdir()) {
                        return null;
                    }
                } catch (IOException e) {
                    log.warn( "IOException", e);
                    return null;
                }
            }
            dir = unique;
            dir.deleteOnExit();
        }

        return dir;
    }

    public String shrink(ViewSession vSession, Photo enrPhoto, int width) {
        File dir = buildTempDir(vSession, null, "shrinked");
        if (dir == null) {
            return null;
        }
        String ext = photoUtil.getExtention(vSession, enrPhoto);
        File fPhoto = new File(dir, enrPhoto.getId() + "-" + width + "." + ext);
        return shrink(vSession, enrPhoto, width, fPhoto.getAbsolutePath()) ;
    }

    public String shrink(ViewSession vSession, Photo enrPhoto, int width, String target) {
        try {
            if (width >= new Integer(enrPhoto.getWidth())) {
                return photoUtil.getImagePath(vSession, enrPhoto);
            }
        } catch (NumberFormatException e) {
            log.error( "Photo {} doesnt have a valid width:{}", new Object[]{enrPhoto, enrPhoto.getWidth()});
            //return photoUtil.getImagePath(vSession, enrPhoto);
        }

        String ext = photoUtil.getExtention(vSession, enrPhoto);
        File fPhoto = new File (target) ;
        fPhoto.getParentFile().mkdirs() ;
        Importer util = getWrapper(enrPhoto.getType(), ext, Importer.Capability.SHRINK);
        if (util != null) {
            util.shrink(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto.toString(), width);
        } else {
            log.warn("No plugin available to shrink {}@{}, just copy it across", enrPhoto.getType(), ext) ;
            plugins.getUsedSystem().copy(cb, fPhoto.toString(), fPhoto.toString() );
        }

        fPhoto.deleteOnExit();

        return fPhoto.toString();
    }

    public boolean thumbnail(String type, String ext, String source, String dest, int height) {
        Importer wrapper = getWrapper(type, ext, Importer.Capability.THUMBNAIL);
        if (wrapper == null) {
            return false;
        } else {
            return wrapper.thumbnail(cb, source, dest, height);
        }
    }

    public boolean rotate(String type, String ext, String degrees, String source, String dest) {
        Importer wrapper = getWrapper(type, ext, Importer.Capability.ROTATE);
        if (wrapper == null) {
            return false;
        } else {
            return wrapper.rotate(cb, degrees, source, dest);
        }
    }

    public boolean supports(String type, String ext, Capability capability) {
        return getWrapper(type, ext, capability) != null;
    }
    
    public boolean retrieveMetadata(String type, String ext, Photo photo, String path) {
        Importer wrapper = getWrapper(type, ext, Importer.Capability.META_DATA);
        if (wrapper == null) {
            return false ;
        } else {
            return wrapper.setMetadata((Metadata)photo, path) ;
        }
    }

    public void addBorder(ViewSession vSession, Photo enrPhoto, Integer borderWidth, String color, String filepath) {
        String ext = photoUtil.getExtention(vSession, enrPhoto);

        Importer util = getWrapper(enrPhoto.getType(), ext, Importer.Capability.ADD_BORDER);
        if (util == null) {
            log.warn("No plugin available to add a border to {}@{}, nothing changed", enrPhoto.getType(), ext);
            return ;
        }

        util.addBorder(cb, filepath, borderWidth, color == null ? "" : StringEscapeUtils.escapeJavaScript(color));
    }
}
