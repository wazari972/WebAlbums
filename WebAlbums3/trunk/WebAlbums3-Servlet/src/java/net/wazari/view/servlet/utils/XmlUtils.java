/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.utils;

import java.io.File;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.wazari.view.servlet.exchange.ConfigurationXML;

/**
 *
 * @author kevinpouget
 */
public class XmlUtils {
    public static <T> void save(File file, Class<T> clazz) throws JAXBException {
        //Create JAXB Context
        JAXBContext jc = JAXBContext.newInstance(clazz);
        //Create marshaller
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(ConfigurationXML.getConf(), file);
    }

    public static <T> String print(T xml, Class<T> clazz) throws JAXBException {
        //Create JAXB Context
        JAXBContext jc = JAXBContext.newInstance(clazz);

        //Create marshaller
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter() ;
        marshaller.marshal(xml, writer);

        return writer.toString() ;
    }

    public static <T> T reload (File file, Class<T> clazz) throws JAXBException {
        //Create JAXB Context
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller um = jc.createUnmarshaller();
        return (T) um.unmarshal(file) ;
    }
}
