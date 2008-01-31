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
    String getBirthdate();

    Integer getTag();

    Tag getTag1();

    void setBirthdate(String birthdate);
}
