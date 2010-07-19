/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
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
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.view.servlet.exchange.ViewSessionImpl;

/**
 *
 * @author kevin
 */
@Stateless
public class DispatcherBean {

    private static final Logger log = Logger.getLogger(DispatcherBean.class.getCanonicalName());
    static {
        prepareLogs();
        log.warning("WebAlbums3-Servlet is being loaded ... ");
        log.log(Level.INFO, "RootPath: {0}", ConfigurationXML.getConf().getRootPath());
    }

    public DispatcherBean() {
        log.warning("WebAlbums3-Servlet DispatcherBean created !");
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
        log.log(Level.INFO, "============= <{0}> =============", page);
                
        long debut = System.currentTimeMillis();
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
                log.log(Level.INFO, "============= Login: {0} =============", request.getUserPrincipal());
                String special = request.getParameter("special");
                if (special != null) {
                    log.log(Level.INFO, "Special XSL-style ({0})", special);
                    if ("RSS".equals(special)) {
                        xslFile = "static/Rss.xsl";
                    } else {
                        xslFile = "static/Empty.xsl";
                    }
                }
                log.log(Level.FINE, "XSL-style{0}", xslFile);
                //try to logon and set the theme
                if (vSession.getThemeId() != null) {
                    log.info("Try to logon");
                    boolean ret = userService.logon((ViewSessionLogin) vSession, request);
                    log.log(Level.FINER, "Logon result: {0}", ret);
                }
                //from here on, the theme must be saved
                if (vSession.getTheme() == null){
                    if (special == null) {
                        log.finer("Not logged in, not a special page, display VOID page");
                        output.add(indexServlet.treatVOID(vSession));
                    } else {
                        isComplete = true;
                        output = new XmlBuilder("nothing");
                        log.finer("Not logged in, special request, nothing to display ...");
                    }
                } else {
                    if (page == Page.CHOIX) {
                        if (special == null) {
                            log.finer("CHOIX page");
                            output.add(choixServlet.displayCHX(vSession));
                        } else {
                            log.finer("CHOIX special page");
                            output = choixServlet.displayChxScript(vSession);
                            response.setContentType("text/javascript;charset=UTF-8");
                            isComplete = true;
                        }
                    } else if (page == Page.ALBUM) {
                        log.finer("ALBUM page");
                        output.add(albumServlet.treatALBM((ViewSessionAlbum) vSession));
                    } else if (page == Page.PHOTO) {
                        log.finer("PHOTO page");
                        output.add(photoServlet.treatPHOTO((ViewSessionPhoto) vSession));
                    } else if (page == Page.CONFIG) {
                        log.finer("CONFIG page");
                        output.add(configServlet.treatCONFIG((ViewSessionConfig) vSession));
                    } else if (page == Page.TAGS) {
                        log.finer("TAGS page");
                        output.add(tagServlet.treatTAGS((ViewSessionTag) vSession));
                    } else if (page == Page.IMAGE) {
                        log.finer("IMAGE page");
                        XmlBuilder ret = imageServlet.treatIMG((ViewSessionImages) vSession);
                        if (ret == null) {
                            isWritten = true;
                        } else {
                            output.add(ret);
                        }
                        log.log(Level.FINE, "IMAGE written? {0}", isWritten);
                    } else {
                        log.log(Level.FINER, "VOID page? ({0})", page);
                        output.add(indexServlet.treatVOID(vSession));
                    }
                }
            }
            
