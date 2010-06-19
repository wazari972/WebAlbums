/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.MaintFacadeLocal;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

/*import org.hibernate.JDBCException;
import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.tool.hbm2ddl.SchemaExport;
*/
import org.xml.sax.InputSource;

/**
 *
 * @author kevinpouget
 */
@Stateless
public class MaintDAOBean implements MaintFacadeLocal {

    @Override
    public boolean treatImportDDL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean treatExportDDL(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treatImportXML(String path, boolean isMySQL) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treatExportXML(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treatTruncateXML(String path, boolean isMySQL) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treatFullImport(String path, boolean isMySQL) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/*
    private static interface Work {
        void execute(Connection connection) throws JDBCException;
    }
    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public boolean treatImportDDL() {
        SchemaExport export = new SchemaExport(new Configuration());
        //export.setImportFile(getPath()+"WebAlbums.sql");
        //export.setDelimiter(";");

        export.create(false, true);

        if (export.getExceptions().isEmpty()) {
            return true;
        }

        boolean correct = false;
        for (Exception e : (List<Exception>) export.getExceptions()) {
            e.printStackTrace();
            if (e.toString().contains("Index already exists")) {
                correct = true;
            }
        }

        return correct;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean treatExportDDL(String path) {
        SchemaExport export = new SchemaExport(new Configuration());
        String file = path + "WebAlbums.sql";
        export.setOutputFile(file);
        export.setDelimiter(";");
        export.create(true, true);

        if (export.getExceptions().isEmpty()) {
            return true;
        }

        boolean correct = false;
        for (Exception e : (List<Exception>) export.getExceptions()) {
            e.printStackTrace();
            if (e.toString().contains("Index already exists")) {
                correct = true;
            }
        }

        return correct;
    }

    @Override
    public void treatImportXML(final String path, final boolean isMySQL) {
        final String file = path + "WebAlbums.xml";

        try {
            HibernateEntityManager hem = (HibernateEntityManager) em;
            @SuppressWarnings("deprecation")
            Connection jdbcConnection = hem.getSession().connection();

            new Work() {

                @Override
                public void execute(Connection cx) throws JDBCException {
                    try {
                        IDatabaseConnection connection = new DatabaseConnection(cx);
                        DatabaseConfig config = connection.getConfig();
                        if (isMySQL) {
                            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
                        } else {
                            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
                        }
                        boolean enableColumnSensing = true;
                        IDataSetProducer producer = new FlatXmlProducer(new InputSource(file), false, enableColumnSensing);
                        IDataSet dataSet = new StreamingDataSet(producer);
                        DatabaseOperation.INSERT.execute(connection, dataSet);
                    } catch (SQLException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (DatabaseUnitException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.execute(jdbcConnection);

        } catch (JDBCException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void treatExportXML(String path) {
        final String filename = path + "WebAlbums";
        try {
            HibernateEntityManager hem = (HibernateEntityManager) em;
            @SuppressWarnings("deprecation")
            Connection jdbcConnection = hem.getSession().connection();
            new Work() {

                @Override
                public void execute(Connection cx) throws JDBCException {
                    try {
                        IDatabaseConnection connection = new DatabaseConnection(cx);
                        DatabaseConfig config = connection.getConfig();
                        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
                        // full database export
                        FlatXmlDataSet.write(connection.createDataSet(), new FileOutputStream(filename + ".xml"));
                        FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream(filename + ".dtd"));
                    } catch (DatabaseUnitException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.execute(jdbcConnection);

        } catch (JDBCException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void treatTruncateXML(final String path, final boolean isMySQL) {
        try {
            HibernateEntityManager hem = (HibernateEntityManager) em;
            @SuppressWarnings("deprecation")
            Connection jdbcConnection = hem.getSession().connection();

            new Work() {

                @Override
                public void execute(Connection cx) throws JDBCException {
                    try {
                        IDatabaseConnection connection = new DatabaseConnection(cx);
                        String file = path + "WebPage.xml";
                        DatabaseConfig config = connection.getConfig();
                        if (!isMySQL) {
                            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
                        } else {
                            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
                        }
                        boolean enableColumnSensing = true;
                        IDataSetProducer producer = new FlatXmlProducer(new InputSource(file), false, enableColumnSensing);
                        IDataSet dataSet = new StreamingDataSet(producer);
                        DatabaseOperation.TRUNCATE_TABLE.execute(connection, dataSet);
                    } catch (DatabaseUnitException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.execute(jdbcConnection);
            
        } catch (JDBCException e) {
            e.printStackTrace();
        } 
    }

    @Override
    public void treatFullImport(String path, boolean isMySQL) {
        if (treatImportDDL()) {
            treatImportXML(path, isMySQL);
        }
    }
  */
}
