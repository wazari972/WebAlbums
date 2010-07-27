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
import net.wazari.common.util.XmlBuilder;

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
    public XmlBuilder getThemeList(ViewSession vSession) {
        XmlBuilder output = new XmlBuilder("index");
        //afficher la liste des themes

        List<Theme> lst = themeDAO.findAll();
        if (lst.isEmpty()) {
            webService.populateEntities() ;
            lst = themeDAO.findAll();
        }
        for (Theme enrTheme : lst) {
            output.add(new XmlBuilder("theme", enrTheme.getNom()).addAttribut("id", enrTheme.getId()));
        }

        return output;
    }
}
