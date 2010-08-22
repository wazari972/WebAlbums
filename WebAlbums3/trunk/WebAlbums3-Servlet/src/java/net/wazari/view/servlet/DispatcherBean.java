/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.UserLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.common.util.XmlBuilder;
import net.wazari.dao.entity.Theme;
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.view.servlet.exchange.ViewSessionImpl;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

/**
 *
 * @author kevin
 */
@Stateless
public class DispatcherBean {

    private static final Logger log = LoggerFactory.getLogger(DispatcherBean.class.getCanonicalName());
    static {
        log.warn("WebAlbums3-Servlet is being loaded ... ");
        log.info( "RootPath: {}", ConfigurationXML.getConf().getRootPath());
    }

    public DispatcherBean() {
        log.warn("WebAlbums3-Servlet DispatcherBean created !");
    }

    @EJB
    private Index indexServlet;
    @EJB
    private Users userServlet;
    @EJB
    private Maint maintServlet;
    @EJB
    private Choix choixServlet;
    @EJB
    private Albums albumServlet;
    @EJB
    private Photos photoServlet;
    @EJB
    private Tags tagServlet;
    @EJB
    private Images imageServlet;
    @EJB
    private Config configServlet;

    @EJB
    private WebPageLocal webPageService;

    @EJB
    private UserLocal userService;
    
    public enum Page {

