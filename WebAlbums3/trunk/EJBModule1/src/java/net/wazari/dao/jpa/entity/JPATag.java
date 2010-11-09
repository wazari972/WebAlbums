/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "Tag",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Nom"})}
)
public class JPATag implements  Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATag.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Id
    @Basic(optional = false)
    private Integer id;

    @XmlElement
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 40)
    private String nom;

    @XmlAttribute
    @Basic(optional = false)
    @Column(name = "TagType", nullable = false)
    private int tagType;

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.LAZY)
    private List<JPATagTheme> jPATagThemeList;

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.LAZY)
    private List<JPATagPhoto> jPATagPhotoList;

    @XmlElement
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "jPATag", fetch = FetchType.LAZY)
    private JPAGeolocalisation jPAGeolocalisation;

    @XmlTransient
    @JoinColumn(name = "Parent", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private JPATag parent;


    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    private List<JPATag> sonList;

    public JPATag() {
    }

    public JPATag(Integer id) {
        this.id = id;
    }

    public JPATag(Integer id, String nom, int tagType) {
        this.id = id;
        this.nom = nom;
        this.tagType = tagType;
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

    
    public int getTagType() {
        return tagType;
    }

    
    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    
    public List<JPATagTheme> getTagThemeList() {
        return (List) jPATagThemeList;
    }

    
    public void setTagThemeList(List<JPATagTheme> jPATagThemeList) {
        this.jPATagThemeList = (List) jPATagThemeList;
    }

    
    public List<JPATagPhoto> getTagPhotoList() {
        return (List) jPATagPhotoList;
    }

    
    public void setTagPhotoList(List<JPATagPhoto> jPATagPhotoList) {
        this.jPATagPhotoList = (List) jPATagPhotoList;
    }

    
    public JPAGeolocalisation getGeolocalisation() {
        return jPAGeolocalisation;
    }

    
    public void setGeolocalisation(JPAGeolocalisation jPAGeolocalisation) {
        this.jPAGeolocalisation = (JPAGeolocalisation) jPAGeolocalisation;
    }

    
    public JPATag getParent() {
        return parent;
    }

    
    public void setParent(JPATag parent) {
        this.parent = (JPATag) parent;
    }
    
    
    public List<JPATag> getSonList() {
        return (List) sonList;
    }

    
    public void setSonList(List<JPATag> sonList) {
        this.sonList = (List) sonList;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        //TODO understand the difference between other.id and other.getId() ;
        if (!(object instanceof JPATag)) {
            return false;
        }
        JPATag other = (JPATag) object;

        if ((this.id == null && other.getId() != null) || (this.id != null && !this.id.equals(other.getId()))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATag[id=" +getId()+ "]";
    }

}
