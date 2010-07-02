/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class JPAUtilisateur implements Utilisateur, Serializable {
    private static final Logger log = Logger.getLogger(JPAUtilisateur.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public List<Album> getAlbumList() {
        return (List) jPAAlbumList;
    }

    @Override
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
        return "net.wazari.dao.jpa.entity.JPAUtilisateur[id=" + id +": "+getNom()+ "]";
    }
}
