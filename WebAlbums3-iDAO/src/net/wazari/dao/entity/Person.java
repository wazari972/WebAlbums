/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.entity;

/**
 *
 * @author kevin
 */
public interface Person {
    Tag getTag();
    void setTag(Tag tag);
            
    String getBirthdate();
    void setBirthdate(String birthdate);
    
    String getContact();
    void setContact(String contact);
}
