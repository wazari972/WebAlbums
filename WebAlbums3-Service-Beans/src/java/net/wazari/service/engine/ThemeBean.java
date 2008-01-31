/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.util.Collections;
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
import net.wazari.service.exchange.xml.XmlThemeList;

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
    public XmlThemeList getThemeList(ViewSession vSession) {
        XmlThemeList output = new XmlThemeList();
        //afficher la liste des themes

        List<Theme> lst = themeDAO.findAll();
        if (lst.isEmpty()) {
            webService.populateEntities() ;
            lst = themeDAO.findAll();
        }
        Collections.reverse(lst);
        for (Theme enrTheme : lst) {
            XmlTheme theme = new XmlTheme() ;
            theme.id = enrTheme.getId() ;
            theme.name = enrTheme.getNom() ;
            theme.picture = enrTheme.getPicture();
            output.theme.add(theme);
        }

        return output;
    }
}
