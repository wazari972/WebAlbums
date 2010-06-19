/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevinpouget
 */
public interface ViewSessionMaint extends ViewSession {

    String getParam();

    String getValue();
    enum MaintAction {
        FULL_IMPORT,
        EXPORT_XML, IMPORT_XML, TRUNCATE_XML,
        EXPORT_DDL, IMPORT_DDL,
        UPDATE_BOOL, UPDATE_STR, UPDATE
    }

    MaintAction getMaintAction() ;
}
