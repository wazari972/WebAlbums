package engine ;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException ;
import java.io.FileOutputStream ;
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
import constante.Path ;

@SuppressWarnings({ "unchecked", "deprecation" })
public class Maint {
  
  private static String getPath () {
    return Path.getSourcePath() +Path.DATA + Path.SEP  ;
  }
  
  public static void treatMAINT(HttpServletRequest request,
				  StringBuilder output)
    {
	String action = request.getParameter("action") ;

	String param = request.getParameter("param") ;
	String value = request.getParameter("value") ;

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
	  Path.updateBoolParam(param, value);
	} else if ("UPDATE_STR".equals(action)) {
	  Path.updateStrParam(param, value);
	} else {
	  output.append("FULL_IMPORT : IMPORT_DDL, IMPORT_XML<br/><br/>");
	  output.append("EXPORT_XML<br/>");
	  output.append("IMPORT_XML<br/>");
	  output.append("TRUNCATE_XML<br/><br/>");
	  output.append("EXPORT_DDL<br/>");
	  output.append("IMPORT_DDL<br/><br/>");
	  output.append("UPDATE_BOOL & param & value <br/>");
	  output.append("UPDATE_STR & param & value <br/>");
	}
    }  

  public static boolean treatImportDDL (StringBuilder output) {
    output.append("<center><h>IMPORT DDL</h></center><br/>");
		
    SchemaExport export = new SchemaExport (HibernateUtil.getConfiguration());
    //export.setImportFile(getPath()+"WebAlbums.sql");
    //export.setDelimiter(";");
    
    export.create(false, true) ;

    Iterator exp =  export.getExceptions().iterator() ;
    if (!exp.hasNext()) return true ;

    boolean correct = false ;
    while (exp.hasNext()) {
      Exception e = (Exception) exp.next() ;
      output.append("Exception :"+e+"<br/>");
      e.printStackTrace () ;
      if (e.toString().contains("Index already exists")) return true ;
    }
    return false;
  }

  public static boolean treatExportDDL (StringBuilder output) {
    output.append("<center><h>EXPORT DDL</h></center><br/>");
		
    SchemaExport export = new SchemaExport (HibernateUtil.getConfiguration());
    export.setOutputFile(getPath()+"WebAlbums.sql");
    export.setDelimiter(";");
    
    export.create(true, true) ;

     Iterator exp =  export.getExceptions().iterator() ;
    if (!exp.hasNext()) return true ;

    boolean correct = false ;
    while (exp.hasNext()) {
      Exception e = (Exception) exp.next() ;
      output.append("Exception :"+e+"<br/>");
      e.printStackTrace () ;
      if (e.toString().contains("Index already exists")) return true ;
    }
    return false;
  }

  public static void treatImportXML (StringBuilder output) {
    output.append("<center><h>IMPORT XML</h></center><br/>");
    output.append("<center>from : "+getPath()+"WebAlbums.xml </center><br/>");
    try {
      Connection jdbcConnection = WebPage.session.connection();
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);	


      DatabaseConfig config = connection.getConfig();
      if (Path.isSgbdHsqldb()) {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
	output.append("<center>Configured for HSQLDB</center><br/>");
      } else {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
	output.append("<center>Configured for MySQL</center><br/>");
      }
    
      boolean enableColumnSensing = true;
      
      IDataSetProducer producer = new FlatXmlProducer(new InputSource(getPath()+"WebAlbums.xml"), false, enableColumnSensing);
      IDataSet dataSet = new StreamingDataSet(producer);
      
      DatabaseOperation.INSERT.execute(connection, dataSet) ;
    } catch (DatabaseUnitException e) {
      output.append("DBUnitException :"+e+"<br/>");
      e.printStackTrace() ;
    } catch (SQLException e) {
      output.append("SQLException :"+e+"<br/>");
      e.printStackTrace() ;
    }
  }
  
  public static void treatExportXML (StringBuilder output) {
    try {
      Connection jdbcConnection = WebPage.session.connection();
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
      
      output.append("<center><h>EXPORT XML</h></center><br/>");
      
      DatabaseConfig config = connection.getConfig();
      config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); 
    
      // full database export
      FlatXmlDataSet.write(connection.createDataSet(), new FileOutputStream(getPath()+"WebAlbums.xml"));
      FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream(getPath()+"WebAlbums.dtd"));
    } catch (DatabaseUnitException e) {
      output.append("DBUnitException :"+e+"<br/>");
      e.printStackTrace() ;
    } catch (SQLException e) {
      output.append("SQLException :"+e+"<br/>");
      e.printStackTrace() ;
    } catch (IOException e) {
      output.append("IOException :"+e+"<br/>");
      e.printStackTrace() ;
    }
  }

  public static void treatTruncateXML (StringBuilder output) {
    try {
      Connection jdbcConnection = WebPage.session.connection();
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
      
      output.append("<center><h>TRUNCATE XML</h></center><br/>");
    
      DatabaseConfig config = connection.getConfig();
      if (Path.isSgbdHsqldb()) {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory()); 
      } else {
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); 
      }
    
      boolean enableColumnSensing = true;
      
      IDataSetProducer producer = new FlatXmlProducer(new InputSource(getPath()+"WebAlbums.xml"), false, enableColumnSensing);
      IDataSet dataSet = new StreamingDataSet(producer);
      
      DatabaseOperation.TRUNCATE_TABLE.execute(connection, dataSet) ;
    } catch (DatabaseUnitException e) {
      output.append("DBUnitException :"+e+"<br/>");
      e.printStackTrace() ;
    } catch (SQLException e) {
      output.append("SQLException :"+e+"<br/>");
      e.printStackTrace() ;
    } 
  }

  public static void treatFullImport (StringBuilder output) {
    if (treatImportDDL (output)) {
      treatImportXML (output) ;
    }
  }

  public static void keepOnlyTheme(StringBuilder output, String themeID) {
    output.append("<center><h>Lightening the datadase ... (keep only th "+themeID+")</h></center><br/>");
    if ("-1".equals(themeID)) {
      output.append("<center>cannot lighten the root theme</center><br/>");
      return ;
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
	    rq = "from UserPhoto up where up.Photo = '"+enrPhoto.getID()+"'" ;
	    Iterator itUserPhoto = WebPage.session.createQuery(rq).iterate() ;
	    while (itUserPhoto.hasNext()) {
	      UserPhoto enrUserPhoto = (UserPhoto) itUserPhoto.next() ;
	      WebPage.session.delete(enrUserPhoto) ;
	    }
	    /** **/
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
	  
	  /*** *** ***/
	  rq = "from UserAlbum ua where ua.Album = '"+enrAlbum.getID()+"'" ;
	  Iterator itUserAlbum = WebPage.session.createQuery(rq).iterate() ;
	  while (itUserAlbum.hasNext()) {
	    UserAlbum enrUserAlbum = (UserAlbum) itUserAlbum.next() ;
	    WebPage.session.delete(enrUserAlbum) ;
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
      tx.commit();
    } catch (JDBCException e) {
      e.printStackTrace() ;
      if (tx != null) tx.rollback() ;
    }
  }
}