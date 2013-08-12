/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Theme;
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionTheme;
import net.wazari.service.exchange.ViewSessionLogin.ViewSessionTempTheme;
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
    @EJB
    private AlbumFacadeLocal albumDAO;
    
    private void sortAlbumAge(ViewSessionTempTheme vSession, List<Theme> themes) {
        Theme origTheme = vSession.getVSession().getTheme();
        final Map<Theme, String> themeDates = new HashMap<Theme, String>();
        for (Theme enrTheme : themes) {
            //race condition possible here
            vSession.setTempTheme(enrTheme);
            Album enrAlbum = albumDAO.loadLastAlbum(vSession.getVSession(), AlbumFacadeLocal.Restriction.THEME_ONLY);
            
            if (enrAlbum != null) {
                themeDates.put(enrTheme, enrAlbum.getDate());
            } else {
                themeDates.put(enrTheme, "0000-00-00");
            }
        }
        vSession.setTempTheme(origTheme);
        
        Collections.sort(themes, new Comparator<Theme>() {
            public int compare(Theme t, Theme t1) {
                String tStr = themeDates.get(t);
                String t1Str = themeDates.get(t1);
                return tStr.compareTo(t1Str);
            }
        });
        Collections.reverse(themes);
    }
    
    @Override
    public XmlThemeList getThemeListSimple(ViewSession vSession) {
        XmlThemeList output = new XmlThemeList();

        List<Theme> lst = themeDAO.findAll();
        daoToXmlService.convertThemes(vSession, lst, output);

        return output;
    }
    public XmlThemeList getThemeList(ViewSessionTheme vSession, Sort order) {
        XmlThemeList output = new XmlThemeList();

        List<Theme> lst = themeDAO.findAll();
        if (lst.isEmpty()) {
            webService.populateEntities() ;
            lst = themeDAO.findAll();
        }
        
        switch(order) {
            case ALBUM_AGE:
                sortAlbumAge(vSession.getTempThemeSession(), lst);
                break;
            case REVERSE:
                Collections.reverse(lst);
                break;
            case NOPE:
            default: 
        }
        daoToXmlService.convertThemes(vSession.getVSession(), lst, output);

        return output;
    }
}
