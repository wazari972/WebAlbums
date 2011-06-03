/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange.xml.database;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlDatabaseStats extends XmlInfoException {
    public static class XmlDatabaseStatsTheme {
        public XmlDatabaseStatsTheme(){}
        public XmlDatabaseStatsTheme(String name) {
            this.name = name;
        }
        public String name;
        public int photos = 0;
        public int albums = 0;
        public int tags = 0;
    }
    public XmlDatabaseStats() {
        theme = new LinkedList();
    }
    public List<XmlDatabaseStatsTheme> theme;
}