            output.validate();
        } catch (WebAlbumsServiceException e) {
            log.log(Level.WARNING, "WebAlbumsServiceException {0}", e) ;
            output.cancel();
        } 
        log.log(Level.FINE, "============= Footer (written:{0}, complete:{1})=============", new Object[]{isWritten, isComplete});
        long fin = System.currentTimeMillis();
        float time = ((float) (fin - debut) / 1000);
        if (!isWritten) {
            preventCaching(request, response);

            if (!isComplete) {
                try {
                    output.add(webPageService.xmlLogin((ViewSessionLogin) vSession));
                } catch (Exception e) {
                    log.log(Level.WARNING, "An exception occured during xmlLogin: {0}", e.toString());
                }
                try {
                    output.add(webPageService.xmlAffichage(vSession));
                } catch (Exception e) {
                    log.log(Level.WARNING, "An exception occured during xmlAffichage: {0}", e.toString());
                }

                XmlBuilder xmlStats = new XmlBuilder("stats");
                output.add(xmlStats);
                xmlStats.add("time", time);

            }
            doWrite(response, output, xslFile, isComplete, vSession);
        }
        log.log(Level.WARNING, "============= <{0}/>: {1} =============", new Object[]{page, time});
    }

    private static final String LOG_DIR = "logs/";

    private static void prepareLogs() {
        // ROOT logger
        Logger rootLogger = Logger.getLogger("");
        for (Handler hand : rootLogger.getHandlers()) if (hand instanceof ConsoleHandler) rootLogger.removeHandler(hand);

        rootLogger.setLevel(Level.INFO);
        //ConsoleHandler
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new MyFormatter(true));
        ch.setLevel(Level.ALL) ;
        rootLogger.addHandler(ch);

        //FileHandler
        try {

            new File(LOG_DIR).mkdirs();
            FileHandler serverFH = new FileHandler(LOG_DIR+"Server.log",
                    /*limit */ 1000000,
                    /*count */ 1,
                    /*append*/ true);
            serverFH.setLevel(Level.ALL) ;
            serverFH.setFormatter(new MyFormatter(false));
            rootLogger.addHandler(serverFH);
        } catch (IOException e) {}

        Logger webalbumsLogger = Logger.getLogger("net.wazari");
        webalbumsLogger.setLevel(Level.ALL);
        //FileHandler
        try {
            new File(LOG_DIR).mkdirs();
            // Create a file handler that uses the custom formatter
            FileHandler webalbumsFH = new FileHandler(LOG_DIR+"WebAlbums.log",
                    /*limit */ 1000000,
                    /*count */ 1,
                    /*append*/ false);

            webalbumsFH.setFormatter(new MyFormatter(false));
            webalbumsLogger.addHandler(webalbumsFH);
            webalbumsFH.setLevel(Level.ALL);
            FileHandler webalbumsWarningFH = new FileHandler(LOG_DIR+"WebAlbums.warnings.%g.log",
                    /*limit */ 1000000,
                    /*count */ 5,
                    /*append*/ true);
            webalbumsWarningFH.setFormatter(new MyFormatter(false));
            webalbumsWarningFH.setLevel(Level.WARNING);
            webalbumsLogger.addHandler(webalbumsWarningFH);
        } catch (IOException e) {}
/*
        log.finest("finest");
        log.finer("finer");
        log.fine("fine");
        log.config("config");
        log.info("info");
        log.warning("warning");
        log.severe("severe");
 */
    }

    public static class MyFormatter extends Formatter {
        private boolean breakLines ;
        public MyFormatter(boolean breakLines) {
            this.breakLines = breakLines ;
        }

        private static  final DateFormat df = new SimpleDateFormat("MMM dd HH:mm:ss.SSS");


        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(df.format(new Date(record.getMillis())));
            builder.append("|").append(record.getLevel());

            for (int count = record.getLevel().toString().length(); count < 8; count++)builder.append(" ") ;
            builder.append("|").append(record.getSourceClassName()).append(".");
            builder.append(record.getSourceMethodName()).append("|");

            if (breakLines) builder.append("\n");

            builder.append(formatMessage(record));
            builder.append("\n");

            if (record.getThrown() != null) {
                Throwable t = record.getThrown() ;
                Throwable cause = null ;

                while (t != null) {
                    builder.append(cause == null ? "" : "\t Caused by: ")
                            .append(t.getClass().getCanonicalName()).append("@")
                            .append(t.getMessage()).append("\n");
                    cause = t ;
                    t = t.getCause() ;
                }

                for (StackTraceElement st : cause.getStackTrace()) {
                    builder.append("\t\t at ").append(st.toString()).append("\n") ;
                }
                builder.append("----\n") ;
            }

            return builder.toString();

        }

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
            log.log(Level.SEVERE, "IOException: {0}", e);
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
