/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.exchange.ViewSessionMaint;

/**
 *
 * @author kevinpouget
 */
@Local
public interface MaintLocal {

    XmlBuilder treatMAINT(ViewSessionMaint vSession);

}
