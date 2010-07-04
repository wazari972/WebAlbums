package net.wazari.service.engine;

import java.util.Arrays;
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
    @EJB MaintFacadeLocal maintDAO ;

    public XmlBuilder treatMAINT(ViewSessionMaint vSession) {
        MaintAction action = vSession.getMaintAction();

        XmlBuilder output = new XmlBuilder("maint");
        if (MaintAction.EXPORT_XML == action) {
            maintDAO.treatExportXML(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_XML == action) {
            maintDAO.treatImportXML(getPath(vSession.getConfiguration()));

        } else if (MaintAction.UPDATE == action) {

        } else {
            for (MaintAction act : Arrays.asList(MaintAction.values())) {
                output.add("action", act);
            }
        }
        return output.validate();
    }
    
    public static void treatUpdate(HttpServletRequest request, XmlBuilder output) {
        if (true) {
            return;
        }

    }
}
