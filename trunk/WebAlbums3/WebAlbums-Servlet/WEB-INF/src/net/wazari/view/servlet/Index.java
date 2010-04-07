package net.wazari.view.servlet;


import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.AlbumLocal;

@EJB(name="net.wazari.service.AlbumLocal", beanInterface=AlbumLocal.class)
public class Index extends HttpServlet {
    private static Logger log = Logger.getLogger(Index.class.toString()) ;
    
    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        try {
            InitialContext ic = new InitialContext();
            System.out.println("-->*<--") ;
            NamingEnumeration<NameClassPair> children = ic.list("") ;
            while (children.hasMore()) {
                NameClassPair pair = children.next() ;
                System.out.print(pair.getName() +"("+pair.getClassName()+")");
            }
            System.out.println("-->*<--") ;
            AlbumLocal local = (AlbumLocal)ic.lookup(AlbumLocal.class.getName()) ;

            System.out.println("-->"+local+"<--") ;

        } catch (Exception ex) {
            Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (true) return ;
        Dispatcher.treat(this, Page.VOID, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public enum Page {

        PHOTO, IMAGE, USER, ALBUM, CONFIG, CHOIX, TAGS, VOID, PERIODE, MAINT
    }
    

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("WebPage initialized !");


        log.info("Ready to serve !");
    }
}
