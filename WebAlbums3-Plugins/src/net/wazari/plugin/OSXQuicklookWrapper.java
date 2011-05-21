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
public class OSXQuicklookWrapper extends GenericImporter {
    private static final Logger log = LoggerFactory.getLogger(OSXQuicklookWrapper.class.getName());

    @Override
    public String getName() {
        return "OSX QuickLook wrapper" ;
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
        return "videos" ;
    }

    @Override
    public Capability[] supports() {
        return new Capability[]{Capability.THUMBNAIL} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if (type.contains("video"))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        File sourceFile = new File(source) ;
        if (!sourceFile.isFile()) return false ;
        String name = sourceFile.getName() ;

        File targetFile = new File(dest) ;
        int width = (int)(height*1.33) ;
        int ret = cb.execWaitFor(new String[]{"qlmanage", "-t", source,
                                                         "-s", Integer.toString(width),
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
                log.warn("copy failed ...");
            }
        } else {
            log.warn("thumnail creation failed ...");
        }
        return false ;
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"qlmanage", "-h"}) == 0) {
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    @Override
    public int getPriority() {
        return 3 ;
    }
}
