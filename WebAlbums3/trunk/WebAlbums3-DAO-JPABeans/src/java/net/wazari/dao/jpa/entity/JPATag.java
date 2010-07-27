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
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.entity.TagTheme;
import org.hibernate.annotations.GenericGenerator;

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
public class JPATag implements Tag, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATag.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated",
                      strategy="net.wazari.dao.jpa.entity.idGenerator.UseIdOrGenerate"
    )
    @Column(name = "ID", nullable = false)
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
    public int getTagType() {
        return tagType;
    }

    @Override
    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    @Override
    public List<TagTheme> getTagThemeList() {
        return (List) jPATagThemeList;
    }

    @Override
    public void setTagThemeList(List<TagTheme> jPATagThemeList) {
        this.jPATagThemeList = (List) jPATagThemeList;
    }

    @Override
    public List<TagPhoto> getTagPhotoList() {
        return (List) jPATagPhotoList;
    }

    @Override
    public void setTagPhotoList(List<TagPhoto> jPATagPhotoList) {
        this.jPATagPhotoList = (List) jPATagPhotoList;
    }

    @Override
    public JPAGeolocalisation getGeolocalisation() {
        return jPAGeolocalisation;
    }

    @Override
    public void setGeolocalisation(Geolocalisation jPAGeolocalisation) {
        this.jPAGeolocalisation = (JPAGeolocalisation) jPAGeolocalisation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
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

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATag[id=" +getId()+ "]";
    }

}
