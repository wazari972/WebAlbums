/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.CanChange;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.xml.XmlTheme;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@File
public class Root extends SDirectory implements ADirectory, CanChange {
    private static final Logger log = LoggerFactory.getLogger(Root.class.getCanonicalName()) ;
    
    public int stars = 1;
    public boolean PHOTO_AS_FILE = false;
    
    @File
    @Directory
    public List<Theme> themes ;
    
    @Directory
    @File(name="config")
    public final Configuration config = new Configuration(this);
    
    @Directory
    @File
    public final List<SDirectory> trashes = new LinkedList<>();
    
    protected final Launch aThis;

    public Root(Launch aThis) throws WebAlbumsServiceException {
        this.aThis = aThis;
    }

    @Override
    public void load() throws VFSException {
        themes = new LinkedList<>();
        for (XmlTheme theme : aThis.themeService.getThemeListSimple(new Session(null, this)).theme) {
            log.debug("LOAD ROOT {}", theme.name);
            themes.add(new Theme(this, theme.id, theme.name, aThis));
        }
    }
    
    @Override
    public void mkdir(String name) {
        log.debug("mkdir {}", name);
        if (name.startsWith(".Trash")) {
            trashes.add(new SDirectory());
        }
    }

    @Override
    public void contentRead() {
        changed = false;
    }

    @Override
    public boolean contentChanged() {
        return changed;
    }
    
    public boolean changed = true;
}