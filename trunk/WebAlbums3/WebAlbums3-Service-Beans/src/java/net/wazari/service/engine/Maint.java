package net.wazari.service.engine;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;


import javax.ejb.Stateless;





import net.wazari.dao.MaintFacadeLocal;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.ViewSessionMaint.MaintAction;
import net.wazari.util.XmlBuilder;

@Stateless
public class Maint {

    private static String getPath(Configuration conf) {
        return conf.getSourcePath() + conf.getData() + conf.getSep();
    }
    @EJB MaintFacadeLocal mainBean ;

    public XmlBuilder treatMAINT(ViewSessionMaint vSession) {
        MaintAction action = vSession.getMaintAction();

        String param = vSession.getParam();
        String value = vSession.getValue();

        XmlBuilder output = new XmlBuilder("maint");
        if (MaintAction.FULL_IMPORT == action) {
            mainBean.treatFullImport(getPath(vSession.getConfiguration()), !vSession.getConfiguration().isSgbdHsqldb());

        } else if (MaintAction.EXPORT_XML == action) {
            mainBean.treatExportXML(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_XML == action) {
            mainBean.treatImportXML(getPath(vSession.getConfiguration()), !vSession.getConfiguration().isSgbdHsqldb());
        } else if (MaintAction.TRUNCATE_XML == action) {
            mainBean.treatTruncateXML(getPath(vSession.getConfiguration()), !vSession.getConfiguration().isSgbdHsqldb());

        } else if (MaintAction.EXPORT_DDL == action) {
            mainBean.treatExportDDL(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_DDL == action) {
            mainBean.treatImportDDL();

        } else if (MaintAction.UPDATE_BOOL == action) {
            boolean ret = vSession.getConfiguration().updateBoolParam(param, value);
            output.add("message", "updating " + param + " to " + value+"==>"+ret);

        } else if (MaintAction.UPDATE == action) {
        } else if (MaintAction.UPDATE_STR == action) {
            boolean ret = vSession.getConfiguration().updateStrParam(param, value);

            output.add("message", "updating " + param + " to " + value+"==>"+ret);
        } else {
            output.add("action", "FULL_IMPORT");

            output.add("action", "IMPORT_XML");
            output.add("action", "EXPORT_XML");
            output.add("action", "TRUNCATE_XML");

            output.add("action", "EXPORT_DDL");
            output.add("action", "IMPORT_DDL");

            output.add("action", "UPDATE");

            output.add("action", "UPDATE_BOOL&amp;param=VAL&amp;value=TRUE|FALSE");
            output.add("action", "UPDATE_STR&amp;param=VAL&amp;value=STR");
        }
        return output.validate();
    }

    

    public static void treatUpdate(HttpServletRequest request, XmlBuilder output) {
        if (true) {
            return;
        }

    }
}
