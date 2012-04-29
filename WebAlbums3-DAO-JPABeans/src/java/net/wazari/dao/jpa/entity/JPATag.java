/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.*;
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
@Table(name = "Tag",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Nom"})}
)
public class JPATag implements Tag, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATag.class.getName());

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
    @Column(name = "Nom", nullable = false, length = 40)
    private String nom;

    @Basic(optional = false)
    @Column(name = "TagType", nullable = false)
    private int tagType;

    @Basic(optional = true)
    @Column(name = "IsMinor", nullable = true)
    private Boolean minor;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.LAZY)
    private List<JPATagTheme> jPATagThemeList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.LAZY)
    private List<JPATagPhoto> jPATagPhotoList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "jPATag", fetch = FetchType.LAZY)
    private JPAGeolocalisation jPAGeolocalisation;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "jPATag", fetch = FetchType.LAZY)
    private JPAPerson jPAPerson;
        
    @JoinColumn(name = "Parent", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private JPATag parent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    private List<JPATag> sonList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tagAuthor", fetch = FetchType.LAZY)
    private List<JPAPhoto> authorList;
    
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

    @XmlAttribute
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @XmlElement
    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setNom(String nom) {
        this.nom = nom;
    }

    @XmlAttribute
    @Override
    public Boolean isMinor() {
        return minor;
    }

    @Override
    public void setMinor(Boolean minor) {
        this.minor = minor;
    }
    
    @XmlAttribute
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

    
    @XmlElement(name="geo")
    @Override
    public JPAGeolocalisation getGeolocalisation() {
        return jPAGeolocalisation;
    }

    @Override
    public void setGeolocalisation(Geolocalisation jPAGeolocalisation) {
        this.jPAGeolocalisation = (JPAGeolocalisation) jPAGeolocalisation;
    }
    
    @XmlElement(name="person")
    @Override
    public JPAPerson getPerson() {
        return jPAPerson;
    }

    @Override
    public void setPerson(Person jPAPerson) {
        this.jPAPerson = (JPAPerson) jPAPerson;
    }

    @Override
    public Tag getParent() {
        return parent;
    }
    
    @XmlAttribute
    public Integer getParentId() {
        if (parent == null)
            return null;
        else
            return parent.getId();
    }

    @Override
    public void setParent(Tag parent) {
        this.parent = (JPATag) parent;
    }
    
    @Override
    public List<Tag> getSonList() {
        return (List) sonList;
    }

    @Override
    public void setSonList(List<Tag> sonList) {
        this.sonList = (List) sonList;
    }
    
    @Override
    public List<Photo> getAuthorList() {
        return (List) authorList;
    }

    @Override
    public void setAuthorList(List<Photo> authorList) {
        this.authorList = (List) authorList;
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
