package entity;

import javax.swing.ImageIcon ;
import java.awt.Image ;

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException ;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import system.wrapper.ConvertPhotoWrapper;
import util.StringUtil;
import util.ImageUtil ;
import util.XmlBuilder;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;

import constante.Path;
import engine.WebPage;
import entity.TagPhoto;
import entity.base.BasePhoto;

/**
 * This is the object class that relates to the Photo table.
 * Any customizations belong here.
 */

@SuppressWarnings("unchecked")
public class Photo extends BasePhoto {
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
      WebPage.log.info("photo "+getID()) ;
      if (tags == null) return ;
      
      rq = "FROM TagPhoto WHERE photo = '"+this.getID()+"'";
      List list = WebPage.session.createQuery(rq).list();
      rq = "done" ;
      
      //ajouter les nouveaux tags
      //qui ne sont pas encore dans la liste existante
      for (int i = 0; i < tags.length; i++) {
	WebPage.log.info("add tag "+tags[i]) ;
	boolean already = false ;
	String newTag = tags[i];
	//verifier que le tag est bien dans la base
	rq = "FROM Tag WHERE id = '"+newTag+"'";
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
	      WebPage.log.warn("Ajout du tag : "+newTag);
	      WebPage.session.save(nouveau);
	    } catch (NumberFormatException e) {
	      WebPage.log.warn("Erreur dans l'id du Tag : "+newTag);
	    }
	  } else WebPage.log.info("already: "+tags[i]) ;
	} else {
	  WebPage.log.warn("Erreur dans l'id du Tag : "+newTag+": introuvable !");
	}
      }
      
    } catch (JDBCException e) {
      e.printStackTrace() ;
      throw new WebPage.AccessorsException (
	"Impossible d'ajouter les tags ("+Arrays.toString(tags)+") "+
	"=> "+rq);
    }
  }

  public void removeTag(String tag) throws WebPage.AccessorsException  {
    String rq = null ;
    try {
      rq = "FROM TagPhoto WHERE photo = '"+this.getID()+"' AND tag = '"+tag+"'";
      TagPhoto enrTagPhoto = (TagPhoto) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;

      if (enrTagPhoto != null) {
	WebPage.session.delete(enrTagPhoto) ;
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      throw new WebPage.AccessorsException (
	"Impossible d'enlever le tag ("+tag+") ==> "+rq);
    }
  }
  public void removeExtraTags(String[] tags)
    throws WebPage.AccessorsException, HibernateException {
        
    String rq = null ;
    try {
      if (tags == null) {
	rq = "FROM TagPhoto WHERE photo = '"+this.getID()+"'";
	Iterator it = WebPage.session.createQuery(rq).iterate();
	rq = "done" ;
	
	while (it.hasNext()) {
	  WebPage.session.delete(it.next()) ;
	}
	return ;
      }
      
      Arrays.sort(tags);
      
      //enlever les tags existants qui ne sont pas dans la nouvelle liste
      rq = "FROM TagPhoto WHERE photo = '"+this.getID()+"'";
      Iterator it = WebPage.session.createQuery(rq).iterate();
      rq = "done" ;
      
      while (it.hasNext()) {
	TagPhoto enrTag = (TagPhoto) it.next() ;
	//si la liste des nouveaux tags ne contient pas le tag courant
	if (Arrays.binarySearch(tags, enrTag.getTag().toString()) < 0) {
	  //alors enlever ce tag des tags existants
	  WebPage.session.delete(enrTag) ;
	}
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      throw new WebPage.AccessorsException (
	"Impossible d'ajouter les tags ("+Arrays.toString(tags)+") ==> "+rq);
    }
  }
  
  public XmlBuilder getXmlExif() {
    XmlBuilder output = new XmlBuilder("exif") ;

    String[] exifs = new String[] {
      getModel(), getDate(), getIso(), getExposure(),
      getFocal(), getHeight(), getWidth(), getFlash()} ;
    
    for (int i = 0; i < exifs.length; i++) {
      if (exifs[i] == null) continue ;
      String[] values = exifs[i].split(" - ") ;
      XmlBuilder data = new XmlBuilder("data", values[1]);
      data.addAttribut("name", values[0]);
      output.add(data);
    }
    return output.validate() ;
  }
	
  public void retreiveExif() {
    String path = Path.getSourceURL()+"/"+Path.getImages()+"/"+ this.getPath() ;
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
      rq = "SELECT t "+
	"FROM Theme t, Album a "+
	"WHERE a.ID = '"+getAlbum()+"' "+
	"AND a.Theme = t.ID" ;
      Theme enrTh = (Theme) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;

      if (enrTh == null) {
	throw new WebPage.AccessorsException (
	  "Erreur dans Photo.rotate(), "+
	  "Impossible to find the photo's theme "+
	  "("+getID()+")");
      } else {
	themeName = enrTh.getNom () ;
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      throw new WebPage.AccessorsException ("Erreur dans Photo.rotate()") ;
    }
    
    String path = this.getPath() ;
    String mini =  Path.getSourcePath() + Path.getMini()+ "/" +themeName + "/"+ path ;
    String image = Path.getSourcePath() + Path.getImages() + "/" +themeName + "/"+ path ;
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

  public void updateDroit(Integer droit){
    if (droit != null && WebPage.getUserName(droit) == null) return ;
    super.setDroit(droit);
  }

  /***/
  
  public String getThemedPath() {
    String rq = "SELECT t FROM Theme t, Album a "+
      "WHERE t.ID = a.Theme AND a.ID = '"+this.getAlbum()+"'" ;
    Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult() ;
    rq = "done" ;
    if (enrTheme == null) {
      return null ;
    }
    return enrTheme.getNom () + "/"+ this.getPath() ;
  }


  public String getImagePath() {
    return Path.getSourcePath()+Path.getImages()+ "/" + getThemedPath();
  }

  public String getMiniPath() {
    return Path.getSourcePath()+Path.getMini()+ "/" + getThemedPath()+".png";
  }

  /***/
  public int getWidth(boolean large) {
    return getImage(large).getWidth(null);
  }

  public int getHeight(boolean large) {
    return getImage(large).getHeight(null);
  }

  private Image getImage(boolean large) {
    String path ;
    if (large) path = getImagePath() ;
    else path = getMiniPath() ;
    
    return new ImageIcon(path).getImage() ;
  }
}