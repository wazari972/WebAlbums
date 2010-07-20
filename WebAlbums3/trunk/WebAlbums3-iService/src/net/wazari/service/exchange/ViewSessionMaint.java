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
    enum MaintAction {
        EXPORT_XML, IMPORT_XML,
        UPDATE
    ,   UPDATE_DAO, PRINT_STATS, TRUNCATE_DB}

    MaintAction getMaintAction() ;
}
