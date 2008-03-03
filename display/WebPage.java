package display;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import constante.Path;

import Entity.*;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import traverse.FilesFinder;
import util.HibernateUtil;
import util.StringUtil;

public class WebPage extends HttpServlet {
	private enum Type {PHOTO, ALBUM}
	private static final long serialVersionUID = -8157612278920872716L;
	public static final Logger log = Logger.getLogger("WebAlbum");
	public static final SimpleDateFormat DATE_STANDARD = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_FRANCE = new SimpleDateFormat("dd-MM-yyyy");
	
	private Session session ;
	
	public WebPage() throws HibernateException {
		super();
		session = HibernateUtil.currentSession();
		try {
			log.setLevel(Level.ALL);
			log.addAppender(new FileAppender(new SimpleLayout(),"/tmp/WebPage.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		this.preventCaching(request, response);
		PrintWriter out = response.getWriter();

		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<html>");
		out.println("  <head>");
		out.println("    <title>WebAlbum4</title>");
		out.println("  </head>");
		out.println("  <body>");
		
		StringBuilder output = new StringBuilder () ;
		try {
			String level = request.getParameter("level") ;
			
			if ("".equals (level) || level == null) {
				treatVOID(output);
				
			} else if ("CHX".equals (level)) {
				treatCHX(request, output);
			} else if ("ALBM".equals (level)) {
				treatALBM(request, output);
			} else if ("PERIODE".equals(level)) {
				treatPERIODE(request, output) ;
			} else if ("PHOTO".equals (level)) {
				treatPHOTO(request, output);
			} else if ("IMPORT".equals(level)){
				treatIMPORT(request, output);
			} else if ("NEWTAG".equals(level)){
				treatNEWTAG(request, output);
			} else if ("THEMES".equals(level)){
				treatTHEMES(request, output);
			} else {
				//...
			}
			out.println(output.toString());
		} catch (Exception e) {
			out.println("Il y a un truc qui n'a pas marché... !") ;
			out.println(e.getMessage() + " \n and "+e);
			out.println("**"+output.toString()+"**");
		}
		
		out.println("  </body>");
		out.println("</html>");
		out.flush();
		out.close();
	}

	private void treatTHEMES(HttpServletRequest request, StringBuilder output) throws HibernateException {
		String auteur = request.getParameter("auteur");
		Type type ;
		if ("PHOTO".equals(request.getParameter("type"))) {
			type = Type.PHOTO ;
		} else if ("ALBUM".equals(request.getParameter("type"))) {
			type = Type.ALBUM ;
		} else {
			return ;
		}
		String[] themes = request.getParameterValues("themes");
		List<Integer> listTagId = new ArrayList<Integer> (themes.length) ;
		for (int i = 0; i < themes.length; i++) {
			listTagId.add(Integer.parseInt(themes[i]));
		}
		output.append("<b>"+(type == Type.PHOTO?"Photos" :(type == Type.ALBUM ? "Albums" : "Erreur"))+ " sur sur les thèmes :</b><br /> \n");
		output.append("<i>\n");
		displayThemes(output, listTagId, false) ;
		output.append("</i><br/><br/>\n");
		
		if (type == Type.PHOTO) {
			
			String rq = "select photo from Photo photo, Album album where photo.ID in (select tagPhoto.PhotoID from TagPhoto as tagPhoto where tagPhoto.TagID in ('-1' " ;
			for (int id : listTagId) {
				rq += ", '"+id+"'" ;
			}
			rq+=")) and photo.Album = album.ID and album.Auteur = '"+auteur+"'";
			List list = session.find(rq);
			displayPhoto(list, output) ;
			
		} else if (type == Type.ALBUM) {
			String rq = "from Album as album where album.Auteur='"+auteur+"' and album.ID in (select tagAlbum.AlbumID from TagAlbum as tagAlbum where tagAlbum.TagID in ('-1' " ;
			for (int id : listTagId) {
				rq += ", '"+id+"'" ;
			}
			rq+="))";
			List list = session.find(rq);
			displayAlbum(list, output) ;
		}
		output.append("<a href='?level=CHX&auteur="+auteur+"'>Retour aux choix</a>\n");
	}

	private void treatNEWTAG(HttpServletRequest request, StringBuilder output) throws HibernateException {
		String action = request.getParameter("action") ;
		
		if ("SUBMIT".equals(action)) {
			Session session = HibernateUtil.currentSession();
			   
			Transaction tx = session.beginTransaction();
			
			Tag t = new Tag () ;
			String nom = request.getParameter("nom") ;
			if (nom != null && !nom.equals("")) {
				t.setNom(nom) ;
				session.save(t) ;
				tx.commit();
				
				output.append("<i>Tag </i>'"+nom+"'<i> correctement ajouté</i><br/><br/>\n");
			} else {
				output.append("Le nom du tag est vide ...<br/>\n");
			}
		}
		output.append("<b>Ajout de thèmes</b><br/><br/>\n");
		output.append("<form action='?' method='get'>\n");
		output.append("<input type='hidden' name='level' value='NEWTAG'/>\n");
		output.append("<input type='hidden' name='action' value='SUBMIT'/>\n");
		output.append("Nouveau tag : <input name='nom' type='text' size='20' maxlength='20'/>\n");
		output.append("<input type='submit' value='Valider' />\n");
		output.append("</form>\n");
		output.append("<br/><br/>\n");
		output.append("<a href='?'>Retour aux auteurs</a>\n");
	}

	private void treatIMPORT(HttpServletRequest request, StringBuilder output) throws HibernateException {
		FilesFinder imp = new FilesFinder () ;
		String authorName = request.getParameter("nom") ;
		
		if (authorName != null) {
			output.append("<i>Begining ...</i>\n") ;
			imp.importAuthor(authorName);
			output.append("<i>Well done !</i><br/><br/>\n") ;
		}
		
		output.append("<b>Importations des albums d'un auteur : </b><br/><br/>\n");
		output.append("<form action='?' method='get'>\n");
		output.append("<input type='hidden' name='level' value='IMPORT'/>\n");
		output.append("Nom : <input type='text' name='nom' size='20' maxlenght='20'/>\n");
		output.append("<input type='submit' value='Importer'/>\n");
		output.append("</form>\n");
		output.append("<br/><br/><a href='?'>Retour aux auteurs</a>\n");
	}

	private void treatPERIODE(HttpServletRequest request, StringBuilder output) throws HibernateException {
		String auteur = request.getParameter("auteur") ;
		String year = request.getParameter("year") ;
		String month = request.getParameter("month") ;
		String debut = request.getParameter("debut") ;
		String fin = request.getParameter("fin") ;
		try {
			StringBuilder out = new StringBuilder () ;
			String desc ;
			if (debut == null) {
				if (year == null) {
					throw new NumberFormatException("Infos Periode non consistantes (no year, no begin)") ;
				}
				
				if (month == null) {
					debut = year+"-01-01" ;
					fin = Integer.toString(Integer.parseInt(year)+1)+"-01-01" ;
					desc = "de l'année " + year ;
				} else {
					debut = year+"-"+month+"-01" ;
					int mois = Integer.parseInt(month)+1 ;
					if (mois > 13) {
						throw new NumberFormatException ("Mois supérieur à décembre ("+(mois-1)+")") ;
					} else if (mois == 13) {
						month = "1" ;
						year = Integer.toString(Integer.parseInt(year)+1) ;
					} else {
						month = Integer.toString(mois);
					}
					fin = year+"-"+month+"-01" ;
					
					desc = "de " + new SimpleDateFormat("MMMM yyyy").format(DATE_STANDARD.parse(debut)) ;
				}
			} else {
				if (fin == null) {
					throw new NumberFormatException("Pas de date de fin de periode") ;
				}
				desc = "entre le " + debut + " et le "+fin ;
				//convertir from FRANCE to STANDARD
				debut = DATE_STANDARD.format(DATE_FRANCE.parse(debut)) ;
				fin = DATE_STANDARD.format(DATE_FRANCE.parse(fin)) ;
			}
			if (debut.compareTo(fin) > 0) {
				throw new NumberFormatException("Periode negative ! (from "+debut+" to "+fin+")") ;
			}
			
			output.append("<b>Liste des albums "+desc+"</b><br/><br/>");
			
			String rq = "from Album where auteur = '"+auteur+"' and date between '"+debut+"' and '"+fin+"' order by Date";
			List list = session.find(rq);
			
			displayAlbum(list, output);
			
			output.append("<a href='?level=CHX&auteur="+auteur+"'>Retour aux choix</a>\n");
			
			output.append(out.toString()) ;
		} catch (NumberFormatException e) {
			output.append("Erreur dans les dates... "+e) ;
		} catch (ParseException e) {
			output.append("Format de date incorrect... "+e) ;
		}
		
	}

	private void treatVOID(StringBuilder output) throws HibernateException {
		//afficher la liste des auteurs
		List list = session.find("from Auteur");
		Auteur enrAuteur = null;
		
		output.append("<b>Liste des auteurs : </b><br/><br/>");
		
		Iterator it = list.iterator();
		while (it.hasNext()) {
			enrAuteur = (Auteur) it.next();
			output.append("<a href='?level=CHX&auteur="+enrAuteur.getID()+"'> "+enrAuteur.getNom()+"</a> <br/>");
		}
		
		output.append("<br/><br/><b>Configuration : </b><br/>");
		output.append("<a href='?level=NEWTAG'>Des thèmes</a><br/>");
		output.append("<a href='?level=IMPORT'>Des imports</a><br/>");
	}

	private void treatCHX(HttpServletRequest request, StringBuilder output) throws HibernateException {
		String auteur = request.getParameter("auteur") ;
		
		output.append("<b>Critères de choix : </b></br></br>\n");
		
		output.append("<form action='?' method='get'>\n" +
				"<input type='hidden' name='level' value='ALBM'/>" +
				"<input type='hidden' name='auteur' value='"+auteur+"'/>" +
				"Liste compléte des albums\n" +
				"<input type='submit' value='OK'/>\n" +
				"</form>\n");
		output.append("<form action='?' method='get'>" +
				"<input type='hidden' name='level' value='PERIODE'/>" +
				"<input type='hidden' name='auteur' value='"+auteur+"'/>" +
				"Par Année <input type='text' name='year' size='4' maxlength='4' />" +
				"<input type='submit' value='OK' />" +
				"</form>") ;
		output.append("<form action='?' method='get'>" +
				"<input type='hidden' name='level' value='PERIODE'/>" +
				"<input type='hidden' name='auteur' value='"+auteur+"'/>" +
				"Par Année <input type='text' name='year' size='4' maxlength='4'>" +
				"et mois <input type='text' name='month' size='2' maxlength='2' />" +
				"<input type='submit' value='OK' />" +
				"</form>\n");
		output.append("<form action='?' method='get'>" +
				"<input type='hidden' name='level' value='PERIODE'/>" +
				"<input type='hidden' name='auteur' value='"+auteur+"'/>" +
				"Sur la Periode du <input type='text' name='debut' size='10' maxlength='10' />" +
				"au <input type='text' name='fin' size='10' maxlength='10' />" +
				"<input type='submit' value='OK' /><br/>(format des dates : JJ-MM-AAAA)" +
				"</form>\n");
		output.append("<form action='?' mehod='get'>" +
				"<table><tr valign='middle'>\n" +
				"<td><input type='hidden' name='level' value='THEMES'/>" +
				"<input type='hidden' name='auteur' value='"+auteur+"'/>" +
				"Par Thèmes :</td>" +
				"<td>");
		displayThemes(output, true);
		output.append("</td>" +
				"<td>" +
				"<input type='radio' name='type' value='ALBUM' checked/>Albums<br>\n"+
				"<input type='radio' name='type' value='PHOTO' />Photos<br>\n"+
				"<td><input type='submit' value='OK'/></td>" +
				"</td>" +
				"</tr>" +
				"<tr><td colspan='3'>(Ctrl pour en selectionner plusieurs)</td></tr>\n" +
				"</table>\n" +
				"</form>\n");
		output.append("<br/>\n" +
				"<a href='?'>Retour aux auteurs</a>\n");
	}

	private void treatALBM(HttpServletRequest request, StringBuilder output) throws HibernateException {
		log.warn("Traitement Album");
		String action = request.getParameter("action") ;
		
		if ("SUBMIT".equals(action)) {
			String albumID = request.getParameter("id") ;
			try {
				List list = session.find("from Album where id = '"+albumID.replace("'", "''")+"'");
				
				Album enrAlbum = (Album) list.iterator().next() ;
				
				String desc = request.getParameter("desc") ;
				String nom = request.getParameter("nom") ;
				String date = request.getParameter("date") ;
				String[] themes = request.getParameterValues("themes") ;
				enrAlbum.setThemes(themes) ;
				enrAlbum.setNom(StringUtil.escapeHTML(nom));
				enrAlbum.setDescription(StringUtil.escapeHTML(desc)) ;
				if (date != null) {
					try {
						enrAlbum.setDate(DATE_STANDARD.parse(date)) ;
					} catch(Exception e) {}
				}
				
				if ("oui".equals(request.getParameter("etendre"))) {
					list = session.find("from Photo where album = '"+enrAlbum.getID()+"'") ;
					Iterator it = list.iterator();
					while (it.hasNext()) {
						Photo photo = (Photo) it.next() ;
						photo.addThemes(themes) ;
					}
				}
				
				output.append("<i> Album ("+enrAlbum.getID()+") correctement mise à jour !</i><br/><br/>\n");
			} catch (Exception e) {
				output.append("Impossible de finaliser la modification de l'album ("+albumID+") => "+e);
				action = "EDIT" ;
				log.warn("Impossible de finaliser la modification de l'album ("+albumID+") => "+e);
			}
		}
		
		if ("EDIT".equals(action)) {
			treatAlbmEDIT(request, output);
			
		} else {
			//sinon afficher la liste des albums de cet auteur
			String auteur = request.getParameter("auteur") ;
			
			output.append("<b>Liste des albums : </b><br/><br/>");
			
			List list = session.find("from Album where auteur = '"+auteur.replace("'", "''")+"' order by Date");
			displayAlbum(list, output);
			
			output.append("<br/>\n");
			output.append("<a href='?level=CHX&auteur="+auteur+"'>Retour aux choix</a>\n");
		}
	}

	private void treatAlbmEDIT(HttpServletRequest request, StringBuilder output) {
		//page de mise a jour de l'album
		String albumID = request.getParameter("id") ;
		try {
			//output.append("from Album where id = '"+albumID.replace("'", "''")+"'");
			List list = session.find("from Album where id = '"+albumID.replace("'", "''")+"'");
			
			Album enrAlbum = (Album) list.iterator().next() ;
			
			StringBuilder str = new StringBuilder () ;
			str.append("<form action='/WebAlbums/servlet/display.WebPage?level=ALBM&action=SUBMIT&auteur="+enrAlbum.getAuteur()+"&id="+enrAlbum.getID()+"' method='POST'>\n");
			str.append("<table valign='middle'>\n");
			str.append("	<tr>\n");
			str.append("		<td align='right'>Nom :</td>" +
					   "		<td> <input type='text' size='100' maxlength='100' name='nom' value='"+enrAlbum.getNom()+"'/></td>\n");
			str.append("	</tr>" +
					   "	<tr>");
			str.append("		<td align='right'>Description :</td>" +
					   "		<td> <textarea name='desc' rows='5' cols='100'>"+enrAlbum.getDescription()+"</textarea></td>\n");
			str.append("	</tr>" +
					   "	<tr>");
			str.append("		<td align='right'>Date :</td><td> <input type='text' size='10' name='date' maxlength='10' value='"+DATE_STANDARD.format(enrAlbum.getDate())+"'/></td>\n");
			str.append("	</tr>" +
					   "	<tr>\n");
			str.append("		<td align='right'>Thèmes :</td>" +
					   "		<td>" +
					   "		<table>" +
					   "		<tr>");
			str.append("			<td>");
			displayThemes(str, enrAlbum.getID(), true, Type.ALBUM ) ;
			str.append("			</td>" +
					   "			<td valign='middle'>" +
					   "				<input type='checkbox' name='etendre' value='oui' checked/> Etendre aux photos de l'album" +
					   "			</td>" +
					   "		</tr>" +
					   "		</table>" +
					   "		</td>");
			str.append("	</tr>\n");
			str.append("	<tr>" +
					   "		<td align='center'><input type='submit' value='Valider' /></td>" +
					   "	</tr>\n");
			str.append("<table>\n");
			str.append("</form>\n");
			str.append("<a href='?level=ALBM&auteur="+enrAlbum.getAuteur()+"'>Retour aux albums</a>\n");
			
			output.append(str.toString());
		} catch (Exception e) {
			output.append("<br/>Impossible de modifier l'album ("+albumID+") => "+e);
		}
	}

	private void displayThemes(StringBuilder str, boolean checkedBox) throws HibernateException {
		displayThemes(str, null, checkedBox) ;
	}
	
	private void displayThemes(StringBuilder str, int id, boolean checkedBox, Type type) throws HibernateException {
		ArrayList<Integer> ids ;
		if (type == Type.PHOTO) {
			List list = session.find("from TagPhoto where PhotoID = '"+id+"'");
			 ids= new ArrayList<Integer>(list.size()) ;
			
			for (Object o : list) {
				TagPhoto tag = (TagPhoto) o ;
				ids.add(tag.getTagID()) ;
			}
		} else if (type == Type.ALBUM){
			List list = session.find("from TagAlbum where AlbumID = '"+id+"'");
			ids = new ArrayList<Integer>(list.size()) ;
			
			for (Object o : list) {
				TagAlbum tag = (TagAlbum) o ;
				ids.add(tag.getTagID()) ;
			}
		} else {
			ids = null ;
		}
		displayThemes(str, ids, checkedBox) ;
	}
	
	private void displayThemes(StringBuilder str, List<Integer> ids, boolean checkedBox) throws HibernateException {	
		List list = session.find("from Tag");
		Iterator it = list.iterator();
		int current = 0;
		int max = (ids == null ? 0 : ids.size()) ;
		
		boolean stop = false ;
		
		Tag enrTag ;
		if (checkedBox) str.append("<select name='themes' multiple>\n");
		
		while (it.hasNext() && !stop) {
			enrTag = (Tag) it.next();
			//str.append(current +" et "+stop);
			if (checkedBox) {
				str.append("<option value='"+enrTag.getID()+"' ") ;
				if (ids != null && ids.contains(enrTag.getID())) {
					str.append("selected") ;
				}
				str.append(">");
				str.append(enrTag.getNom());
				str.append("</option>") ;
			} else {
				if (ids != null && ids.contains(enrTag.getID())) {
					str.append(enrTag.getNom());
					if (current < max - 1) {
						str.append(", ");
					} 
					current++ ;
				}
				if (current == max) stop = true;
			}
			str.append("\n");
		}
		if (checkedBox) str.append("</select>\n");
	}

	private void displayAlbum(List list, StringBuilder output) throws HibernateException {
		Album enrAlbum;
		Iterator it = list.iterator();
		
		Date oldDate = null ;
		output.append("<table width='100%'>");
		while (it.hasNext()) {
			enrAlbum = (Album) it.next();
			output.append("	<tr align='center' valign='middle'>\n" +
					"		<td>"+StringUtil.displayDate(enrAlbum.getDate(), oldDate)+"</td>\n" +
					"		<td><b><a href='?level=PHOTO&album="+enrAlbum.getID()+"'> "+enrAlbum.getNom()+"</a></b></td>\n" +
					"		<td width='70%' align='left'>" +
					"		<table>" +
					"			<tr>" +
					"				<td>"+enrAlbum.getDescription()+"</td>\n" +
					"			</tr>" +
					"			<tr>" +
					"				<td>" +
					"					<i>");
			displayThemes(output, enrAlbum.getID(), false, Type.ALBUM);
			output.append("				</i>" +
					"				</td>" +
					"			</tr>" +
					"		</table>" +
					"		<td width ='5%'> <a href=?level=ALBM&action=EDIT&id="+enrAlbum.getID()+"><img src='/data/web/edit.png' width='25' heigh='25'></a></td>\n\n" +
					"	</tr>\n\n");
			
			oldDate = enrAlbum.getDate();
		}
		output.append("</table>\n");
	}

	private void treatPHOTO(HttpServletRequest request, StringBuilder output) throws HibernateException {
		String action = request.getParameter("action") ;
		
		if ("SUBMIT".equals(action)) {
			String photoID = request.getParameter("id") ;
			try {
				List list = session.find("from Photo where id = '"+photoID.replace("'", "''")+"'");
				
				Photo enrPhoto = (Photo) list.iterator().next() ;
				
				String desc = request.getParameter("desc") ;
				String[] themes = request.getParameterValues("themes");
				
				enrPhoto.setThemes(themes) ;
				enrPhoto.setDescription(StringUtil.escapeHTML(desc)) ;
				
				output.append("<b> Photo ("+enrPhoto.getID()+") correctement mise à jour !</b><br/><br/>\n");
			} catch (Exception e) {
				output.append("Impossible de finaliser la modification de la photo ("+photoID+") => "+e);
				action = "EDIT" ;
			}
			//ensuite afficher la liste *normal* des photos s'il n'y a pas eu de probleme
		}
		
		if ("EDIT".equals(action)) {
			treatPhotoEDIT(request, output);
			
		} else {
			//afficher les photos
			//afficher la liste des albums de cet auteur
			String album = request.getParameter("album") ;
			int auteurID ;
			Album enrAlbum = null ;
			try {
				enrAlbum = (Album) session.find("from Album where id = '"+album.replace("'", "''")+"'").iterator().next();
				auteurID = enrAlbum.getAuteur();
				
				output.append("<b>Liste des photos de cet album : </b></br>\n");
				
				List list = session.find("from Photo where album = '"+album.replace("'", "''")+"' order by path");
				displayPhoto(list, output);
				
				output.append("<br/>\n");
				output.append("<a href='?level=ALBM&auteur="+auteurID+"'>Retour aux albums</a>\n");
				
			} catch (NullPointerException e) {
				if (enrAlbum == null) {
					output.append("L'album ("+album+") n'existe pas dans la base...") ;
				}
			}
		}
	}

	private void treatPhotoEDIT(HttpServletRequest request, StringBuilder output) {
		String photoID = request.getParameter("id") ;
		try {
			List list = session.find("from Photo where id = '"+photoID.replace("'", "''")+"'");
			
			Photo enrPhoto = (Photo) list.iterator().next() ;
			
			StringBuilder str = new StringBuilder () ;
			str.append("<form action='/WebAlbums/servlet/display.WebPage?level=PHOTO&action=SUBMIT&album="+enrPhoto.getAlbum()+"&id="+enrPhoto.getID()+"' method='POST'>\n");
			str.append("<table>\n");
			str.append("	<tr valign='middle'>\n");
			str.append("		<td rowspan='2'><img alt='"+"/"+Path.IMAGES+"/"+enrPhoto.getPath()+"' src='/"+Path.MINI+"/"+enrPhoto.getPath()+".png'/></td>\n");
			str.append("		<td>Description :</td>");
			str.append("		<td> <textarea type='text' name='desc'>"+enrPhoto.getDescription()+"</textarea></td>\n");
			str.append("		<td align='right'>Thèmes :</td>");
			str.append("		<td>");
			displayThemes(str, enrPhoto.getID(), true, Type.PHOTO) ;
			str.append("		</td>");
			str.append("	</tr>\n");
			str.append("	<tr>");
			str.append("		<td colspan='2' align='center'><input type='submit' value='Valider' /></td>\n");
			str.append("	</tr>\n");
			str.append("<table>\n");
			str.append("</form>");
			str.append("<br/><br/><a href='?level=PHOTO&album="+enrPhoto.getAlbum()+">Retour aux photos</a>");
			
			output.append(str.toString());
		} catch (Exception e) {
			output.append("Impossible de modifier la photo ("+photoID+") => "+e);
		}
	}

	private void displayPhoto(List list, StringBuilder output) throws HibernateException {
		Photo enrPhoto = null;
		output.append("<table>\n");
		Iterator it = list.iterator();
		while (it.hasNext()) {
			enrPhoto = (Photo) it.next();

			output.append("	<tr align='center' valign='middle'>\n" +
			"		<td><a href='/"+Path.IMAGES+"/"+enrPhoto.getPath()+"'> <img alt='"+"/"+Path.IMAGES+"/"+enrPhoto.getPath()+"' src='/"+Path.MINI+"/"+enrPhoto.getPath()+".png' /></a></td>\n"+
			"		<td width='70%' align='left'>" +
			"		<table>" +
			"			<tr>" +
			"				<td>"+enrPhoto.getDescription()+"</td>\n" +
			"			</tr>" +
			"			<tr>" +
			"				<td>" +
			"					<i>");
	displayThemes(output, enrPhoto.getID(), false, Type.PHOTO);
			output.append("		</i>" +
			"				</td>" +
			"			</tr>" +
			"		</table>" +
			"		</td>" +
			"		<td width='5%'><a href=?level=PHOTO&action=EDIT&id="+enrPhoto.getID()+"><img src='/data/web/edit.png' width='25' heigh='25'></a></td>\n" +
			"	</tr>\n");
		}
		output.append("</table>\n");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response) ;
	}

	protected void preventCaching(HttpServletRequest request,
			HttpServletResponse response) {
		// see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
		String protocol = request.getProtocol();
		if ("HTTP/1.0".equalsIgnoreCase(protocol)) {
			response.setHeader("Pragma", "no-cache");
		} else if ("HTTP/1.1".equalsIgnoreCase(protocol)) {
			response.setHeader("Cache-Control", "no-cache"); // "no-store" work also 
		}
		response.setDateHeader("Expires", 0);
	}

}
