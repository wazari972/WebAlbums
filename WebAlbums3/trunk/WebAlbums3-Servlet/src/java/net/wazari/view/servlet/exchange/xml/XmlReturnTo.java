/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlReturnTo {
    public String name;
    public Integer count;
    public Integer album;
    public Integer albmCount;
    
    public Integer page;
    public Integer[] tagsAsked;

}
