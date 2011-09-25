/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Person;
import net.wazari.dao.entity.Tag;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface PersonFacadeLocal {

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Person newPerson(Tag enrTag) ;
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void create(Person person);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void edit(Person person);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void remove(Person person);
}
