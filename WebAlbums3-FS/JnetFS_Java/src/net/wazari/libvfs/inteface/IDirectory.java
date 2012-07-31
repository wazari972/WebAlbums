/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import java.util.Map;

/**
 *
 * @author kevin
 */
public interface IDirectory extends IFile {
    Map<String, IFile> listFiles() ;
}
