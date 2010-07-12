/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.Metadata;
import net.wazari.common.plugins.ProcessCallback;

/**
 *
 * @author kevinpouget
 */
public class OSXToolsWrapper implements Importer {
    private static final String PREVIEW = "/Applications/Preview.app/Contents/MacOS/Preview" ;
    private static final Logger log = Logger.getLogger(OSXToolsWrapper.class.getName());

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
        return new Capability[]{Capability.THUMBNAIL, Capability.FULLSCREEN_SINGLE} ;
    }

    public boolean supports(String type, String ext, Capability cap) {
        if (type.contains("video"))
            return Arrays.asList(supports()).contains(cap) ;
        else if (type.contains("image"))
            return Capability.FULLSCREEN_SINGLE.equals(cap) ;
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
        int ret = cb.execWaitFor(new String[]{"qlmanage", "-t", source,
                                                         "-s", Integer.toString(height),
                                                         "-o", targetFile.getParent()}) ;
        if (ret == 0) {
            String genMiniFileName = targetFile.getParent() + File.separator + name+".png" ;
            if (targetFile.getAbsolutePath().equals(new File(genMiniFileName).getAbsolutePath())) {
                log.info("Miniature already at the right place");
                return true ;
            }
            ret = cb.execWaitFor(new String[]{"mv",genMiniFileName, dest}) ;
            if (ret == 0) {
                return true ;
            } else {
                log.warning("copy failed ...");
            }
        } else {
            log.warning("thumnail creation failed ...");
        }
        return false ;
    }

    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fullscreenMultiple(ProcessCallback cb, String path) {
         throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setMetadata(Metadata data, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SanityStatus sanityCheck(ProcessCallback cb) {
        if ((cb.execWaitFor(new String[]{"qlmanage", "-h"}) == 0
        && new File(PREVIEW).canExecute())) {
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    public int getPriority() {
        return 3 ;
    }

    public void fullscreenFile(ProcessCallback cb, String path) {
        cb.exec(new String[]{PREVIEW, path});
    }
}
