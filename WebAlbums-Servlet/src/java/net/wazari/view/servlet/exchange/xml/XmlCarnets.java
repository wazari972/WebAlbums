/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import net.wazari.service.exchange.xml.carnet.XmlCarnetEdit;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsDisplay;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsTop;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
public class XmlCarnets extends XmlInfoException {
    public XmlCarnetEdit edit;
    public XmlCarnetsDisplay display;
    public XmlCarnetsTop topCarnets;

}
