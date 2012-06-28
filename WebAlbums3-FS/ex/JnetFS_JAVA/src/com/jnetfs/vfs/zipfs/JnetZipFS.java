/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.zipfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.ACLAdpater;
import com.jnetfs.core.relay.impl.JnetAttributes;
import com.jnetfs.core.relay.impl.JnetEnv;
import com.jnetfs.core.relay.impl.JnetFSAdapter;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.JnetList;
import com.jnetfs.core.relay.impl.JnetOpen;
import com.jnetfs.core.relay.impl.JnetRead;
import com.jnetfs.core.relay.impl.JnetStatfs;

/**
 * Zip file read-only file system
 *
 * @author jacky
 */
public class JnetZipFS extends JnetFSAdapter {

    private static Map<String, ZipEntry> entits = new HashMap<String, ZipEntry>();
    private static ZipFile zipfile = null;

    static class FileInfo {

        public InputStream file = null;
        public ZipEntry entry = null;
        public String name = null;
        public long position = 0;
        public long handle = 0;
        public long reference = 0;
    }
    // opened files
    protected static final Map<String, FileInfo> registry = new HashMap<String, FileInfo>();
    // last file handle
    protected static long file_handle = 1000;
    protected static long clientcount = 0;

    /**
     * default constructor
     */
    public JnetZipFS() {
        super(new ACLAdpater());
    }

    /**
     * init file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    @SuppressWarnings("unchecked")
    public int init(JnetJNIConnector jniEnv) throws JnetException {
        debug("INIT");
        clientcount++;
        try {
            zipfile = new ZipFile(JnetEnv.getJnetRoot());
            Enumeration enumeration = zipfile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) enumeration.nextElement();
                String name = "/" + entry.getName();
                if (entry.isDirectory()) {
                    name = name.substring(0, name.length() - 1);
                    entry.setSize(1 << 12);
                }
                entits.put(name, entry);
            }
            ZipEntry root = new ZipEntry("/");
            root.setSize(1 << 12);
            entits.put(root.getName(), root);
            return ESUCCESS;
        } catch (IOException ex) {
            throw new JnetException(EIO, ex.getMessage());
        }
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
        try {
            zipfile.close();
        } catch (IOException ex) {
        }
        zipfile = null;
        entits.clear();
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
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        ZipEntry file = getFile(path);
        debug("ATTRIBUTES\t" + path);
        if (file == null) {
            return ENOENT;
        }
        int st_mode = 0;
        long st_nlink = 1;
        long st_mtim = 0;
        long st_size = 0;
        st_mode = getRigtsMask(file);
        st_mtim = file.getTime() / 1000l;
        st_size = file.getSize();
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
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        ZipEntry file = getFile(path);
        debug("LIST\t" + path);
        if (file == null) {
            return ENOENT;
        }
        List<String> files = new ArrayList<String>();
        for (Iterator<String> iterator = entits.keySet().iterator();
                iterator.hasNext();) {
            String name = iterator.next();
            if (name.startsWith(path)) {
                String val = name.substring(path.length());
                if (val.startsWith("/")) {
                    val = val.substring(1);
                }
                int idx = val.indexOf("/");
                if (idx != -1) {
                    val = val.substring(0, idx);
                }
                if (val.length() > 0 && !files.contains(val)) {
                    files.add(val);
                }
            }
        }
        int count = 0;
        if (files != null) {
            for (String f : files) {
                JnetList.addName(jniEnv, count, f);
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
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        ZipEntry file = getFile(path);
        debug("OPEN\t" + path);
        if (file == null) {
            return ENOENT;
        }
        long flags = JnetOpen.getFlags(jniEnv);
        flags &= O_ACCMODE;
        switch ((int) flags) {
            case O_RDONLY:
                break;
            case O_WRONLY:
            case O_RDWR:
                return EACCES;
        }
        if (!registry.containsKey(path)) {
            try {
                long handle = file_handle++;
                FileInfo info = new FileInfo();
                info.entry = file;
                info.file = zipfile.getInputStream(file);
                info.handle = handle;
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
            long maxlen = info.entry.getSize();
            if (offset >= maxlen) {
                return 0;
            }
            if (maxlen - offset < size) {
                size = maxlen - offset;
            }
            byte buffer[] = new byte[(int) size];
            if (offset != info.position) {
                info.file.close();
                info.file = zipfile.getInputStream(info.entry);
                long skipped = info.file.skip(offset);
                skipped = offset - skipped;
                while (skipped > 0) {
                    info.file.read();
                    skipped--;
                }
                info.position = offset;
            }
            for (int b = 0; b < size;) {
                int s = info.file.read(buffer, b, (int) (size - b));
                if (s == -1) {
                    return EIO;
                }
                b += s;
            }
            JnetRead.setDate(jniEnv, buffer);
            buffer = null;
            info.position += size;
        } catch (IOException ex) {
            return EIO;
        }
        return (int) size;
    }

    /**
     * release file
     *
     * @param jniEnv JnetJNIConnector
     * @return OK
     * @throws JnetException JnetException
     */
    public int release(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        ZipEntry file = getFile(path);
        debug("RELEASE\t" + path);
        if (file == null) {
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
     * statistic of fs
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    public int statfs(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        debug("STATFS\t" + path);
        long count = zipfile.size();
        JnetStatfs.setNameMaxLen(jniEnv, 255);
        JnetStatfs.setBlockSize(jniEnv, 1 << 12);
        JnetStatfs.setBlocks(jniEnv, count / (1 << 12));
        JnetStatfs.setFreeBlocks(jniEnv, 0);
        JnetStatfs.setAvailableBlocks(jniEnv, 0);
        JnetStatfs.setFreeSize(jniEnv, 0);
        JnetStatfs.setFiles(jniEnv, entits.size());
        JnetStatfs.setFileFree(jniEnv, 0);
        JnetStatfs.setFileAvailable(jniEnv, entits.size());
        JnetStatfs.setFileSID(jniEnv, 0);
        return ESUCCESS;
    }

    /**
     * get file rights
     *
     * @param file File
     * @return rights
     */
    protected int getRigtsMask(ZipEntry file) {
        int r = 0;
        r |= 1 << 2; //read-only
        r = (r << 6) | (r << 3);
        if (file.isDirectory()) {
            r |= S_IFDIR;
        } else {
            r |= S_IFREG;
        }
        return r;
    }

    /**
     * return a file
     *
     * @param root String
     * @param child String
     * @return File
     */
    protected ZipEntry getFile(String child) {
        return entits.get(child);
    }
}
