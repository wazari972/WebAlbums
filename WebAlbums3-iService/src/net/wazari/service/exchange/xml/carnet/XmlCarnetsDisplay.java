/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.XmlPage;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlCarnetsDisplay extends XmlInfoException{
    public XmlCarnetsDisplay() {
        carnet = new LinkedList<XmlCarnet>() ;
    }
    public XmlCarnetsDisplay(int length) {
        carnet = new ArrayList<XmlCarnet>(length) ;
    }
    public final List<XmlCarnet> carnet ;
    public XmlPage page;
}
