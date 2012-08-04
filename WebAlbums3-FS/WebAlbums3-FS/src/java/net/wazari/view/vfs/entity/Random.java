/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbumYear;
import net.wazari.service.exchange.xml.album.XmlAlbumYears;
import net.wazari.service.exchange.xml.photo.XmlPhotoRandom;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import net.wazari.view.vfs.entity.TagDirectory.WhatTag;

/**
 *
 * @author kevin
 */
public class Random extends SDirectory implements ADirectory {
    @File(name="random.jpg")
    public Photo photo;
    
    @File(name="random_album")
    @Directory
    public Album album;
    
    @File(name="By Years")
    @Directory
    public RandYears years;
    
    private final Theme theme;
    private final Launch aThis;
    
    public Random(net.wazari.dao.entity.Theme theme, Launch aThis) throws WebAlbumsServiceException {
        this.theme = theme;
        this.aThis = aThis;
    }

    @Override
    public void load() throws Exception {
        Session session = new Session(theme);
        
        XmlPhotoRandom rand = aThis.photoService.treatRANDOM(session);
        photo = new Photo(rand.details);
        years = new RandYears(theme, aThis);
    }
    
    @Override
    public void rmdir() {
        try {
            load();
        } catch (Exception e) {
        }
    }

    @Override
    public void unload() {
    }

    public static class RandYear extends SDirectory implements ADirectory {
        @Directory
        @File
        public List<Album> albums = new LinkedList<Album>();
        
        private final Theme theme;
        private final Launch aThis;
        private final String name;
        private final List<XmlAlbum> thealbums;

        public RandYear(Integer year, List<XmlAlbum> thealbums, net.wazari.dao.entity.Theme theme, Launch aThis) throws WebAlbumsServiceException {
            this.name = year.toString();
            this.thealbums = thealbums;
            
            this.theme = theme;
            this.aThis = aThis;
        }
        
        @Override
        public String getShortname() {
            return name;
        }

        @Override
        public void load() throws Exception {
            for (XmlAlbum anAlbum : thealbums) {
                albums.add(new Album(anAlbum.albmDate, anAlbum.name, anAlbum.id, theme, aThis));
            }
        }

        @Override
        public void unload() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static class RandYears implements ADirectory {
        @Directory
        @File
        public List<RandYear> years = new LinkedList<RandYear>();
        
        private final Theme theme;
        private final Launch aThis;

        public RandYears(net.wazari.dao.entity.Theme theme, Launch aThis) throws WebAlbumsServiceException {
            this.theme = theme;
            this.aThis = aThis;
        }

        @Override
        public void load() throws Exception {
            Session session = new Session(theme);
            XmlAlbumYears theYears = aThis.albumService.treatYEARS(session);
            
            for (XmlAlbumYear year : theYears.year) {
                years.add(new RandYear(year.year, year.album, theme, aThis));
            }
        }

        @Override
        public void unload() {
        }
    }
}

