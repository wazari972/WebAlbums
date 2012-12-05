/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

/**
 *
 * @author kevin
 */
public class ValueFile extends SFile {
    private SetterCallback setter = null;
    private GetterCallback getter = null;
    
    public interface GetterCallback {
        String getContent();
    }
    
    public interface SetterCallback {
        void setContent(String content);
    }
    
    public ValueFile(SetterCallback setter, GetterCallback getter) {
        this.setter = setter;
        this.getter = getter;
    }

    public ValueFile() {}
    
    @Override
    public void open() {
        //currently, open doesn't check of a O_CREAT / O_APPEND flag ...
        this.truncate();
    }
    
    @Override
    public void close() {
        if (setter != null) {
            setter.setContent(content);
        }
    }
    
    @Override
    public String getContent() {
        if (getter != null) {
            return getter.getContent();
        } else {
            return content;
        }
    }    
}
