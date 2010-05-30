package net.wazari.service.engine;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.service.ChoixLocal;
import net.wazari.service.UserLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession;

import net.wazari.util.XmlBuilder;

@Stateless
@DeclareRoles(UserLocal.VIEWER_ROLE)
public class ChoixBean implements ChoixLocal {
    @EJB
    private WebPageLocal webPageService ;

    private static final long serialVersionUID = 1L;

    @Override
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    public XmlBuilder displayChxScript(ViewSession vSession) throws WebAlbumsServiceException {
        XmlBuilder output = webPageService.displayMapInScript(vSession, "mapChoix",
                null);
        return output;
    }

    @Override
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    public XmlBuilder displayCHX(ViewSession vSession) throws WebAlbumsServiceException {
        XmlBuilder choix = new XmlBuilder("choix");

        XmlBuilder tagList;
        tagList = webPageService.displayListBN(Mode.TAG_USED, vSession,
                Box.MULTIPLE, "tagAsked");

        choix.add(tagList);

        XmlBuilder tagMap;
        tagMap = webPageService.displayMapInBody(vSession, "mapChoix",
                null);
        choix.add(tagMap);

        choix.validate();

        return choix;
    }
}
