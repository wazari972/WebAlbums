package net.wazari.service.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.common.util.StringUtil;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.service.ImageLocal;
import net.wazari.service.UserLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionImages.ImgMode;
import net.wazari.service.exchange.xml.XmlImage;
import net.wazari.util.system.SystemTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@DeclareRoles({UserLocal.VIEWER_ROLE})
public class ImageBeaned implements ImageLocal {
    private static final Logger log = LoggerFactory.getLogger(ImageBeaned.class.getName());
    private static final long serialVersionUID = 1L;
    @EJB private PhotoFacadeLocal photoDAO ;
    @EJB private TagFacadeLocal tagDAO ;
    @EJB private PhotoUtil photoUtil ;

    @EJB private SystemTools sysTools ;
    @EJB private ThemeFacadeLocal themeDAO ;
    @EJB private Configuration configuration;
    
    @Override
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    public String treatSHRINK(ViewSessionImages vSession)
            throws WebAlbumsServiceException {
        Photo enrPhoto = photoDAO.loadIfAllowed(vSession.getVSession(), vSession.getId());
        if (enrPhoto == null) {
            return"Cette photo (" + vSession.getId() + ") n'est pas accessible ou n'existe pas ..." ;
        }
        String filepath;
        try {
            Integer width = vSession.getWidth();
            filepath = sysTools.shrink(vSession.getVSession(), enrPhoto, width);
            log.warn("Shrinked filepath: {}", filepath) ;
        } catch (NumberFormatException e) {
            return "Impossible de parser la taille demandee" ;
        }
        
        try {
            Integer borderWidth = vSession.getBorderWidth();
            if (borderWidth != null) {
                String color = vSession.getBorderColor() ;
                sysTools.addBorder(vSession.getVSession(), enrPhoto, borderWidth, color, filepath);
                log.warn("Border {}*{} ({}) added to file: {}", new Object[]{borderWidth, borderWidth, color, filepath}) ;
            }
        } catch (NumberFormatException e) {
            return "Impossible de parser le taille de la bordure a ajouter" ;
        }
        return filepath;
    }
    
