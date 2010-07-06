package net.wazari.util.system;

import net.wazari.common.plugins.Importer.Capability;
import net.wazari.common.plugins.System;
import java.util.logging.Level;
import net.wazari.service.SystemToolsLocal;
import java.io.File;
import java.io.IOException;
import java.util.List;

import java.util.LinkedList;
import java.util.ServiceLoader;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import net.wazari.common.util.ClassPathUtil;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.PhotoLocal.TypeRequest;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.ViewSession;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.ProcessCallback;

@Stateful
//@Singleton
public class SystemTools implements SystemToolsLocal {

    private static final Logger log = Logger.getLogger(SystemTools.class.getCanonicalName());
    private static final List<Importer> validWrappers = new LinkedList<Importer>();
    private static final List<Importer> invalidWrappers = new LinkedList<Importer>();
    private static System system = null ;
    private static final List<System> notUsedSystems = new LinkedList<System>();
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private PhotoUtil photoUtil;
    private static final ProcessCallback cb = ProcessCallback.getProcessCallBack();

    public static boolean init() {
        return true;
    }

    @Override
    public void reloadPlugins(String path) {
        if (path != null) {
            ClassPathUtil.addDirToClasspath(new File(path));
        }
        validWrappers.clear();
        invalidWrappers.clear();
        log.log(Level.INFO, "+++ Loading services for \"{0}\"", Importer.class.getCanonicalName());
        ServiceLoader<Importer> servicesImg = ServiceLoader.load(Importer.class);
        for (Importer current : servicesImg) {
            log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
            if (current.sanityCheck(cb) == Importer.SanityStatus.PASS) {
                validWrappers.add(current);
            } else {
                invalidWrappers.add(current);
            }
        }

        log.log(Level.INFO, "+++ Loading services for \"{0}\"", System.class.getCanonicalName());
        ServiceLoader<System> servicesSys = ServiceLoader.load(System.class);
        this.system = null ;
        for (System current : servicesSys) {
            log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
            if (system == null && current.sanityCheck(cb) == Importer.SanityStatus.PASS) {
                system = current;
            } else {
                notUsedSystems.add(current);
            }
        }
    }

    @Override
    public List<Importer> getPluginList() {
        List<Importer> wrappers = new LinkedList<Importer>(validWrappers);
        wrappers.addAll(invalidWrappers);
        return wrappers;
    }

    @Override
    public List<System> getNotUsedSystemList() {
        return notUsedSystems ;
    }

    @Override
    public System getUsedSystem() {
        return system ;
    }


    private static Importer getWrapper(String type, String ext, Importer.Capability cap) {
        Importer wrap = null;
        for (Importer util : validWrappers) {
            if (util.supports(type, ext, cap)) {
                wrap = util;
                break ;
            }
        }
        log.log(Level.WARNING, "Wrapper for {0}-{1}: {2}", new Object[]{type, ext, wrap});
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
                    e.printStackTrace();
                    return null;
                }
            }
            dir = unique;
            dir.deleteOnExit();
        }

        return dir;
    }

    @Override
    public boolean fullscreen(ViewSession vSession, PhotoRequest rq, String type, Integer id, Integer page) {
        page = (page == null ? 0 : page);
        if (system == null) {
            log.warning("No ISystemUtil available ...");
            return false;
        }
        SubsetOf<Photo> lstPhoto;
        if (rq.type == TypeRequest.PHOTO) {
            lstPhoto = photoDAO.loadFromAlbum(vSession, rq.albumId, null);
        } else {
            lstPhoto = photoDAO.loadByTags(vSession, rq.listTagId, null);
        }
        File dir = null;
        int i = 0;
        boolean first = true;
        Importer util = getWrapper("image", null, Importer.Capability.DIR_FULLSCREEN);
        if (util == null) {
            return false;
        }
        for (Photo enrPhoto : lstPhoto.subset) {
            if (first) {
                dir = buildTempDir(vSession, type, id);
                if (dir == null) {
                    return false;
                }
            }

            int currentPage = i / vSession.getPhotoSize();
            File fPhoto = new File(dir, "" + i + "-p" + currentPage + "-" + enrPhoto.getId() + "." + photoUtil.getExtention(vSession, enrPhoto));
            system.link(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto);

            if (first && page == currentPage) {
                util.fullscreen(cb, fPhoto.toString());
                first = false;
            }
            fPhoto.deleteOnExit();
            i++;
        }
        return true;
    }

    @Override
    public String shrink(ViewSession vSession, Photo enrPhoto, int width) {
        if (width >= new Integer(enrPhoto.getWidth())) {
            return photoUtil.getImagePath(vSession, enrPhoto);
        }

        File dir = buildTempDir(vSession, "shrinked", null);
        if (dir == null) {
            return null;
        }
        String ext = photoUtil.getExtention(vSession, enrPhoto);
        File fPhoto = new File(dir, enrPhoto.getId() + "-" + width + "." + ext);
        Importer util = getWrapper(enrPhoto.getType(), ext, Importer.Capability.SHRINK);
        if (util == null) {
            return photoUtil.getImagePath(vSession, enrPhoto);
        }

        util.shrink(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto.toString(), width);
        fPhoto.deleteOnExit();

        return fPhoto.toString();
    }

    @Override
    public boolean thumbnail(String type, String ext, String source, String dest, int height) {
        Importer wrapper = getWrapper(type, ext, Importer.Capability.THUMBNAIL);
        if (wrapper == null) {
            return false;
        } else {
            return wrapper.thumbnail(cb, source, dest, height);
        }
    }

    @Override
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
}
