/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import net.wazari.service.*;
import javax.ejb.Stateless;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Theme;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Stateless
public class ThemeBean implements ThemeLocal {

    @EJB
    ThemeFacadeLocal themeDAO;

    @Override
    public XmlBuilder treatVOID(ViewSession vSession) {
        XmlBuilder output = new XmlBuilder("index");
        //afficher la liste des themes

        List<Theme> lst = themeDAO.findAll();
        for (Theme enrTheme : lst) {
            output.add(new XmlBuilder("theme", enrTheme.getNom()).addAttribut("id", enrTheme.getId()));
        }

        if (vSession.getConfiguration().lightenDb()) {
            output.add(new XmlBuilder("reload"));
        }


        return output.validate();
    }
}
