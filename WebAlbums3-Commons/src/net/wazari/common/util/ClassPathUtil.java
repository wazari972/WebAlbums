package net.wazari.common.util;

//retrieved from
//http://www.solitarygeek.com/java/a-simple-pluggable-java-application/
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathUtil {

    private static Logger log = LoggerFactory.getLogger(ClassPathUtil.class.getName());
    private static final Class[] PARAMS = new Class[]{URL.class};

    /**
     * Adds the jars in the given directory to classpath
     * @param directory
     * @throws IOException
     */
    public static ClassLoader addDirToClasspath(File directory) {
        MyURLClassLoader myCl = new MyURLClassLoader() ;
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.getName().endsWith(".jar")) {
                    try {
                        myCl.addURL(file.toURI().toURL());
                    } catch (MalformedURLException ex) {
                        log.error( "MalformedURLException: ", ex);
                    }
                }
            }
        } else {
            log.warn( "The directory \"{}\" does not exist!", directory);
        }
        return myCl ;
    }

    /**
     * Add URL to the System ClassPath
     * @param u URL
     * @throws IOException IOException
     */
    private static void addURL(URL u) {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URL urls[] = sysLoader.getURLs();
        for (int i = 0; i < urls.length; i++) {
            if (urls[i].toString().equalsIgnoreCase(u.toString())) {
                log.info( "URL {} is already in the CLASSPATH", u);
                return;
            }
        }
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMS);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[]{u});
            log.info( "ADD {}: OK!", u);
        } catch (Throwable t) {
            log.error ("Error, could not add URL to system classloader", t);
        }
    }

    private static class MyURLClassLoader extends URLClassLoader {
        public MyURLClassLoader() {
            super(new URL[]{}, Thread.currentThread().getContextClassLoader());
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
}
