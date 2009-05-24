package engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import constante.Path;
import entity.*;

import display.VoidPage ;
import display.Periode ;

import engine.GoogleMap.Point ;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.classic.Session;

import util.HibernateUtil;
import util.StringUtil;

public class WebPage extends HttpServlet {
  public enum Page {PHOTO, IMG, USR, ALBM, CONFIG, CHX, TAGS, VOID, PERIODE} 
  public enum Type {PHOTO, ALBUM}
  public enum Box  {NONE, MULTIPLE, LIST, MAP} ;
  public enum Mode {USER, TAG_USED, TAG_NUSED, TAG_ALL, TAG_NONE, TAG_GEO} ;
  
  private static final long serialVersionUID = -8157612278920872716L;
  public static final Logger log = Logger.getLogger("WebAlbum");
  public static final Logger stat = Logger.getLogger("Stats");
  public static final SimpleDateFormat DATE_STANDARD =
    new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat DATE_FRANCE =
    new SimpleDateFormat("dd-MM-yyyy");
  public static final SimpleDateFormat DATE_HEURE =
    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
  
  public static final String USER_CHEAT = "-1" ;
  
  public static final int TAILLE_PHOTO = 10;
  public static final int TAILLE_ALBUM = 15;
  
  public static Session session ;

  public static final Map<HttpServletRequest, Header> hash =
    new HashMap<HttpServletRequest, Header> () ;
      
  static {
    try {
      session = HibernateUtil.currentSession();
      
      log.setLevel(Level.ALL);
      log.addAppender(new FileAppender(
			new SimpleLayout(),
			"/"+Path.TMP+"/WebPage.log"));
      stat.setLevel(Level.ALL);
      stat.addAppender(new FileAppender(
			 new SimpleLayout(),
			 "/"+Path.TMP+"/stat.log"));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (HibernateException e) {
      e.printStackTrace();
    } 
  }
  
  public void init() {
    Path.setLocation(this) ;
  }
  
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    treat(Page.VOID, request, response);
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  
  public static void treat(Page page,
			   HttpServletRequest request,
			   HttpServletResponse response)
    throws IOException {
    long debut = System.currentTimeMillis();
    hash.remove(request) ;
    StringBuilder out = new StringBuilder() ;
    
    boolean isCorrect = false ;
    boolean isPhoto = false ;
    
    StringBuilder err = new StringBuilder () ;
    StringBuilder output = new StringBuilder () ;

    try {
      if (page == Page.VOID) {
	VoidPage.treatVOID(output);
      } else if (page == Page.USR) {
	Users.treatUSR(request, output);
      } else {
	updateLogInformation(request, output) ;
	String userID  = Users.getUser(request) ;
	String themeID = getThemeID(request) ;
	//a partir d'ici, l'utilisateur doit être en memoire
	if (userID != null) { 
	  if (page == Page.CHX) {
	    Choix.treatCHX(request, output);
	  }else if (page == Page.ALBM) {
	    Albums.treatALBM(request, output);
	  } else if (page == Page.PERIODE) {
	    display.Periode.treatPERIODE(request, output) ;
	  } else if (page == Page.PHOTO) {
	    Photos.treatPHOTO(request, output);
	  } else if (page == Page.CONFIG){
	    Config.treatCONFIG(request, output);
	  } else if (page == Page.TAGS){
	    Tags.treatTAGS(request, output);
	  } else if (page == Page.IMG){
	    isPhoto = Images.treatIMG(request, output, response);
	  } else {
	    VoidPage.treatVOID(output);
	  }
	} else {
	  VoidPage.treatVOID(output);
	}
      
      }
      isCorrect = true ;
    } catch (JDBCException e) {
      
      err.append("Il y a une erreur dans la requete ... !<br/>\n"+
		 e.getSQLException()+"<br/>\n") ;
      err.append(e);
      
    } catch (HibernateException e) {
      err.append("Problème avec Hibernate ... !\n" +
		 e+"<br/><br/>**<br/>\n" +
		 StringUtil.escapeHTML(output.toString())+
		 "<br/>**<br/><br/>\n");
    } catch (AccessorsException e) {
      err.append("Problème avec les accesseurs ... !\n" +
		 e+"<br/><br/>**<br/>\n" +
		 StringUtil.escapeHTML(output.toString())+
		 "<br/>**<br/><br/>\n");
    }
    
    long fin = System.currentTimeMillis();

    Header head = hash.get(request) ;
    if (!isPhoto) {
      response.setContentType("text/html");
      preventCaching(request, response);
      out.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD "+
		 "HTML 4.01 Transitional//EN\">\n" +
		 "<html>\n" +
		 "  <head>\n" +
		 "    <title>WebAlbum4</title>\n" +
		 (head == null ? "" : head.header())+
		 "  </head>\n" +
		 "  <body "+
		 (head == null ? "" : head.bodyAttributes())+">\n" +
		 (isCorrect ? output.toString() : err.toString())+
		 "		<br/>Page générée en "+
		 (((double)(fin-debut))/1000)+"s\n" +
		 "		<br/>\n"+
		 "  </body>\n" +
		 "</html>");
      PrintWriter sortie = response.getWriter();
      sortie.println(out.toString());
      sortie.flush();
      sortie.close();
    }
  }

