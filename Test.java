import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.sf.hibernate.*;
import util.*;
import Entity.* ;

public class Test {
		
	public static final SimpleDateFormat DATE_STANDARD = new SimpleDateFormat("yyyy-MM-dd");
	public static final Logger log = Logger.getLogger("WebAlbum");
	
	public static void main(String[] args)
		throws HibernateException {
		log.setLevel(Level.ALL);
		Session session = HibernateUtil.currentSession();
		   
		Transaction tx = session.beginTransaction();

		Auteur auteur = new Auteur();
		auteur.setNom("Dupont");
		session.save(auteur);

		auteur = new Auteur();
		auteur.setNom("Albert");
		session.save(auteur);
		
		try {
			Album album = new Album () ;
			album.setAuteur(0);
			
			album.setDate(DATE_STANDARD.parse("2005-10-15"));
			album.setDescription("Salut, j'ai toujours rien a dire");
			album.setNom("Vive les vacances");
			
			session.save(album);
			
			log.info("Id :"+album.getID());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Photo photo = new Photo () ;
		
		photo.setAlbum(0);
		photo.setDescription("Salut a tous !!!");
		photo.setPath("\\test\\photoAutre.jpg");
		session.save(photo);
		tx.commit();

		HibernateUtil.closeSession();
	}
}