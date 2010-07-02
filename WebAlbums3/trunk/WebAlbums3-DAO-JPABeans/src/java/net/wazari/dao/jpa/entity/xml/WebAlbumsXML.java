/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.jpa.entity.*;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
public class WebAlbumsXML {
    @XmlElement private List<JPATheme> themes ;
    @XmlElement private List<JPAUtilisateur> utilisateurs ;
    @XmlElement private List<JPAAlbum> albums ;
    @XmlElement private List<JPAPhoto> photos ;
    @XmlElement private List<JPATag> tags ;
    @XmlElement private List<JPAGeolocalisation> geolicalisation ;
    @XmlElement private List<JPATagTheme> tagThemes ;
    @XmlElement private List<JPATagPhoto> tagPhoto ;

    public WebAlbumsXML(){}
    
    public WebAlbumsXML(List<Theme> themes, List<Utilisateur> utilisateurs, List<Album> albums, List<Photo> photos, 
            List<Tag> tags, List<TagTheme> tagThemes, List<TagPhoto> tagPhoto)
    {
        this.themes = (List) themes;
        this.utilisateurs = (List) utilisateurs;
        this.albums = (List) albums;
        this.photos = (List) photos;
        this.tags = (List) tags;
        this.geolicalisation = (List) geolicalisation;
        this.tagThemes = (List) tagThemes;
        this.tagPhoto = (List) tagPhoto;
    }

    public List<JPAAlbum> getAlbums() {
        return albums;
    }

    public List<JPAGeolocalisation> getGeolicalisation() {
        return geolicalisation;
    }

    public List<JPAPhoto> getPhotos() {
        return photos;
    }

    public List<JPATagPhoto> getTagPhoto() {
        return tagPhoto;
    }

    public List<JPATagTheme> getTagThemes() {
        return tagThemes;
    }

    public List<JPATag> getTags() {
        return tags;
    }

    public List<JPATheme> getThemes() {
        return themes;
    }

    public List<JPAUtilisateur> getUtilisateurs() {
        return utilisateurs;
    }
    
}
