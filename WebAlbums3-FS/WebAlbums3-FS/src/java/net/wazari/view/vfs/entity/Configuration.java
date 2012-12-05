/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.logging.Level;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.SFile;
import net.wazari.libvfs.inteface.ValueFile;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Configuration  extends SDirectory implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(SFile.class.getCanonicalName()) ;
    
    @File(name="stars")
    public ValueFile stars;
    
    private Root root;
    
    public Configuration(Root root) {
        this.root = root;
    }
    
    @Override
    public void load() throws Exception {
        stars = new ValueFile(new ValueFile.SetterCallback() {

            @Override
            public void setContent(String content) {
                try {
                    Session.stars = Integer.parseInt(content);
                    root.changed = true;
                } catch(NumberFormatException e) {
                    log.warn("Coudln't parse a number in '{}'", content);
                    throw e;
                } catch (Exception ex) {
                    log.warn(ex.getMessage());
                }
            }
        }, new ValueFile.GetterCallback() {

            @Override
            public String getContent() {
                return Integer.toString(Session.stars);
            }
        });
    }
    
}
