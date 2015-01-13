/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Util {
    private static final Logger log = LoggerFactory.getLogger(Tomee.class.getName());
    public static Config cfg;
    public static final String DEFAULT_CONFIG_PATH = "conf/config.xml";
    
    static {
        try {
            log.debug("Load configuration from '{}'.", DEFAULT_CONFIG_PATH);
            cfg = Config.load(DEFAULT_CONFIG_PATH);
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't load the configuration file: " + ex.getMessage());
        }
    }
    
    @XmlRootElement
    public static class Config {

        public static Config loadDefault(String cfgFilePath) throws Exception {
            Config cfg = new Config();
            cfg.sunResourcesXML = "./conf/sun-resources.xml";
            cfg.libJnetFs = "./lib/libJnetFS.so";
            cfg.webAlbumsEAR = "./bin/WebAlbums-ea.ear";
            cfg.root_path = "./";
            cfg.port = 8080;
            cfg.user = new LinkedList<>();

            cfg.webAlbumsFS = "./WebAlbums-FS";
            
            User usr = new User();
            usr.name = "kevin";
            usr.password = "";
            usr.groups = "Admin:Manager";
            cfg.user.add(usr);

            cfg.save(cfgFilePath);

            return cfg;
        }

        public static Config load(String path) throws Exception {
            File file = new File(path);
            if (!file.isFile()) {
                log.info("Path '{}' is not a file ...", file.getCanonicalPath());
                return loadDefault(path);
            }

            //Create JAXB Context
            JAXBContext jc = JAXBContext.newInstance(Config.class);
            Unmarshaller um = jc.createUnmarshaller();
            Config cfg = (Config) um.unmarshal(file);

            return cfg;
        }

        public String print() throws JAXBException {
            //Create JAXB Context
            JAXBContext jc = JAXBContext.newInstance(Config.class);

            //Create marshaller
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(this, writer);

            return writer.toString();
        }

        public void save(String path) throws Exception {
            //Create JAXB Context
            JAXBContext jc = JAXBContext.newInstance(Config.class);
            //Create marshaller
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            File file = new File(path);
            file.getParentFile().mkdirs();
            marshaller.marshal(this, file);
            log.info("Configuration saved into '{}'.", file.getCanonicalPath());
        }
        
        @XmlAttribute
        int port;
        @XmlElement
        String sunResourcesXML;
        @XmlElement
        String webAlbumsEAR;
        @XmlElement
        String webAlbumsFS;
        @XmlElement
        String libJnetFs;
        @XmlElement
        String root_path;
        @XmlElementWrapper(name = "users")
        List<User> user;

        public static class User {

            @XmlAttribute
            String name;
            @XmlAttribute
            String password;
            @XmlAttribute
            String groups;
        }
    }
    
    /**
    * Ajoute un nouveau répertoire dans le java.library.path.
    * @param dir Le nouveau répertoire à ajouter.
    */
    public static void addToJavaLibraryPath(File dir) {
            final String LIBRARY_PATH = "java.library.path";
            if (!dir.isDirectory()) {
                    throw new IllegalArgumentException(dir + " is not a directory.");
            }
            String javaLibraryPath = System.getProperty(LIBRARY_PATH);
            System.setProperty(LIBRARY_PATH, javaLibraryPath + File.pathSeparatorChar + dir.getAbsolutePath());

            resetJavaLibraryPath();
    }
    
/**
 * Supprime le cache du "java.library.path".
 * Cela forcera le classloader à revérifier sa valeur lors du prochaine chargement de librairie.
 * 
 * Attention : ceci est spécifique à la JVM de Sun et pourrait ne pas fonctionner
 * sur une autre JVM...
 */
    public static void resetJavaLibraryPath() {
	synchronized(Runtime.getRuntime()) {
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			field.set(null, null);
			
			field = ClassLoader.class.getDeclaredField("sys_paths");
			field.setAccessible(true);
			field.set(null, null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
    }   
}