  protected static void updateLogInformation (HttpServletRequest request, StringBuilder output)
    throws HibernateException {
    String rq = null ;
    String themeName = request.getParameter("themeName");
    String userName = request.getParameter("userName");
    if (themeName != null) {
      try {
	rq = "from Theme where nom = '"+themeName+"'" ;
	List list = session.find(rq);
	rq = "done" ;
	
	Theme enrTheme = (Theme) list.iterator().next() ;

	saveTheme (request, enrTheme) ;
	
      } catch (NoSuchElementException e) {
	output.append("<i>Impossible de trouver ce theme "+
		      "("+themeName+")</i><br/>\n");
      } catch (JDBCException e) {
	output.append("<br/><i>Impossible d'executer la requete </i>"+
		      "=> "+rq+"<br/>\n"+e+"<br/>\n"+
		      e.getSQLException()+"<br/>\n");
      } 
    }

    if (userName != null) {
      try {
	rq = "from Utilisateur where nom = '"+userName+"'" ;
	List list = session.find(rq);
	rq = "done" ;
	
	Utilisateur enrUtilisateur = (Utilisateur) list.iterator().next() ;

	Users.saveUser(request, enrUtilisateur, null);
      } catch (NoSuchElementException e) {
	output.append("<i>Impossible de trouver cet utilisateur "+
		      "("+userName+")</i><br/>\n");
      } catch (JDBCException e) {
	output.append("<br/><i>Impossible d'executer la requete </i>"+
		      "=> "+rq+"<br/>\n"+e+"<br/>\n"+
		      e.getSQLException()+"<br/>\n");
      }
    }
    saveEditionMode(request);
    saveDetails(request) ;
    saveMaps(request) ;
  }
  
  public static String listPhotoAllowed(HttpServletRequest request) {
    String user = engine.Users.getUser(request) ;
    String rq = null ;
    
    if (isLoggedAsCurrentManager(request)) {
      rq = "select ph.ID from Photo ph" ;
    } else {
      rq = "select ph.ID from Photo ph " +
	"where " +
	//photo non masqué dans les albums autorisé
	"(" +
	"  ph.Album in ("+
	"    select ua.Album from UserAlbum ua where ua.User = '"+user+
	"' ) "+
	"  and ph.ID not in ("+
	"    select up.Photo from UserPhoto up where up.User = '"+user+
	"' )" +
	") "+
	"or " +
	//photo autorisé dans les albums masqué
	"ph.ID in (select p.ID "+
	"from Album a, Photo p "+
	"where "+
	"a.ID = p.Album and "+
	"a.ID not in ( "+
	//liste des albums autorisés
	"    select usrA.Album "+
	"    from UserAlbum usrA "+
	"    where usrA.User = "+user+" "+
	") " + 
	"and " +
	"p.ID in (" +
	//liste des photos avec status inverse
	"	select usrP.Photo " +
	"	from UserPhoto usrP " +
	"	where usrP.User = '"+user+"'" +
	")" +
	")";
    }
    try {
      StringBuilder str = new StringBuilder() ;
      Iterator it = session.find(rq).iterator() ;
      while (it.hasNext()) {
	str.append(it.next() + ", ");
      }
      str.append("-1");
      return str.toString();
    } catch (Exception e) {
      return "" ;
    }
    //return rq;
  }
  

  public static String listAlbumAllowed(HttpServletRequest request) {
    String user = engine.Users.getUser(request) ;
    String rq = null ;
    if (isLoggedAsCurrentManager(request)) {
      rq = "select al.ID from Album al " ;
    }
    else {
      rq = "select al.ID from Album al " +
	"where " +
	//albums autorisés
	"al.ID in ("+
	"  select ua.Album from UserAlbum ua where ua.User = '"+user+
	"')" +
	" or " +
	// albums ayant des photos autorisés dans un album masqué
	"al.ID in (select p.Album "+
	"from Album a, Photo p "+ 
	"where "+
	"a.ID = p.Album and "+
	// liste des albums NON autorisés
	"a.ID not in ( "+
	"    select usrA.Album "+
	"    from UserAlbum usrA "+
	"    where usrA.User = '"+user+"' "+
	") " +
	"and " +
	"p.ID in (" +
	//liste des photos avec status inverse
	"	select usrP.Photo " +
	"	from UserPhoto usrP " +
	"	where usrP.User = '"+user+"'" +
	"  )" +
	")" ;
    }
    try {
      StringBuilder str = new StringBuilder() ;
      Iterator it = session.find(rq).iterator() ;
      while (it.hasNext()) {
	str.append(it.next()+ ", ");
      }
      str.append("-1");
      return str.toString();
    } catch (Exception e) {
      return "" ;
    }
    //return rq;
  }
  
  
  
