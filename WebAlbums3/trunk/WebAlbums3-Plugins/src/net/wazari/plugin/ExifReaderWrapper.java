/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.plugin;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.common.plugins.GenericImporter;

/**
 *
 * @author kevinpouget
 */
public class ExifReaderWrapper extends GenericImporter {
    private static final Logger log = LoggerFactory.getLogger(ExifReaderWrapper.class.getName());

    @Override
    public String getName() {
        return "Exif Reader" ;
    }

    @Override
    public String getVersion() {
        return "1" ;
    }

    @Override
    public String getTargetSystem() {
        return "Java" ;
    }

    @Override
    public String getSupportedFilesDesc() {
        return "images with EXIF data" ;
    }

    @Override
    public Capability[] supports() {
        return new Capability[] {Capability.META_DATA} ;
    }

    @Override
    public boolean supports(String type, String ext, Capability cap) {
        if ( type.contains("image"))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    @Override
    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setMetadata(Metadata p, String path) {
        try {
            File photo = new File(path);
            ExifReader ex = new ExifReader(photo);
            Iterator it = ex.extract().getDirectoryIterator();
            while (it.hasNext()) {
                Directory dir = (Directory) it.next();
                Iterator it2 = dir.getTagIterator();
                while (it2.hasNext()) {
                    Tag t = (Tag) it2.next();
                    boolean model = false,
                            date = false,
                            iso = false,
                            expo = false,
                            focal = false,
                            height = false,
                            width = false,
                            flash = false;

                    if (!model && t.getTagName().equals("Model")) {
                        p.setModel(escapeBracket(t.toString()));

                        model = true;
                    } else if (!date && t.getTagName().equals("Date/Time")) {
                        p.setDate(escapeBracket(t.toString()));
                        date = true;

                    } else if (!iso && t.getTagName().equals("ISO Speed Ratings")) {
                        p.setIso(escapeBracket(t.toString()));
                        iso = true;

                    } else if (!expo && t.getTagName().equals("Exposure Time")) {
                        p.setExposure(escapeBracket(t.toString()));
                        expo = true;

                    } else if (!focal && t.getTagName().equals("Focal Length")) {
                        p.setFocal(escapeBracket(t.toString()));
                        focal = true;

                    } else if (!height && t.getTagName().equals("Exif Image Height")) {
                        p.setHeight(escapeBracket(t.toString()));
                        height = true;

                    } else if (!width && t.getTagName().equals("Exif Image Width")) {
                        p.setWidth(escapeBracket(t.toString()));
                        width = true;

                    } else if (!flash && t.getTagName().equals("Flash")) {
                        p.setFlash(escapeBracket(t.toString()));
                        flash = true;
                    }
                }
            }
            return true ;
        } catch (JpegProcessingException e) {
            log.warn( "Exception JPEG durant le traitement exif : {0}", e.getMessage());
            log.warn(path);
        }
        return false ;
    }

    private String escapeBracket(String str) {
        int pos = str.indexOf("]");
        return str.substring(pos + 2);
    }

    @Override
    public SanityStatus sanityCheck(ProcessCallback cb) {
        return SanityStatus.PASS ;
    }

    @Override
    public int getPriority() {
        return 7 ;
    }
}
