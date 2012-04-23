package entity;

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException ;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import util.StringUtil;
import util.ConvertPhotoWrapper ;
import util.ImageUtil ;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;

import constante.Path;
import engine.WebPage;
import entity.TagPhoto;
import entity.UserPhoto;
import entity.base.BasePhoto;

/**
 * This is the object class that relates to the Photo table.
 * Any customizations belong here.
 */

@SuppressWarnings("unchecked")
public class Photo extends BasePhoto implements Comparable {
  private static final long serialVersionUID = 1L;

  private static final ImageUtil util = new ConvertPhotoWrapper () ;
    
  public void setTags(String[] tags)
    throws WebPage.AccessorsException, HibernateException {
    removeExtraTags(tags);
    addTags(tags);
  }
  public void addTags(String[] tags)
    throws WebPage.AccessorsException, HibernateException {
    
    String rq = null ;
    
    try {
      if (tags == null) return ;
			
      rq = "from TagPhoto where photo = '"+this.getID()+"'";
      List list = WebPage.session.createQuery(rq).list();
      rq = "done" ;
      
      //ajouter les nouveaux tags
      //qui ne sont pas encore dans la liste existante
      for (int i = 0; i < tags.length; i++) {
	boolean already = false ;
	String newTag = tags[i];
	//verifier que le tag est bien dans la base
	rq = "from Tag where id = '"+newTag+"'";
	boolean exists = (WebPage.session.createQuery(rq).uniqueResult() != null) ;
	rq = "done" ;
	if (exists) {
	  //regarder si le tag est déjà dans la liste
	  Iterator it = list.iterator() ;
	  while (it.hasNext() && !already) {
	    TagPhoto tag = (TagPhoto) it.next() ;
	    //si le tag est déjà dans la liste
	    if (tag.getTag().toString().equals(newTag)){
	      already = true ;
	    }
	  }
	
	  if (!already) {
	    //alors on l'ajoute
	    try {
	      TagPhoto nouveau = new TagPhoto();
	      nouveau.setPhoto(this.getID());
	      nouveau.setTag(Integer.parseInt(newTag));
	      
	      WebPage.session.save(nouveau);
	    } catch (NumberFormatException e) {
	      WebPage.log.warn("Erreur dans l'id du Tag : "+newTag);
	    }
	  }
	} else {
	  WebPage.log.warn("Erreur dans l'id du Tag : "+newTag+": introuvable !");
	}
      }
      
    } catch (JDBCException e) {
      throw new WebPage.AccessorsException (
	"Impossible d'ajouter les tags ("+Arrays.toString(tags)+") "+
	"=> "+rq+"<br/>\n"+e);
    }
  }
	
  public void removeExtraTags(String[] tags)
    throws WebPage.AccessorsException, HibernateException {
        
    String rq = null ;
    try {
      if (tags == null) {
	rq = "from TagPhoto where photo = '"+this.getID()+"'";
	Iterator it = WebPage.session.createQuery(rq).iterate();
	rq = "done" ;
	
	while (it.hasNext()) {
	  WebPage.session.delete(it.next()) ;
	}
	return ;
      }
      
      Arrays.sort(tags);
      
      //enlever les tags existants qui ne sont pas dans la nouvelle liste
      rq = "from TagPhoto where photo = '"+this.getID()+"'";
      List list = WebPage.session.createQuery(rq).list();
      rq = "done" ;
      
      WebPage.log.info(list.size()+" resultats : "+list.toString());
      WebPage.log.info("tags :"+Arrays.toString(tags));
      
      Iterator it = list.iterator() ;
      
      while (it.hasNext()) {
	TagPhoto tag = (TagPhoto) it.next() ;
	//si la liste des nouveaux tags ne contient pas le tag courant
	if (Arrays.binarySearch(tags, tag.getTag().toString()) < 0) {
	  //alors enlever ce tag des tags existants
	  WebPage.session.delete(tag) ;
	  WebPage.log.info("Delete : "+tag.getTag());
	}
      }
    } catch (JDBCException e) {
      throw new WebPage.AccessorsException (
	"Impossible d'ajouter les tags ("+Arrays.toString(tags)+") "+
	"=> "+rq+"<br/>\n"+e);
    }
  }

  //users[id][different?]
  public void setUsers(String[][] users)
    throws WebPage.AccessorsException, HibernateException {
        
    String rq = null ;
    int level = 0 ;
    try {
      //requete des UserPhoto a supprimer
      rq = "from UserPhoto "+
	"where photo = '"+this.getID()+"' "+
	"and user in ('-1'" ;
      for (int i = 0; i < users.length; i++) {
	if (users[i][1] == null) {
	  rq += ", '"+users[i][0]+"'" ;
	} 
      }
      rq += ")" ;
      
      Iterator it = WebPage.session.createQuery(rq).iterate() ;
      rq = "done" ;
      
      while (it.hasNext()) {
	WebPage.session.delete(it.next());
      }

      //UserPhoto a ajouter -- s'il n'existe pas
      for (int i = 0; i < users.length; i++) {
	if (users[i][1] != null) {
	  rq = "from UserPhoto "+
	    "where photo = '"+this.getID()+"' "+
	    "and user = '"+users[i][0]+"'" ;
	  UserPhoto enrUserPhoto = (UserPhoto)
	    WebPage.session.createQuery(rq).uniqueResult();
	  if (enrUserPhoto == null) {
	    enrUserPhoto = new UserPhoto() ;
	    enrUserPhoto.setPhoto(this.getID()) ;
	    enrUserPhoto.setUser (Integer.parseInt(users[i][0])) ;
	    WebPage.session.save(enrUserPhoto) ;
	  }
	} 
      }
      
      
    } catch (JDBCException e) {
      throw new WebPage.AccessorsException (
	"Erreur dans setUsers "+level+": <br/>\n"+
	rq+"<br/>"+e.getSQLException()+"<br/>") ;
    }
  }
  
