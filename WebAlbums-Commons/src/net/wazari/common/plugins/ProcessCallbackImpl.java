/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.common.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import net.wazari.common.plugins.Importer.ProcessCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
public class ProcessCallbackImpl implements ProcessCallback {
    private static final Logger log = LoggerFactory.getLogger(ProcessCallbackImpl.class.getName());

    private static final ProcessCallbackImpl cb = new ProcessCallbackImpl() ;

    public static ProcessCallback getProcessCallBack () {
        return cb ;
    }

    private ProcessCallbackImpl(){}

    private Process execPS(String[] cmd) {
        try {
            log.debug( "exec: {}", Arrays.toString(cmd));

            return Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            log.warn( "Couldn''t execute the process:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public void exec(String[] cmd) {
        //execPS(cmd);
        execWaitFor(cmd);
    }

    @Override
    public int execWaitFor(String[] cmd) {
        Process ps = execPS(cmd);

        if (ps == null) {
            return -1;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String str;

        while (true) {
            try {
                while ((str = reader.readLine()) != null) {
                    log.debug(str);
                }

                reader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));

                while ((str = reader.readLine()) != null) {
                    log.debug( "err - {}", str);
                }
                int ret = ps.waitFor();
                log.debug( "ret:{}", ret);

                return ret;

            } catch (InterruptedException | IOException e) {}
        }
    }
}
