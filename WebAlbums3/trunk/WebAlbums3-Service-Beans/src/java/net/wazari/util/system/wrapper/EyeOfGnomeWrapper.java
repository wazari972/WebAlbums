/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.util.Arrays;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.ProcessCallback;

/**
 *
 * @author kevinpouget
 */
public class EyeOfGnomeWrapper implements Importer {

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
        return new Capability[] {Capability.DIR_FULLSCREEN, Capability.FILE_FULLSCREEN} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if ((type != null && type.contains("image")))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fullscreen(ProcessCallback cb, String path) {
        if (path == null) {
            return;
        }
        cb.exec(new String[]{"eog", "--fullscreen", path});
    }

    @Override
    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"eog", "--help"}) == 0){
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    public String getDescription() {
        return "Wrapper for EyeOfGnome image visualizer" ;
    }

    @Override
    public String getSupportedFilesDesc() {
        return "all kind of photos" ;
    }
}
