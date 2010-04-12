/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevin
 */
@Local
public interface UtilisateurFacadeLocal {

    void create(Utilisateur utilisateur);

    void edit(Utilisateur utilisateur);

    void remove(Utilisateur utilisateur);

    Utilisateur find(Object id);

    List<Utilisateur> findAll();

    public Utilisateur loadByName(String name) ;
    
    public Utilisateur loadUserOutside(int albmId) ;

    public List<Utilisateur> loadUserInside(int id) ;
}
