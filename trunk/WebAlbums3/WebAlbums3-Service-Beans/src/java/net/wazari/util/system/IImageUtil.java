/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system;

/**
 *
 * @author kevinpouget
 */
public interface IImageUtil {
    interface FileUtilWrapperCallBack {
        int execWaitFor(String[] cmd) ;
        void exec(String[] cmd) ;
    }

    boolean support(String type, String ext);

    boolean shrink(FileUtilWrapperCallBack cb, String source, String dest, int width);

    boolean thumbnail(FileUtilWrapperCallBack cb, String source, String dest, int height);

    boolean rotate(FileUtilWrapperCallBack cb, String degrees, String source, String dest);

    void fullscreen(FileUtilWrapperCallBack cb, String path);
}
