/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import net.wazari.service.*;
import javax.ejb.Stateless;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Theme;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.XmlTheme;
import net.wazari.service.exchange.xml.XmlThemes;

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

    @Override
    public XmlThemes getThemeList(ViewSession vSession) {
        XmlThemes output = new XmlThemes();
        //afficher la liste des themes

        List<Theme> lst = themeDAO.findAll();
        if (lst.isEmpty()) {
            webService.populateEntities() ;
            lst = themeDAO.findAll();
        }
        for (Theme enrTheme : lst) {
            output.themes.add(new XmlTheme(enrTheme));
        }

        return output;
    }
}
