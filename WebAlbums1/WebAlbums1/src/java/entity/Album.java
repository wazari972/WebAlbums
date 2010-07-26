package entity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import engine.WebPage;
import entity.Photo;
import entity.UserAlbum;
import entity.base.BaseAlbum;

import java.text.ParseException ;

/**
 * This is the object class that relates to the Album table.
 * Any customizations belong here.
 */
@SuppressWarnings("unchecked")
public class Album extends BaseAlbum implements Comparable {
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
    int myID = this.getID();
    String rq = null ;
    try {
      rq = "from Photo where album = '"+myID+"'" ;
      Iterator it = WebPage.session.createQuery(rq).iterate() ;
      while (it.hasNext()) {
       	Photo photo = (Photo) it.next() ;
	if (force) {
	  photo.setTags(tags) ;
	} else {
	  photo.addTags(tags) ;
	}
      }
			
    
      
    } catch (JDBCException e) {
      throw new WebPage.AccessorsException
	("Impossible d'ajouter les tags ("+
	 Arrays.toString(tags)+") => "+rq+
	 "<br/>\n"+e);
    }
  }
  
  public String toString() {
    return "["+getClass()+"*"+getID()+"*, "+getNom()+"]";
  }
  
  public void setUsers (String[] users)
    throws WebPage.AccessorsException, HibernateException {
    int myID = this.getID();
    String rq = null ;
    
    //si aucun utilisateur n'a ete selectionner
    if (users == null) {
      rq = "from UserAlbum where album = '"+myID+"'";
      Iterator it = WebPage.session.createQuery(rq).iterate();
      rq = "done" ;
      while (it.hasNext()) {
	UserAlbum user = (UserAlbum) it.next() ;
	deleteRelatedPhotoRights(user) ;
	WebPage.session.delete(user) ;
      }
      
      return ;
    }
    
    Arrays.sort(users);
      
    //enlever les utilisateurs existants qui ne sont pas dans la nouvelle liste
    rq = "from UserAlbum where album = '"+myID+"'";
    List list = WebPage.session.createQuery(rq).list();
    rq = "done" ;
    WebPage.log.warn(list.size()+" resultats : "+list.toString());
    WebPage.log.warn("Users :"+Arrays.toString(users));
    
    Iterator it = list.iterator() ;
      
    while (it.hasNext()) {
      UserAlbum userAlbm = (UserAlbum) it.next() ;
      //si la liste des nouveaux tags ne contient pas le tag courant
      if (Arrays.binarySearch(users, userAlbm.getUser().toString()) < 0) {
	//alors enlever ce tag des tags existants
	deleteRelatedPhotoRights(userAlbm) ;
	WebPage.session.delete(userAlbm) ;
	  WebPage.log.warn("Delete : "+userAlbm.getUser());
      }
    }
      
    //ajouter les nouveaux tags qui ne sont pas encore dans la liste existante
    for (int i = 0; i < users.length; i++) {
      boolean already = false ;
      String newUser = users[i];
      
      //regarder si le tag est déjà dans la liste
      it = list.iterator() ;
      while (it.hasNext() && !already) {
	UserAlbum userAlbm = (UserAlbum) it.next() ;
	//si le tag est déjà dans la liste
	if (userAlbm.getUser().toString().equals(newUser)){
	  already = true ;
	  WebPage.log.warn("Déjà : "+newUser);
	}
      }
	
      if (!already) {
	//alors on l'ajoute
	UserAlbum nouveau = new UserAlbum();
	nouveau.setAlbum(this.getID());
	nouveau.setUser(Integer.parseInt(newUser));
	deleteRelatedPhotoRights(nouveau) ;
	WebPage.session.save(nouveau);
	WebPage.log.warn("Nouveau : "+newUser);
      }
    }
  }
	
  private void deleteRelatedPhotoRights(UserAlbum user)
    throws WebPage.AccessorsException, HibernateException {
    
    String rq = null ;
    try {
      rq = "select user from UserPhoto user, Photo photo " +
	"where user.Photo = photo.ID " +
	"and photo.Album = '"+this.getID()+"'" +
	"and user.User = '"+user.getUser()+"'" ;
      Iterator it = WebPage.session.createQuery(rq).iterate();
      rq = "done" ;

      while (it.hasNext()) {
	WebPage.session.delete(it.next()) ;
      }
    } catch (JDBCException e) {
      throw new WebPage.AccessorsException ("Impossible d'effectuer la "+
					    "requete de suppression des "+
					    "droits photo => "+rq+"<br/>\n"+e);
    }
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

  public int compareTo(Object o) {
    if (!(o instanceof Album)) return 0 ;
    Album a = (Album) o ;
    
    return 0 - getDate().compareTo(a.getDate()) ;
  }
}