        PHOTO, IMAGE, USER, ALBUM, CONFIG, CHOIX, TAGS, VOID, PERIODE, MAINT
    }

    
    public void treat(ServletContext context,
            Page page,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException 
    {
        Page actualPage = page ;
        StopWatch stopWatch = new Slf4JStopWatch(log) ;
        request.setCharacterEncoding("UTF-8") ;
        ViewSession vSession = new ViewSessionImpl(request, response, context);
        if (request.getParameter("logout") != null) {
            log.info("Logout and cleanup the session");
            request.logout();
            userService.cleanUpSession((ViewSessionLogin) vSession);
        }
        if (page != Page.USER && page != Page.MAINT) {
            log.info("Authenticated the session");
            request.authenticate(response) ;
        }
        log.info( "============= <{}> =============", page);
                
        response.setContentType("text/xml");

        XmlBuilder output = new XmlBuilder("root");

        String xslFile = null;

        boolean isWritten = false;
        boolean isComplete = false;
        try {
            xslFile = "static/Display.xsl";
            if (page == Page.USER) {
                XmlBuilder resp = userServlet.treatLogin((ViewSessionLogin) vSession,request, response) ;
                if (resp == null) return ;
                output.add(resp) ;
            } else if (page == Page.VOID) {
                output.add(indexServlet.treatVOID(vSession));
            } else if (page == Page.MAINT) {
                isComplete = true;
                xslFile = "static/Empty.xsl";
                output.add(maintServlet.treatMaint((ViewSessionMaint) vSession));
            } else {
                log.info( "============= Login: {} =============", request.getUserPrincipal());
                String special = request.getParameter("special");
                if (special != null) {
                    log.info( "Special XSL-style ({})", special);
                    if ("RSS".equals(special)) {
                        xslFile = "static/Rss.xsl";
                    } else {
                        xslFile = "static/Empty.xsl";
                    }
                }
                log.debug( "XSL-style{}", xslFile);
                //try to logon and set the theme
                if (vSession.getThemeId() != null) {
                    log.info("Try to logon");
                    boolean ret = userService.logon((ViewSessionLogin) vSession, request);
                    log.debug( "Logon result: {}", ret);
                }
                //from here on, the theme must be saved
                if (vSession.getTheme() == null){
                    actualPage = Page.VOID ;
                    if (special == null) {
                        log.debug("Not logged in, not a special page, display VOID page");
                        output.add(indexServlet.treatVOID(vSession));

                    } else {
                        isComplete = true;
                        output = new XmlBuilder("nothing");
                        log.debug("Not logged in, special request, nothing to display ...");
                    }
                } else {
                    if (page == Page.CHOIX) {
                        if (special == null) {
                            log.debug("CHOIX page");
                            output.add(choixServlet.displayCHX(vSession));
                        } else {
                            log.debug("CHOIX special page");
                            output = choixServlet.displayChxScript(vSession);
                            response.setContentType("text/javascript;charset=UTF-8");
                            isComplete = true;
                        }
                    } else if (page == Page.ALBUM) {
                        log.debug("ALBUM page");
                        output.add(albumServlet.treatALBM((ViewSessionAlbum) vSession));
                    } else if (page == Page.PHOTO) {
                        log.debug("PHOTO page");
                        output.add(photoServlet.treatPHOTO((ViewSessionPhoto) vSession));
                    } else if (page == Page.CONFIG) {
                        log.debug("CONFIG page");
                        output.add(configServlet.treatCONFIG((ViewSessionConfig) vSession));
                    } else if (page == Page.TAGS) {
                        log.debug("TAGS page");
                        output.add(tagServlet.treatTAGS((ViewSessionTag) vSession));
                    } else if (page == Page.IMAGE) {
                        log.debug("IMAGE page");
                        XmlBuilder ret = imageServlet.treatIMG((ViewSessionImages) vSession);
                        if (ret == null) {
                            isWritten = true;
                        } else {
                            output.add(ret);
                        }
                        log.debug( "IMAGE written? {}", isWritten);
                    } else {
                        log.debug( "VOID page? ({})", page);
                        output.add(indexServlet.treatVOID(vSession));
                        actualPage = Page.VOID ;
                    }
                }
            }
            output.validate();
        } catch (WebAlbumsServiceException e) {
            log.warn( "WebAlbumsServiceException", e) ;
            output.cancel();
        } 
        log.debug( "============= Footer (written:{}, complete:{})=============", new Object[]{isWritten, isComplete});
        Theme currentTheme = vSession.getTheme() ;
        stopWatch.stop("View.dispatch."+actualPage+(vSession.getSpecial() != null ? "."+vSession.getSpecial() :"")+(currentTheme != null ? "."+currentTheme.getNom() : "NoTheme")) ;
        String strTime = DurationFormatUtils.formatDuration(stopWatch.getElapsedTime(), "m'min' s's' S'ms'", false) ;
        if (!isWritten) {
            preventCaching(request, response);

            if (!isComplete) {
                try {
                    output.add(webPageService.xmlLogin((ViewSessionLogin) vSession));
                } catch (Exception e) {
                    log.warn( "An exception occured during xmlLogin:", e);
                }
                try {
                    output.add(webPageService.xmlAffichage(vSession));
                } catch (Exception e) {
                    log.warn( "An exception occured during xmlAffichage:", e);
                }
                output.add("time", strTime);

            }
            doWrite(response, output, xslFile, isComplete, vSession);
        }


        log.warn( "============= <{}/>: {} =============", new Object[]{page, strTime});
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    private static void doWrite(HttpServletResponse response, XmlBuilder output, String xslFile, boolean isComplete, ViewSession vSession) {
        try {
            PrintWriter sortie = response.getWriter();

            if (!isComplete) {
                output.addHeader("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                output.addHeader("<!DOCTYPE xsl:stylesheet  [" +
                        "<!ENTITY auml   \"&#228;\" >" +
                        "<!ENTITY ouml   \"&#246;\" >" +
                        "<!ENTITY uuml   \"&#252;\" >" +
                        "<!ENTITY szlig  \"&#223;\" >" +
                        "<!ENTITY Auml   \"&#196;\" >" +
                        "<!ENTITY Ouml   \"&#214;\" >" +
                        "<!ENTITY Uuml   \"&#220;\" >" +
                        "<!ENTITY euml   \"&#235;\" >" +
                        "<!ENTITY ocirc  \"&#244;\" >" +
                        "<!ENTITY nbsp   \"&#160;\" >" +
                        "<!ENTITY Agrave \"&#192;\" >" +
                        "<!ENTITY Egrave \"&#200;\" >" +
                        "<!ENTITY Eacute \"&#201;\" >" +
                        "<!ENTITY Ecirc  \"&#202;\" >" +
                        "<!ENTITY egrave \"&#232;\" >" +
                        "<!ENTITY eacute \"&#233;\" >" +
                        "<!ENTITY ecirc  \"&#234;\" >" +
                        "<!ENTITY agrave \"&#224;\" >" +
                        "<!ENTITY iuml   \"&#239;\" >" +
                        "<!ENTITY ugrave \"&#249;\" >" +
                        "<!ENTITY ucirc  \"&#251;\" >" +
                        "<!ENTITY uuml   \"&#252;\" >" +
                        "<!ENTITY ccedil \"&#231;\" >" +
                        "<!ENTITY AElig  \"&#198;\" >" +
                        "<!ENTITY aelig  \"&#330;\" >" +
                        "<!ENTITY OElig  \"&#338;\" >" +
                        "<!ENTITY oelig  \"&#339;\" >" +
                        "<!ENTITY euro   \"&#8364;\">" +
                        "<!ENTITY laquo  \"&#171;\" >" +
                        "<!ENTITY raquo  \"&#187;\" >" +
                        "]>");
                
                output.addHeader("<?xml-stylesheet type=\"text/xsl\" href=\"" + xslFile + "\"?>");
            }
            sortie.println(output.toString());

            sortie.flush();
            sortie.close();
        } catch (IOException e) {
            log.error( "IOException: ", e);
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
