/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

/**
 *
 * @author kevin
 */
public class VFSException extends Exception {

    public VFSException(String message) {
        super(message);
    }

    public VFSException(Exception ex) {
        super(ex);
    }
    
}
