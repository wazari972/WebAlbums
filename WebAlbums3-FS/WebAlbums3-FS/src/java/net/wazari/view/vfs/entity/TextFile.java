/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.inteface.SFile;

/**
 *
 * @author kevin
 */
class TextFile extends SFile {
    public TextFile(String content, String name) {
        this.content = content;
        this.myName = name;
    }
}
