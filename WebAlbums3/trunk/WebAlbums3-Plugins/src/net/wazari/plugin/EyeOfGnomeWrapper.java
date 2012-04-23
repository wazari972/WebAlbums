/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.plugin;

import java.util.Arrays;
import net.wazari.common.plugins.GenericImporter;

/**
 *
 * @author kevinpouget
 */
public class EyeOfGnomeWrapper extends GenericImporter {

    @Override
    public String getName() {
        return "Eye of Gnome wrapper" ;
    }

    @Override
    public String getVersion() {
        return "1" ;
    }

    @Override
    public Capability[] supports() {
        return new Capability[] {Capability.FULLSCREEN_MULTIPLE, Capability.FULLSCREEN_SINGLE} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if ((type != null && type.contains("image")))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public void fullscreenMultiple(ProcessCallback cb, String path) {
        if (path == null) {
            return;
        }
        cb.exec(new String[]{"eog", "--fullscreen", path});
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"eog", "--help"}) == 0){
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    @Override
    public String getTargetSystem() {
        return "Linux/Gnome" ;
    }

    @Override
    public String getSupportedFilesDesc() {
        return "photos" ;
    }

    public int getPriority() {
        return 6 ;
    }

    public void fullscreenFile(ProcessCallback cb, String path) {
        fullscreenMultiple(cb, path);
    }
}
