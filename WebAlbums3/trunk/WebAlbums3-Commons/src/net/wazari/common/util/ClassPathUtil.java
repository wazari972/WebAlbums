package net.wazari.common.util;

//retrieved from
//http://www.solitarygeek.com/java/a-simple-pluggable-java-application/
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassPathUtil {

    private static Logger log = Logger.getLogger(ClassPathUtil.class.getName());
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
                        log.log(Level.SEVERE, "MalformedURLException: ", ex);
                    }
                }
            }
        } else {
            log.log(Level.WARNING, "The directory \"{0}\" does not exist!", directory);
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
                log.log(Level.INFO, "URL {0} is already in the CLASSPATH", u);
                return;
            }
        }
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMS);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[]{u});
            log.log(Level.INFO, "ADD {0}: OK!", u);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Error, could not add URL to system classloader", t);
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
