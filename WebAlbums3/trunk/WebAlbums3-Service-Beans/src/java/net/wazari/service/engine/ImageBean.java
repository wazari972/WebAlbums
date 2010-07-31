package net.wazari.service.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.wazari.service.exchange.ViewSessionImages;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.common.util.StringUtil;
import net.wazari.common.util.XmlBuilder;
import net.wazari.dao.entity.Photo;

import net.wazari.service.ImageLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionImages.ImgMode;
import net.wazari.util.system.SystemTools;

@Stateless
public class ImageBean implements ImageLocal {
    private static final Logger log = LoggerFactory.getLogger(ImageBean.class.getName());
    private static final long serialVersionUID = 1L;
    @EJB private PhotoFacadeLocal photoDAO ;
    @EJB private PhotoUtil photoUtil ;

    @EJB private SystemTools sysTools ;

    @Override
    public XmlBuilder treatIMG(ViewSessionImages vSession)
            throws WebAlbumsServiceException {
        //StopWatch stopWatch = new Slf4JStopWatch("treatIMG", log) ;

        XmlBuilder output = new XmlBuilder("img");
        Integer imgId = vSession.getId();

        ImgMode mode = vSession.getImgMode();
        mode = (mode == null ? ImgMode.PETIT : mode) ;
        String filepath = null;
        String type = null;
        try {
            if (mode == ImgMode.BACKGROUND) {
                if (vSession.getTheme() != null && vSession.getTheme().getPicture() != null) {
                    imgId = vSession.getTheme().getPicture() ;
                }
            }
            if (imgId == null) {
                output.addException("No photo asked ... (id=null)");
                return output.validate();
            }

            Photo enrPhoto = photoDAO.loadIfAllowed(vSession, imgId);
            if (enrPhoto == null) {
                output.addException("Cette photo (" + imgId + ") n'est pas accessible ou n'existe pas ...");
                return output.validate();
            }

            if (enrPhoto.getPath() == null) {
                output.addException("Cette photo (" + imgId + ") a un path null ...");
                return output.validate();
            }

            type = (mode == ImgMode.PETIT || enrPhoto.getType() == null ? "image/png" : enrPhoto.getType());
            if (mode == ImgMode.SHRINK) {
                String width = vSession.getWidth();
                try {
                    filepath = sysTools.shrink(vSession, enrPhoto, new Integer(width));
                    log.warn("Shrinked filepath: {}", filepath) ;
                } catch (NumberFormatException e) {
                    output.addException("Impossible de parser le nombre " + width);
                    return output.validate();
                }
            } else if (mode == ImgMode.BACKGROUND) {
                final int SIZE = 1280 ;

                String backgroundpath =  vSession.getConfiguration()
                        .getTempPath()+vSession.getTheme().getNom()+File.separator+SIZE+".jpg" ;

                if (!new File (backgroundpath).exists()) {
                    filepath = sysTools.shrink(vSession, enrPhoto, SIZE, backgroundpath);
                } else {
                    filepath = backgroundpath ;
                }
                
            } else if (mode == ImgMode.GRAND) {
                filepath = photoUtil.getImagePath(vSession, enrPhoto) ;
            } else {
                filepath = photoUtil.getMiniPath(vSession, enrPhoto);
            }
            log.warn("Open image {}", filepath);
            if (vSession.getConfiguration().isPathURL()) {
                vSession.redirect(filepath) ;
                return null ;
            }

            if (mode == ImgMode.FULLSCREEN) {
                 sysTools.fullscreenImage(vSession, enrPhoto);
                 return null ;
            }

            filepath = "file://" + filepath;

           
            //null = correct, true = incorrect, but contentType already set
            Boolean correct = sendFile(vSession, filepath, type, output);
            if (correct == null || correct) {
                output = null;
            } else {
                output.validate();
            }
        } catch (Exception e) {
            log.warn(e.getClass().toString(), "{}: ", new Object[]{e.getClass().getSimpleName(), e}) ;
            output.addException("Exception", e.getMessage());
            output.validate();
        } finally {
            //stopWatch.stop() ;
        }
        return output;
    }

    protected static Boolean sendFile(ViewSessionImages vSession,
            String filepath,
            String type,
            XmlBuilder output) {

        boolean uniq = false;
        try {
            InputStream in = null;


            String safeFilepath = StringUtil.escapeURL(filepath);
            URLConnection conn = new URL(safeFilepath).openConnection();
            in = conn.getInputStream();

            int bufferSize = (int) Math.min(conn.getContentLength(), 4 * 1024);
            byte[] buffer = new byte[bufferSize];
            int nbRead;


            uniq = true;
            vSession.setContentDispositionFilename(new File(safeFilepath).getName() + "\"");
            vSession.setContentLength(conn.getContentLength());
            vSession.setContentType(type);
            OutputStream out = vSession.getOutputStream();
            while ((nbRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, nbRead);
            }
            out.flush();
            out.close();

            return null;
        } catch (MalformedURLException e) {
            log.warn( "MalformedURLException: {}", e.getMessage()) ;
            output.addException("MalformedURLException", filepath);

        } catch (ConnectException e) {
            log.warn( "ConnectException: {}", e.getMessage()) ;
            output.addException("ConnectException", filepath);
        } catch (IOException e) {
            log.warn( "IOException {}({})", new Object[]{filepath, e.getMessage()});
            output.addException("IOException", filepath + "(" + e.getMessage() + ")");
        }
        return uniq;
    }
}
