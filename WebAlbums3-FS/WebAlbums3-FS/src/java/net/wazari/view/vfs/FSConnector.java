/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import com.jnetfs.core.JnetFS;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import net.wazari.libvfs.inteface.IResolver;
import net.wazari.libvfs.vfs.LibVFS;
import net.wazari.libvfs.vfs.Resolver;
import net.wazari.service.AlbumLocal;
import net.wazari.service.CarnetLocal;
import net.wazari.service.ImageLocal;
import net.wazari.service.PhotoLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import static net.wazari.view.vfs.Launch.getFolderPrefix;
import net.wazari.view.vfs.entity.PhotoResolver;
import net.wazari.view.vfs.entity.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN) // multiple async
public class FSConnector {
    private static final Logger log = LoggerFactory.getLogger(Launch.class.getCanonicalName()) ;
    public static final String DEFAULT_PATH = "/home/kevin/WebAlbums3-FS";
    public static final boolean CONNECT_ON_STARTUP = true;
    
    @EJB public ImageLocal imageService;
    @EJB public PhotoLocal photoService;
    @EJB public AlbumLocal albumService;
    @EJB public ThemeLocal themeService ;
    @EJB public CarnetLocal carnetService ;
    @EJB public TagLocal tagService;
    @EJB public WebPageLocal webPageService ;
    
    @EJB private FSConnector con; /* for async call. */
    
    @PostConstruct
    void init() {
        if (!CONNECT_ON_STARTUP) {
            return;
        }
        log.info("Connecting {} ...", DEFAULT_PATH);
        con.connectLibVFS(true, DEFAULT_PATH);
        log.info("Connection done :-)");
    }
    
    @Asynchronous
    public void connectLibVFS(boolean mount, String path) {
        if (mount) {
            Root root = new Root(this);
            IResolver externalResolver = new PhotoResolver(root);
            LibVFS.resolver = new Resolver(root, getFolderPrefix(true), 
                    externalResolver, true);

            JnetFS.do_mount(new String[]{path});
        } else {
            JnetFS.do_umount(path);
        }
    }
}