/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.plugin;

import java.util.Arrays;
import net.wazari.common.plugins.GenericImporter;
import net.wazari.common.plugins.Importer.Capability;
import net.wazari.common.plugins.Importer.ProcessCallback;
import net.wazari.common.plugins.Importer.SanityStatus;

/**
 *
 * @author kevinpouget
 */
public class FfmpegWrapper  extends GenericImporter {

    public String getName() {
        return "ffmpeg wrapper" ;
    }

    public String getVersion() {
        return "1" ;
    }

    public String getTargetSystem() {
        return "various" ;
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
}
