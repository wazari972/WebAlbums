package engine ;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileOutputStream ;
import java.util.Iterator ;
import org.xml.sax.InputSource ;

import org.dbunit.database.IDatabaseConnection ;
import org.dbunit.database.DatabaseConnection ;
import org.dbunit.database.DatabaseConfig ;

import org.dbunit.dataset.IDataSet  ;
import org.dbunit.dataset.ReplacementDataSet ;
import org.dbunit.dataset.xml.FlatXmlDataSet ;
import org.dbunit.dataset.xml.FlatXmlProducer ;
import org.dbunit.dataset.xml.FlatDtdDataSet ;
import org.dbunit.dataset.xml.FlatDtdProducer ;
import org.dbunit.dataset.stream.StreamingDataSet ;
import org.dbunit.dataset.stream.IDataSetProducer ;
import org.dbunit.ext.mysql.MySqlDataTypeFactory ;
import org.dbunit.operation.DatabaseOperation ;

import org.hibernate.tool.hbm2ddl.SchemaExport ;

import util.HibernateUtil ;
import constante.Path ;
import java.sql.Connection ;

public class Maint {
    @SuppressWarnings("deprecation")
    public static void treatMAINT(HttpServletRequest request,
				  StringBuilder output)
    {
	String action = request.getParameter("action") ;

	String param = request.getParameter("param") ;
	String value = request.getParameter("value") ;

	try {
	    Connection jdbcConnection = WebPage.session.connection();
	    IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);	
	    
	    if ("EXPORT".equals(action)) {
		output.append("<center><h>EXPORT</h></center><br/>");
	    
		DatabaseConfig config = connection.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); 

		// full database export
		FlatXmlDataSet.write(connection.createDataSet(), new FileOutputStream("/home/kevin/WebAlbums.xml"));
		FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream("/home/kevin/WebAlbums.dtd"));
	    } else if ("IMPORT".equals(action)) {
		output.append("<center><h>IMPORT</h></center><br/>");
	
		DatabaseConfig config = connection.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); 
		
		boolean enableColumnSensing = true;
		IDataSetProducer producer = new FlatXmlProducer(new InputSource("/home/kevin/WebAlbums.xml"), false, enableColumnSensing);
		IDataSet dataSet = new StreamingDataSet(producer);
		DatabaseOperation.TRUNCATE_TABLE.execute(connection, dataSet) ;

		producer = new FlatXmlProducer(new InputSource("/home/kevin/WebAlbums.xml"), false, enableColumnSensing);
		dataSet = new StreamingDataSet(producer);
		DatabaseOperation.INSERT.execute(connection, dataSet) ;
	    	
	    } else if ("EXPORT_DDL".equals(action)) {
		output.append("<center><h>EXPORT DDL</h></center><br/>");
		
		SchemaExport export = new SchemaExport (HibernateUtil.getConfiguration());
		export.setOutputFile("/home/kevin/WebAlbums.sql");
		export.setDelimiter(";");
		export.create(true, true) ;
	    } else if ("IMPORT_DDL".equals(action)) {
		output.append("<center><h>IMPORT DDL</h></center><br/>");
		
		SchemaExport export = new SchemaExport (HibernateUtil.getConfiguration());
		export.setImportFile("/home/kevin/WebAlbums.sql");
		export.setDelimiter(";");
		export.create(false, true) ;

		Iterator exp =  export.getExceptions().iterator() ;
		while (exp.hasNext()) {
		    Exception e = (Exception) exp.next() ;
		    output.append("Exception :"+e+"<br/>");
		    e.printStackTrace () ;
		}
	    } else if ("UPDATE_BOOL".equals(action)) {
		Path.updateBoolParam(param, value);
	    } else if ("UPDATE_STR".equals(action)) {
		Path.updateStrParam(param, value);
	    } else {
		output.append("EXPORT<br/>");
		output.append("IMPORT<br/><br/>");
		output.append("EXPORT_DDL<br/>");
		output.append("IMPORT_DDL<br/><br/>");
		output.append("UPDATE_BOOL & param & value <br/>");
		output.append("UPDATE_STR & param & value <br/>");
	    }
	} catch (Exception e) {
	    output.append("Exception :"+e+"<br/>");
	    e.printStackTrace() ;
	}
    }
}