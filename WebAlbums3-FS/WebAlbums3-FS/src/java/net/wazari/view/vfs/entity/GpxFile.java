/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.wazari.libvfs.inteface.SFile;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class GpxFile extends SFile {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SFile.class.getCanonicalName()) ;
    private static final String XML_ENCODING = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
    
    public GpxFile(GpxPoints loc) {
        try {
            JAXBContext jc = JAXBContext.newInstance(GpxPoints.class);
            //Create marshaller
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            
            StringWriter writer = new StringWriter() ;
            marshaller.marshal(loc, writer);
            
            this.content = XML_ENCODING + "\n" + writer.toString();
        } catch (JAXBException ex) {
            ex.printStackTrace();
            log.warn("Couldn't marshall the GPX object: {}", ex);
            this.content = "GPX creation failed: "+ex.getMessage();
        }
    }
    
    @Override
    public String getShortname() {
        return "location.gpx";
    }
}
