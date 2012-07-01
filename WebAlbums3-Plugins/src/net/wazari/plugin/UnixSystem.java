/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.plugin;

import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.Importer.SanityStatus;
import net.wazari.common.plugins.System;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author pk033
 */
public class UnixSystem implements System{
    private static final Logger log = LoggerFactory.getLogger(UnixSystem.class.getCanonicalName()) ;
    
    public boolean link(ProcessCallback cb, String source, String dest) {
        return 0 == cb.execWaitFor(new String[]{"ln", "-s", source, dest});
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

    public boolean copy(ProcessCallback cb, String source, String dest) {
        return 0 == cb.execWaitFor(new String[]{"cp", source, dest});
    }
}
