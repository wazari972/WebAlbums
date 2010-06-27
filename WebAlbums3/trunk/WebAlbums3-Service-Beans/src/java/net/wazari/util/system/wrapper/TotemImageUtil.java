package net.wazari.util.system.wrapper;

import net.wazari.util.system.IImageUtil;
import net.wazari.util.system.IImageUtil.FileUtilWrapperCallBack;



public class TotemImageUtil implements IImageUtil {

    public boolean support(String type, String ext) {
        if (type != null) {
            if (type.contains("video")) {
                return true;
            }
        }

        if (ext != null) {
            if (String.CASE_INSENSITIVE_ORDER.compare(ext, "asf") == 0) {
                return true;
            }
        }

        return false;

    }

    public boolean thumbnail(FileUtilWrapperCallBack cb, String source, String dest, int height) {
        return 0 == cb.execWaitFor(new String[]{"totem-video-thumbnailer", "-s", "" + height, source, dest});
    }

    public boolean rotate(FileUtilWrapperCallBack cb, String degrees, String source, String dest) {
        return true;
    }

    public boolean shrink(FileUtilWrapperCallBack cb, String source, String dest, int width) {
        return true;
    }

    public void fullscreen(FileUtilWrapperCallBack cb, String path) {
        if (path == null) {
            return;
        }
        cb.exec(new String[]{"totem", path});
    }
}
