/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.config;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlConfigModPers extends XmlInfoException {
    public String newBirthdate;
    public String newContact;
}