    @Override
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    public XmlImage treatIMG(ViewSessionImages vSession)
            throws WebAlbumsServiceException {
        ImgMode mode = vSession.getImgMode();
        mode = (mode == null ? ImgMode.MINI : mode) ;

        //StopWatch stopWatch = new Slf4JStopWatch(log) ;

        XmlImage output = new XmlImage();
        Integer imgId = vSession.getId();
        Photo enrPhoto;
        String filepath;
        String type;
        Theme enrThemeForBackground = vSession.getVSession().getTheme() ;
        try {
            if (mode == ImgMode.RANDOM_TAG || mode == ImgMode.REPRESENT_TAG) {
                Tag enrTag = tagDAO.find(imgId);
                if (enrTag != null) {
                    if (mode == ImgMode.RANDOM_TAG) {
                        SubsetOf<Photo> photos = photoDAO.loadByTags(vSession.getVSession(), 
                                Arrays.asList(new Tag[]{enrTag}),
                                new Bornes(1), ListOrder.RANDOM);
                        
                        if (!photos.subset.isEmpty()) {
                            imgId = photos.subset.get(0).getId() ;
                            mode = ImgMode.GRAND ;
                        } else {
                            log.warn("No photo in tag: {}",imgId);
                            imgId = null ;
                        }
                    } else /* REPRESENT_TAG */{
                        mode = ImgMode.MINI ;
                        Photo enrTagThemePhoto = tagDAO.getTagThemePhoto(vSession.getVSession(), enrTag);
                        if (enrTagThemePhoto != null) {
                            imgId = enrTagThemePhoto.getId();
                        } else {
                            imgId = null;
                        }
                    }
                } else {
                    log.warn("No such tag: {}",imgId);
                    imgId = null ;
                }
            } else if (mode == ImgMode.BACKGROUND) {
                if (vSession.getVSession().isRootSession()) {
                    List<Theme> lstThemes = themeDAO.findAll() ;
                    enrThemeForBackground = lstThemes.get(new Random().nextInt(lstThemes.size())) ;
                    log.info("Using Theme[{}] for root background picture", enrThemeForBackground);

                }
                if (enrThemeForBackground != null && enrThemeForBackground.getBackground() != null) {
                    imgId = enrThemeForBackground.getBackground().getId() ;
                }
            }


            if (imgId == null) {
                output.exception = "No photo asked ... (id=null)" ;
                return output;
            }

            enrPhoto = photoDAO.loadIfAllowed(vSession.getVSession(), imgId);
            if (enrPhoto == null) {
                output.exception = "Cette photo (" + imgId + ") n'est pas accessible ou n'existe pas ..." ;
                return output ;
            }

            if (enrPhoto.getPath(false) == null) {
                output.exception = "Cette photo (" + imgId + ") a un path null ..." ;
                return output ;
            }

            type = (mode == ImgMode.MINI || enrPhoto.getType() == null ? "image/png" : enrPhoto.getType());
            if (mode == ImgMode.SHRINK) {
                try {
                    Integer width = vSession.getWidth();
                    filepath = sysTools.shrink(vSession.getVSession(), enrPhoto, width);
                    log.warn("Shrinked filepath: {}", filepath) ;
                } catch (NumberFormatException e) {
                    output.exception = "Impossible de parser la taille demandee" ;
                    return output ;
                }

                try {
                    Integer borderWidth = vSession.getBorderWidth();
                    if (borderWidth != null) {
                        String color = vSession.getBorderColor() ;
                        sysTools.addBorder(vSession.getVSession(), enrPhoto, borderWidth, color, filepath);
                        log.warn("Border {}*{} ({}) added to file: {}", new Object[]{borderWidth, borderWidth, color, filepath}) ;
                    }
                } catch (NumberFormatException e) {
                    output.exception = "Impossible de parser le taille de la bordure a ajouter" ;
                    return output ;
                }
            } else if (mode == ImgMode.BACKGROUND) {
                assert(enrThemeForBackground != null);
                
                final Integer SIZE = vSession.getScreenSize();
                
                String backgroundpath = configuration
                        .getTempPath()+enrThemeForBackground.getNom()+File.separator+SIZE+".jpg" ;
                if (configuration.isPathURL()) {
                    filepath = photoUtil.getImagePath(vSession.getVSession(), enrPhoto) ;
                } else if (!new File (backgroundpath).exists()) {
                    filepath = sysTools.shrink(vSession.getVSession(), enrPhoto, SIZE, backgroundpath);
                } else {
                    filepath = backgroundpath ;
                }

            } else if (mode == ImgMode.GRAND) {
                filepath = photoUtil.getImagePath(vSession.getVSession(), enrPhoto) ;
            } else if (mode == ImgMode.GPX) {
                filepath = photoUtil.getImagePath(vSession.getVSession(), enrPhoto) ;
            } else {
                filepath = photoUtil.getMiniPath(vSession.getVSession(), enrPhoto);
            }
            

            //redirect if the image can be accessed from HTTP
            if (!configuration.isPathURL()) {
                filepath = "file://" + filepath;
            } else {
                if (false) {
                    vSession.redirect(filepath) ;
                    return null ;
                }
            }

            //null = correct, true = incorrect, but contentType already set
            Boolean correct = sendFile(vSession, filepath, type, output);
            if (correct == null || correct) {
                output = null;
            }
        } catch (Exception e) {
            log.warn ("{}: {} ", e.getClass().getSimpleName(), e) ;
            output.exception = e.getMessage();
        }
        return output;
    }
    
    protected static Boolean sendFile(ViewSessionImages vSession,
                                      String filepath,
                                      String type,
                                      XmlImage output)
    {
        boolean uniq = false;
        try {
            String safeFilepath = filepath ;
            safeFilepath = StringUtil.unescapeHtml(safeFilepath);
            
            URLConnection conn = new URL(StringUtil.escapeURL(safeFilepath)).openConnection();
            InputStream in = conn.getInputStream();
                    
            int bufferSize = (int) Math.min(conn.getContentLength(), 4 * 1024);
            byte[] buffer = new byte[bufferSize];
            int nbRead;

            uniq = true;
            String filename = new File(safeFilepath).getName();
            vSession.setContentDispositionFilename(filename);
            
            vSession.setContentLength(conn.getContentLength());
            vSession.setContentType(type);
            try (OutputStream out = vSession.getOutputStream()) {
                while ((nbRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, nbRead);
                }
                out.flush();
            }
            
            return null;
        } catch (MalformedURLException e) {
            log.warn( "MalformedURLException: {}", e) ;
            output.exception = "MalformedURLException:"+ filepath;

        } catch (ConnectException e) {
            log.warn( "ConnectException: {}", e) ;
            output.exception = "ConnectException" + filepath;
        } catch (IOException e) {
            log.warn( "IOException {}({})", new Object[]{filepath, e});
            output.exception = "IOException: "+ filepath + "(" + e.getMessage() + ")" ;
        }
        return uniq;
    }
}
