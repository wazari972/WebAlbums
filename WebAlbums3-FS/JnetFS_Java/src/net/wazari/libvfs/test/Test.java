/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.test;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IFile;

/**
 *
 * @author kevin
 */
public class Test {
    public static void main(String[] args) {
        treatFolder(new Root(), 0);
    }
    public static void treatFolder(Object current, int depth) {
        if (current.getClass().isAnnotationPresent(Directory.class)) {
            Directory annotation = current.getClass().getAnnotation(Directory.class);
            print("<root directory: "+annotation.name()+">", depth);
        }
        print("===", depth);
        depth += 1;
        for (Field field : current.getClass().getDeclaredFields()) {
            if (field == null) 
                continue;
            
            Object field_value;
            try {
                field_value = field.get(current) ;
            } catch (IllegalArgumentException ex) {
                print(ex.getMessage(), depth);
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                return;
            } catch (IllegalAccessException ex) {
                print(ex.getMessage(), depth);
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            if (field.isAnnotationPresent(Directory.class)) {
                Directory folder = field.getAnnotation(Directory.class);
                
                print("<directory: "+folder.name()+">", depth);
                treatFolder(field_value, depth);
                
            } else if (field.isAnnotationPresent(File.class)) {
                File file_desc = field.getAnnotation(File.class);
                print("<file: "+file_desc.name()+" "+file_desc.access()+ ">", depth);
                IFile file  = (IFile) field_value;
                print("---", depth);
                print(file.getContent(), depth);
                print("---", depth);
            } else {
                print("No annotation ... ", depth);
            }
        }
    }
    
    private static void print(String str, int depth) {
        String prefix = "";
        for (int i = 0; i < depth; i++)
            prefix += "    ";
        System.out.println(prefix + str);
    }
}
