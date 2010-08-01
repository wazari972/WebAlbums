/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author kevin
 */
public interface ViewSessionImages extends ViewSession {

    enum ImgMode {PETIT, GRAND, SHRINK, FULLSCREEN, BACKGROUND, RANDOM_TAG}

    Integer getId();

    String getWidth();

    ImgMode getImgMode();

    void setContentDispositionFilename(String string) ;

    void setContentLength(int contentLength);

    void setContentType(String type);

    void redirect(String filepath);

    OutputStream getOutputStream() throws IOException;
}
