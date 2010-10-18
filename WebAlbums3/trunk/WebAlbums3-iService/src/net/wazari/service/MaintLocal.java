/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.xml.XmlMaint;

/**
 *
 * @author kevinpouget
 */
@Local
public interface MaintLocal {
    XmlMaint treatMAINT(ViewSessionMaint vSession);
}