  public String getExif() {
    String str = "" ;
    if (getModel() != null) str += getModel()+"\n" ;
    if (getDate() != null) str += getDate()+"\n" ;
    if (getIso() != null) str += getIso()+"\n" ;
    if (getExposure() != null) str += getExposure()+"\n";
    if (getFocal() != null) str += getFocal()+"\n";
    if (getHeight() != null ) str += getHeight()+"\n";
    if (getWidth() != null) str += getWidth()+"\n" ;
    if (getFlash() != null) str += getFlash()+"\n";
    
    return str ;
  }
	
  public void retreiveExif() {
    String path = Path.getSourceURL()+"/"+Path.IMAGES+"/"+ this.getPath() ;
    retreiveExif(path);
  }
  
  public void retreiveExif(String path) {
    try {
      File photo = new File (new URI(StringUtil.escapeURL(path))) ;
      ExifReader ex = new ExifReader(photo);
      Iterator it = ex.extract().getDirectoryIterator() ;
      while (it.hasNext()) {
	Directory dir = (Directory) it.next() ;
	Iterator it2 = dir.getTagIterator();
	while (it2.hasNext()) {
	  Tag t = (Tag)it2.next() ;
	  boolean model = false,
	    date = false,
	    iso = false,
	    expo = false,
	    focal = false,
	    height = false,
	    width = false,
	    flash = false ;
	  
	  if (!model && t.getTagName().equals("Model")) {
	    setModel(escapeBracket(t.toString())) ;
	    
	    model = true ;
	  } else if (!date && t.getTagName().equals("Date/Time")) {
	    setDate(escapeBracket(t.toString()));
	    date  = true ;
	    
	  } else if (!iso && t.getTagName().equals("ISO Speed Ratings")) {
	    setIso(escapeBracket(t.toString()));
	    iso  = true ;
	    
	  } else if (!expo && t.getTagName().equals("Exposure Time")) {
	    setExposure(escapeBracket(t.toString()));
	    expo = true ;
	    
	  } else if (!focal && t.getTagName().equals("Focal Length")) {
	    setFocal(escapeBracket(t.toString()));
	    focal = true ;
						
	  } else if (!height && t.getTagName().equals("Exif Image Height")) {
	    setHeight(escapeBracket(t.toString()));
	    height = true ;
	    
	  } else if (!width && t.getTagName().equals("Exif Image Width")) {
	    setWidth(escapeBracket(t.toString()));
	    width = true ;
						
	  } else if (!flash && t.getTagName().equals("Flash")) {
	    setFlash(escapeBracket(t.toString()));
	    flash = true ;
	  }
	}
      }
    } catch (JpegProcessingException e) {
      WebPage.log.warn("Exception JPEG durant le traitement exif : "+e) ;
      WebPage.log.warn(path) ;
    } catch (URISyntaxException e) {
      WebPage.log.warn("URISyntaxException durant le traitement exif : "+e) ;
      WebPage.log.warn(path) ;
    }
  }
  private String escapeBracket(String str) {
    int pos = str.indexOf("]") ;
    return str.substring(pos + 2);
  }
  
  public boolean rotate (String degrees)
    throws  WebPage.AccessorsException, HibernateException {
      
      if (getType() != null && !getType().contains("image")) {
	  return true ;
      }

    String rq = null ;
    String themeName = null ;
    try {
      rq = "select t "+
	"from Theme t, Album a "+
	"where a.ID = '"+getAlbum()+"' "+
	"and a.Theme = t.ID" ;
      Theme enrTh = (Theme) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;

      if (enrTh == null) {
	throw new WebPage.AccessorsException (
	  "Erreur dans Photo.rotate()<br/>\n"+
	  "Impossible to find the photo's theme "+
	  "("+getID()+")");
      } else {
	themeName = enrTh.getNom () ;
      }
    } catch (JDBCException e) {
      throw new WebPage.AccessorsException (
	"Erreur dans Photo.rotate() : <br/>\n"+
	rq+"<br/>"+e.getSQLException()+"<br/>") ;
    }
    
    String path = this.getPath() ;
    String mini =  Path.getSourcePath() + Path.MINI + "/" +themeName + "/"+ path ;
    String image = Path.getSourcePath() + Path.IMAGES + "/" +themeName + "/"+ path ;
    WebPage.log.info("Rotation de "+degrees+" de "+path);
    if (util.rotate(degrees, mini+".png", mini+".png")) {
      if (!util.rotate(degrees, image, image)) {
	util.rotate("-"+degrees, mini+".png", mini+".png");
	return false ;
      } else {
	return true ;
      }
    } else {
      return false ;
    }
  }

  public int compareTo(Object o) {
    if (!(o instanceof Photo)) return -1 ;
    Photo p = (Photo) o ;

    return getPath().compareTo(p.getPath()) ;
  }
}