/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.jfs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.JnetAttributes;
import com.jnetfs.core.relay.impl.JnetChmod;
import com.jnetfs.core.relay.impl.JnetEnv;
import com.jnetfs.core.relay.impl.JnetFSAdapter;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.JnetList;
import com.jnetfs.core.relay.impl.JnetOpen;
import com.jnetfs.core.relay.impl.JnetRead;
import com.jnetfs.core.relay.impl.JnetReadLink;
import com.jnetfs.core.relay.impl.JnetStatfs;
import com.jnetfs.core.relay.impl.JnetTouch;
import com.jnetfs.core.relay.impl.JnetTruncate;
import com.jnetfs.core.relay.impl.JnetWrite;

public class JnetJavaFS extends JnetFSAdapter {

    private static String SOFTLINK_SUFFIX = ".slink";
    private static boolean JDK_1_6 = System.getProperty("java.version").startsWith("1.6");
    private static boolean windows = System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;

    static class FileInfo {

        public RandomAccessFile file = null;
        public String name = null;
        public String mode = null;
        public long handle = 0;
        public long reference = 0;
    }
    //opened files
    protected static final Map<String, FileInfo> registry = new HashMap<String, FileInfo>();
    //last file handle
    protected static long file_handle = 1000;
    protected static long clientcount = 0;
    
    /**
     * init file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int init(JnetJNIConnector jniEnv) throws JnetException {
        debug("INIT");
        clientcount++;
        return ESUCCESS;
    }

    /**
     * destroy file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int destroy(JnetJNIConnector jniEnv) throws JnetException {
        debug("DESTROY");
        clientcount--;
        if (clientcount <= 0) {
            for (String key : registry.keySet()) {
                try {
                    registry.get(key).file.close();
                } catch (IOException ex) {
                }
            }
            registry.clear();
        }
        return ESUCCESS;
    }

    /**
     * get attributes of a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int attributes(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("ATTRIBUTES\t" + file);
        if (file != null && !file.exists()) {
            file = getFile(root, path + SOFTLINK_SUFFIX);
        }
        if (file != null && !file.exists()) {
            return ENOENT;
        }
        int st_mode = 0;
        long st_nlink = 1;
        long st_mtim = 0;
        long st_size = 0;
        if (file == null) {
            st_mode = S_IFDIR | 0755;
            st_mtim = System.currentTimeMillis() / 1000l;
            st_size = 1l << 12;
        } else {
            st_mode = getRigtsMask(file);
            st_mtim = file.lastModified() / 1000l;
            st_size = file.length();
        }
        JnetAttributes.setMode(jniEnv, st_mode);
        JnetAttributes.setTime(jniEnv, st_mtim);
        JnetAttributes.setLinks(jniEnv, st_nlink);
        JnetAttributes.setSize(jniEnv, st_size);
        return ESUCCESS;
    }

    /**
     * read directory
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int list(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("LIST\t" + file);
        if (file != null && !file.exists()) {
            return ENOENT;
        }
        File[] files = null;
        if (file == null) {
            files = File.listRoots();
        } else {
            files = file.listFiles();
        }
        int count = 0;
        if (files != null) {
            boolean allowHidden = Boolean.valueOf(JnetEnv.getPropoerty("jnet.allow.hidden"));
            for (File f : files) {
                //windows patch
                if (file != null && !allowHidden && f.isHidden()) {
                    continue;
                }
                String name = f.getName();
                if (windows) {
                    if ("".equals(name)) {
                        name = f.getAbsolutePath();
                        name = name.substring(0, 1);
                    }
                }
                if (name.endsWith(SOFTLINK_SUFFIX)) {
                    name = name.substring(0, name.lastIndexOf('.'));
                }
                JnetList.addName(jniEnv, count, name);
                count++;
            }
        }
        JnetList.setCount(jniEnv, count);
        return ESUCCESS;
    }

    /**
     * open a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int open(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("OPEN\t" + file);
        if (!file.exists()) {
            return ENOENT;
        }
        if (path.endsWith(SOFTLINK_SUFFIX)) {
            return EACCES;
        }
        long flags = JnetOpen.getFlags(jniEnv);
        flags &= O_ACCMODE;
        String mode = "";
        switch ((int) flags) {
            case O_RDONLY:
                if (!file.canRead()) {
                    return EACCES;
                }
                mode = "r";
                break;
            case O_WRONLY:
            case O_RDWR:
                if (!file.canWrite() && !file.canRead()) {
                    return EACCES;
                }
                mode = "rw";
                break;
        }
        if (!registry.containsKey(path)) {
            try {
                long handle = file_handle++;
                FileInfo info = new FileInfo();
                info.file = new RandomAccessFile(file, mode);
                info.handle = handle;
                info.mode = mode;
                info.name = path;
                info.reference++;
                registry.put(path, info);
                JnetOpen.setHandle(jniEnv, handle);
                JnetOpen.setDirectIO(jniEnv, false);
                JnetOpen.setKeepCache(jniEnv, false);
            } catch (IOException ex) {
                return EACCES;
            }
        } else {
            FileInfo info = registry.get(path);
            if ("rw".equals(mode) && "r".equals(info.mode)) {
                return EACCES;
            }
            info.reference++;
            JnetOpen.setHandle(jniEnv, info.handle);
            JnetOpen.setDirectIO(jniEnv, false);
            JnetOpen.setKeepCache(jniEnv, false);
        }
        return ESUCCESS;
    }

    /**
     * read files
     *
     * @param jniEnv JnetJNIConnector
     * @return count
     * @throws JnetException JnetException
     */
    public int read(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("READ\t" + path);
        FileInfo info = registry.get(path);
        if (info == null) {
            return EACCES;
        }
        long size = JnetRead.getSize(jniEnv);
        long offset = JnetRead.getOffset(jniEnv);
        try {
            long maxlen = info.file.length();
            if (offset >= maxlen) {
                return 0;
            }
            if (maxlen - offset < size) {
                size = maxlen - offset;
            }
            byte buffer[] = new byte[(int) size];
            if (offset != info.file.getFilePointer()) {
                info.file.seek(offset);
            }
            info.file.readFully(buffer);
            JnetRead.setDate(jniEnv, buffer);
            buffer = null;
        } catch (IOException ex) {
            return EIO;
        }
        return (int) size;
    }

