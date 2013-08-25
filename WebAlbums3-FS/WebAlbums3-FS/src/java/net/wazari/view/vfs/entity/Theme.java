/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.List;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.TagTheme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Theme extends SDirectory implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(Theme.class.getCanonicalName()) ;
    
    @Directory
    @File(name="Albums")
    public Albums albums;
    @Directory
    @File(name="Tags")
    public Tags tags ;
    @Directory
    @File(name="Carnets")
    public Carnets carnets = new Carnets(this);
    
    private String name;
    
    @Directory
    @File(name="Random")
    public Random random;
    
    @File
    public GpxFile location;
    
    protected final ATheme theme;
    private final Launch aThis;
    
    Theme(int id, String name, Launch aThis) {
        this.name = name;
        this.theme = new ATheme(id, name);
        this.aThis = aThis;
    }
    
    @Override
    public String getShortname() {
        return name;
    }

    @Override
    public void load() throws VFSException {
        this.tags = new Tags(theme, aThis);
        this.albums = new Albums(theme, aThis);
        this.random = new Random(theme, aThis);
        
        this.loadLocation();
    }
    
    @Override
    public String toString() {
        return "Directory["+name+"]";
    }

    private void loadLocation() throws VFSException {
        Session session = new Session(theme);
        GpxPoints loc = new GpxPoints();
        
        XmlTagPersonsPlaces lst = aThis.tagService.treatTagPlaces(session);
        for (XmlTag tag : lst.tagList) {
            if (tag.lat != null && tag.lng != null) {
                loc.addPoint(tag.name, tag.lat, tag.lng);
            }
        }
        location = new GpxFile(loc);
    }
    
    public class ATheme implements net.wazari.dao.entity.Theme {
        private final String name;
        private final int id;
        public ATheme(int id, String name) {
            this.name = name;
            this.id = id;
        }
        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public void setId(Integer id) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getNom() {
            return name;
        }

        @Override
        public void setNom(String nom) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<TagTheme> getTagThemeList() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setTagThemeList(List<TagTheme> tagThemeList) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Photo getPicture() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPicture(Photo picture) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Photo getBackground() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBackground(Photo background) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Album> getAlbumList() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setAlbumList(List<Album> carnetList) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Album> getCarnetList() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setCarnetList(List<Carnet> carnetList) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getLatitude() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setLatitude(String lat) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getLongitude() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setLongitude(String longitude) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
