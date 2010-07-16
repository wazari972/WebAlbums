package net.wazari.service.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import net.wazari.service.exchange.ViewSessionImages;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.logging.Logger;
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
    private static final Logger log = Logger.getLogger(ImageBean.class.getName());
    private static final long serialVersionUID = 1L;
    @EJB private PhotoFacadeLocal photoDAO ;
    @EJB private PhotoUtil photoUtil ;

    @EJB private SystemTools sysTools ;

    @Override
    public XmlBuilder treatIMG(ViewSessionImages vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("img");
        Integer imgId = vSession.getId();

        ImgMode mode = vSession.getImgMode();
        mode = (mode == null ? ImgMode.PETIT : mode) ;
        String filepath = null;
        String type = null;
        try {
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

            type = (mode == ImgMode.PETIT || enrPhoto.getType() == null ? "image/jpeg" : enrPhoto.getType());

            if (mode == ImgMode.SHRINK) {
                String width = vSession.getWidth();
                try {
                    filepath = sysTools.shrink(vSession, enrPhoto, new Integer(width));
                } catch (NumberFormatException e) {
                    output.addException("Impossible de parser le nombre " + width);
                    return output.validate();
                }
            } else {
                filepath = (mode == ImgMode.GRAND ? photoUtil.getImagePath(vSession, enrPhoto) : photoUtil.getMiniPath(vSession, enrPhoto));
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
            log.log(Level.SEVERE, "{0}: ", new Object[]{e.getClass().getSimpleName(), e}) ;
            output.addException("Exception", e.getMessage());
            output.validate();
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
            log.log(Level.WARNING, "MalformedURLException: {0}", e.getMessage()) ;
            output.addException("MalformedURLException", filepath);

        } catch (ConnectException e) {
            log.log(Level.WARNING, "ConnectException: {0}", e.getMessage()) ;
            output.addException("ConnectException", filepath);
        } catch (IOException e) {
            log.log(Level.WARNING, "IOException {0}({1})", new Object[]{filepath, e.getMessage()});
            output.addException("IOException", filepath + "(" + e.getMessage() + ")");
        }
        return uniq;
    }
}
