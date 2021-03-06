/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.util;


import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author kevinpouget
 */
public class XmlUtils {
    public static <T> void save(File file, T obj, Class<?> ... clazz) throws JAXBException {
        //Create JAXB Context
        JAXBContext jc = JAXBContext.newInstance(clazz);
        //Create marshaller
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(obj, file);
    }

    public static <T> String print(T xml, Class<?> ... clazz) throws JAXBException {
        //Create JAXB Context
        JAXBContext jc = JAXBContext.newInstance(clazz);

        //Create marshaller
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter() ;
        marshaller.marshal(xml, writer);

        return writer.toString() ;
    }

    public static <T> T reload (InputStream is, Class<?> ... clazz) throws JAXBException {
        //Create JAXB Context
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller um = jc.createUnmarshaller();
        return (T) um.unmarshal(is) ;
    }
}
