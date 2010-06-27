/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.io.File;
import java.util.logging.Logger;
import net.wazari.util.system.IImageUtil.FileUtilWrapperCallBack;
import net.wazari.util.system.ISystemUtil;
/**
 *
 * @author pk033
 */
public class LinuxSystemUtil implements ISystemUtil{
    private static final Logger log = Logger.getLogger(LinuxSystemUtil.class.getCanonicalName()) ;
    
    public boolean link(FileUtilWrapperCallBack cb, String source, File dest) {
        return 0 == cb.execWaitFor(new String[]{"ln", "-s", source, dest.toString()});
    }
}
