/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevin
 */
@Local
public interface ThemeFacadeLocal {

    void create(Theme theme);

    void edit(Theme theme);

    void remove(Theme theme);

    Theme find(Object id);

    List<Theme> findAll();

    public Theme loadByName(String themeName) ;
}
