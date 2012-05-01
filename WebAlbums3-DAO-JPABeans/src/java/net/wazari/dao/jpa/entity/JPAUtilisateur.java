/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Utilisateur;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "Utilisateur",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Nom"})}
)
public class JPAUtilisateur implements Utilisateur, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAUtilisateur.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated",
                      strategy="net.wazari.dao.jpa.entity.idGenerator.UseIdOrGenerate"
    )
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;

    @OneToMany(mappedBy = "droit", fetch = FetchType.LAZY)
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

    @XmlAttribute
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @XmlValue
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
