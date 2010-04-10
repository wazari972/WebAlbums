package engine ;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException ;
import java.io.FileOutputStream ;
import java.io.File ;

import java.util.Iterator ;
import java.sql.Connection ;
import java.sql.SQLException;

import org.xml.sax.InputSource ;

import org.dbunit.database.IDatabaseConnection ;
import org.dbunit.database.DatabaseConnection ;
import org.dbunit.database.DatabaseConfig ;

import org.dbunit.dataset.IDataSet  ;
import org.dbunit.dataset.xml.FlatXmlDataSet ;
import org.dbunit.dataset.xml.FlatXmlProducer ;
import org.dbunit.dataset.xml.FlatDtdDataSet ;
import org.dbunit.dataset.stream.StreamingDataSet ;
import org.dbunit.dataset.stream.IDataSetProducer ;
import org.dbunit.ext.mysql.MySqlDataTypeFactory ;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory ;

import org.dbunit.operation.DatabaseOperation ;
import org.dbunit.DatabaseUnitException ;

import org.hibernate.tool.hbm2ddl.SchemaExport ;
import org.hibernate.JDBCException;
import org.hibernate.Transaction;

import entity.* ;
import util.HibernateUtil ;
import util.XmlBuilder;
import constante.Path ;

@SuppressWarnings({ "unchecked", "deprecation" })
public class Maint {
  
  private static String getPath () {
    return Path.getSourcePath() +Path.getData() + Path.SEP  ;
  }
  
  public static XmlBuilder treatMAINT(HttpServletRequest request) {
    String action = request.getParameter("action") ;
    
    String param = request.getParameter("param") ;
    String value = request.getParameter("value") ;
    
    XmlBuilder output = new XmlBuilder ("maint") ;
    if ("FULL_IMPORT".equals(action)) {
      treatFullImport (output) ;
      
    } else if ("EXPORT_XML".equals(action)) {
	treatExportXML (output) ;
    } else if ("IMPORT_XML".equals(action)) {
	treatImportXML (output) ;
    } else if ("TRUNCATE_XML".equals(action)) {
      treatTruncateXML (output) ;
      
    } else if ("EXPORT_DDL".equals(action)) {
      treatExportDDL (output) ;
    } else if ("IMPORT_DDL".equals(action)) {
      treatImportDDL (output) ;
      
    } else if ("UPDATE_BOOL".equals(action)) {
      output.add("message", "updating "+param+" to "+value);
      Path.updateBoolParam(param, value);
    } else if ("UPDATE".equals(action)) {
      treatUpdate (request, output) ;
      
    } else if ("UPDATE_STR".equals(action)) {
      output.add("message", "updating "+param+" to "+value);
      Path.updateStrParam(param, value);
    } else {
      output.add("action","FULL_IMPORT") ;
      
      output.add("action","IMPORT_XML");
      output.add("action","EXPORT_XML");
      output.add("action","TRUNCATE_XML");
      
      output.add("action","EXPORT_DDL");
      output.add("action","IMPORT_DDL");
      
      output.add("action","UPDATE");
      
      output.add("action","UPDATE_BOOL&amp;param=VAL&amp;value=TRUE|FALSE");
      output.add("action","UPDATE_STR&amp;param=VAL&amp;value=STR");
    }
    return output.validate() ;
  }  
  
  public static boolean treatImportDDL (XmlBuilder output) {
    output.add("action", "IMPORT_DDL");
		
    SchemaExport export = new SchemaExport (HibernateUtil.getConfiguration());
    //export.setImportFile(getPath()+"WebAlbums.sql");
    //export.setDelimiter(";");
    
    export.create(false, true) ;

    Iterator exp =  export.getExceptions().iterator() ;
    if (!exp.hasNext()) return true ;

    boolean correct = false ;
    while (exp.hasNext()) {
      Exception e = (Exception) exp.next() ;
      output.addException(e.toString());
      e.printStackTrace () ;
      if (e.toString().contains("Index already exists")) correct = true ;
    }

    return correct;
  }

  public static boolean treatExportDDL (XmlBuilder output) {
    output.add("action", "EXPORT_DDL");
    
    SchemaExport export = new SchemaExport (HibernateUtil.getConfiguration());
    String file = getPath()+"WebAlbums.sql";
    output.add("message", "Output file: "+file) ;
    export.setOutputFile(file);
    export.setDelimiter(";");
    export.create(true, true) ;
    Iterator exp =  export.getExceptions().iterator() ;
    if (!exp.hasNext()) return true ;
    
    boolean correct = false ;
    while (exp.hasNext()) {
      Exception e = (Exception) exp.next() ;
      output.addException(e);
      e.printStackTrace () ;
      if (e.toString().contains("Index already exists")) correct = true ;
    }
    output.add("message", "correct? "+correct) ;
    output.validate() ;

    return correct;
  }

