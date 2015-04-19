/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.io.IOException;
import java.io.OutputStream;
import net.wazari.service.exchange.ViewSession.VSession;

/**
 *
 * @author kevin
 */
public interface ViewSessionImages extends VSession {

    Integer getBorderWidth();

    String getBorderColor();

    int getScreenSize();

    enum ImgMode {MINI, GRAND, SHRINK, BACKGROUND, RANDOM_TAG, REPRESENT_TAG, GPX}

    Integer getId();

    Integer getWidth();

    ImgMode getImgMode();

    void setContentDispositionFilename(String string) ;

    void setContentLength(int contentLength);

    void setContentType(String type);

    void redirect(String filepath);

    OutputStream getOutputStream() throws IOException;
}
