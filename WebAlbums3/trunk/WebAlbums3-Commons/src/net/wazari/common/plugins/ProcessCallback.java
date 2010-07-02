/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.common.plugins;

/**
 *
 * @author kevinpouget
 */
public interface ProcessCallback {

    int execWaitFor(String[] cmd);

    void exec(String[] cmd);
}
