package net.wazari.util.system.wrapper;

import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.ProcessCallback;


public class ImageMagickImageUtil implements Importer {

    public boolean support(String type, String ext) {
        return (type != null && type.contains("image"));
    }

    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-resize", "" + width + "x", source, dest});
    }

    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-thumbnail", "x" + height, source, dest});
    }

    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-rotate", degrees, source, dest});
    }

    public void fullscreen(ProcessCallback cb, String path) {
        if (path == null) {
            return;
        }
        cb.exec(new String[]{"eog", "--fullscreen", path});
    }

    public String getName() {
        return "Image Magick wrapper" ;
    }
}
