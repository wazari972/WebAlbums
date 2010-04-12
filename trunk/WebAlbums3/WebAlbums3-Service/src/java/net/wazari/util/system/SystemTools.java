package net.wazari.util.system;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import net.wazari.util.FileUtilWrapper;

import net.wazari.service.engine.WebPageBean;
import net.wazari.dao.entity.Photo;
import java.io.*;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.FileUtilWrapper.FileUtilWrapperCallBack;
import net.wazari.util.system.wrapper.*;

@Stateless
public class SystemTools implements SystemToolsService {

    private static final List<FileUtilWrapper> wrappers = new ArrayList<FileUtilWrapper>(2);
    private static final Logger log = Logger.getLogger("Process");
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;
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
        addWrapper(new ConvertPhotoWrapper());
        addWrapper(new TotemVideoWrapper());
    }

    private static void addWrapper(FileUtilWrapper wrapper) {
        wrappers.add(wrapper);
    }

    private static FileUtilWrapper getWrapper(String type, String ext) {
        for (FileUtilWrapper util : wrappers) {
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
        dir = new File(dir, themeDAO.find(vSession.getThemeId()).getNom());
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
    public void fullscreen(ViewSession vSession, List<Photo> lstPhoto, String type, Integer id, Integer page) {
        page = (page == null ? 0 : page);

        File dir = null;
        int i = 0;
        boolean first = true;
        FileUtilWrapper util = getWrapper("image", null);
        for (Photo enrPhoto : lstPhoto) {
            if (first) {
                dir = buildTempDir(vSession, type, id);
                if (dir == null) {
                    return;
                }
            }

            int currentPage = i / WebPageBean.TAILLE_PHOTO;
            File fPhoto = new File(dir, "" + i + "-p" + currentPage + "-" + enrPhoto.getId() + "." + photoUtil.getExtention(vSession, enrPhoto));
            link(photoUtil.getImagePath(vSession, enrPhoto), fPhoto);

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

        File fPhoto = new File(dir, enrPhoto.getId() + "-" + width + "." + photoUtil.getExtention(vSession, enrPhoto));
        FileUtilWrapper util = getWrapper(enrPhoto.getType(), null);
        if (util == null) {
            return photoUtil.getImagePath(vSession, enrPhoto);
        }

        util.shrink(cb, photoUtil.getImagePath(vSession, enrPhoto), fPhoto.toString(), width);
        fPhoto.deleteOnExit();

        return fPhoto.toString();
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

    public boolean link(String source, File dest) {
        return 0 == execWaitFor(new String[]{"ln", "-s", source, dest.toString()});
    }

    public void remove(String file) {
        execPS(new String[]{"rm", file, "-rf"});
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
}

