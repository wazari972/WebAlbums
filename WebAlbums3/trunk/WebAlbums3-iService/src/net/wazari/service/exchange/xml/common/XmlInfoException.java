/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlInfoException {
    public String exception;
    public String message;
}
