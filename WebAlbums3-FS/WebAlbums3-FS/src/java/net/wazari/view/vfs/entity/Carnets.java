/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.common.exception.WebAlbumsException;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.xml.album.XmlAlbumYear;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsDisplay;
import net.wazari.service.exchange.xml.common.XmlDate;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class Carnets implements ADirectory {
    private final net.wazari.dao.entity.Theme theme;
    private final Launch aThis;
    
    @Directory
    @File
    public List<Carnet> carnets = new LinkedList<>();
    
    public Carnets(net.wazari.dao.entity.Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
    }

    @Override
    public void load() throws VFSException {
        try {
            Session session = new Session(theme);
            
            XmlCarnetsDisplay carnetList = aThis.carnetService.treatDISPLAY(session, null);
            
            for (XmlCarnet carnet : carnetList.carnet) {
                carnets.add(new Carnet(carnet.date, carnet.name, carnet.id, 
                        carnet.text, carnet.picture, carnet.photo, 
                        theme, aThis));
            }
        } catch (Exception ex) {
            throw new VFSException(ex);
        }
    }
    
    public static class Carnet extends SDirectory implements ADirectory {
        @Directory
        @File
        public List<Photo> photoset = new LinkedList<>();
        
        @File
        public Photo picture;
        
        @File
        public TextFile text;
        
        private String name ;
        private final net.wazari.dao.entity.Theme theme;
        private final Launch aThis;
        private final int carnetId;
        private final List<String> textLines;
        private final XmlPhotoId pictureId;
        private final List<XmlPhotoId> photosIds;
        
        public Carnet(XmlDate date, String name, int carnetId, 
                List<String> text, XmlPhotoId picture, List<XmlPhotoId> photos,
                net.wazari.dao.entity.Theme theme, Launch aThis) 
        {
            this.name = date.date + " " + name;
            this.theme = theme;
            this.aThis = aThis;
            this.carnetId = carnetId;
            
            this.textLines = text;
            this.pictureId = picture;
            this.photosIds = photos;
        }
        
        @Override
        public void load() throws VFSException {
            try {
                Session session = new Session(theme);
                
                text = new TextFile("textLines", "name");
                
                throw new WebAlbumsServiceException(
                        WebAlbumsException.ErrorType.ServiceException,
                        "not implemented yet");
            } catch (WebAlbumsServiceException ex) {
                throw new VFSException(ex);
            }
        }
        
    }
}
