/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system.wrapper;

import java.util.Arrays;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.Metadata;
import net.wazari.common.plugins.ProcessCallback;

/**
 *
 * @author kevinpouget
 */
public class FfmpegWrapper implements Importer {

    public String getName() {
        return "ffmpeg wrapper" ;
    }

    public String getVersion() {
        return "1" ;
    }

    public String getDescription() {
        return "creates thumbnails from videos with ffmpeg" ;
    }

    public String getSupportedFilesDesc() {
        return "videos" ;
    }

    public Capability[] supports() {
        return new Capability[]{Capability.THUMBNAIL} ;
    }

    public boolean supports(String type, String ext, Capability cap) {
        if (type.contains("video"))
            return Arrays.asList(supports()).contains(cap) ;
        else return false ;
    }

    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        int width = (int)(height*16.)/9 ;
        return 0 == cb.execWaitFor(new String[]{"ffmpeg", "-i", source,
        "-vcodec", "png",
        "-vframes", "1",
        "-an",
        "-f", "rawvideo",
        "-s", width+"x"+height,
        "-y", dest});
    }

    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fullscreenMultiple(ProcessCallback cb, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setMetadata(Metadata data, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SanityStatus sanityCheck(ProcessCallback cb) {
        if (cb.execWaitFor(new String[]{"ffmpeg", "-L"}) == 0) {
            return SanityStatus.PASS;
        } else {
            return SanityStatus.FAIL ;
        }
    }

    public int getPriority() {
        return 4 ;
    }

    public void fullscreenFile(ProcessCallback cb, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
