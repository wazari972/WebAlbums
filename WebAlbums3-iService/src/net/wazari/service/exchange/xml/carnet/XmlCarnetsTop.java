/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlCarnetsTop {
    public XmlCarnetsTop() {
        carnet = new LinkedList<XmlCarnet>() ;
    }
    public final List<XmlCarnet> carnet ;

}
