/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsDisplay;
import net.wazari.service.exchange.xml.common.XmlDate;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;
import net.wazari.view.vfs.FSConnector;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Carnets implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(Carnets.class.getCanonicalName()) ;
    
    private final net.wazari.dao.entity.Theme theme;
    private final FSConnector aThis;
    private final Root root;
    
    @Directory
    @File
    public List<Carnet> carnets = new LinkedList<>();
    
    public Carnets(Root root, net.wazari.dao.entity.Theme theme, FSConnector aThis) {
        this.theme = theme;
        this.aThis = aThis;
        this.root = root;
    }

    @Override
    public void load() throws VFSException {
        try {
            Session session = new Session(theme, this.root);
            session.setAllCarnetText(true);
            
            XmlCarnetsDisplay carnetList = aThis.carnetService.treatDISPLAY(session, null);
            
            for (XmlCarnet carnet : carnetList.carnet) {
                carnets.add(new Carnet(this.root, carnet.date, carnet.name, carnet.id, 
                        carnet.text, carnet.picture, carnet.photo));
            }
        } catch (Exception ex) {
            throw new VFSException(ex);
        }
    }
    
    public class Carnet extends SDirectory implements ADirectory {
        @File
        public List<Photo> photoset = new LinkedList<>();
        
        @File
        public Photo picture;
        
        @File
        public TextFile text;
        
        private final String carnet_name;
        
        @Override
        public String getShortname() {
            return carnet_name;
        }
        
        public Carnet(Root root, XmlDate date, String name, int carnetId, 
                List<String> text, XmlPhotoId picture, List<XmlPhotoId> photos) 
        {
            this.carnet_name = date.date + " " + name;
            
            if (text != null) {
                StringBuilder textcontent = new StringBuilder();
                for (String line : text) {
                    textcontent.append(line).append("\n");
                }
                this.text = new TextFile(textcontent.toString(), name + ".md");
            } else {
                this.text = null;
            }
            if (picture != null) {
                this.picture = new Photo(Carnets.this.root, picture.path, picture.id);
                this.picture.setName(carnet_name+".jpg");
            }
            for (XmlPhotoId photo : photos) {
                Photo p = new Photo(Carnets.this.root, photo.path, photo.id);
                p.setName(""+photo.id+".jpg");
                photoset.add(p);
            }
        }        

        @Override
        public void load() throws VFSException {}
    }
}
