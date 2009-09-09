package engine;


import java.io.IOException;
import java.util.Enumeration ;
import java.util.NoSuchElementException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.JDBCException;
import org.hibernate.HibernateException;

import constante.Path;
import engine.WebPage.Mode;
import entity.Utilisateur;
import entity.Theme ;

public class Choix {
  private static final long serialVersionUID = 1L;
  
  public static void treatCHX(HttpServletRequest request,
		       StringBuilder output)
    throws HibernateException {
    output.append(WebPage.getHeadBand(request));
    display.Choix.displayCHX(request, output) ;
  }
}
