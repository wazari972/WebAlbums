package net.wazari.plugin;

import java.util.Arrays;
import net.wazari.common.plugins.Importer;

public class TotemWrapper implements Importer {

    private boolean supports(String type, String ext) {
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

    @Override
    public Capability[] supports() {
        return new Capability[] {Capability.THUMBNAIL, Capability.FULLSCREEN_SINGLE} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if (supports(type, ext))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        return 0 == cb.execWaitFor(new String[]{"totem-video-thumbnailer", "-s", "" + height, source, dest});
    }

    @Override
    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        return true;
    }

    @Override
    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        return true;
    }

    @Override
    public void fullscreenFile(ProcessCallback cb, String path) {
        if (path == null) {
            return;
        }
        cb.exec(new String[]{"totem", path});
    }

    @Override
    public String getName() {
        return "Totem Wrapper" ;
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        if ((cb.execWaitFor(new String[]{"totem", "--version"}) == 0)
                && (cb.execWaitFor(new String[]{"totem-video-thumbnailer", "--help"}) == 0))
        {
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    @Override
    public String getTargetSystem() {
        return "Linux" ;
    }

    @Override
    public String getSupportedFilesDesc() {
        return "all kind of videos and ASF" ;
    }

    @Override
    public boolean setMetadata(Metadata data, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPriority() {
        return 9 ;
    }

    public void fullscreenMultiple(ProcessCallback cb, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
