package net.wazari.plugin;

import java.util.Arrays;
import net.wazari.common.plugins.GenericImporter;
import net.wazari.common.plugins.Importer.Capability;
import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.Importer.SanityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImageMagickWrapper extends GenericImporter {
    @Override
    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-resize", "" + width + "x", source, dest});
    }

    @Override
    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-thumbnail", "x" + height, source, dest});
    }

    @Override
    public boolean rotate(ProcessCallback cb, String degrees , String source, String dest) {
        return 0 == cb.execWaitFor(new String[]{"convert", "-rotate", degrees, source, dest});
    }

    @Override
    public String getName() {
        return "Image Magick wrapper" ;
    }

    @Override
    public String getVersion() {
        return "1" ;
    }

    @Override
    public Capability[] supports() {
        return new Capability[] {Capability.ROTATE, Capability.SHRINK, Capability.THUMBNAIL, Capability.ADD_BORDER} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if (type.contains("image"))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"convert", "--version"}) == 0){
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    @Override
    public String getTargetSystem() {
        return "various" ;
    }

    @Override
    public String getSupportedFilesDesc() {
        return "photos" ;
    }

    @Override
    public int getPriority() {
        return 8 ;
    }
    private static final Logger log = LoggerFactory.getLogger(ImageMagickWrapper.class.getName());

    @Override
    public void addBorder(ProcessCallback cb, String imagePath, Integer borderWidth, String color) {
        cb.execWaitFor(new String[]{"convert", "-border", "" + borderWidth + "x"+borderWidth, "-bordercolor", color, imagePath, imagePath});
    }
}
