package net.wazari.util.system;

import net.wazari.service.SystemToolsLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.ServiceLoader;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.PhotoLocal.TypeRequest;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.system.IImageUtil.FileUtilWrapperCallBack;

@Stateless
public class SystemTools implements SystemToolsLocal {
    private static final Logger log = Logger.getLogger(SystemTools.class.getCanonicalName()) ;
    private static final List<IImageUtil> wrappers = new ArrayList<IImageUtil>(2);
    private static final ISystemUtil systemUtil ;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private PhotoFacadeLocal photoDAO;
    @EJB
    private PhotoUtil photoUtil;

    private static final FileUtilWrapperCallBack cb = new FileUtilWrapperCallBack() {
        public int execWaitFor(String[] cmd) {
            return SystemTools.execWaitFor(cmd) ;
        }
        public void exec(String[] cmd) {
            SystemTools.execPS(cmd) ;
        }
    } ;

    static {

        log.info("+++ Loading services for \"" + IImageUtil.class.getCanonicalName() + "\"");
        ServiceLoader<IImageUtil> servicesImg = ServiceLoader.load(IImageUtil.class);
        for (IImageUtil current : servicesImg) {
            wrappers.add(current);
        }
        
        log.info("+++ Loading services for \"" + ISystemUtil.class.getCanonicalName() + "\"");
        ServiceLoader<ISystemUtil> servicesSys = ServiceLoader.load(ISystemUtil.class);
        
        if (servicesSys.iterator().hasNext()) {
            systemUtil = servicesSys.iterator().next();
        } else {
            systemUtil = null ;
        }
    }
    private static Process execPS(String[] cmd) {
        try {
            log.info("exec: " + Arrays.toString(cmd));
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
                    log.info("err - " + str);
                }
                int ret = ps.waitFor();
                log.info("ret:" + ret);

                return ret;

            } catch (InterruptedException e) {
            } catch (IOException e) {
            }
        }
    }
    private static IImageUtil getWrapper(String type, String ext) {
        for (IImageUtil util : wrappers) {
            if (util.support(type, ext)) {
                return util;
            }
        }
        log.warning("no wrapper for " + type);
        return getWrapper("image", null);
    }

    private File buildTempDir(ViewSession vSession, String type, Integer id) {
        File root = vSession.getTempDir();
        if (!root.isDirectory() && !root.mkdir()) {
            return null;
        }

        //build temp/USER
        File dir = new File(root, userDAO.find(vSession.getUserId()).getNom());
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
        IImageUtil util = getWrapper("image", null);
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
        IImageUtil util = getWrapper(enrPhoto.getType(), ext);
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