  public static boolean isLoggedAsCurrentManager(HttpServletRequest request) {
    String logedID  = (String) request.getSession().getAttribute("LogInID") ;
    
    try {
      return logedID.equals(USER_CHEAT) ;
    } catch (NullPointerException e) {
      return false ;
    }
  }
  

  public static boolean isLoggedAsCurrentManager(HttpServletRequest request,
					  StringBuilder output) {
    if (isLoggedAsCurrentManager(request)) {
      return true ;
    } else {
      output.append("<i>Vous n'êtes pas le manager de ce theme !</i>"+
		    "<br/><br/>\n");
      return false ;
    }
  }
  
  protected static String tryToSaveTheme(HttpServletRequest request,
					 StringBuilder output,
					 String theme)
    throws HibernateException {
    //String theme = request.getParameter("theme") ;
    if (theme == null) {
      //pas de nouvelles informations
      return (String) request.getSession().getAttribute("ThemeID");
    }
    String rq = null ;
    //memoriser le nom de l'auteur
    try {
      Session session = HibernateUtil.currentSession();
      rq = "from Theme where id='"+theme+"'" ;
      Theme enrTheme = (Theme) session.find(rq).iterator().next();
      rq = "done" ;
      
      saveTheme (request, enrTheme);
    } catch (JDBCException e) {
      output.append("<br/><i>Impossible d'executer la requete </i>"+
		    "=> "+rq+"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
      return null ;
    } catch (NoSuchElementException e) {
      output.append("<i>Ce theme ("+theme+") n'existe pas ...</i>"+e+"<br/>\n");
      return null;
    }
    return theme ;
  }

  protected static void saveTheme(HttpServletRequest request,
				  Theme enrTheme) {
    String oldID = (String) request.getSession().getAttribute("ThemeID");
    String newID = Integer.toString(enrTheme.getID()) ;

    if (!newID.equals(oldID) && isLoggedAsCurrentManager(request)) {
      Users.clearUser(request);
    }
    request.getSession().setAttribute("ThemeName", enrTheme.getNom()) ;
    request.getSession().setAttribute("ThemeID", newID) ;
    request.getSession().setAttribute("EditionMode", EditMode.NORMAL) ;
  }
  
  public static String getThemeID(HttpServletRequest request) {
    String theme = request.getParameter("theme") ;
    if (theme != null) {
      try {
	tryToSaveTheme(request, new StringBuilder(), theme) ;
      } catch (HibernateException e) {}
    } else {
      theme = (String) request.getSession().getAttribute("ThemeID");
    }
    
    return theme ;
  }
  
  public static boolean isRootSession (HttpServletRequest request) {
    return getThemeID(request).equals("-1"); 
  }

  public static String getThemeName(HttpServletRequest request) {
    return (String) request.getSession().getAttribute("ThemeName");
  }
  
  public static void displayMapIn(HttpServletRequest request,
				  StringBuilder str, String info)
    throws HibernateException {
    displayListLBNI(Mode.TAG_USED, request, str, null, Box.MAP, null, info) ;
  }

  public static void displayMapId(HttpServletRequest request,
				  StringBuilder str,
				  int id) throws HibernateException {
    displayListIBT(Mode.TAG_USED, request, str, id, Box.MAP, Type.ALBUM) ;
  }
  
  //display a list into STR
  //according to the MODE
  //and the information found in the REQUEST.
  //List made up of BOX items,
  //and is named NAME
  public static void displayListBN(Mode mode,
				   HttpServletRequest request,
				   StringBuilder str,
				   Box box,
				   String name)
    throws HibernateException {
    displayListLBNI(mode, request, str, null, box, name, null) ;
  }
  
  //display a list into STR
  //according to the MODE
  //and the information found in REQUEST.
  //List is made up of BOX items
  //and is named with the default name for this MODE
  public static void displayListB(Mode mode,
				  HttpServletRequest request,
				  StringBuilder str,
				  Box box)
    throws HibernateException {
    displayListLBNI(mode, request, str, null, box,
		    (mode == Mode.TAG_USED
		     || mode == Mode.TAG_NUSED
		     || mode == Mode.TAG_ALL
		     || mode == Mode.TAG_NONE
		     || mode == Mode.TAG_GEO ?
		     "tags" : (mode == Mode.USER ? "users" : "???")),
		    null) ;
  }
  
  //display a list into STR
  //according to the MODE
  //and the information found in REQUEST.
  //List is made up of BOX items
  //and is named with the default name for this MODE
  //if type is PHOTO, info (MODE) related to the photo #ID are put in the list
  //if type is ALBUM, infon (MODE) related to the album #ID are put in the list
  public static void displayListIBT(Mode mode,
				    HttpServletRequest request,
				    StringBuilder str,
				    int id,
				    Box box,
				    Type type)
    throws HibernateException {
    displayListIBTNI(mode, request, str, id, box, type,
		     (mode == Mode.TAG_USED
		      || mode == Mode.TAG_NUSED
		      || mode == mode.TAG_ALL
		      || mode == Mode.TAG_NONE
		      || mode == Mode.TAG_GEO
		      ? "tags" : (mode == Mode.USER ? "users" : "???")),
		     null) ;
  }
  
  //display a list into STR
  //according to the MODE
  //and the information found in REQUEST.
  //List is made up of BOX items
  //and is filled with the IDs 
  public static void displayListLB(Mode mode,
				   HttpServletRequest request,
				   StringBuilder str,
				   List<Integer> ids,
				   Box box)
    throws HibernateException {
    displayListLBNI(mode, request, str, ids, box,
		    (mode == Mode.TAG_USED
		     || mode == Mode.TAG_NUSED
		     || mode == mode.TAG_ALL
		     || mode == Mode.TAG_NONE
		     || mode == Mode.TAG_GEO ?
		     "tags" : (mode == Mode.USER ? "users" : "???")),
		    null) ;
  }
  
  
  //display a list into STR
  //according to the MODE
  //and the information found in REQUEST.
  //List is made up of BOX items
  //and is named NAME
  //if type is PHOTO, info (MODE) related to the photo #ID are put in the list
  //if type is ALBUM, info (MODE) related to the album #ID are put in the list
  @SuppressWarnings("unchecked")
  public static void displayListIBTNI(Mode mode,
				      HttpServletRequest request,
				      StringBuilder str,
				      int id,
				      Box box,
				      Type type,
				      String name,
				      String info)
    throws HibernateException {
    String rq = null ;
    try {
      List<Integer> ids ;
      if (type == Type.PHOTO) {
	if (mode == Mode.TAG_USED
	    || mode == Mode.TAG_NUSED
	    || mode == mode.TAG_ALL
	    || mode == Mode.TAG_NONE
	    || mode == Mode.TAG_GEO) {
	  rq = "from TagPhoto " +
	    "where photo = '"+id+"'" ;
	  List list = session.find(rq);
	  rq = "done" ;
	  ids= new ArrayList<Integer>(list.size()) ;
	  for (Object o : list) {
	    TagPhoto tag = (TagPhoto) o ;
	    ids.add(tag.getTag()) ;
	  }
	} else if (mode == Mode.USER){
	  //liste des utilisateurs autorisé à voir une photo
	  rq = "select u.ID " +
	    "from Utilisateur u " +
	    "where ("+
	    "	u.ID not in (select up.User from UserPhoto up "+
	    "                 where up.Photo = '"+id+"')" +
	    "	and u.ID in (select ua.User from UserAlbum ua, Photo p "+
	    "                 where p.ID = '"+id+"' and p.Album = ua.Album)" +
	    ") or (" +
	    "	u.ID not in (select ua.User from UserAlbum ua, Photo p "+
	    "                 where p.ID = '"+id+"' and p.Album = ua.Album)" +
	    "	and u.ID in (select up.User from UserPhoto up "+
	    "                 where up.Photo = '"+id+"')" +
	    ")" ;
	  ids = (List<Integer>)session.find(rq);
	  rq = "done" ;
	  
	} else {
	  ids = null ;
	}
      } else if (type == Type.ALBUM){
	
	if (mode == Mode.TAG_USED
	    || mode == Mode.TAG_NUSED
	    || mode == mode.TAG_ALL
	    || mode == Mode.TAG_NONE
	    || mode == Mode.TAG_GEO) {
	  rq = "select tp " +
	    "from Photo photo, TagPhoto tp " +
	    "where photo.Album = '"+id+"' " +
	    "and photo.ID = tp.Photo " +
	    "group by tp.Tag" ;
	  List list = session.find(rq);
	  rq = "done" ;
	  ids = new ArrayList<Integer>(list.size()) ;
	  for (Object o : list) {
	    TagPhoto user = (TagPhoto) o ;
	    ids.add(user.getTag()) ;
	  }
	} else if (mode == Mode.USER) {
	  //list des utilisateur autorisé d'un album
	  rq = "select ua from UserAlbum ua where ua.Album = '"+id+"'" ;
	  List list = session.find(rq);
	  rq = "done" ;
	  ids = new ArrayList<Integer>(list.size()) ;
	  for (Object o : list) {
	    UserAlbum ua = (UserAlbum) o ;
	    ids.add(ua.getUser()) ;
	  }
	} else {
	  ids = null ;
	}
	
      } else {
	ids = null ;
      }
      displayListLBNI(mode, request, str, ids, box, name, info) ;
    } catch (JDBCException e) {
      str.append("Il y a une erreur dans la requete : "+rq+"<br/>\n"
		 +e+"<br/>\n"+e.getSQLException()+"<br/>\n"
		 +e.getSQLException()+"<br/>\n") ;
    }
  }
  
  //display a list into STR
  //according to the MODE
  //and the information found in REQUEST.
  //List is made up of BOX items
  //and is named NAME
  //Only IDS are added to the list
  //Mode specific information can be provide throug info (null otherwise)
  //(used by Mode.MAP for the link to the relevant address)
  public static void displayListLBNI(Mode mode,
				     HttpServletRequest request,
				     StringBuilder str,
				     List<Integer> ids,
				     Box box,
				     String name,
				     String info
    ) throws HibernateException {	
    String rq = null ;
    String themeID = getThemeID(request) ;
    String userID = engine.Users.getUser(request) ;
    try {
      List list ;
      //affichage de la liste des tags
      if (mode == Mode.TAG_USED
	  || mode == Mode.TAG_NUSED
	  || mode == mode.TAG_ALL
	  || mode == Mode.TAG_NONE
	  || mode == Mode.TAG_GEO) {	
	//afficher que les tags où il y aura des photos
	rq =
	  "from Tag t0 where "+
	  "t0.ID in ("+
	  // Tags des photos autorisés dans album autorisé
	  "  select t1.ID "+
	  "  from Album a1, Photo p1, Tag t1, TagPhoto tp1 "+
	  "  where "+
	  "  a1.ID = p1.Album "+
	  "  and t1.ID = tp1.Tag  "+
	  "  and tp1.Photo = p1.ID " ;
	
	if (!isRootSession(request)) {
	  // albums de ce theme
	  rq += "  and a1.Theme = '"+themeID+"' " ;
	}
	
	if (!isLoggedAsCurrentManager(request)) {
	  rq +=
	    // liste des albums autorisés
	    "  and a1.ID in ( "+
	    "    select usrA11.Album "+
	    "    from UserAlbum usrA11 "+
	    "    where usrA11.User = "+userID+" "+
	    "  ) "+
	    // liste des photos autorisés
	    "  and p1.id not in ( "+
	    "    select usrP12.Photo "+
	    "    from UserPhoto usrP12 "+
	    "    where usrP12.User = "+userID+" "+
	    "  ) " ;
	}
	rq += ") "+
	  //Tags des photos autorisés dans album masqué
	  "or t0.ID in ("+
	  "  select t2.ID "+
	  "  from Album a2, Photo p2, Tag t2, TagPhoto tp2 "+
	  "  where "+
	  "  a2.ID = p2.Album "+
	  "  and t2.ID = tp2.Tag "+
	  "  and tp2.Photo = p2.ID " ;
	if (!isRootSession(request)) {
	  // albums de ce theme
	  rq += "  and a2.Theme = '"+themeID+"' " ;
	}
	
	if (!isLoggedAsCurrentManager(request)) {
	  rq +=
	    // pas dans liste des albums autorisés
	    "  and a2.ID not in ( "+
	    "    select usrA21.Album "+
	    "    from UserAlbum usrA21 "+
	    "    where usrA21.User = "+userID+" "+
	    "  )"+
	    // dans la liste des photos autorisées
	    "and p2.ID in ( "+
	    "    select usrP22.Photo "+
	    "    from UserPhoto usrP22 "+
	    "    where usrP22.User = "+userID+" "+
	    "  )" ;
	}
	rq +=  ")" ;
	 
	  
	if (isLoggedAsCurrentManager(request)) {
	  if (mode ==  Mode.TAG_ALL) {
	    //afficher tous les tags
	    rq = "from Tag" ;
	  } else if (mode == Mode.TAG_NUSED || mode == Mode.TAG_NONE) {
	    //remove the tags used in this theme
	    if (mode == Mode.TAG_NONE) {
	      //remove ALL the tags used
	      rq = "select t from Tag t, TagPhoto tp "+
		"where t.ID = tp.Tag" ;
	    }

	    //rq = "from Tag where id not in ("+rq+")" ;
	    list = session.find(rq) ;
	    Iterator iterTags = list.iterator () ;
	    StringBuilder tagsUsed = new StringBuilder () ;
	    if (iterTags.hasNext()) {
	      while (true) {
		Tag enrTag = (Tag) iterTags.next();
		tagsUsed.append(""+enrTag.getID());
		if (iterTags.hasNext()) {
		  tagsUsed.append(", ");
		} else break ;
	      }
	      rq = "from Tag where id not in ("+tagsUsed.toString()+")" ;
	    } else {
	      rq = "from Tag" ;
	    }
	  } else if (mode == Mode.TAG_GEO) {
	    rq = "from Tag where tagtype = '3'" ;
	  }
	  rq += " order by TagType, Nom" ;
	} else {
	  rq += "group by t0.ID order by TagType, Nom" ;
	}
      } else if (mode == Mode.USER) /*sinon on afiche la liste des users*/ {
	rq = "from Utilisateur" ;
      } else {
	str.append("Mode incorrect :"+mode+"<br/>\n");
	return ;
      }
      
      list = session.find(rq) ;
      rq = "done" ;
      
      int current = 0;
      int max = (ids == null ? 0 : ids.size()) ;
      
      if (box == Box.MULTIPLE) str.append("<select size='7' "+
					  "name='"+name+"' multiple>\n");
      if (box == Box.LIST) str.append("<select name='"+name+"'>\n");

      GoogleMap map = null;
      if (box == Box.MAP)  {
	GoogleHeader head = (GoogleHeader) hash.get(request) ;
	if (head == null) {
	  head = new GoogleHeader() ;
	  hash.put(request, head) ;
	}
	map = new GoogleMap () ;
	head.addMap(map) ;
	if (info == null) {
	  map.setSize(200, 266) ;
	  map.displayInfo(false);
	} else {
	  map.setSize(500, 700) ;  
	}
      }

      boolean first = true ;
      int prevTagType = -1 ;
      Iterator it = list.iterator();
      while (it.hasNext()) {
	int id = -1 ;
	String nom = null ;
	Point p = null;
	Integer photo = null ;
	if (mode == Mode.TAG_USED
	    || mode == Mode.TAG_NUSED
	    || mode == Mode.TAG_ALL
	    || mode == Mode.TAG_NONE
	    || mode == Mode.TAG_GEO) {
	  Tag enrTag = (Tag) it.next();
	  if (box == Box.MAP) {
	    if (enrTag.getTagType() == 3) {
	      rq = "from Geolocalisation "+
		"where tag = "+enrTag.getID()+" ";
	      List geoList = session.find(rq) ;
	      Iterator geoIt = geoList.iterator() ;
	      if (geoIt.hasNext()) {
		id = enrTag.getID() ;
		Geolocalisation enrGeo = (Geolocalisation) geoIt.next();
		p = new Point (enrGeo.getLat(),
			       enrGeo.getLong(),
			       enrTag.getNom()) ;
		nom = enrTag.getNom() ;
		photo = enrTag.getPhoto() ;
	      } 
	    }
	  } else {

	    id = enrTag.getID() ;
	    nom = enrTag.getNom();
	    
	    if (box == Box.MULTIPLE  || box == box.LIST) {
	      if (prevTagType != enrTag.getTagType () &&
		  !(box == Box.MULTIPLE && first)) {
		str.append("<option value='-1'>==========</option>\n") ;
	      }
	      
	      String type ;
	      switch (enrTag.getTagType()) {
	        case 1 : type = "[WHO]" ; break ;
	        case 2 : type = "[WHAT]" ; break ;
	        case 3 : type = "[WHERE]" ; break ;
	        default : type = "[-]" ; break ;
	      }
	      String havePhoto = "" ;
	      if (enrTag.getPhoto() == null) {
		havePhoto = "*" ;
	      }
	      nom = type + " " + nom + havePhoto;
	    }
	    prevTagType = enrTag.getTagType () ;
	  } 	  
	} else if (mode == Mode.USER){
	  if (box == box.LIST) {
	    //first loop
	    if (first) {
	      str.append("<option value='-1'>==========</option>\n") ;
	    }
	  }
	  Utilisateur enrUser = (Utilisateur) it.next() ;
	  id = enrUser.getID();
	  nom = enrUser.getNom();
	
	} else {
	  /* reserved for a future growing up*/
	  str.append("Error");
	  throw new RuntimeException ("Unknown mode "+mode);
	}
	
	//display the value [if in ids][select if in ids]
	if (box == Box.MAP)  {
	  if (nom != null && (ids == null || ids.contains(id))) {
	    String msg ;
	    if (info != null) {
	      msg = String.format("<center><a href='%s'>%s</a></center>",
				  info+id,p.name );
	      if (photo != null) {
		msg += String.format("<br/><img alt='%s' "+
				     "src='%s.Images?"+
				     "id=%d&amp;mode=PETIT'>",
				     p.name,
				     Path.LOCATION,
				     photo);
				     
	      }
	    } else {
	      msg = p.name ;
	    }
	    p.setMsg(msg);
	    map.addPoint(p) ;
	  }				  
	} else if (box != Box.NONE) {
	  String selected = "" ;

	  if (ids == null) {
	    str.append("<option value='"+id+"' "+selected+">"+
		       nom+
		       "</option>\n") ;
	  } else {
	    if (box == Box.MULTIPLE) {
	      if (ids.contains(id)) {
		selected = "selected" ;
	      }
	      str.append("<option value='"+id+"' "+selected+">"+
			 nom+
			 "</option>\n") ;
	    } else {
	      if (ids.contains(id)) {
		str.append("<option value='"+id+"' "+selected+">"+
			   nom+
			   "</option>\n") ;
	      }
	      
	    }
	  }

	  /*
	  if (ids != null && ids.contains(id) && box == Box.MULTIPLE) {
	    selected = "selected" ;
	  }
	  str.append("<option value='"+id+"' "+selected+">"+
		     nom+
		     "</option>\n") ;
	  */
	} else {
	  if (ids != null && ids.contains(id)) {
	    current++ ;
	    str.append(nom);
	    if (current < max) {
	      str.append(", ");
	    }
	  }
	}
	first = false ;
      }
      
      if (box == Box.MAP)  {
	str.append(map.getBody());
      }
      else if (box != Box.NONE) str.append("</select>\n");
      
    } catch (JDBCException e) {
      str.append("Impossible d'effectuer la requete suivante :<br/>\n"+
		 rq+"<br/>\nException : "+e+"<br/><br/>\n"+
		 "SQLException : "+e.getSQLException()+"<br/><br/>\n\n") ;
    }
  }
  
  
  public static void displayUserPhoto (int albumId,
					  int photoId,
					  StringBuilder output)
    throws HibernateException {
    String rq = null ;
    try {
      //liste des status individuels, par photo 
      rq = "from UserPhoto where photo = '"+photoId+"'" ;
      List list = session.find(rq);
      rq = "done" ;
      List<Integer> details = new ArrayList<Integer>(list.size()) ;

      for (Object o : list) {
	UserPhoto user = (UserPhoto) o ;
	details.add(user.getUser()) ;
      }
      
      //liste des status de l'album
      rq = "from UserAlbum where album = '"+albumId+"'" ;
      list = session.find(rq);
      rq = "done" ;
      List<Integer> global = new ArrayList<Integer>(list.size()) ;

      for (Object o : list) {
	UserAlbum user = (UserAlbum) o ;
	global.add(user.getUser()) ;
      }
      
      //afficher pour chaque theme
      rq = "from Utilisateur" ;
      list = session.find(rq);
      rq = "done" ;
      boolean album, photo ;
      output.append("<table>\n" +
		    "<tr>\n" +
		    "	<th>Utilisateurs</th>\n" +
		    "	<th>Album</th>\n" +
		    "	<th>Different ?</th>\n" +
		    "</tr>") ;
      for (Object o : list) {
	output.append("<tr>\n") ;
	Utilisateur user = (Utilisateur) o ;
	album = global.contains(user.getID()) ;
	photo = details.contains(user.getID()) ;
	
	output.append("<td>"+user.getNom()+"</td>\n" +
		      "<td>"+(album ? "visible" : "masqué")+"</td>\n" +
		      "<td align='center'>"+
		      "<input type='checkbox' "+
		      "name='user"+user.getID()+"'"+
		      " "+(photo ? "checked" : "")+"></td>\n"+
		      "</tr>\n") ;
      }
      output.append("</table>\n") ;
      
    } catch (JDBCException e) {
      output.append("Probleme dans la requete => "+rq+"<br/>\n"+
		    e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
    }
  }
  
  protected static void saveDetails (HttpServletRequest request) {
    String details = request.getParameter("details") ;
    if (details != null) {
      if (details.equals("OUI")) {
	request.getSession().setAttribute("details", true) ;
      } else {
	request.getSession().setAttribute("details", false) ;
      }
    }
  }
  public static Boolean getDetails(HttpServletRequest request) {
    Boolean details = (Boolean) request.getSession().
      getAttribute("details") ;
		
    return (details == null ? false : details) ;
  }

  protected static void saveMaps (HttpServletRequest request) {
    String details = request.getParameter("maps") ;
    if (details != null) {
      if (details.equals("OUI")) {
	request.getSession().setAttribute("maps", true) ;
      } else {
	request.getSession().setAttribute("maps", false) ;
      }
    }
  }
  public static Boolean getMaps(HttpServletRequest request) {
    Boolean maps = (Boolean) request.getSession().
      getAttribute("maps") ;
		
    return (maps == null ? false : maps) ;
  }

  public static String getCurrentURL(HttpServletRequest request) {
    String[] removed = {"maps", "edition", "details"} ;
    String uri = request.getRequestURI() ;
    String query = request.getQueryString();

    if (query == null) return uri+"?" ;
    
    for (int i = 0; i < removed.length; i++) {
      int idx = query.indexOf(removed[i]+"=") ;
      while (idx != -1) {
	if (idx != 0 && query.charAt(idx -1) == '&') {
	  idx-- ;
	}
	String avant = query.substring(0, idx) ;
	int end = query.indexOf("&", idx+1) ;
	String apres = "" ;
	if (end != -1) {
	  apres = query.substring(end);
	}
	query = avant+apres ;
	log.info(query);
	idx = query.indexOf(removed[i]+"=") ;
      }
    }
      
    
    return uri+"?"+ query ;
  }

  public static String getHeadBand(HttpServletRequest request) {
    StringBuilder str = new StringBuilder() ;
    str.append("<table><tr>\n"+
	       "<td>\n"+
	       "<a href='"+getCurrentURL(request)+
	       "&maps="+(getMaps(request) ?"NON" : "OUI")+"'>"+
	       (getMaps(request) ?"Carte" : "NoCarte")+
	       "</a>\n"+
	       "</td>"+
	       "<td>"+
	       "<a href='"+getCurrentURL(request)+
	       "&details="+(getDetails(request) ?"NON" : "OUI")+"'>"+
	       (getDetails(request) ?"Details" : "NoDetails")+
	       "</a>\n"+
	       "</td>") ;
    if(isLoggedAsCurrentManager(request)
       && !isRootSession(request)) {
      str.append("<td>"+
		 "<a href='"+getCurrentURL(request)+
		 "&edition="+getNextEditionMode(request)+"'>"+
		 getEditionMode(request)+"</a>"+
		 "</td>\n");
		 
      str.append("<td>"+
		 "<a href='"+Path.LOCATION+".Config'>"+
		    "Config</a>"+
		 "</td>\n");
    }
    str.append("</tr></table>\n");
    return str.toString() ;
  }
  
  public static EditMode getEditionMode(HttpServletRequest request) {
    try {
      EditMode mode = (EditMode) request.getSession()
	.getAttribute("edition") ;
      
      return (mode == null ? EditMode.NORMAL : mode) ;
    } catch (Exception e) {
			return EditMode.NORMAL ;
    }
  }
 
  public static void saveEditionMode(HttpServletRequest request) {
    String edition = request.getParameter("edition");
    EditMode editionMode ;
    EditMode next = null ;
    
    if ("VISITE".equals(edition)) {
      editionMode = EditMode.VISITE ;
    } else if ("NORMAL".equals(edition)) {
      editionMode = EditMode.NORMAL ;
    } else if ("EDITION".equals(edition)) {
      editionMode = EditMode.EDITION ;
    }else {
      editionMode = getEditionMode(request) ;
    }
        
    request.getSession().setAttribute("edition", editionMode) ;
    
  }

  public static EditMode getNextEditionMode(HttpServletRequest request) {
    EditMode editionMode = getEditionMode(request) ;
    EditMode next ;
    if (editionMode == EditMode.VISITE) {
      next = EditMode.NORMAL ;
    } else if (editionMode == EditMode.NORMAL) {
      next = EditMode.EDITION ;
    } else if (editionMode == EditMode.EDITION) {
      next = EditMode.VISITE ;
    } else {
      next = EditMode.NORMAL ;
    }
    return next ;
  }
  
  //[begin][end][nb pages][page]
  public static Integer[] calculBornes(Type type,
				       String page,
				       String asked,
				       int size) {
    int ipage ;
    int taille = (type == Type.ALBUM ? TAILLE_ALBUM : TAILLE_PHOTO) ;
    Integer[] bornes = new Integer[4] ;
    try {
      if (asked != null) {
	//compute the page into wich the element asked is in
	ipage = (int) Math.floor(Integer.parseInt(asked)/taille);
	bornes[0] = ipage * taille  ;
      } else if (page == null) {
	bornes[0] = 0 ;
	ipage = 0 ;
      } else {
	ipage = Integer.parseInt(page) ;
	bornes[0] = Math.min(ipage * taille , size)  ;
      }
    } catch (Exception e) {
      //cannot parse the page
      bornes[0] = 0 ;
      ipage = 0 ;
    }
    bornes[0] = (bornes[0] < 0 ? 0 : bornes[0]) ;
    bornes[1] = Math.min(bornes[0]+taille, size);
    bornes[2] = (int) Math.ceil(((double)size)/((double)taille)) ;
    bornes[3] = ipage ;
    return bornes ;
  }
  

  public static void displayPages(String from,
				     Integer[] bornes,
				     StringBuilder output) {
    if (bornes[2] > 1) {
      int ipage = bornes[3] ;
      output.append("<center>\n");
      if (ipage != -1 && ipage != 0) {
	output.append("<a href='"+from+"&page="+0+"'>&lt;&lt;<a/>\n");
	output.append("<a href='"+from+"&page="+(ipage - 1)+"'>&lt;<a/>\n");
      }
      
      for (int i = 0; i < bornes[2]; i++) {
	if (i == ipage || ipage == -1 && i == 0) {
	  output.append(" ~"+i+"~ ");
	} else {
	  output.append("<a href='"+from+"&page="+i+"'>"+i+"<a/>\n");
	      }
      }
      if (bornes[2] >= 3 && ipage != bornes[2] - 1 ) {
	output.append("<a href='"+from+"&page="+(ipage + 1)+"'>&gt;<a/>\n");
	output.append("<a href='"+from+"&page="+(bornes[2]-1)+"'>"+
		      "&gt;&gt;<a/>\n");
      }
      output.append("</center>\n");
    }
  }
  
  public static void preventCaching(HttpServletRequest request,
				       HttpServletResponse response) {
    // see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
    String protocol = request.getProtocol();
    if ("HTTP/1.0".equalsIgnoreCase(protocol)) {
      response.setHeader("Pragma", "no-cache");
    } else if ("HTTP/1.1".equalsIgnoreCase(protocol)) {
      response.setHeader("Cache-Control", "no-cache"); // "no-store" work also 
		}
    response.setDateHeader("Expires", 0);
  }
  
  public enum EditMode {VISITE,NORMAL,EDITION}
  public static class AccessorsException extends Exception {
    private String e ;
    public AccessorsException(String e) {
      this.e = e ;
    }
    public String toString() {
      return e ;
    }
  }
}