package net.wazari.util.system;

import net.wazari.common.plugins.Importer.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.PhotoLocal.TypeRequest;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.ViewSession;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.Importer.Metadata;
import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.ProcessCallbackImpl;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.service.PluginManagerLocal;

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

    private File buildTempDir(ViewSession vSession, String type, Integer id) {
        File root = vSession.getTempDir();
        if (!root.isDirectory() && !root.mkdir()) {
            return null;
        }

        //build temp/USER
        File dir = new File(root, vSession.getUser().getNom());
        if (!dir.isDirectory() && !dir.mkdir()) {
            return null;
        }
        dir.deleteOnExit();

        //build temp/user/THEME
        dir = new File(dir, vSession.getTheme().getNom());
        if (!dir.isDirectory() && !dir.mkdir()) {
            return null;
        }
        dir.deleteOnExit();

        //build temp/user/theme/TYPE
        dir = new File(dir, type);
        if (!dir.isDirectory() && !dir.mkdir()) {
            return null;
        }
        dir.deleteOnExit();

        if (id != null) {
            //build temp/user/theme/type/idID
            File unique = new File(dir, "id" + id);
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

    public boolean fullscreenMultiple(ViewSession vSession, PhotoRequest rq, String type, Integer id, Integer page) {
        if (vSession.isRemoteAccess()) return false ;

        int pageAsked = (page == null ? 0 : page);
        if (plugins.getUsedSystem() == null) {
            log.warn("No System plugin available ...");
            return false;
        }
        Importer util = getWrapper("image", null, Importer.Capability.FULLSCREEN_MULTIPLE);
        if (util == null) {
            log.warn("No Importer plugin available ...");
            return false;
        }
        SubsetOf<Photo> lstPhoto;
        if (rq.type == TypeRequest.PHOTO) {
            lstPhoto = photoDAO.loadFromAlbum(vSession, rq.albumId, null, ListOrder.ASC);
        } else {
            lstPhoto = photoDAO.loadByTags(vSession, rq.listTagId, null, ListOrder.DESC);
        }
        File dir = null;
        int i = 0;
        boolean first = true;
        log.warn( "Fullscreen multiple: page asked:{}", pageAsked);
        for (Photo enrPhoto : lstPhoto.subset) {
            if (first) {
                dir = buildTempDir(vSession, type, id);
                if (dir == null) {
                    return false;
                }
            }

            int currentPage = i / vSession.getPhotoSize();
            log.info( "Fullscreen multiple: current page:{}", currentPage);
            File fPhoto = new File(dir, "" + i + "-p" + currentPage + "-" + enrPhoto.getId() + "." + photoUtil.getExtention(vSession, enrPhoto));
            plugins.getUsedSystem().link(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto);

            if (first && currentPage ==  pageAsked) {
                util.fullscreenMultiple(cb, fPhoto.toString());
                first = false;
            }
            fPhoto.deleteOnExit();
            i++;
        }
        return true;
    }

    public String shrink(ViewSession vSession, Photo enrPhoto, int width) {
        File dir = buildTempDir(vSession, "shrinked", null);
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
        if (util == null) {
            return photoUtil.getImagePath(vSession, enrPhoto);
        }

        util.shrink(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto.toString(), width);
        fPhoto.deleteOnExit();

        return fPhoto.toString();
    }

    public boolean fullscreenImage(ViewSession vSession, Photo enrPhoto) {
        if (vSession.isRemoteAccess()) return false ;
        
        File dir = buildTempDir(vSession, "fullscreen", null);
        if (dir == null) {
            return false;
        }
        String ext = photoUtil.getExtention(vSession, enrPhoto);
        Importer util = getWrapper(enrPhoto.getType(), ext, Importer.Capability.FULLSCREEN_SINGLE);
        if (util == null) {
            return false;
        }

        util.fullscreenFile(cb, photoUtil.getImagePath(vSession, enrPhoto));

        return true ;
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
    
    public boolean retreiveMetadata(String type, String ext, Photo photo, String path) {
        Importer wrapper = getWrapper(type, ext, Importer.Capability.META_DATA);
        if (wrapper == null) {
            return false ;
        } else {
            return wrapper.setMetadata((Metadata)photo, path) ;
        }
    }
}
