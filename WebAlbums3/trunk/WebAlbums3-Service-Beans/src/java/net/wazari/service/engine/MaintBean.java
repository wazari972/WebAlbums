package net.wazari.service.engine;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import javax.ejb.Stateless;

import net.wazari.dao.MaintFacadeLocal;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.ViewSessionMaint.MaintAction;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.MaintLocal;

@Stateless
public class MaintBean implements MaintLocal {

    private static String getPath(Configuration conf) {
        return conf.getBackupPath();
    }
    @EJB MaintFacadeLocal mainBean ;

    public XmlBuilder treatMAINT(ViewSessionMaint vSession) {
        MaintAction action = vSession.getMaintAction();

        XmlBuilder output = new XmlBuilder("maint");
        if (MaintAction.FULL_IMPORT == action) {
            mainBean.treatFullImport(getPath(vSession.getConfiguration()));

        } else if (MaintAction.EXPORT_XML == action) {
            mainBean.treatExportXML(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_XML == action) {
            mainBean.treatImportXML(getPath(vSession.getConfiguration()));
        } else if (MaintAction.TRUNCATE_XML == action) {
            mainBean.treatTruncateXML(getPath(vSession.getConfiguration()));

        } else if (MaintAction.EXPORT_DDL == action) {
            mainBean.treatExportDDL(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_DDL == action) {
            mainBean.treatImportDDL();

        } else if (MaintAction.UPDATE == action) {

        } else {
            output.add("action", "FULL_IMPORT");

            output.add("action", "IMPORT_XML");
            output.add("action", "EXPORT_XML");
            output.add("action", "TRUNCATE_XML");

            output.add("action", "EXPORT_DDL");
            output.add("action", "IMPORT_DDL");

            output.add("action", "UPDATE");
        }
        return output.validate();
    }

    

    public static void treatUpdate(HttpServletRequest request, XmlBuilder output) {
        if (true) {
            return;
        }

    }
}
