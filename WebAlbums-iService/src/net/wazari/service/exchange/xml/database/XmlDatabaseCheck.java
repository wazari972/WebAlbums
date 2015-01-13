/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange.xml.database;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlDatabaseCheck extends XmlInfoException{
    public XmlDatabaseCheck() {
        this.files = new HashMap<>();
    }
    public Map<String, String> files ;
}
