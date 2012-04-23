/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.plugin;

import java.io.File;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.common.plugins.GenericImporter;
import net.wazari.common.plugins.Importer.Capability;
import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.Importer.SanityStatus;

/**
 *
 * @author kevinpouget
 */
public class OSXPreviewWrapper  extends GenericImporter {
    private static final String PREVIEW = "/Applications/Preview.app/Contents/MacOS/Preview" ;
    private static final Logger log = LoggerFactory.getLogger(OSXPreviewWrapper.class.getName());

    @Override
    public String getName() {
        return "OSX Preview wrapper" ;
    }

    @Override
    public String getVersion() {
        return "1" ;
    }

    @Override
    public String getTargetSystem() {
        return "OSX" ;
    }

    @Override
    public String getSupportedFilesDesc() {
        return "images" ;
    }

    @Override
    public Capability[] supports() {
        return new Capability[]{Capability.FULLSCREEN_SINGLE, Capability.FULLSCREEN_MULTIPLE} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if (type.contains("image"))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (new File(PREVIEW).canExecute()) {
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    @Override
    public int getPriority() {
        return 3 ;
    }

    @Override
    public void fullscreenFile(ProcessCallback cb, String path) {
        cb.exec(new String[]{PREVIEW, path});
    }

    @Override
    public void fullscreenMultiple(ProcessCallback cb, String path) {
        cb.exec(new String[]{PREVIEW, path});
    }
}
