package net.wazari.service.engine;

import java.util.Arrays;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import javax.ejb.Stateless;

import net.wazari.dao.MaintFacadeLocal;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.ViewSessionMaint.MaintAction;
import net.wazari.service.MaintLocal;
import net.wazari.service.exchange.xml.XmlMaint;

@Stateless
public class MaintBean implements MaintLocal {

    private static String getPath(Configuration conf) {
        return conf.getBackupPath();
    }
    @EJB MaintFacadeLocal maintDAO ;

    public XmlMaint treatMAINT(ViewSessionMaint vSession) {
        MaintAction action = vSession.getMaintAction();

        XmlMaint output = new XmlMaint();
        if (MaintAction.EXPORT_XML == action) {
            maintDAO.treatExportXML(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_XML == action) {
            maintDAO.treatImportXML(vSession.getConfiguration().wantsProtectDB(), getPath(vSession.getConfiguration()));
        } else if (MaintAction.TRUNCATE_DB == action) {
            maintDAO.treatTruncateDB(vSession.getConfiguration().wantsProtectDB());
        } else if (MaintAction.PRINT_STATS == action) {
            maintDAO.treatDumpStats();

        } else if (MaintAction.UPDATE_DAO == action) {
            maintDAO.treatUpdate();
        } else {
            for (MaintAction act : Arrays.asList(MaintAction.values())) {
                output.actions.add(act.toString());
            }
        }
        return output ;
    }
}
