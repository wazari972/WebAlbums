/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core;

import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.JnetFSAdapter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.wazari.libvfs.vfs.LibVFS;

/**
 * Bridge Fuse-J to jnetFS
 *
 * @author jacky
 */
public final class JnetFS implements Code {
    private JnetFSAdapter adapter = new LibVFS() ;

    /**
     * init file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int init(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.init(jniEnv);
    }

    /**
     * destroy file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int destroy(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.destroy(jniEnv);
    }

    /**
     * get attributes of a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int attributes(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.attributes(jniEnv);
    }
    
    public int rmdir(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.rmdir(jniEnv);
    }
    
    public int chmod(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.chmod(jniEnv);
    }

    /**
     * read directory
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int list(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.list(jniEnv);
    }

    /**
     * open a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int open(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.open(jniEnv);
    }

    /**
     * read files
     *
     * @param jniEnv JnetJNIConnector
     * @return count
     * @throws JnetException JnetException
     */
    public int read(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.read(jniEnv);
    }

    public int write(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.write(jniEnv);
    }
    
    /**
     * release file
     *
     * @param jniEnv JnetJNIConnector
     * @return OK
     * @throws JnetException JnetException
     */
    public int release(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.release(jniEnv);
    }

    /**
     * Close a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int flush(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.flush(jniEnv);
    }

    /**
     * statistic of fs
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int statfs(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.statfs(jniEnv);
    }
    
    
    public int readlink(JnetJNIConnector jniEnv) throws JnetException {
        return adapter.readlink(jniEnv);
    }

    /**
     * print JnetFS Usage
     */
    private static void printUsage() {
        PrintStream ps = System.out;
        ps.println("usage: jnetfs server mountpoint [options]");
        ps.println();
        ps.println("general options:");
        ps.println("   -o opt,[opt...]        mount options");
        ps.println("   -h   --help            print help");
        ps.println();
        ps.println("FUSE options:");
        ps.println("   -d   -o debug          enable debug output (implies -f)");
        ps.println("   -f                     foreground operation");
        ps.println("   -s                     disable multi-threaded operation");
        ps.println("   -o allow_other         allow access to other users");
        ps.println("   -o allow_root          allow access to root");
        ps.println("   -o nonempty            allow mounts over non-empty file/dir");
        ps.println("   -o default_permissions enable permission checking by kernel");
        ps.println("   -o fsname=NAME         set filesystem name");
        ps.println("   -o subtype=NAME        set filesystem type");
        ps.println("   -o large_read          issue large read requests (2.4 only)");
        ps.println("   -o max_read=N          set maximum size of read requests");
        ps.println("   -o hard_remove         immediate removal (don't hide files)");
        ps.println("   -o use_ino             let filesystem set inode numbers");
        ps.println("   -o readdir_ino         try to fill in d_ino in readdir");
        ps.println("   -o direct_io           use direct I/O");
        ps.println("   -o kernel_cache        cache files in kernel");
        ps.println("   -o [no]auto_cache      enable caching based on modification times (off)");
        ps.println("   -o umask=M             set file permissions (octal)");
        ps.println("   -o uid=N               set file owner");
        ps.println("   -o gid=N               set file group");
        ps.println("   -o entry_timeout=T     cache timeout for names (1.0s)");
        ps.println("   -o negative_timeout=T  cache timeout for deleted names (0.0s)");
        ps.println("   -o attr_timeout=T      cache timeout for attributes (1.0s)");
        ps.println("   -o ac_attr_timeout=T   auto cache timeout for attributes (attr_timeout)");
        ps.println("   -o intr                allow requests to be interrupted");
        ps.println("   -o intr_signal=NUM     signal to send on interrupt (10)");
        ps.println("   -o modules=M1[:M2...]  names of modules to push onto filesystem stack");
        ps.println("   -o max_write=N         set maximum size of write requests");
        ps.println("   -o max_readahead=N     set maximum readahead");
        ps.println("   -o async_read          perform reads asynchronously (default)");
        ps.println("   -o sync_read           perform reads synchronously");
        ps.println("   -o atomic_o_trunc      enable atomic open+truncate support");
        ps.println();
        ps.println("Module options:");
        ps.println("[subdir]");
        ps.println("   -o subdir=DIR          prepend this directory to all paths (mandatory)");
        ps.println("   -o [no]rellinks        transform absolute symlinks to relative");
        ps.println("[iconv]");
        ps.println("   -o from_code=CHARSET   original encoding of file names (default: UTF-8)");
        ps.println("   -o to_code=CHARSET     new encoding of the file names (default: UTF-8)");
    }

    /**
     * Program entrance
     *
     * @param args string[]
     */
    public static void main(String[] args) {
        String licence =
                "JavaNET FileSystem 1.0 Copyright (C) 2009 - " + Calendar.getInstance().get(Calendar.YEAR) + " Jacky WU (hongzhi_wu@hotmail.com)\n"
                + "This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.\n"
                + "This is free software, and you are welcome to redistribute it\n"
                + "under certain conditions.\n"
                + "see <http://www.gnu.org/licenses/>";
        System.out.println(licence);
        //setup parameter for java(fuse)
        List<String> fuseArgs = new ArrayList<String>();
        String mpoint = null;
        boolean option = false;
        for (int i = 0; i < args.length; i++) {
            if ("-".startsWith(args[i])) {
                option = true;
            }
            if (mpoint == null
                    && !"-".startsWith(args[i])
                    && !option) {
                mpoint = args[i];
            }
            fuseArgs.add(args[i]);
        }
        if (mpoint == null || fuseArgs.indexOf("-h") != -1) {
            printUsage();
            if (mpoint == null) {
                System.exit(-1);
            } else {
                System.exit(0);
            }
        }

        //set java daemon
        if (fuseArgs.indexOf("-s") == -1) {
            fuseArgs.add("-s");
        }
        if (fuseArgs.indexOf("-f") == -1) {
            fuseArgs.add("-f");
        }
        String[] fargv = fuseArgs.toArray(new String[fuseArgs.size()]);
        try {
            //setup shutdown hook/unmount JnetFS
            final String mountPoint = mpoint;
            final JnetFS jnet = new JnetFS();
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    System.out.println("Bye!");
                    jnet.umount(mountPoint);
                }
            });
            jnet.mount(fargv, false);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Mount file system by calling native c function
     *
     * @param arg String[]
     * @param debug debug
     * @return integer result
     * @throws JnetException JnetException
     */
    private native int mount(String[] arg, boolean debug) throws JnetException;

    /**
     * unmount file system by calling native c function
     *
     * @param arg String
     */
    private native void umount(String path);
    
    static {
        try {
            System.loadLibrary("JnetFS");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }
    
}
