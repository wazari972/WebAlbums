/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.Person;
import net.wazari.dao.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "Person")
public class JPAPerson implements Person, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAGeolocalisation.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "Tag", nullable = false)
    private Integer id;
    
    @JoinColumn(name = "Tag", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private JPATag tag;

    @Basic(optional = true)
    @Column(name = "Birthdate", nullable = true, length = 10)
    private String birthdate = null;
    
    @Basic(optional = true)
    @Column(name = "Contact", nullable = true, length = 100)
    private String contact;

    public JPAPerson() {
    }

    @Override
    public Tag getTag() {
        return (JPATag) tag;
    }
    
    @Override
    public void setTag(Tag tag) {
        this.tag = (JPATag) tag;
        this.id = tag.getId();
    }

    @XmlAttribute
    @Override
    public String getBirthdate() {
        return birthdate;
    }

    @Override
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    
    @XmlAttribute
    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tag != null ? tag.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JPAPerson)) {
            return false;
        }
        JPAPerson other = (JPAPerson) object;
        if ((this.tag == null && other.tag != null) || (this.tag != null && !this.tag.equals(other.tag))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAPerson[tag=" + tag + "("+id+")]";
    }

}
