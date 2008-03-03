package traverse;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.* ;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import constante.Path;

import Entity.*;
import util.*;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;


public class FilesFinder {
	
	public static final SimpleDateFormat DATE_STANDARD = new SimpleDateFormat("yyyy-MM-dd");
	public static final Logger log = Logger.getLogger("WebAlbum");

	private Session session ;
	private Transaction tx ;
	
	private String auteur ;
	private int annee ;
	private String dossier ;
	
	private ImageResizer resizer = new ImageResizer () ;
	
	public FilesFinder () {
		log.setLevel(Level.ALL);
		try {
			log.addAppender(new FileAppender(new SimpleLayout(),"/tmp/FilesFinder.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void importAuthor(String authorName) throws HibernateException {
		
		log.info ("Importing author : "+authorName) ;
		session = HibernateUtil.currentSession();
		   
		tx = session.beginTransaction();
		
		Thread th = new Thread (resizer) ;
		th.setName("Resizer stack") ;
		th.start() ;
		
		File author = new File(Path.ABSOLUTE_PATH+Path.FTP+"/"+authorName);
		log.warn (Path.ABSOLUTE_PATH+Path.FTP+"/"+authorName) ;
		
		if (!author.exists() || !author.isDirectory()){
			log.fatal ("Le dossier Auteur '"+author.getName()+"' n'existe pas");
		
		} else {
			//plus prevention des problemes de ' => ''
			List list = session.find("from Auteur where nom = '"+authorName.replace("'", "''")+"'");
			
			//si l'auteur n'est pas encore dans la base de données, on l'ajoute
			if (list.size() > 1) {
				log.fatal("Plusieurs auteurs ont le même nom ! ==> "+authorName) ;
				
			} else {
				Auteur enrAuteur = null ;
				if (list.size() == 0) {
					log.info("L'auteur n'est pas dans la table") ;
					enrAuteur = new Auteur () ;
					enrAuteur.setNom(authorName) ;
					session.save(enrAuteur) ;
					log.debug("L'auteur a correctement été ajouté") ;
					
				} else if (list.size() == 1) {
					log.info("L'auteur est déjà dans la table") ;
					enrAuteur = (Auteur) list.iterator().next();
				}
				
				this.auteur = authorName ;
				
				int myID = enrAuteur.getID() ;
				
				File[] subfiles = author.listFiles();
				
				log.info("Le dossier '"+authorName+"' contient "+ subfiles.length+" fichier"+(subfiles.length>1?"s":""));
				for (int i=0 ; i<subfiles.length; i++){
					importAlbum(subfiles[i], myID);
				}
			}
		}
		log.info ("Waiting for Resizer termination");
		resizer.terminate (author) ;
		
		log.info ("Commit for author : "+authorName) ;
		tx.commit() ;
		log.info ("Well done !") ;
	}
	
	private void importAlbum (File album, int authorID) throws HibernateException  {
		log.info ("## Import of : "+album.getName()) ;
		
		if (!album.exists() || !album.isDirectory()){
			log.warn("## Le dossier Album '"+album.getName()+"' n'existe pas");
			return ;
		} else {
			File[] subfiles = album.listFiles();
			
			String nom = album.getName().substring(11) ;
			String date = album.getName().substring(0, 10) ;
			
			log.info("## DATE :" +date) ;
			log.info("## NOM  :" +nom) ;
			
			List list = session.find("from Album where date = '"+date+"'");
			
			Album enrAlbum = null ;
			//si l'album est plusieurs (>1) fois dans la base, probleme !
			if (list.size() > 1) {
				log.fatal("## Plusieurs Albums ont la même date ! ==> "+date) ;
				
			} else {
				//si il n'y est pas, on l'ajoute
				if (list.isEmpty()) {
					log.info("## L'album n'est pas dans la table") ;
					enrAlbum = new Album () ;
					
					enrAlbum.setNom(nom);
					enrAlbum.setDescription("");
					enrAlbum.setAuteur(authorID) ;
					try {
						enrAlbum.setDate(DATE_STANDARD.parse(date));
						log.debug("## Date correctement parsée : "+enrAlbum.getDate()) ;
					} catch (ParseException e) {
						try {
							log.warn("## Date incorrect, on met celle par defaut") ;
							enrAlbum.setDate(DATE_STANDARD.parse("1986-03-14"));
						} catch (ParseException e1) {/*never reached*/}
					}
					
					
					log.debug("## On tente d'ajouter l'album dans la base");
					session.save(enrAlbum) ;
					log.info("## On vient de lui donner l'ID "+enrAlbum.getID()) ;
					
				} else { // list.size() == 1 // il y est 1 fois, on le charge 
					enrAlbum = (Album) list.iterator().next();
					log.info("## L'album est dans la table : ID "+enrAlbum.getID()) ;
				}
				
				int myID = enrAlbum.getID() ;
				
				//definition des attributs de l'album pour la prochaine procedure
				dossier = album.getName () ;
				annee = Integer.parseInt(dossier.substring(0, 4)) ;
				
				log.info("## Le répertoire '"+album.getName()+"' contient "+ subfiles.length+" fichier"+(subfiles.length>1?"s":""));
				for (int i=0 ; i<subfiles.length; i++){
					log.info("## Traitement de "+subfiles[i].getName());
					importPhoto(subfiles[i], myID);
				}
			}
			log.info ("## Import of : "+album.getName() +" completed") ;
			
		}
		
	}
	
	private void importPhoto (File photo, int albumID) throws HibernateException {
		log.info ("#### Import of : "+photo.getName() +"") ;
		
		URL url;
		try {
			url = photo.toURI().toURL();
			URLConnection connection = url.openConnection();
			String type = connection.getContentType();
			
			log.info("#### type : "+type);
			
			if (!type.contains("image")) {
				log.warn("#### ce n'est pas une image ...");
				return ;
			}
		} catch (MalformedURLException e) {
			log.warn("#### URL mal formée ..." + e);
			return ;
		} catch (IOException e) {
			log.warn("#### Erreur d'IO ..." + e);
			return ;
		}

		String path = auteur+"/"+annee+"/"+dossier+"/"+photo.getName() ;
		
		ImageResizer.Element elt = new ImageResizer.Element (path, photo) ;
		resizer.push (elt) ;
		
		//plus prevention des problemes de ' => ''
		List list = session.find("from Photo where Path = '"+path.replace("'", "''")+"'");
		
		Photo enrPhoto = null ;
		//si l'album est plusieurs (>1) fois dans la base, probleme !
		if (list.size() > 1) {
			log.fatal("## Plusieurs Photos ont le même path ! ==> "+path) ;
			
		} else {
			//si il n'y est pas, on l'ajoute
			if (list.isEmpty()) {
				enrPhoto = new Photo () ;
				enrPhoto.setDescription("") ;
				enrPhoto.setPath(StringUtil.escapeURL(path)) ;
			} else {
				enrPhoto = (Photo) list.iterator().next();
			}
		}
		
		enrPhoto.setAlbum (albumID) ;
		session.saveOrUpdate (enrPhoto) ;
		
		log.info ("#### Import of : "+photo.getName() +" : completed") ;
	}


	
	public static void main (String[] args) {
		
		// if (true) return ;
		
		FilesFinder finder = new FilesFinder () ;
		try {
			finder.importAuthor("Kevin") ;
			
			while (!finder.resizer.isDone()) {
				try {
					Thread.sleep(1000) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.info ("esperando, esperando ...") ;
			}
			log.info ("ciao !!!") ;
		} catch (HibernateException e) {}
	}
}