/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Type;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface WebPageLocal {
    public static class Bornes {
        public int first ;
        public int last ;
        public int nbPages ;
        public int page ;
    }
    Bornes calculBornes(Type type, Integer page, Integer asked, int size);

    XmlBuilder displayListB(Mode mode, ViewSession vSession, Box box) throws WebAlbumsServiceException;

    XmlBuilder displayListBN(Mode mode, ViewSession vSession, Box box, String name) throws WebAlbumsServiceException;

    @SuppressWarnings(value = "unchecked")
    XmlBuilder displayListDroit(Integer right, Integer albmRight) throws WebAlbumsServiceException;

    XmlBuilder displayListIBT(Mode mode, ViewSession vSession, int id, Box box, Type type) throws WebAlbumsServiceException;

    @SuppressWarnings(value = "unchecked")
    XmlBuilder displayListIBTNI(Mode mode, ViewSession vSession, int id, Box box, Type type, String name, String info) throws WebAlbumsServiceException;

    XmlBuilder displayListLB(Mode mode, ViewSession vSession, List<Integer> ids, Box box) throws WebAlbumsServiceException;

    @SuppressWarnings(value = "unchecked")
    XmlBuilder displayListLBNI(Mode mode, ViewSession vSession, List<Integer> ids, Box box, String name, String info) throws WebAlbumsServiceException;

    XmlBuilder displayMapInBody(ViewSession vSession, String name, String info) throws WebAlbumsServiceException;

    XmlBuilder displayMapInScript(ViewSession vSession, String name, String info) throws WebAlbumsServiceException;

    EditMode getNextEditionMode(ViewSession vSession);

    XmlBuilder xmlPage(XmlBuilder from, Bornes bornes) ;

    XmlBuilder xmlLogin(ViewSession vSession) ;

    XmlBuilder xmlAffichage(ViewSession vSession) ;
}
