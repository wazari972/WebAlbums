/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.ejb.Local;
import net.wazari.dao.entity.Person;

/**
 *
 * @author kevin
 */
@Local
public interface PersonFacadeLocal {
    Person newPerson() ;
    
    void create(Person person);

    void edit(Person person);

    void remove(Person person);
}
