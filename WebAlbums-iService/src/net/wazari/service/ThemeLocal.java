/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionTheme;
import net.wazari.service.exchange.xml.XmlThemeList;

/**
 *
 * @author kevin
 */
@Local
public interface ThemeLocal {
    enum Sort {NOPE, REVERSE, ALBUM_AGE}
    
    XmlThemeList getThemeList(ViewSessionTheme vSession, Sort order) ;
    
    XmlThemeList getThemeListSimple(ViewSession vSession) ;
}
