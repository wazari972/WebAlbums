package net.wazari.util;

public interface FileUtilWrapper {
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
