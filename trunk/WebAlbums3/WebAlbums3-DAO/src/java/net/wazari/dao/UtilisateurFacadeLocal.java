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
    final static String ADMIN_ROLE = "ADMIN" ;
    final static String VIEWER_ROLE = "VIEWER" ;

    void create(Utilisateur utilisateur);

    void edit(Utilisateur utilisateur);

    void remove(Utilisateur utilisateur);

    Utilisateur loadByName(String name) ;
    
    Utilisateur loadUserOutside(int albmId) ;

    List<Utilisateur> loadUserInside(int id) ;

    Utilisateur find(Integer droit);

    List<Utilisateur> findAll();
}
