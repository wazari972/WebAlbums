/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.io.File;
import java.util.Arrays;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.Metadata;
import net.wazari.common.plugins.ProcessCallback;

/**
 *
 * @author kevinpouget
 */
public class OSXQuickLookWrapper implements Importer {

    public String getName() {
        return "OSX QuickLook wrapper" ;
    }

    public String getVersion() {
        return "1" ;
    }

    public String getDescription() {
        return "creates thumbnails from videos with OSX default tool" ;
    }

    public String getSupportedFilesDesc() {
        return "videos" ;
    }

    public Capability[] supports() {
        return new Capability[]{Capability.THUMBNAIL} ;
    }

    public boolean supports(String type, String ext, Capability cap) {
        if (type.contains("video"))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        File sourceFile = new File(source) ;
        if (!sourceFile.isFile()) return false ;
        String name = sourceFile.getName() ;

        File targetFile = new File(dest) ;
        if (0 == cb.execWaitFor(new String[]{"qlmanage", "-t", source,
                                                         "-s", Integer.toString(height),
                                                         "-o", targetFile.getParent()}))
        {
            if (0 == cb.execWaitFor(new String[]{"cp",
                                                 targetFile.getParent() + File.separator + name,
                                                 dest}))
            {
                return true ;
            }
        }
        return false ;
    }

    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fullscreen(ProcessCallback cb, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setMetadata(Metadata data, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"qlmanage", "-h"}) == 0) {
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    public int getPriority() {
        return 3 ;
    }

}
