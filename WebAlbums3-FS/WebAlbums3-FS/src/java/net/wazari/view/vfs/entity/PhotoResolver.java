/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.IResolver;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class PhotoResolver implements IResolver {
    private static final Logger log = LoggerFactory.getLogger(Album.class.getCanonicalName()) ;
    
    private final Root root;
    
    public PhotoResolver(Root root) {
        this.root = root;
    }

    @Override
    public IFile getFile(String search) {
        search = search.substring(search.indexOf("/")+1);
        try {
            Session session = new Session(null, this.root);
            XmlDetails details = root.aThis.photoService.getPhotoByPath(session, search);
            if (details != null) {
                log.info("Photo found for path {} >> ", search, details.photoId);
                return new Photo(this.root, details);
            }
            
            log.info("Photo not found for path {}", search);
        } catch (WebAlbumsServiceException ex) {
            log.warn("Resolver crashed for path {}", search, ex);
        }
        return null;
    }
    
}
