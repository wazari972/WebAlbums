package util;

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
      log.addAppender(
	new FileAppender(new SimpleLayout(),
			 "/tmp/FilesFinder.log"));
      log.fatal("====================\n\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
	
  public boolean importAuthor(HttpServletRequest request,
			      String themeName,
			      String passwrd,
			      StringBuilder out)
    throws HibernateException {
    String rq = null ;
    boolean correct = false ;
    File theme = null ;
    
    if (!resizer.isDone()) {
      info (out, "The previous resize stack is not empty "+
	    "("+resizer.getStackSize() +"), "+
	    "please wait a second ") ;
      return false ;
    }
    Transaction tx = WebPage.session.beginTransaction() ;
    try {
      info (out, "Importing for theme : "+themeName) ;
            
      //plus prevention des problemes de ' => ''
      rq = "from Theme "+
	"where nom = '"+themeName.replace("'", "''")+"'" ;
      Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      //si l'auteur n'est pas encore dans la base de données,
      //on l'ajoute
      if (enrTheme == null) {
	info (out, "Le theme n'est pas dans la table") ;
	enrTheme = new Theme () ;
	enrTheme.setNom(themeName) ;
	if (passwrd != null && !passwrd.equals("")) {
	  enrTheme.setPassword(passwrd) ;
	  WebPage.session.save(enrTheme) ;
	  info (out, "Le theme a correctement été ajouté") ;
	  correct = true ;
	} else {
	  info (out, "Nouveau theme sans mot de passe, "+
		"on abandonne ...") ;
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
	    info (out, "Le mot de passe est incorrect...") ;
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
	
	Thread th = new Thread (resizer) ;
	th.setName("Resizer stack") ;
	th.start() ;
	
	theme = new File(Path.getSourcePath()+Path.FTP+"/"+themeName+"/");
	info (out, "Dossier source : "+theme.toString()) ;
	//creer le dossier d'import s'il n'existe pas encore
	if (!theme.isDirectory()) {
	  info(out, "Creation du dossier d'import ("+theme+")");
	  theme.mkdirs() ;
	}
	if (!theme.isDirectory()) {
	  info (out, theme.getAbsolutePath()+
		" n'est pas un dossier/impossible de le creer  ... ") ;
	  correct = false ;
	} else {
	  this.themeName = themeName ;
	  int myID = enrTheme.getID() ;
	  info(out, "ID du theme : "+myID+"");
	  File[] subfiles = theme.listFiles();
	  
	  info (out, "Le dossier '"+themeName+
		"' contient "+ subfiles.length+
		" fichier"+(subfiles.length > 1 ? "s" : ""));

	  correct = true ;
	  int err = 0 ;
	  for (int i=0 ; i < subfiles.length; i++){
	    info (out, "Important de l'album "+subfiles[i]+"");
	    if (!importAlbum(subfiles[i], myID, out)) {
	      info (out, "<b>An error occured during "+
		    "importantion of album ("+subfiles[i]+")...</b>") ;
	      correct = false ;
	      err++ ;
	    }
	  }
	  info (out, "## Import of theme "+themeName+" completed") ;
	  if (err != 0) info (out, "## with "+err+" errors");
	  
	}
      } else {
	info (out, "An error occured during initialization process ...") ;
	correct = false ;
      }
    
    } catch (JDBCException e) {
      info (out, "Erreur de requete ... "+rq+" : "+e) ;
      correct = false ;
    }
	
    info (out, "Say to the Resizer that we are done") ;
    resizer.terminate (theme) ;
    tx.commit () ;
    return correct ;
  }

  private boolean importAlbum (File album, int authorID, StringBuilder out)
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
	  nom = album.getName().substring(11) ;
	  info (out, "## NOM  : " +nom) ;

	  strDate = album.getName().substring(0, 10) ;
	  Date date = DATE_STANDARD.parse(strDate) ;
	  info (out, "## DATE : " +date) ;
	  
	} catch(StringIndexOutOfBoundsException e) {
	  info (out, "## Erreur dans le format du nom de l'album "+
		"("+album+"), on skip");
	  return false ;
	} catch(ParseException e) {
	  info (out, "## Erreur dans le format de la date "+
		"("+strDate+"), on skip");
	  return false ; 
	}
	
	//rechercher s'il est deja dans la liste
	rq = "from Album "+
	  "where date = '"+strDate+"' and Nom = '"+nom+"'";
	Album enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult();
       	rq = "done" ;
		
	if (enrAlbum == null) {
	  //si il n'y est pas, on l'ajoute
	  info (out, "## L'album n'est pas dans la table") ;
	  enrAlbum = new Album () ;
	  
	  enrAlbum.setNom(nom);
	  enrAlbum.setDescription("");
	  enrAlbum.setTheme(authorID) ;
	  enrAlbum.setSource("-");
	  enrAlbum.setDate(strDate);
	  	  
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
	  info(out, "Impossible de connaitre le nombre de fichiers ..."+
	       "(dossier ? "+album.isDirectory()+")");
	}
	return true ;
      }
    } catch (JDBCException e) {
      if ("done".equals(rq)) {
	info (out, "Erreur pendant le commit ... "+e+
	      " > "+e.getSQLException()) ;
      } else {
	info (out, "Erreur de requete Album ... "+rq+" : "+e+
	      " > "+e.getSQLException()) ;
      }
      return false ;
    }
  }
  
  private boolean importPhoto (File photo,
			       int albumID,
			       StringBuilder out)
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
	info (out, "### URL mal formée ..." + e);
	return false ;
      } catch (IOException e) {
	info (out, "### Erreur d'IO ..." + e);
	return false ;
      }
      
      if (!ImageResizer.support(type)) {
	  info (out, "### "+photo+" n'est pas supportée ... ("+type+")");
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
      info (out, "Erreur de requete Photo ... "+rq+" : "+
	    e+" > "+e.getSQLException()) ;
      return false;
    }
  }
  @SuppressWarnings("unchecked")
  public static void deleteAlbum(String albumID, StringBuilder out)
    throws HibernateException {
    
    Transaction tx = null ;
    String rq = null ;
    try {
      rq = "from Album where ID ='"+albumID+"'" ;
      Album enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      if (enrAlbum == null) {
	info (out, "Impossible de trouver l'album ("+albumID+")");
	return ;
      }

      rq = "from Photo where album ='"+albumID+"'" ;
      Iterator it = WebPage.session.createQuery(rq).iterate() ;
      rq = "done" ;
      Photo enrPhoto ;
      while (it.hasNext()) {
	enrPhoto = (Photo) it.next() ;
	String ret = deletePhoto(enrPhoto.getID().toString(), out) ;
	if (ret.contains("Erreur")) {
	  info(out, ret);
	}
      }

      //if (!ok) return ;
                  
      tx = WebPage.session.beginTransaction() ;
      //suppression des droits utilisateur de cette photo
      rq = "from UserAlbum where album ='"+albumID+"'" ;
      it = WebPage.session.createQuery(rq).iterate() ;
      rq = "done" ;
      while (it.hasNext()) {
	UserAlbum up = (UserAlbum) it.next() ;
	WebPage.session.delete(up) ;
      }
      //suppression de l'album
      WebPage.session.delete(enrAlbum) ;
      tx.commit();
    } catch (JDBCException e) {
      info (out, "Erreur dans la requete ... "+rq+
	    " =>"+e+"\n"+e.getSQLException());
      if (tx != null) tx.rollback() ;
    }
  }
  @SuppressWarnings("unchecked")
    //retourne obligatoirement "Erreur ..." en cas d'echec
  public static String deletePhoto(String photoID,
				   StringBuilder out)
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
	      info(out, "theme impossible à trouver ... (photo="+photoID+")");
	      return "Theme impossible à trouver (photo="+photoID+")" ;
	  }
	  info (out, "Traitement de la photo "+enrPhoto.getID());
	  tx = WebPage.session.beginTransaction() ;	  					
	  //suppression des droits utilisateur de cette photo
	  rq = "from UserPhoto where photo ='"+photoID+"'" ;
	  Iterator it2 = WebPage.session.createQuery(rq).iterate() ;
	  rq = "done" ;
	  while (it2.hasNext()) {
	    UserPhoto up = (UserPhoto) it2.next() ;
	    WebPage.session.delete(up) ;
	  }
	  //suppression des tags de cette photo
	  rq = "from TagPhoto where photo ='"+photoID+"'" ;
	  it2 = WebPage.session.createQuery(rq).iterate() ;
	  rq = "done" ;
	  
	  while (it2.hasNext()) {
	    TagPhoto up = (TagPhoto) it2.next() ;
	    WebPage.session.delete(up) ;
	  }
	  	  
	  //suppression des photos physiquement
	  url = source+Path.IMAGES + Path.SEP + enrTheme.getNom() + Path.SEP + enrPhoto.getPath() ;
	  
	  fichier = new File (new URL(StringUtil.escapeURL(url)).toURI()) ;
	  info (out, "On supprime sa photo :"+ url);
	  if (!fichier.delete()) {
	      info (out, "mais ça marche pas ...");
	  }
	  //pas de rep vide
	  fichier.getParentFile().delete() ;

	  //miniature
	  url = source+Path.MINI + Path.SEP + enrTheme.getNom() + Path.SEP + enrPhoto.getPath()+".png" ;	  
	  fichier = new File (new URL(StringUtil.escapeURL(url)).toURI()) ;
	  info (out, "On supprime sa miniature :"+ url);
	  if (!fichier.delete()) {
	      info (out, "mais ça marche pas ...");
	  }
	  //pas de rep vide
	  fichier.getParentFile().delete() ;
	  //suppression du champs dans la table Photo
	  WebPage.session.delete(enrPhoto) ;
	  tx.commit() ;

	} catch (MalformedURLException e) {
	  info (out, "MalformedURLException "+url);
	  info (out,e.toString());
	  if (tx != null) tx.rollback();
	} catch (URISyntaxException e) {
	  info (out, "URISyntaxException "+url);
	  info (out,e.toString());
	  if (tx != null) tx.rollback();
	}
      }
      return "Photo correctement supprimée !" ;

    } catch (JDBCException e) {
      String erreur = "Erreur dans la requete ... "+
	rq+" =>"+e+"\n"+e.getSQLException() ;
      info (out, erreur);
      if (tx != null) tx.rollback();
      return erreur ;
    }
  }
	
  
  private static void info (StringBuilder out, String msg) {
    out.append(msg + "<br/>\n") ;
    log.info (msg);
  }
}