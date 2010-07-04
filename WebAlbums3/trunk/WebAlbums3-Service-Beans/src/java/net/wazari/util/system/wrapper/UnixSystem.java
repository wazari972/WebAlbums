/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.io.File;
import java.util.logging.Logger;
import net.wazari.common.plugins.Importer.SanityStatus;
import net.wazari.common.plugins.ProcessCallback;
import net.wazari.common.plugins.System;
/**
 *
 * @author pk033
 */
public class UnixSystem implements System{
    private static final Logger log = Logger.getLogger(UnixSystem.class.getCanonicalName()) ;
    
    public boolean link(ProcessCallback cb, String source, File dest) {
        return 0 == cb.execWaitFor(new String[]{"ln", "-s", source, dest.toString()});
    }

    public String getName() {
        return "Unix system tools";
    }

    public String getVersion() {
        return "1";
    }

    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"ln", "-h"}) == 1){
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }
}
