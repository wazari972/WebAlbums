/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Theme")
@NamedQueries({@NamedQuery(name = "JPATheme.findAll", query = "SELECT j FROM JPATheme j")})
public class JPATheme implements Theme, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;
    @Basic(optional = false)
    @Column(name = "Password", nullable = false, length = 100)
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "theme", fetch = FetchType.LAZY)
    private List<JPATagTheme> jPATagThemeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "theme", fetch = FetchType.LAZY)
    private List<JPAAlbum> jPAAlbumList;

    public JPATheme() {
    }

    public JPATheme(Integer id) {
        this.id = id;
    }

    public JPATheme(Integer id, String nom, String password) {
        this.id = id;
        this.nom = nom;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<TagTheme> getTagThemeList() {
        return (List) jPATagThemeList;
    }

    public void setTagThemeList(List<TagTheme> jPATagThemeList) {
        this.jPATagThemeList = (List) jPATagThemeList;
    }

    public List<Album> getAlbumList() {
        return (List) jPAAlbumList;
    }

    public void setAlbumList(List<Album> jPAAlbumList) {
        this.jPAAlbumList = (List) jPAAlbumList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JPATheme)) {
            return false;
        }
        JPATheme other = (JPATheme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATheme[id=" + id + "]";
    }

}
