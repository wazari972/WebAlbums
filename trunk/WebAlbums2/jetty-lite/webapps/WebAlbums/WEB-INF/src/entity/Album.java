package entity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import engine.WebPage;
import entity.Photo;
import entity.base.BaseAlbum;

import java.text.ParseException ;
import java.io.File ;
import engine.Users ;
import javax.servlet.http.HttpServletRequest;

import system.SystemTools ;

/**
 * This is the object class that relates to the Album table.
 * Any customizations belong here.
 */
@SuppressWarnings("unchecked")
public class Album extends BaseAlbum {
  private static final long serialVersionUID = 1L;

  /*[CONSTRUCTOR MARKER BEGIN]*/
  public Album () {
    super();
  }
  
  /**
   * Constructor for primary key
   */
  public Album (java.lang.Integer _iD) {
    super(_iD);
  }
/*[CONSTRUCTOR MARKER END]*/

  public void setTagsToPhoto(String[] tags,
			     boolean force)
    throws HibernateException, WebPage.AccessorsException {
    String rq = null ;
    try {
      rq = "FROM Photo WHERE album = '"+this.getID()+"'" ;
      Iterator it = WebPage.session.createQuery(rq).iterate() ;
      while (it.hasNext()) {
       	Photo enrPhoto = (Photo) it.next() ;
	WebPage.log.info("apply tags to "+enrPhoto.getID()) ;
	if (force) {
	  enrPhoto.setTags(tags) ;
	} else {
	  enrPhoto.addTags(tags) ;
	}
      }     
    } catch (JDBCException e) {
      e.printStackTrace() ;
      throw new WebPage.AccessorsException
	("Impossible d'ajouter les tags ("+
	 Arrays.toString(tags)+") => "+rq);
    }
  }
  
  public String toString() {
    return "["+getClass()+"*"+getID()+"*, "+getNom()+"]";
  }
  
  public void setDateStr(String date) {
    if (date != null) {
      try {
	//verification
	new SimpleDateFormat("yyyy-MM-dd").parse(date) ;
	this.setDate(date) ;
      } catch(ParseException e) {}
    }
  }
	
  @Override
  public void setNom (String nom) {
    if (nom == null) return ;
    
    super.setNom(nom) ;
  }

  public void updateDroit(Integer droit){
    if (WebPage.getUserName(droit) == null) return ;
    if (droit.equals(getDroit())) return ;

    super.setDroit(droit);
 
    String rq = "FROM Photo p WHERE p.Album = '"+this.getID()+"'" ;
    Iterator it = WebPage.session.createQuery(rq).iterate() ;
    while (it.hasNext()) {
      Photo enrPhoto = (Photo) it.next() ;
      enrPhoto.setDroit(null) ;
    }
  }

  public Photo getPictureEnt() {
    String rq = "FROM Photo p WHERE p.ID = '"+this.getPicture()+"'" ;
    return (Photo) WebPage.session.createQuery(rq).uniqueResult() ;
  }
}