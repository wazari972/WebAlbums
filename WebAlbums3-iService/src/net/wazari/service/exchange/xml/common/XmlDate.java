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
public class XmlDate {
    public String year;
    public String month;
    public String day;
}
