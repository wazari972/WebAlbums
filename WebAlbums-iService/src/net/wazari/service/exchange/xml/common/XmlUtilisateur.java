/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlUtilisateur {
    public XmlUtilisateur() {}
    public XmlUtilisateur(Utilisateur user) {}
}
