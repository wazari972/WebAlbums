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
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Utilisateur")
@NamedQueries({@NamedQuery(name = "JPAUtilisateur.findAll", query = "SELECT j FROM JPAUtilisateur j")})
public class JPAUtilisateur implements Utilisateur, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "droit", fetch = FetchType.LAZY)
    private List<JPAAlbum> jPAAlbumList;

    public JPAUtilisateur() {
    }

    public JPAUtilisateur(Integer id) {
        this.id = id;
    }

    public JPAUtilisateur(Integer id, String nom) {
        this.id = id;
        this.nom = nom;
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
        if (!(object instanceof JPAUtilisateur)) {
            return false;
        }
        JPAUtilisateur other = (JPAUtilisateur) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAUtilisateur[id=" + id + "]";
    }

}
