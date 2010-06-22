/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.io.File;

/**
 *
 * @author kevinpouget
 */
public interface ViewSessionSession extends ViewSession {
    void setTempDir(File temp);
}
