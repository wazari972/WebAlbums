package Entity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import display.WebPage;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import util.HibernateUtil;

import Entity.base.BaseAlbum;

/**
 * This is the object class that relates to the Album table.
 * Any customizations belong here.
 */
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

	public void setThemes(String[] themes) throws HibernateException {
		int myID = this.getID();
		Session session = HibernateUtil.currentSession();
		Transaction tx = session.beginTransaction() ;
		
		if (themes == null) {
			String rq = "from TagAlbum where AlbumId = '"+myID+"'";
			List list = session.find(rq);
			Iterator it = list.iterator() ;
			while (it.hasNext()) {
				session.delete(it.next()) ;
			}
			tx.commit();
			return ;
		}
		
		Arrays.sort(themes);
		
		//enlever les tags existants qui ne sont pas dans la nouvelle liste
		String rq = "from TagAlbum where AlbumId = '"+myID+"'";
		List list = session.find(rq);
		
		WebPage.log.info(list.size()+" resultats : "+list.toString());
		WebPage.log.info("Themes :"+Arrays.toString(themes));
		
		Iterator it = list.iterator() ;
		
		while (it.hasNext()) {
			TagAlbum tag = (TagAlbum) it.next() ;
			//si la liste des nouveaux tags ne contient pas le tag courant
			if (Arrays.binarySearch(themes, tag.getTagID().toString()) < 0) {
				//alors enlever ce tag des tags existants
				session.delete(tag) ;
				WebPage.log.info("Delete : "+tag.getTagID());
			}
		}
		
		//ajouter les nouveaux tags qui ne sont pas encore dans la liste existante
		for (int i = 0; i < themes.length; i++) {
			boolean already = false ;
			String newTag = themes[i];
			
			//regarder si le tag est déjà dans la liste
			it = list.iterator() ;
			while (it.hasNext() && !already) {
				TagAlbum tag = (TagAlbum) it.next() ;
				//si le tag est déjà dans la liste
				if (tag.getTagID().toString().equals(newTag)){
					already = true ;
					WebPage.log.info("Déjà : "+newTag);
				}
			}
			
			if (!already) {
				//alors on l'ajoute
				TagAlbum nouveau = new TagAlbum();
				nouveau.setAlbumID(this.getID());
				nouveau.setTagID(Integer.parseInt(newTag));
				
				session.save(nouveau);
				WebPage.log.warn("Nouveau : "+newTag);
			}
		}
		tx.commit();
	}
	
	public String toString() {
		return "["+getClass()+"*"+getID()+"*, "+getNom()+"]";
	}
}