/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Theme;
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.XmlThemeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
public class ThemeBean implements ThemeLocal {
    private static final Logger log = LoggerFactory.getLogger(ThemeBean.class.getName());
    
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private WebPageLocal webService ;
    @EJB
    private DaoToXmlBean daoToXmlService;
    
    @Override
    public XmlThemeList getThemeList(ViewSession vSession) {
        XmlThemeList output = new XmlThemeList();

        List<Theme> lst = themeDAO.findAll();
        if (lst.isEmpty()) {
            webService.populateEntities() ;
            lst = themeDAO.findAll();
        }
        
        Collections.reverse(lst);
        daoToXmlService.convertThemes(vSession, lst, output);

        return output;
    }
}
