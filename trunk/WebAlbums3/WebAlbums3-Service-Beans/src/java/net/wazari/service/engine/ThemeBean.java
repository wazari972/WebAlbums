/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.util.List;
import java.util.logging.Logger;
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
    private static final Logger log = Logger.getLogger(ThemeBean.class.getName());
    
    @EJB
    ThemeFacadeLocal themeDAO;

    @Override
    public XmlBuilder getThemeList(ViewSession vSession) {
        XmlBuilder output = new XmlBuilder("index");
        //afficher la liste des themes

        List<Theme> lst = themeDAO.findAll();
        for (Theme enrTheme : lst) {
            output.add(new XmlBuilder("theme", enrTheme.getNom()).addAttribut("id", enrTheme.getId()));
        }

        return output;
    }
}
