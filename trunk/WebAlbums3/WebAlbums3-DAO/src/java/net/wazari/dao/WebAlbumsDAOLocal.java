/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.text.SimpleDateFormat;
import javax.ejb.Local;

/**
 *
 * @author kevin
 */
@Local
public interface WebAlbumsDAOLocal {

    static final SimpleDateFormat DATE_STANDARD =
            new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat DATE_HEURE =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    String processListID(ServiceSession session, String rq, boolean restrict);

    String restrictToAlbumsAllowed(ServiceSession session, String album);

    String restrictToPhotosAllowed(ServiceSession session, String photo);

    String restrictToThemeAllowed(ServiceSession session, String album);
}
