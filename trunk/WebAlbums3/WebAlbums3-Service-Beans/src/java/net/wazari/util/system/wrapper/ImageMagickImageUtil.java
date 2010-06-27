package net.wazari.util.system.wrapper;

import net.wazari.util.system.IImageUtil;
import net.wazari.util.system.IImageUtil.FileUtilWrapperCallBack;


public class ImageMagickImageUtil implements IImageUtil {

    public boolean support(String type, String ext) {
        return (type != null && type.contains("image"));
    }

    public boolean shrink(FileUtilWrapperCallBack cb, String source, String dest, int width) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-resize", "" + width + "x", source, dest});
    }

    public boolean thumbnail(FileUtilWrapperCallBack cb, String source, String dest, int height) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-thumbnail", "x" + height, source, dest});
    }

    public boolean rotate(FileUtilWrapperCallBack cb, String degrees, String source, String dest) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-rotate", degrees, source, dest});
    }

    public void fullscreen(FileUtilWrapperCallBack cb, String path) {
        if (path == null) {
            return;
        }
        cb.exec(new String[]{"eog", "--fullscreen", path});
    }
}
