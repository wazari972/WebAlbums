/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author kevin
 */
@Entity
@Table(name = "Theme")
@NamedQueries({@NamedQuery(name = "Theme.findAll", query = "SELECT t FROM Theme t"), @NamedQuery(name = "Theme.findById", query = "SELECT t FROM Theme t WHERE t.id = :id"), @NamedQuery(name = "Theme.findByNom", query = "SELECT t FROM Theme t WHERE t.nom = :nom"), @NamedQuery(name = "Theme.findByPassword", query = "SELECT t FROM Theme t WHERE t.password = :password")})
public class Theme implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom")
    private String nom;
    @Basic(optional = false)
    @Column(name = "Password")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "theme")
    private List<TagTheme> tagThemeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "theme")
    private List<Album> albumList;

    public Theme() {
    }

    public Theme(Integer id) {
        this.id = id;
    }

    public Theme(Integer id, String nom, String password) {
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
        return tagThemeList;
    }

    public void setTagThemeList(List<TagTheme> tagThemeList) {
        this.tagThemeList = tagThemeList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
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
        if (!(object instanceof Theme)) {
            return false;
        }
        Theme other = (Theme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Theme[id=" + id + "]";
    }

}
