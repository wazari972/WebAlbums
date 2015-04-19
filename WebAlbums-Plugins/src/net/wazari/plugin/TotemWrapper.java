package net.wazari.plugin;

import java.util.Arrays;
import net.wazari.common.plugins.GenericImporter;
import net.wazari.common.plugins.Importer.Capability;
import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.Importer.SanityStatus;

public class TotemWrapper extends GenericImporter {

    private boolean supports(String type, String ext) {
        if (true) return false;
        if (type != null && (type.contains("video") || type.toLowerCase().contains("mpeg"))) {
            return true;
        }
        
        if (ext != null && "asf 3gp".contains(ext.toLowerCase())) {
            return true;
        }

        return false;
    }

    @Override
    public Capability[] supports() {
        return new Capability[] {Capability.THUMBNAIL} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if (supports(type, ext))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public String getName() {
        return "Totem Wrapper DISABLED" ;
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
    public int getPriority() {
        return 9 ;
    }
}
