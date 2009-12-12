package system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import constante.Path;
import engine.WebPage ;
import entity.*;
import util.ImageResizer;
import util.StringUtil ;
import util.XmlBuilder;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Transaction;


public class FilesFinder {
	
  public static final SimpleDateFormat DATE_STANDARD =
    new SimpleDateFormat("yyyy-MM-dd");
  public static final Logger log =
    Logger.getLogger("FilesFinder");
  
  private String themeName ;
  private int annee ;
  private String dossier ;
  
  private static ImageResizer resizer = new ImageResizer () ;
  
  static {
    log.setLevel(Level.ALL);
    try {
      log.addAppender(new FileAppender(new SimpleLayout(), Path.getTempDir()+"/FilesFinder.log"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
	
  public boolean importAuthor(HttpServletRequest request,
			      String themeName,
			      String passwrd,
			      XmlBuilder out)
    throws HibernateException {
    String rq = null ;
    boolean correct = false ;

    if (!resizer.isDone()) {
      warn (out, "The previous resize stack is not empty "+
	    "("+resizer.getStackSize() +"), "+
	    "please wait a second ") ;
      return false ;
    }
    Transaction tx = WebPage.session.beginTransaction() ;
    try {
      info (out, "Importing for theme : "+themeName) ;
      
      rq = "from Theme where nom = '"+themeName+"'" ;
      Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      //si l'auteur n'est pas encore dans la base de données,
      //on l'ajoute
      if (enrTheme == null) {
	if (String.CASE_INSENSITIVE_ORDER.compare("root", themeName) == 0) {
	  out.addException("root is a reserved keyword");
	  out.validate();
	  return false ;
	}

	if (themeName.indexOf(' ') != -1) {
	  out.addException("pas d'espace dans le nom du theme");
	  out.validate();
	  return false ;
	}
	
	info (out, "Le theme n'est pas dans la table") ;
	if (passwrd != null && !passwrd.equals("")) {
	  enrTheme = new Theme () ;
	  enrTheme.setNom(themeName) ;
	  enrTheme.setPassword(passwrd) ;
	  
	  WebPage.session.save(enrTheme) ;
	  tx = null ;
	  info (out, "Le theme a correctement été ajouté") ;
	  correct = true ;
	} else {
	  warn (out, "Nouveau theme sans mot de passe...");
	  enrTheme = null ;
	}
      } 
      //if theme already exists
      else {
	info (out, "Le theme est dans la table");
	//pas besoin de mot de passe si on importe le theme courant
	if (!Integer.toString(enrTheme.getID()).equals(
	      WebPage.getThemeID(request))) {
	  if (passwrd == null ||
	      !passwrd.equals(enrTheme.getPassword())) {
	    warn (out, "Le mot de passe est incorrect...") ;
	  } else {
	    info (out, "Mot de passe correct") ;
	    correct = true ;
	  }
	} else {
	  info (out, "Pas besoin de mot de passe") ;
	  correct = true ;
	}
      }

      //if init was performed correctly
      if (correct) {
	File dirTheme = null ;
    
	dirTheme = new File(Path.getSourcePath()+Path.getFTP()+"/"+themeName+"/");
	info (out, "Dossier source : "+dirTheme) ;
	//creer le dossier d'import s'il n'existe pas encore
	if (!dirTheme.isDirectory()) {
	  info(out, "Creation du dossier d'import ("+dirTheme+")");
	  dirTheme.mkdirs() ;
	}
	
	if (!dirTheme.isDirectory()) {
	  warn (out, dirTheme.getAbsolutePath()+ " n'est pas un dossier/impossible de le creer  ... ") ;
	  correct = false ;
	} else {
	  Thread th = new Thread (resizer) ;
	  th.setName("Resizer stack") ;
	  th.start() ;
	  
	  
	  this.themeName = themeName ;
	  int myID = enrTheme.getID() ;
	  info(out, "ID du theme : "+myID+"");
	  File[] subfiles = dirTheme.listFiles();
	  
	  warn (out, "Le dossier '"+themeName+"' contient "+
		subfiles.length+" fichier"+(subfiles.length > 1 ? "s" : ""));

	  correct = true ;
	  int err = 0 ;
	  for (int i=0 ; i < subfiles.length; i++){
	    if (subfiles[i].isDirectory()) {
	      info (out, "Important de l'album "+subfiles[i]+"");
	      if (!importAlbum(subfiles[i], myID, out)) {
		warn (out, "An error occured during "+
		      "importation of album ("+subfiles[i]+")...") ;
		correct = false ;
		err++ ;
	      }
	      subfiles[i].delete() ;
	    }
	  }

	  info (out, "## Import of theme "+themeName+" completed") ;
	  if (err != 0) warn (out, "## with "+err+" errors");
	}
      } 

      if (!correct) {
	warn (out, "An error occured during initialization process ...") ;
      }
    
    } catch (JDBCException e) {
      e.printStackTrace();
      warn (out, "Erreur de requete ... "+rq) ;
      correct = false ;

    }
    info (out, "Say to the Resizer that we are done") ;
    resizer.terminate () ;
    tx.commit() ;	
    return correct ;
  }

  private boolean importAlbum (File album, int authorID, XmlBuilder out)
    throws HibernateException  {
    info (out, "##");
    info (out, "## Import of : "+album.getName()) ;
    
    String rq = null ;
    try {
      if (!album.exists() || !album.isDirectory()){
	info (out, "## Le dossier Album '"+album.getName()+"' n'existe pas") ;
	
	return false ;
      } else {
	String nom, strDate = null ;
		
	try {
	  nom = StringUtil.escapeHTML(album.getName().substring(11)) ;
	  info (out, "## NOM  : " +nom) ;

	  strDate = album.getName().substring(0, 10) ;
	  Date date = DATE_STANDARD.parse(strDate) ;
	  info (out, "## DATE : " +date) ;
	  
	} catch(StringIndexOutOfBoundsException e) {
	  warn (out, "## Erreur dans le format du nom de l'album "+
		"("+album+"), on skip");
	  return false ;
	} catch(ParseException e) {
	  warn (out, "## Erreur dans le format de la date "+
		"("+strDate+"), on skip");
	  return false ; 
	}
	
	//rechercher s'il est deja dans la liste
	rq = "FROM Album a "+
	  " WHERE a.Date = '"+strDate+"' "+
	  " AND a.Nom = '"+nom+"'";
	Album enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult();
       	rq = "done" ;
		
	if (enrAlbum == null) {
	  //si il n'y est pas, on l'ajoute
	  info (out, "## L'album n'est pas dans la table") ;
	  enrAlbum = new Album () ;
	  
	  enrAlbum.setNom(nom);
	  enrAlbum.setDescription("");
	  enrAlbum.setTheme(authorID) ;
	  enrAlbum.setDate(strDate);
	  enrAlbum.setDroit(3);
	  	  
	  info (out, "## On tente d'ajouter l'album dans la base");
	  WebPage.session.save(enrAlbum) ;
	  info (out, "## On vient de lui donner l'ID "+enrAlbum.getID()) ;
	  
	} else {
	  info (out, "## L'album est dans la table : ID "+enrAlbum.getID()) ;
	  enrAlbum.setTheme(authorID) ;
	  WebPage.session.update(enrAlbum) ;
	}
	int err = 0 ;
	int myID = enrAlbum.getID() ;
	
	//definition des attributs de l'album pour la prochaine procedure
	dossier = album.getName () ;
	annee = Integer.parseInt(dossier.substring(0, 4)) ;
	info (out, "## Année : "+annee);

	File[] subfiles = album.listFiles();
	if (subfiles != null) {
	  
	  info (out, "## Le répertoire '"+dossier+
		"' contient "+ subfiles.length+
		" fichier"+(subfiles.length > 1 ? "s":""));
	  
	  for (int i = 0 ; i < subfiles.length; i++){
	    info (out, "## Traitement de "+subfiles[i].getName());
	    if (!importPhoto(subfiles[i], myID, out)) {
	      err++ ;
	    }
	  }
	  info (out, "## Import of : "+album.getName() +" completed") ;
	  if (err != 0) info (out, "## with "+err+" errors");

	} else {
	  warn(out, "Impossible de connaitre le nombre de fichiers ..."+
	       "(dossier ? "+album.isDirectory()+")");
	}
	return true ;
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      warn (out, rq);
      warn (out, e.getSQLException()) ;
      
      return false ;
    }
  }
  
  private boolean importPhoto (File photo,
			       int albumID,
			       XmlBuilder out)
    throws HibernateException {
    info (out, "### Import of : "+photo.getName() +"") ;
    String rq = null ;
    try {
      if ("Thumbs.db".equals(photo.getName())) {
	info (out, "### Supression de "+photo);
	photo.delete() ;
	return true ;
      }
      //verification du type du fichier
      String type = null ;
      try {
	URL url = photo.toURI().toURL();
	URLConnection connection = url.openConnection();
	type = connection.getContentType();
	
	info (out, "### Type : "+type);
	
      } catch (MalformedURLException e) {
	warn (out, "### URL mal formée ..." + e);
	return false ;
      } catch (IOException e) {
	warn (out, "### Erreur d'IO ..." + e);
	return false ;
      }
      
      if (!ImageResizer.support(type)) {
	  warn (out, "### "+photo+" n'est pas supportée ... ("+type+")");
	  return true;
      }

      String path = annee+"/"+dossier+"/"+photo.getName() ;
      
      //plus prevention des problemes de ' => ''
      rq = "from Photo where Path = '"+path.replace("'", "''")+"'" ;
      Photo enrPhoto = (Photo) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      
      //si l'image (son path) n'est pas encore dans la base
      if (enrPhoto == null) {
	info (out, "### Creation d'un nouvel enregistrement");
	//on crée la nouvelle photo
	enrPhoto = new Photo () ;
	enrPhoto.setDescription("") ;
	enrPhoto.setPath(path) ;
	enrPhoto.retreiveExif("file://"+photo.getAbsolutePath()) ;
	enrPhoto.setAlbum (albumID) ;
	enrPhoto.setType (type) ;
	
	WebPage.session.save (enrPhoto) ;
      } else /* sinon on update son nom d'album*/ {
	info (out, "### Mise à jour de l'enregistrement");
	enrPhoto.setAlbum (albumID) ;

	WebPage.session.update (enrPhoto) ;
      }
      
      ImageResizer.Element elt =
	  new ImageResizer.Element (this.themeName+"/"+path, photo, type) ;
      resizer.push (elt) ;
      
      info (out, "### Import of : "+photo.getName() +" : completed") ;
      return true ;
    } catch (JDBCException e) {
      e.printStackTrace() ;
      warn (out, rq);
      warn (out, e.getSQLException()) ;
      
      return false;
    }
  }
  @SuppressWarnings("unchecked")
  public static boolean deleteAlbum(String albumID, XmlBuilder out)
    throws HibernateException {
    
    Transaction tx = null ;
    String rq = null ;
    try {
      rq = "from Album where ID ='"+albumID+"'" ;
      Album enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      if (enrAlbum == null) {
	info (out, "Impossible de trouver l'album ("+albumID+")");
	return false ;
      }

      rq = "from Photo where album ='"+albumID+"'" ;
      Iterator it = WebPage.session.createQuery(rq).iterate() ;
      rq = "done" ;
      Photo enrPhoto ;
      boolean correct = true ;
      while (it.hasNext()) {
	enrPhoto = (Photo) it.next() ;
	if (!deletePhoto(enrPhoto.getID().toString(), out)) {
	  warn(out, "Problem during the deletion ...");
	  correct = false ;
	}
      }
      if (correct) {
	tx = WebPage.session.beginTransaction() ;
	//suppression de l'album
	WebPage.session.delete(enrAlbum) ;
	tx.commit();
	return true ;
      } 
    } catch (JDBCException e) {
      e.printStackTrace() ;
      warn (out, rq);
      warn (out, e.getSQLException()) ;
      
      if (tx != null) tx.rollback() ;
    }
    return false ;
  }
  @SuppressWarnings("unchecked")
  public static boolean deletePhoto(String photoID,
				    XmlBuilder out)
    throws HibernateException {

    String source =  Path.getSourceURL() ;
    String rq = null ;
    Transaction tx = null ;
    try {
      rq = "from Photo where ID ='"+photoID+"'" ;
      Photo enrPhoto = (Photo) WebPage.session.createQuery(rq).uniqueResult() ;
      
      File fichier ;
      String url = null ;
      if (enrPhoto != null) {
	try {
	  rq = "select t from Album a, Theme t where a.ID ='"+enrPhoto.getAlbum()+"'"+
	      " and a.Theme = t.ID ";
	  Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult() ;
	  rq = "done" ;

	  if (enrTheme == null) {
	      warn(out, "theme impossible à trouver ... (photo="+photoID+")");
	      return false ;
	  }
	  info (out, "Traitement de la photo "+enrPhoto.getID());
	  tx = WebPage.session.beginTransaction() ;
	  
	  //suppression des tags de cette photo
	  rq = "from TagPhoto where photo ='"+photoID+"'" ;
	  Iterator it2 = WebPage.session.createQuery(rq).iterate() ;
	  rq = "done" ;
	  
	  while (it2.hasNext()) {
	    TagPhoto up = (TagPhoto) it2.next() ;
	    WebPage.session.delete(up) ;
	  }
	  	  
	  //suppression des photos physiquement
	  url = source+Path.getImages() + Path.SEP + enrTheme.getNom() + Path.SEP + enrPhoto.getPath() ;
	  
	  fichier = new File (new URL(StringUtil.escapeURL(url)).toURI()) ;
	  info (out, "On supprime sa photo :"+ url);
	  if (!fichier.delete()) {
	      warn (out, "mais ça marche pas ...");
	  }
	  //pas de rep vide
	  fichier.getParentFile().delete() ;

	  //miniature
	  url = source+Path.getMini() + Path.SEP + enrTheme.getNom() + Path.SEP + enrPhoto.getPath()+".png" ;	  
	  fichier = new File (new URL(StringUtil.escapeURL(url)).toURI()) ;
	  info (out, "On supprime sa miniature :"+ url);
	  if (!fichier.delete()) {
	      warn (out, "mais ça marche pas ...");
	  }
	  //pas de rep vide
	  fichier.getParentFile().delete() ;
	  //suppression du champs dans la table Photo
	  WebPage.session.delete(enrPhoto) ;
	  tx.commit() ;

	  info(out, "Photo correctement supprimée !") ;
	  out.validate() ;
	  return true ;
	} catch (MalformedURLException e) {
	  e.printStackTrace() ;
	  warn (out, "MalformedURLException "+url);
	  warn (out,e.toString());
	  if (tx != null) tx.rollback();
	} catch (URISyntaxException e) {
	  e.printStackTrace() ;
	  warn (out, "URISyntaxException "+url);
	  warn (out,e.toString());
	  if (tx != null) tx.rollback();
	}
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      warn (out, rq);
      warn (out, e.getSQLException()) ;
    }
    if (tx != null) tx.rollback();
    return false ;
  }

  private static void warn (XmlBuilder output, Object msg) {
    if (msg != null) {
      output.add("Exception", msg.toString()) ;
      log.warn (msg.toString());
    }
  }
  
  private static void info (XmlBuilder output, Object msg) {
    output.add("message", msg.toString()) ;
    log.info (msg.toString());
  }
}