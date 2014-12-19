/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.wazari.service.UserLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.*;
import net.wazari.service.exchange.xml.XmlImage;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.view.servlet.exchange.ViewSessionImpl;
import net.wazari.view.servlet.exchange.xml.XmlWebAlbums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
public class DispatcherBean {

    private static final Logger log = LoggerFactory.getLogger(DispatcherBean.class.getCanonicalName());

    static {
        log.warn("WebAlbums3-Servlet is being loaded ... ");
        log.info("RootPath: {}", ConfigurationXML.getConf().getRootPath());
        
    }

    public DispatcherBean() {
    }
    @EJB private Index indexServlet;
    @EJB private Users userServlet;
    @EJB private Choix choixServlet;
    @EJB private Albums albumServlet;
    @EJB private Photos photoServlet;
    @EJB private Tags tagServlet;
    @EJB private Carnets carnetServlet;
    @EJB private Images imageServlet;
    @EJB private Database databaseServlet;
    @EJB private Config configServlet;
    @EJB private WebPageLocal webPageService;
    @EJB private UserLocal userService;
    
    public enum Page {
        PHOTO, IMAGE, USER, ALBUM, CONFIG, CHOIX, TAGS, VOID, PERIODE, 
        DATABASE, CARNET, BENCHMARK}

