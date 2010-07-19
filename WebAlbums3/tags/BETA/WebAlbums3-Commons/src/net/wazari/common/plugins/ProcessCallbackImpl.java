/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.common.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wazari.common.plugins.Importer.ProcessCallback;

/**
 *
 * @author kevinpouget
 */
public class ProcessCallbackImpl implements ProcessCallback {
    private static final Logger log = Logger.getLogger(ProcessCallbackImpl.class.getName());

    private static final ProcessCallbackImpl cb = new ProcessCallbackImpl() ;

    public static ProcessCallback getProcessCallBack () {
        return cb ;
    }

    private ProcessCallbackImpl(){}

    private Process execPS(String[] cmd) {
        try {
            log.log(Level.INFO, "exec: {0}", Arrays.toString(cmd));

            return Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            log.log(Level.WARNING, "Couldn''t execute the process:{0}", e.getMessage());
            return null;
        }
    }

    public void exec(String[] cmd) {
        execPS(cmd);
    }

    public int execWaitFor(String[] cmd) {
        Process ps = execPS(cmd);

        if (ps == null) {
            return -1;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String str = null;

        while (true) {
            try {
                while ((str = reader.readLine()) != null) {
                    log.info(str);
                }

                reader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));

                while ((str = reader.readLine()) != null) {
                    log.log(Level.INFO, "err - {0}", str);
                }
                int ret = ps.waitFor();
                log.log(Level.INFO, "ret:{0}", ret);

                return ret;

            } catch (InterruptedException e) {
            } catch (IOException e) {
            }
        }
    }
}
