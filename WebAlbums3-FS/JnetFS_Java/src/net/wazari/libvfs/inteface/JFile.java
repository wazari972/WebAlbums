/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class JFile extends SFile implements IFile {
    private static final Logger log = LoggerFactory.getLogger(JFile.class.getCanonicalName());
    
    private java.io.File file;
    
    public void setJFile(java.io.File file) {
        this.file = file;
    }
    
    @Override
    public long getSize() {
        if (this.file != null) {
            return this.file.length();
        } else {
            return super.getSize();
        }
    }

    public java.io.File getJFile() {
        return this.file;
    }
}
