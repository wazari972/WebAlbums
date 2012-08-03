/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

/**
 *
 * @author kevin
 */
public class SLink extends SFile implements ILink {

    @Override
    public String getTarget() {
        return "generic_link";
    }
    
}
