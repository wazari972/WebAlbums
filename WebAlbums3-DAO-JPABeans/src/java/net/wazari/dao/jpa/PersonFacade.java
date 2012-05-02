/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Person;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.jpa.entity.JPAPerson;

/**
 *
 * @author kevin
 */
@Stateless
public class PersonFacade implements PersonFacadeLocal {
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void create(Person person) {
        em.persist(person);
    }

    @Override
    public void edit(Person person) {
        em.merge(person);
    }

    @Override
    public void remove(Person person) {
        em.remove(em.merge(person));
    }

    @Override
    public Person newPerson() {
        return new JPAPerson();
    }
}
