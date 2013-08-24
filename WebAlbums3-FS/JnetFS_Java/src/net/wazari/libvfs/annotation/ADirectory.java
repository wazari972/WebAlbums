/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.annotation;

import net.wazari.libvfs.inteface.VFSException;

/**
 *
 * @author kevin
 */
public interface ADirectory extends AFile {
    /* Call once if doesnt implement CanChange. Otherwise, called after a 
     * positive (true) call to contentChanged(). */
    void load() throws VFSException ;
}
