package Entity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import util.HibernateUtil;
import display.WebPage;
import Entity.base.BasePhoto;

/**
 * This is the object class that relates to the Photo table.
 * Any customizations belong here.
 */
public class Photo extends BasePhoto {
	private static final long serialVersionUID = 1L;

	public Photo () {
		super();
	}
	public Photo (java.lang.Integer _iD) {
		super(_iD);
	}
	
	public void setThemes(String[] themes) throws HibernateException {
		removeExtraThemes(themes);
		addThemes(themes);
	}

	public void addThemes(String[] themes) throws HibernateException {
		Session session = HibernateUtil.currentSession();
		Transaction tx = session.beginTransaction() ;
		
		if (themes == null) return ;
		
		String rq = "from TagPhoto where PhotoId = '"+this.getID()+"'";
		List list = session.find(rq);
		
		//ajouter les nouveaux tags qui ne sont pas encore dans la liste existante
		for (int i = 0; i < themes.length; i++) {
			boolean already = false ;
			String newTag = themes[i];
			
			//regarder si le tag est déjà dans la liste
			Iterator it = list.iterator() ;
			while (it.hasNext() && !already) {
				TagPhoto tag = (TagPhoto) it.next() ;
				//si le tag est déjà dans la liste
				if (tag.getTagID().toString().equals(newTag)){
					already = true ;
					WebPage.log.info("Déjà : "+newTag);
				}
			}
			
			if (!already) {
				//alors on l'ajoute
				TagPhoto nouveau = new TagPhoto();
				nouveau.setPhotoID(this.getID());
				nouveau.setTagID(Integer.parseInt(newTag));
				
				session.save(nouveau);
				WebPage.log.warn("Nouveau : "+newTag);
			}
		}
		tx.commit();
	}
	
	public void removeExtraThemes(String[] themes) throws HibernateException {
		Session session = HibernateUtil.currentSession();
		Transaction tx = session.beginTransaction() ;
		
		if (themes == null) {
			String rq = "from TagPhoto where PhotoID = '"+this.getID()+"'";
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
		String rq = "from TagPhoto where PhotoId = '"+this.getID()+"'";
		List list = session.find(rq);
		
		WebPage.log.info(list.size()+" resultats : "+list.toString());
		WebPage.log.info("Themes :"+Arrays.toString(themes));
		
		Iterator it = list.iterator() ;
		
		while (it.hasNext()) {
			TagPhoto tag = (TagPhoto) it.next() ;
			//si la liste des nouveaux tags ne contient pas le tag courant
			if (Arrays.binarySearch(themes, tag.getTagID().toString()) < 0) {
				//alors enlever ce tag des tags existants
				session.delete(tag) ;
				WebPage.log.info("Delete : "+tag.getTagID());
			}
		}
		tx.commit();
	}
}