  public static void treatImportXML (XmlBuilder output) {
    String file = getPath()+"WebAlbums.xml";
    output.add("action", "IMPORT_XML");
    output.add("message", "from : "+file);

    try {
      Connection jdbcConnection = WebPage.session.connection();
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);	
      
      DatabaseConfig config = connection.getConfig();
      if (Path.isSgbdHsqldb()) {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
	output.add("message", "configured for HSQLDB");
      } else {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
	output.add("message", "configured for MySQL");
      }
    
      boolean enableColumnSensing = true;
      IDataSetProducer producer = new FlatXmlProducer(new InputSource(file), false, enableColumnSensing);
      IDataSet dataSet = new StreamingDataSet(producer);
      
      DatabaseOperation.INSERT.execute(connection, dataSet) ;
      output.add("message", "done");
    } catch (DatabaseUnitException e) {
      output.addException("DatabaseUnitException", e);
      e.printStackTrace() ;
    } catch (SQLException e) {
      output.addException("SQLException", e);
      e.printStackTrace() ;
    }
    output.validate();
  }
  
  public static void treatExportXML (XmlBuilder output) {
    String filename = getPath()+"WebAlbums" ;
    output.add("action", "EXPORT_XML");
    output.add("message", "file: "+filename+"{.xml,.dtd}");
    try {
      Connection jdbcConnection = WebPage.session.connection();
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        
      DatabaseConfig config = connection.getConfig();
      config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); 
    
      // full database export
      FlatXmlDataSet.write(connection.createDataSet(), new FileOutputStream(filename + ".xml"));
      output.add("message", "done xml");
      FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream(filename + ".dtd"));
      output.add("message", "done dtd");
    } catch (DatabaseUnitException e) {
      output.addException("DatabaseUnitException", e);
      e.printStackTrace() ;
    } catch (SQLException e) {
      output.addException("SQLException", e);
      e.printStackTrace() ;
    } catch (IOException e) {
      output.addException("IOException", e);
      e.printStackTrace() ;
    }
    output.validate() ;
  }

  public static void treatTruncateXML (XmlBuilder output) {
    try {
      Connection jdbcConnection = WebPage.session.connection();
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
      String file = getPath()+"WebPage.xml" ;
      output.add("action","TRUNCATE XML");
    
      DatabaseConfig config = connection.getConfig();
      if (Path.isSgbdHsqldb()) {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory()); 
      } else {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); 
      }
    
      boolean enableColumnSensing = true;
      
      IDataSetProducer producer = new FlatXmlProducer(new InputSource(file), false, enableColumnSensing);
      IDataSet dataSet = new StreamingDataSet(producer);
      
      DatabaseOperation.TRUNCATE_TABLE.execute(connection, dataSet) ;
      output.add("message", "done");
    } catch (DatabaseUnitException e) {
      output.addException("DBUnitException", e);
      e.printStackTrace() ;
    } catch (SQLException e) {
      output.addException("SQLException", e);
      e.printStackTrace() ;
    }
    output.validate() ;
  }

  public static void treatFullImport (XmlBuilder output) {
    if (treatImportDDL (output)) {
      treatImportXML (output) ;
    }
  }

  public static XmlBuilder keepOnlyTheme(XmlBuilder output, String themeID) {
    output.add("message", "Lightening the datadase ... (keep only th "+themeID+")");
    if ("-1".equals(themeID)) {
      output.addException("cannot lighten the root theme");
      return output.validate() ;
    }
    
    Transaction tx = null ;
    try {
      tx = WebPage.session.beginTransaction();
      
      String rq = "from Theme t " +
	"where t.ID != '"+themeID+"'" ;
      Iterator itTheme = WebPage.session.createQuery(rq).iterate() ;
      while (itTheme.hasNext()) {
	Theme enrTheme = (Theme) itTheme.next() ;
	WebPage.log.info("\n\n\n");
	WebPage.log.info("Supression du Theme "+enrTheme.getID());
	WebPage.session.delete(enrTheme) ;
		
	rq = "from Album a where a.Theme = '"+enrTheme.getID()+"'" ;
	Iterator itAlbum = WebPage.session.createQuery(rq).iterate() ;
	while (itAlbum.hasNext()) {
	  Album enrAlbum = (Album) itAlbum.next() ;
	  WebPage.log.info("\tSupression de l'Album "+enrAlbum.getID()+"");
	  WebPage.session.delete(enrAlbum) ;
	  
	  /*** *** ***/
	  rq = "from Photo p where p.Album = '"+enrAlbum.getID()+"'" ;
	  Iterator itPhoto = WebPage.session.createQuery(rq).iterate() ;
	  while (itPhoto.hasNext()) {
	    Photo enrPhoto = (Photo) itPhoto.next() ;
	    WebPage.session.delete(enrPhoto) ;
	    /*** *** ***/
	    rq = "from TagPhoto tp where tp.Photo = '"+enrPhoto.getID()+"'" ;
	    Iterator itTagPhoto = WebPage.session.createQuery(rq).iterate() ;
	    while (itTagPhoto.hasNext()) {
	      TagPhoto enrTagPhoto = (TagPhoto) itTagPhoto.next() ;
	      WebPage.session.delete(enrTagPhoto) ;
	    }
	    /** **/
	  }
	  /** **/
	}

	/*** *** ***/
	rq = "from TagTheme tt where tt.Theme = '"+enrTheme.getID()+"'" ;
	Iterator itTagTheme = WebPage.session.createQuery(rq).iterate() ;
	while (itTagTheme.hasNext()) {
	  TagTheme enrTagTheme = (TagTheme) itTagTheme.next() ;
	  WebPage.session.delete(enrTagTheme) ;
	}
	/** **/
      }
      WebPage.log.info("Alright!");
      output.add("message", "Alright!");
      tx.commit();
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.addException("JDBCException", e.getSQLException()) ;
      if (tx != null) tx.rollback() ;
    }
    
    return output.validate();
  }

  public static void treatUpdate (HttpServletRequest request, XmlBuilder output) {
    //if (true) return ;
    File root = new File(getPath()+"virtual");
    
    String rq = null ;
    try {
      rq = "FROM Theme t WHERE t.ID = '-1'" ;
      Iterator itTheme = WebPage.session.createQuery(rq).iterate() ;
      while (itTheme.hasNext()) {
	Theme enrTheme = (Theme) itTheme.next();
	WebPage.log.info("=================");
	WebPage.log.info("=================\n");
	WebPage.log.info("Theme : "+enrTheme.getNom());
	File fTheme = new File(root, enrTheme.getNom()) ;
	if (!fTheme.isDirectory() && !fTheme.mkdir()) {
	  output.addException("cannot create '"+enrTheme.getNom()+"' on "+fTheme); 
	  output.validate() ;
	  return ;
	}

	rq = "FROM Utilisateur" ;
	Iterator itUser = WebPage.session.createQuery(rq).iterate() ;
	while (itUser.hasNext()) {
	  Utilisateur enrUser = (Utilisateur) itUser.next();
	  WebPage.log.info("Utilisateur : "+enrUser.getNom()+"\n");
	  File fUser = new File(fTheme, enrUser.getNom()); 
	  if (!fUser.isDirectory() && !fUser.mkdir()) {
	    output.addException("cannot create '"+enrUser.getNom()+"' on "+fTheme); 
	    output.validate() ;
	    return ;
	  }
	  
	  File fTag = new File(fUser, "tags"); 
	  if (!fTag.isDirectory() && !fTag.mkdir()) {
	    output.addException("cannot create 'tags' on "+fTag); 
	    output.validate() ;
	    return ;
	  }
	  
	  rq = "SELECT DISTINCT ta FROM Tag ta, TagPhoto tp, Photo p, Album a"+
	    " WHERE ta.ID = tp.Tag "+
	    " AND tp.Photo = p.ID "+
	    " AND p.Album = a.ID "+
	    (!"-1".equals(enrTheme.getID()) ? " AND a.Theme = "+enrTheme.getID() : "")+
	    " AND (((p.Droit = 0 OR p.Droit is null) AND a.Droit >= '"+enrUser.getID()+"') "+
	    "OR "+
	    "(p.Droit >= '"+enrUser.getID()+"')" +
	    ") " ;
	  Iterator itTag = WebPage.session.createQuery(rq).iterate() ;
	  while(itTag.hasNext()) {
	    Tag enrTag = (Tag) itTag.next();
	    WebPage.log.info("Tag : "+enrTag.getNom());
	    File fCurrentTag = new File(fTag, enrTag.getNom()); 
	    if (!fCurrentTag.isDirectory() && !fCurrentTag.mkdir()) {
	      output.addException("cannot create '"+enrTag.getNom()+"' on "+fCurrentTag); 
	      output.validate() ;
	      return ;
	    }
	    rq = "SELECT DISTINCT p FROM Tag ta, TagPhoto tp, Photo p, Album a"+
	      " WHERE ta.ID = '"+enrTag.getID()+"' AND ta.ID = tp.Tag "+
	      " AND tp.Photo = p.ID "+
	      " AND p.Album = a.ID "+
	      (!enrTheme.getID().equals(-1) ? " AND a.Theme = "+enrTheme.getID() : "")+
	      " AND (((p.Droit = 0 OR p.Droit is null) AND a.Droit >= '"+enrUser.getID()+"') "+
	      "OR "+
	      "(p.Droit >= '"+enrUser.getID()+"')" +
	      ") " ;
	    Iterator itPhoto = WebPage.session.createQuery(rq).iterate() ;
	    while(itPhoto.hasNext()) {
	      Photo enrPhoto = (Photo) itPhoto.next();
	      WebPage.log.info(enrTheme.getNom()+"."+enrUser.getNom()+"."+enrTag.getNom()+": "+enrPhoto.getID());
	      File fPhoto = new File(fCurrentTag, enrPhoto.getID()+".jpg") ;
	      String[] command = new String[4] ;
	      command[0] = "ln" ;
	      command[1] = "-s" ;
	      command[2] = Path.getSourcePath()+Path.getImages()+"/" +enrPhoto.getThemedPath();
	      command[3] = fPhoto.toString() ;
	      //WebPage.log.info(java.util.Arrays.toString(command));
	      try {
	    	  Runtime.getRuntime().exec(command);
	      } catch (Exception e) {}
	    }
	  }
	  /* ****************************************** */ 
	  WebPage.log.info("-------");
	  /* ****************************************** */ 
	  File fImages = new File(fUser, "images");
	  if (!fImages.isDirectory() && !fImages.mkdir()) {
	    output.addException("cannot create 'images' on "+fImages); 
	    output.validate() ;
	    return ;
	  }
	  rq = "SELECT DISTINCT a FROM Photo p, Album a"+
	      " WHERE p.Album = a.ID "+
	      (!enrTheme.getID().equals(-1) ? " AND a.Theme = "+enrTheme.getID() : "")+
	      " AND (((p.Droit = 0 OR p.Droit is null) AND a.Droit >= '"+enrUser.getID()+"') "+
	      "OR "+
	      "(p.Droit >= '"+enrUser.getID()+"')" +
	      ") " ;
	    Iterator itAlbum = WebPage.session.createQuery(rq).iterate() ;
	    while(itAlbum.hasNext()) {
	      Album enrAlbum = (Album) itAlbum.next();
	      WebPage.log.info("Album: "+enrAlbum.getNom());
	      File fAlbum = new File(fImages, enrAlbum.getDate()+" "+enrAlbum.getNom());
	      if (!fAlbum.isDirectory() && !fAlbum.mkdir()) {
		output.addException("cannot create 'album' on "+fAlbum); 
		output.validate() ;
		return ;
	      }
	      rq = "SELECT DISTINCT p FROM Photo p, Album a"+
		" WHERE a.ID = '"+enrAlbum.getID()+"' AND p.Album = a.ID "+
		(!enrTheme.getID().equals(-1) ? " AND a.Theme = "+enrTheme.getID() : "")+
		" AND (((p.Droit = 0 OR p.Droit is null) AND a.Droit >= '"+enrUser.getID()+"') "+
		"OR "+
		"(p.Droit >= '"+enrUser.getID()+"')" +
		") " ;
	      Iterator itPhoto = WebPage.session.createQuery(rq).iterate() ;
	      while(itPhoto.hasNext()) {
		Photo enrPhoto = (Photo) itPhoto.next();
		WebPage.log.info(enrTheme.getNom()+"."+enrUser.getNom()+"."+enrAlbum.getNom()+": "+enrPhoto.getID());
		File fPhoto = new File(fAlbum, enrPhoto.getID()+".jpg") ;
		String[] command = new String[4] ;
		command[0] = "ln" ;
		command[1] = "-s" ;
		command[2] = Path.getSourcePath()+Path.getImages()+"/" +enrPhoto.getThemedPath();
		command[3] = fPhoto.toString() ;
		//WebPage.log.info(java.util.Arrays.toString(command));
		try {
		  Runtime.getRuntime().exec(command);
		} catch (Exception e) {}
	      }
	    }
	}
      }
      output.add("message", "ok");
    } catch (JDBCException e) {
      output.addException("JDBCException", e.getSQLException()) ;
      output.addException("JDBCException", rq) ;
    }
  }
}