    public void treat(ServletContext context,
            Page page,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        
        
        request.setCharacterEncoding("utf-8");
        ViewSessionDispatcher vSession = new ViewSessionImpl(request, response, context);
        if (request.getParameter("logout") != null) {
            log.info("Logout and cleanup the session");
            request.logout();
            userService.cleanUpSession(vSession.getSessionLogin());
        }
        if (page != Page.USER) {
            if (!request.authenticate(response)) {
                throw new ServletException("Not authenticated ...");
            }
        }
        
        log.debug("============= <{}> =============", page);
        
        response.setContentType("text/xml");

        XmlWebAlbums output = new XmlWebAlbums();

        boolean isWritten = false;
        try {
            output.xslFile = "static/Display.xsl";
            switch (page) {
                case USER:
                    output.login = userServlet.treatLogin(vSession.getSessionLogin(), request, response);
                    if (output.login == null) {
                        return;
                    }
                break;
            case VOID:
                output.themes = indexServlet.treatVOID(vSession.getSessionTheme());
                break;
            case IMAGE:
                XmlImage img = imageServlet.treatIMG(vSession.getSessionImage());
                if (img == null) {
                    isWritten = true;
                } else {
                    output.image = img;
                }
                log.debug("IMAGE written? {}", isWritten);
                break;
            default: 
                log.debug("============= Login: {} =============", request.getUserPrincipal());
                String special = request.getParameter("special");
                String type    = request.getParameter("type");
                
                if (special != null) {
                    log.info("Special XSL-style ({})", special);
                    output.xslFile = "static/Empty.xsl";
                }
                log.debug("XSL-style{}", output.xslFile);
                //try to logon and set the thlaiestloleme
                if (vSession.getThemeId() != null) {
                    log.info("Try to logon");
                    boolean ret = userService.logon(vSession.getSessionLogin(), request);
                    log.debug("Logon result: {}", ret);
                }
                //from here on, the theme must be saved
                if (vSession.getTheme() == null) {
                    if (special == null) {
                        log.debug("Not logged in, not a special page, display VOID page");
                        output.themes = indexServlet.treatVOID(vSession.getSessionTheme());

                    } else {
                        output.isComplete = true;
                        log.debug("Not logged in, special request, nothing to display ...");
                    }
                } else {
                    log.debug("{} {}page", page, special != null ? special : "");
                    switch(page) {
                        case CHOIX:
                            if (!"JSON".equals(type)) {
                                output.choix = choixServlet.displayCHX(vSession.getSessionChoix());
                            } else {
                                XmlWebAlbumsList ret = choixServlet.displayChxJSON(vSession.getSessionChoix());
                                if (ret != null) {
                                    output.blob = ret.blob;
                                }
                                response.setContentType("text/javascript;charset=UTF-8");
                                output.isBlob = true;
                                output.isComplete = true;   
                            }
                            break;
                        case ALBUM:
                            output.albums = albumServlet.treatALBM(vSession.getSessionAlbum());
                            break;
                        case PHOTO:
                            if (!"JSON".equals(type)) {
                                output.photos = photoServlet.treatPHOTO(vSession.getSessionPhoto());
                            } else {
                                XmlWebAlbumsList ret = photoServlet.treatJsonPHOTO(vSession.getSessionAnAlbum());
                                if (ret != null) {
                                    output.blob = ret.blob;
                                }
                                response.setContentType("text/javascript;charset=UTF-8");
                                output.isBlob = true;
                                output.isComplete = true;
                            }
                            break;
                        case CONFIG:
                            output.config = configServlet.treatCONFIG(vSession.getSessionConfig());
                            break;
                        case TAGS:
                            output.tags = tagServlet.treatTAGS(vSession.getSessionTag());
                            break;
                        case CARNET:
                            if (!"JSON".equals(type)) {
                                output.carnets = carnetServlet.treatCARNETS(vSession.getSessionCarnet());
                            } else {
                                XmlWebAlbumsList ret = carnetServlet.treatJsonCARNET(vSession.getSessionCarnetSimple());
                                if (ret != null) {
                                    output.blob = ret.blob;
                                }
                                response.setContentType("text/javascript;charset=UTF-8");
                                output.isBlob = true;
                                output.isComplete = true;
                            }
                            break;
                        case DATABASE:
                            output.database = databaseServlet.treatDATABASE(vSession.getSessionDatabase());
                            break;
                        default: 
                            output.themes = indexServlet.treatVOID(vSession.getSessionTheme());
                    }
                }
            }
        } catch (WebAlbumsServiceException e) {
            log.warn("WebAlbumsServiceException", e);
        }
        log.debug("============= Footer (written:{}, complete:{})=============", new Object[]{isWritten, output.isComplete});
        
        String strTime = "nop";
        //String strTime = DurationFormatUtils.formatDuration(stopWatch.getTime(), "m'min' s's' S'ms'", false);
        if (!isWritten) {
            preventCaching(request, response);

            if (!output.isComplete) {
                output.loginInfo = webPageService.xmlLogin(vSession.getSessionLogin());
                output.affichage = webPageService.xmlAffichage(vSession);
                output.time = strTime;
            }
            doWrite(response, output);
        }

        log.debug("============= <{}/>: {} =============", new Object[]{page, strTime});
    }

    
    public interface ViewSessionDispatcher extends ViewSession {
        String getRawSpecial();   
        ViewSessionAlbum getSessionAlbum();
        ViewSession.ViewSessionChoix getSessionChoix();
        ViewSessionPhoto getSessionPhoto();
        ViewSessionPhoto.ViewSessionAnAlbum getSessionAnAlbum();
        ViewSessionCarnet.ViewSessionCarnetSimple getSessionCarnetSimple();
        ViewSessionLogin getSessionLogin();
        ViewSessionImages getSessionImage();
        ViewSessionTheme getSessionTheme();
        ViewSessionDatabase getSessionDatabase();
        ViewSession.SessionConfig getLocalSessionConfig();
        ViewSessionConfig getSessionConfig();
        ViewSessionTag getSessionTag();
        ViewSessionCarnet getSessionCarnet();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    private static void doWrite(HttpServletResponse response, XmlWebAlbums output) {
        try {
            try (PrintWriter sortie = response.getWriter()) {
                if (!output.isBlob) {
                    if (!output.isComplete) {
                        sortie.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                        sortie.println("<!DOCTYPE xsl:stylesheet  ["
                                + "<!ENTITY auml   \"&#228;\" >"
                                + "<!ENTITY ouml   \"&#246;\" >"
                                + "<!ENTITY uuml   \"&#252;\" >"
                                + "<!ENTITY szlig  \"&#223;\" >"
                                + "<!ENTITY Auml   \"&#196;\" >"
                                + "<!ENTITY Ouml   \"&#214;\" >"
                                + "<!ENTITY Uuml   \"&#220;\" >"
                                + "<!ENTITY euml   \"&#235;\" >"
                                + "<!ENTITY ocirc  \"&#244;\" >"
                                + "<!ENTITY icirc  \"&#238;\" >"
                                + "<!ENTITY nbsp   \"&#160;\" >"
                                + "<!ENTITY Agrave \"&#192;\" >"
                                + "<!ENTITY Egrave \"&#200;\" >"
                                + "<!ENTITY Eacute \"&#201;\" >"
                                + "<!ENTITY Ecirc  \"&#202;\" >"
                                + "<!ENTITY egrave \"&#232;\" >"
                                + "<!ENTITY eacute \"&#233;\" >"
                                + "<!ENTITY ecirc  \"&#234;\" >"
                                + "<!ENTITY agrave \"&#224;\" >"
                                + "<!ENTITY iuml   \"&#239;\" >"
                                + "<!ENTITY ugrave \"&#249;\" >"
                                + "<!ENTITY ucirc  \"&#251;\" >"
                                + "<!ENTITY uuml   \"&#252;\" >"
                                + "<!ENTITY ccedil \"&#231;\" >"
                                + "<!ENTITY AElig  \"&#198;\" >"
                                + "<!ENTITY aelig  \"&#330;\" >"
                                + "<!ENTITY OElig  \"&#338;\" >"
                                + "<!ENTITY oelig  \"&#339;\" >"
                                + "<!ENTITY euro   \"&#8364;\">"
                                + "<!ENTITY laquo  \"&#171;\" >"
                                + "<!ENTITY raquo  \"&#187;\" >"
                                + "]>");
                        if (output.xslFile != null) {
                            sortie.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + output.xslFile + "\"?>");
                        }
                    }
                    //Create JAXB Context
                    JAXBContext jc = JAXBContext.newInstance(XmlWebAlbums.class);
                    
                    //Create marshaller
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    StringWriter writer = new StringWriter() ;
                    marshaller.marshal(output, writer);
                    sortie.append(writer.toString());
                } else {
                    sortie.println(output.blob);
                }
                sortie.flush();
            }
        } catch (IOException | JAXBException e) {
            log.error("Exception: ", e);
        }
    }

    protected static void preventCaching(HttpServletRequest request,
            HttpServletResponse response) {
        // see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
        String protocol = request.getProtocol();
        if ("HTTP/1.0".equalsIgnoreCase(protocol)) {
            response.setHeader("Pragma", "no-cache");
        } else if ("HTTP/1.1".equalsIgnoreCase(protocol)) {
            response.setHeader("Cache-Control", "no-cache"); // "no-store" work also
        }
        response.setDateHeader("Expires", 0);
    }// </editor-fold>
}
