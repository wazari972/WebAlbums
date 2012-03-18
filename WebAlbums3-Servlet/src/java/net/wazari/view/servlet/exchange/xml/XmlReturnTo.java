/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlReturnTo {
    public String name;
    public Integer album;
    public Integer albmPage;
    
    public Integer page;
    public final List<Integer> tagsAsked = new LinkedList<Integer>();

}
