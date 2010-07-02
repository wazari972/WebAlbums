/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

import java.io.File;

/**
 *
 * @author kevinpouget
 */
public interface System {
    boolean link(ProcessCallback cb, String source, File dest);
}
