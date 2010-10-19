/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlFrom {
    public String name;
    public Integer album;
    public Integer albmCount;
    public List<Integer> tagAsked;

}
