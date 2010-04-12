/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

import javax.ejb.Local;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface UserLocal {

    XmlBuilder treatUSR(ViewSession vSession);

    boolean saveUser(ViewSession vSession,
            Utilisateur enrUser,
            String passwd,
            boolean asThemeManager);
}
