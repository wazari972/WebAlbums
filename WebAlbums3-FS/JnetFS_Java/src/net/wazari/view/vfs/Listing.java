/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;

/**
 *
 * @author kevin
 */
public class Listing implements ADirectory {
    @Directory
    @File(name="all")
    public Photos all = new Photos(this) ;
}