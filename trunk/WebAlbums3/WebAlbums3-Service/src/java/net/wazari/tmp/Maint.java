package net.wazari.tmp ;

import net.wazari.service.engine.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException ;
import java.io.FileOutputStream ;

import java.util.Iterator ;
import java.sql.Connection ;
import java.sql.SQLException;

import javax.ejb.Stateless;
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


import net.wazari.dao.entity.* ;
import net.wazari.service.exchange.Configuration;
import net.wazari.util.XmlBuilder;

@SuppressWarnings({ "unchecked", "deprecation" })
@Stateless
public class Maint  {
  /*
  private static String getPath (Configuration conf) {
    return conf.getSourcePath() +conf.getData() + conf.getSep()  ;
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
      conf.updateBoolParam(param, value);
    } else if ("UPDATE".equals(action)) {
      treatUpdate (request, output) ;
      
    } else if ("UPDATE_STR".equals(action)) {
      output.add("message", "updating "+param+" to "+value);
      conf.updateStrParam(param, value);
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
      Connection jdbcConnection = ThemeDAO.createSession().connection();
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
      Connection jdbcConnection = ThemeDAO.createSession().connection();
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
        Connection jdbcConnection = ThemeDAO.createSession().connection();
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

  public static XmlBuilder keepOnlyTheme(XmlBuilder output, int themeID) {
    output.add("message", "Lightening the datadase ... (keep only th "+themeID+")");
    if ("-1".equals(themeID)) {
      output.addException("cannot lighten the root theme");
      return output.validate() ;
    }
    
    Transaction tx = null ;
    try {
        Session  session = ThemeDAO.createSession();

      tx = session.beginTransaction();
      
      String rq = "from Theme t " +
	"where t.ID != '"+themeID+"'" ;
      Iterator itTheme = session.createQuery(rq).iterate() ;
      while (itTheme.hasNext()) {
	Theme enrTheme = (Theme) itTheme.next() ;
	WebPage.log.info("\n\n\n");
	WebPage.log.info("Supression du Theme "+enrTheme.getID());
	session.delete(enrTheme) ;
		
	rq = "from Album a where a.Theme = '"+enrTheme.getID()+"'" ;
	Iterator itAlbum = session.createQuery(rq).iterate() ;
	while (itAlbum.hasNext()) {
	  Album enrAlbum = (Album) itAlbum.next() ;
	  WebPage.log.info("\tSupression de l'Album "+enrAlbum.getID()+"");
	  session.delete(enrAlbum) ;
	  
	  //*** *** ***
	  rq = "from Photo p where p.Album = '"+enrAlbum.getID()+"'" ;
	  Iterator itPhoto = session.createQuery(rq).iterate() ;
	  while (itPhoto.hasNext()) {
	    Photo enrPhoto = (Photo) itPhoto.next() ;
	    session.delete(enrPhoto) ;
	    //*** *** ***
	    rq = "from TagPhoto tp where tp.Photo = '"+enrPhoto.getID()+"'" ;
	    Iterator itTagPhoto = session.createQuery(rq).iterate() ;
	    while (itTagPhoto.hasNext()) {
	      TagPhoto enrTagPhoto = (TagPhoto) itTagPhoto.next() ;
	      session.delete(enrTagPhoto) ;
	    }
	    //** **
	  }
	  //** **
	}

	//*** ***
	rq = "from TagTheme tt where tt.Theme = '"+enrTheme.getID()+"'" ;
	Iterator itTagTheme = session.createQuery(rq).iterate() ;
	while (itTagTheme.hasNext()) {
	  TagTheme enrTagTheme = (TagTheme) itTagTheme.next() ;
	  session.delete(enrTagTheme) ;
	}
	//** **
      }
      WebPage.log.info("Alright!");
      output.add("message", "Alright!");
      tx.commit();
      session.close();

    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.addException("JDBCException", e.getSQLException()) ;
      if (tx != null) tx.rollback() ;
    }
    return output.validate();
  }

  public static void treatUpdate (HttpServletRequest request, XmlBuilder output) {
    if (true) return ;
   
  }
   * */
}