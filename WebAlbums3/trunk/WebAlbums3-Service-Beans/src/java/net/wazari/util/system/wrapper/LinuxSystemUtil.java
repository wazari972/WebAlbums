/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.io.File;
import java.util.logging.Logger;
import net.wazari.common.plugins.ProcessCallback;
import net.wazari.common.plugins.System;
/**
 *
 * @author pk033
 */
public class LinuxSystemUtil implements System{
    private static final Logger log = Logger.getLogger(LinuxSystemUtil.class.getCanonicalName()) ;
    
    public boolean link(ProcessCallback cb, String source, File dest) {
        return 0 == cb.execWaitFor(new String[]{"ln", "-s", source, dest.toString()});
    }
}
