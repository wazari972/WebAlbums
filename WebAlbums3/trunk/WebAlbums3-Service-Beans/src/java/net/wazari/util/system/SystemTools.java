package net.wazari.util.system;

import net.wazari.common.plugins.System;
import java.util.logging.Level;
import net.wazari.service.SystemToolsLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ServiceLoader;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import net.wazari.common.util.ClassPathUtil;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
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
    
    private static final Logger log = Logger.getLogger(SystemTools.class.getCanonicalName()) ;
    
    private static final List<Importer> wrappers = new LinkedList<Importer>();
    private static System systemUtil ;

    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private PhotoUtil photoUtil;

    private static final ProcessCallback cb = new ProcessCallback() {
        public int execWaitFor(String[] cmd) {
            return SystemTools.execWaitFor(cmd) ;
        }
        public void exec(String[] cmd) {
            SystemTools.execPS(cmd) ;
        }
    } ;

    public static boolean init() {
        return true ;
    }

    public void reloadPlugins(String path) {
        if (path != null) {
            ClassPathUtil.addDirToClasspath(new File(path)) ;
        }
        wrappers.clear() ;
        log.log(Level.INFO, "+++ Loading services for \"{0}\"", Importer.class.getCanonicalName());
        ServiceLoader<Importer> servicesImg = ServiceLoader.load(Importer.class);
        for (Importer current : servicesImg) {
            log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
            wrappers.add(current);
        }

        log.log(Level.INFO, "+++ Loading services for \"{0}\"", System.class.getCanonicalName());
        ServiceLoader<System> servicesSys = ServiceLoader.load(System.class);

        if (servicesSys.iterator().hasNext()) {
            systemUtil = servicesSys.iterator().next();
            log.log(Level.INFO, "+++ Adding \"{0}\"", systemUtil.getClass().getCanonicalName());
        } else {
            systemUtil = null ;
        }
    }

    public List<Importer> getPluginList() {
        return wrappers ;
    }

    private static Process execPS(String[] cmd) {
        try {
            log.log(Level.INFO, "exec: {0}", Arrays.toString(cmd));
            return Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static int execWaitFor(String[] cmd) {
        Process ps = execPS(cmd);
        if (ps == null) {
            return -1;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String str = null;
        while (true) {
            try {
                while ((str = reader.readLine()) != null) {
                    log.info(str);
                }

                reader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
                while ((str = reader.readLine()) != null) {
                    log.log(Level.INFO, "err - {0}", str);
                }
                int ret = ps.waitFor();
                log.log(Level.INFO, "ret:{0}", ret);

                return ret;

            } catch (InterruptedException e) {
            } catch (IOException e) {
            }
        }
    }
    private static Importer getWrapper(String type, String ext) {
        Importer img = null ;
        for (Importer util : wrappers) {
            if (util.support(type, ext)) {
                return util;
            }
            if (img == null) {
                if (util.support("image", null)) {
                    img = util ;
                }
            }
        }
        log.log(Level.WARNING, "no wrapper for {0}, trying image wrapper({1})", new Object[]{type, img});
        return img ;
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

    @SuppressWarnings("unchecked")
    public void fullscreen(ViewSession vSession,PhotoRequest rq, String type, Integer id, Integer page) {
        page = (page == null ? 0 : page);
        if (systemUtil == null) {
            log.warning("No ISystemUtil available ...");
            return ;
        }
        SubsetOf<Photo> lstPhoto ;
        if (rq.type == TypeRequest.PHOTO) {
            lstPhoto = photoDAO.loadFromAlbum(vSession, rq.albumId, null);
        } else {
            lstPhoto = photoDAO.loadByTags(vSession, rq.listTagId, null);
        }
        File dir = null;
        int i = 0;
        boolean first = true;
        Importer util = getWrapper("image", null);
        for (Photo enrPhoto : lstPhoto.subset) {
            if (first) {
                dir = buildTempDir(vSession, type, id);
                if (dir == null) {
                    return;
                }
            }

            int currentPage = i / vSession.getPhotoSize();
            File fPhoto = new File(dir, "" + i + "-p" + currentPage + "-" + enrPhoto.getId() + "." + photoUtil.getExtention(vSession, enrPhoto));
            systemUtil.link(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto);

            if (first && page == currentPage) {
                util.fullscreen(cb, fPhoto.toString());
                first = false;
            }
            fPhoto.deleteOnExit();
            i++;
        }
    }

    public String shrink(ViewSession vSession, Photo enrPhoto, int width) {
        if (width >= new Integer(enrPhoto.getWidth())) {
            return photoUtil.getImagePath(vSession, enrPhoto);
        }

        File dir = buildTempDir(vSession, "shrinked", null);
        if (dir == null) {
            return null;
        }
        String ext = photoUtil.getExtention(vSession, enrPhoto) ;
        File fPhoto = new File(dir, enrPhoto.getId() + "-" + width + "." + ext );
        Importer util = getWrapper(enrPhoto.getType(), ext);
        if (util == null) {
            return photoUtil.getImagePath(vSession, enrPhoto);
        }

        util.shrink(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto.toString(), width);
        fPhoto.deleteOnExit();

        return fPhoto.toString();
    }
    
    public boolean support(String type, String ext) {
        return getWrapper(type, ext) != null;
    }

    public boolean thumbnail(String type, String ext, String source, String dest, int height) {
        return getWrapper(type, ext).thumbnail(cb, source, dest, height) ;
    }

    public boolean rotate(String type, String ext, String degrees, String source, String dest) {
        return getWrapper(type, ext).rotate(cb, degrees, source, dest) ;
    }

    public void remove(String toString) {

    }
}

