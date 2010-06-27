/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system;

import java.io.File;
import net.wazari.util.system.IImageUtil.FileUtilWrapperCallBack;

/**
 *
 * @author kevinpouget
 */
public interface ISystemUtil {
    boolean link(FileUtilWrapperCallBack cb, String source, File dest);
}
