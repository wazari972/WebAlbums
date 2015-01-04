/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

/**
 *
 * @author kevin
 */
public class SLink extends JFile implements ILink {

    @Override
    public String getTarget() {
        return "generic_link";
    }

    @Override
    public boolean forceFile() {
        return false;
    }

    @Override
    public void truncate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
