/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;

/**
 *
 * @author kevinpouget
 */
public interface ImportExporter {

    void exportXml(String path) throws DatabaseFacadeLocalException;

    void importXml(String path) throws DatabaseFacadeLocalException;

    void truncateDb();

}
