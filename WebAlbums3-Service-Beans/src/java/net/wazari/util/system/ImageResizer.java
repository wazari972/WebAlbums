 package net.wazari.util.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.common.util.StringUtil;
import net.wazari.service.exchange.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ImageResizer {
    private static final Logger log = LoggerFactory.getLogger(ImageResizer.class.getCanonicalName());
    private static final int HEIGHT = 200;
    
    @EJB private SystemTools sysTool;
    @EJB private Configuration configuration;
    
    @Asynchronous
    public void resize(Stack<Element> stack, File author) {
        log.info("Starting the ImageResizer Thread");

        Element current;
        while (!stack.empty()) {
            current = stack.pop();
            log.info(current.path);

            try {
                if (!current.dontThumbnail) {
                    log.debug("Resizing...");
                    if (!thumbnail(current)) {
                        continue;
                    }
                }

                log.debug("Moving...");
                if (!move(current)) {
                    continue;
                }

                log.debug("Done !");
            } catch (URISyntaxException | IOException e) {
                log.warn("Couldn't thumbnail/move image {}", current.path, e);
            }

        }
        if (author != null && author.isDirectory()) {
            log.debug("Nettoyage du dossier {}", author);
            File[] lst = author.listFiles();

            //supprimer recursivement tous les dossiers de ce repertoire
            for (File f : lst) {
                clean(f);
            }
        } else {
            log.debug("Pas de dossier à nettoyer");
        }
        log.debug("Finished !");
    }

    public static void clean(File rep) {
        if (rep.isFile()) {
            if ("Thumbs.db".equals(rep.getName())) {
                rep.delete();
            }
            //on fait rien

            log.debug("File found: {} !", rep);
        } else if (rep.isDirectory()) {
            log.info("Suppression du dossier {} ...", rep);
            File[] lst = rep.listFiles();

            //supprimer recursivement tous les dossiers vides de ce repertoire
            for (File f : lst) {
                clean(f);
            }
            //et supprimer le repertoire lui meme
            rep.delete();
        }
    }

    public boolean move(Element elt) throws MalformedURLException, URISyntaxException {
        String url = "file://" + configuration.getImagesPath(true) + configuration.getSep() + elt.path;
        log.debug("SOURCE = {}", url);
        URI uri = new URL(StringUtil.escapeURL(url)).toURI();
        File destination = new File(uri);
        destination.getParentFile().mkdirs();
        log.debug("Move {} to {}", new Object[]{elt.image, destination});

        if (!elt.image.renameTo(destination)) {
            log.warn("Couldn't move {} to {}", elt.image, destination);
            return false;
        }

        return true;
    }
    
    private boolean thumbnail(Element source) throws URISyntaxException, IOException {
        String path = configuration.getMiniPath(true) + configuration.getSep() + source.path + ".png";

        File destination = new File(path);
        File parent = destination.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            log.warn("Impossible de creer le dossier destination ({})", parent);
            return false;
        } else {
            log.warn("Parent directories created ({})", parent);
            String ext = null;
            int idx = source.image.getName().lastIndexOf('.');
            if (idx != -1) {
                ext = source.image.getName().substring(idx + 1);
            }
            return sysTool.thumbnail(source.type, ext, source.image.getAbsolutePath(),
                    destination.getAbsolutePath(),
                    HEIGHT);
        }
    }

    public static class Element {

        public String path;
        public File image;
        public String type;
        public boolean dontThumbnail;
        
        public Element(String path, File image, String type, boolean dontThumbnail) {
            this.path = path;
            this.image = image;
            this.type = type;
            this.dontThumbnail = dontThumbnail;
        }
    }
}