    /**
     * write to file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int write(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("WRITE\t" + path);
        FileInfo info = registry.get(path);
        if (info == null) {
            return EACCES;
        }
        int size = (int) JnetWrite.getSize(jniEnv);
        long offset = JnetWrite.getOffset(jniEnv);
        try {
            info.file.seek(offset);
            info.file.write(JnetWrite.getDate(jniEnv));
        } catch (IOException ex) {
            return EIO;
        }
        return size;
    }

    /**
     * release file
     *
     * @param jniEnv JnetJNIConnector
     * @return OK
     * @throws JnetException JnetException
     */
    public int release(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("RELEASE\t" + file);
        if (!file.exists()) {
            return ENOENT;
        }
        FileInfo info = registry.get(path);
        if (info == null) {
            return EACCES;
        }
        try {
            if (info.reference < 1) {
                registry.remove(path).file.close();
            }
        } catch (IOException ex) {
        }
        return ESUCCESS;
    }

    /**
     * truncate a file
     *
     * @param jniEnv JnetJNIConnector
     * @return OK
     * @throws JnetException JnetException
     */
    public int truncate(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("TRUNCATE\t" + file);
        if (!file.exists()) {
            return ENOENT;
        }
        long offset = JnetTruncate.getOffset(jniEnv);
        FileInfo info = registry.get(path);
        if (info != null) {
            try {
                info.file.setLength(offset);
            } catch (IOException ex) {
                return EIO;
            }
        } else {
            RandomAccessFile ras = null;
            try {
                ras = new RandomAccessFile(file, "rw");
                ras.setLength(offset);
            } catch (IOException ex) {
                return EIO;
            } finally {
                try {
                    if (ras != null) {
                        ras.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
        return ESUCCESS;
    }

    /**
     * Close a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int flush(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        debug("CLOSE\t" + path);
        FileInfo info = registry.get(path);
        if (info == null) {
            return EACCES;
        }
        info.reference--;
        return ESUCCESS;
    }

    /**
     * create a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int create(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("CREATE\t" + file);
        if (file.exists()) {
            return EEXIST;
        }
        if (path.endsWith(SOFTLINK_SUFFIX)) {
            return EACCES;
        }
        try {
            if (!file.createNewFile()) {
                return EIO;
            }
        } catch (IOException ex) {
            return EIO;
        }
        return ESUCCESS;
    }

    /**
     * create a directory
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int mkdir(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("MKDIR\t" + file);
        if (path.endsWith(SOFTLINK_SUFFIX)) {
            return EACCES;
        }
        if (file.exists()) {
            return EEXIST;
        }
        if (!file.mkdir()) {
            return EIO;
        }
        return ESUCCESS;
    }

    /**
     * remove a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int delete(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("DELETE\t" + file);
        if (!file.exists()) {
            file = getFile(root, path + SOFTLINK_SUFFIX);
        }
        if (!file.exists()) {
            return ENOENT;
        }
        if (!file.delete()) {
            return EIO;
        }
        return ESUCCESS;
    }

    /**
     * remove a directory
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int rmdir(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("RMDIR\t" + file);
        if (!file.exists()) {
            return ENOENT;
        }
        if (!file.delete()) {
            return EIO;
        }
        return ESUCCESS;
    }

    /**
     * rename a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int rename(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        String to = jniEnv.getString(JnetFSImpl.TO);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("RENAME\t" + file + "->" + to);
        if (!file.exists()) {
            file = getFile(root, path + SOFTLINK_SUFFIX);
        }
        if (!file.exists()) {
            return ENOENT;
        }
        if (file.getName().endsWith(SOFTLINK_SUFFIX)) {
            to += SOFTLINK_SUFFIX;
        } else if (to.endsWith(SOFTLINK_SUFFIX)) {
            return EACCES;
        }
        File fto = null;
        if (windows) {
            int idx = to.lastIndexOf('/');
            if (idx != -1) {
                to = to.substring(idx + 1);
            }
            fto = new File(file.getParentFile(), to);
        } else {
            fto = getFile(root, to);
        }
        if (!file.renameTo(fto)) {
            return EIO;
        }
        return ESUCCESS;
    }

    /**
     * change the time
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int touch(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("TOUCH\t" + file);
        if (windows) {
            return ESUCCESS;
        }
        long lastupdate = JnetTouch.getSecond(jniEnv) * 1000;
        lastupdate += JnetTouch.getMillionSecond(jniEnv) / 1000000;
        if (!file.setLastModified(lastupdate)) {
            return EACCES;
        }
        return ESUCCESS;
    }

    /**
     * statistic of fs
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int statfs(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        debug("STATFS\t" + path);
        long count = 1l << 26;
        JnetStatfs.setNameMaxLen(jniEnv, 255);
        JnetStatfs.setBlockSize(jniEnv, 1 << 12);
        JnetStatfs.setBlocks(jniEnv, count);
        JnetStatfs.setFreeBlocks(jniEnv, count);
        JnetStatfs.setAvailableBlocks(jniEnv, count);
        JnetStatfs.setFreeSize(jniEnv, count);
        JnetStatfs.setFiles(jniEnv, count);
        JnetStatfs.setFileFree(jniEnv, count);
        JnetStatfs.setFileAvailable(jniEnv, count);
        JnetStatfs.setFileSID(jniEnv, 0);
        return ESUCCESS;
    }

    /**
     * change the mode of a file
     */
    public int chmod(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path);
        debug("CHMOD\t" + file);
        if (file != null && !file.exists()) {
            return ENOENT;
        }
        int mode_t = JnetChmod.getMode(jniEnv);
        int r = ESUCCESS;
        boolean or = (mode_t & (1 << 8)) != 0;
        boolean ow = (mode_t & (1 << 7)) != 0;
        if (JDK_1_6) {
            if (!file.setReadable(or)
                    || !file.setWritable(ow)) {
                r = EACCES;
            }
        } else {
            r = EACCES;
        }
        return r;
    }

    /**
     * create a symlink
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int symlink(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        String to = jniEnv.getString(JnetFSImpl.TO);
        if (path == null || to == null) {
            return ENOENT;
        }
        File tofile = getFile(root, to + SOFTLINK_SUFFIX);
        debug("SOFTLN\t" + tofile + "->" + path);
        if (registry.containsKey(to)) {
            return EACCES;
        }
        if (path.equals(to)) {
            return ELOOP;
        }
        if (tofile != null && tofile.exists()) {
            return EEXIST;
        }
        Writer out = null;
        try {
            out = new FileWriter(tofile);
            out.write(path);
        } catch (IOException ex) {
            return EACCES;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
        return ESUCCESS;
    }

    /**
     * read the content of link
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int readlink(JnetJNIConnector jniEnv) throws JnetException {
        String root = getJnetRoot();
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        File file = getFile(root, path + SOFTLINK_SUFFIX);
        debug("READLN\t" + file);
        if (file != null && !file.exists()) {
            return ENOENT;
        }
        Reader in = null;
        try {
            in = new FileReader(file);
            char[] buff = new char[(int) file.length()];
            in.read(buff);
            JnetReadLink.setRealPath(jniEnv, new String(buff));
        } catch (IOException ex) {
            return EACCES;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
        return ESUCCESS;
    }

    /**
     * get file rights
     *
     * @param file File
     * @return rights
     */
    protected int getRigtsMask(File file) {
        int r = 0;
        if (JDK_1_6) {
            if (file.canExecute()) {
                r |= 1;
            }
        }
        if (file.canWrite()) {
            r |= 1 << 1;
        }
        if (file.canRead()) {
            r |= 1 << 2;
        }
        r = (r << 6) | (r << 3);
        if (file.isDirectory()) {
            r |= S_IFDIR;
        } else if (file.getName().endsWith(SOFTLINK_SUFFIX)) {
            r |= S_IFLNK;
        } else {
            r |= S_IFREG;
        }
        return r;
    }

    /**
     * get root file
     *
     * @return String
     */
    protected String getJnetRoot() {
        if (windows && "/".equals(JnetEnv.getJnetRoot())) {
            return null;
        }
        return JnetEnv.getJnetRoot();
    }

    /**
     * return a file
     *
     * @param root String
     * @param child String
     * @return File
     */
    protected File getFile(String root, String child) {
        if (root != null) {
            return new File(root, child);
        }
        //patch for windows
        if ("/".equals(child)) {
            return null;
        }
        String driver = child.substring(1, 2);
        child = child.substring(2);
        StringBuffer buff = new StringBuffer(driver).append(":\\");
        buff.append(child);
        return new File(buff.toString());
    }
}
