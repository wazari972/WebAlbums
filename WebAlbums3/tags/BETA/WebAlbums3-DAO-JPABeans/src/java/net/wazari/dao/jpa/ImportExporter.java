/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

/**
 *
 * @author kevinpouget
 */
public interface ImportExporter {

    void exportXml(String path);

    void importXml(String path);

    void truncateDb();

}
