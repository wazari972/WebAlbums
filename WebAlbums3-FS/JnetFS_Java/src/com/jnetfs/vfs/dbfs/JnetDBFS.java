/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.dbfs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.JnetAttributes;
import com.jnetfs.core.relay.impl.JnetFSAdapter;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.JnetList;
import com.jnetfs.core.relay.impl.JnetOpen;
import com.jnetfs.core.relay.impl.JnetRead;
import com.jnetfs.core.relay.impl.JnetStatfs;
import com.jnetfs.core.relay.impl.JnetTruncate;
import com.jnetfs.core.relay.impl.JnetWrite;
import com.jnetfs.core.vfs.VFile;
import com.jnetfs.core.vfs.VStream;
import com.jnetfs.vfs.dbfs.impl.DB;
import com.jnetfs.vfs.dbfs.impl.Filter;

public class JnetDBFS extends JnetFSAdapter {
    //opened files

    protected static final Map<String, VFile> files = new HashMap<String, VFile>();
    protected static final Map<String, Long> handles = new HashMap<String, Long>();
    protected static DB db;
    //last file handle
    protected static long file_handle = 1000;

    /**
     * init file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    public int init(JnetJNIConnector jniEnv) throws JnetException {
        db = new DB();
        try {
            debug("INIT");
            db.load();
        } catch (IOException ex) {
            debug(ex);
            return EIO;
        }
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
        db.unload();
        db = null;
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
        debug("ATTRIBUTES\t" + path);
        try {
            VFile vf = VFile.find(path);
            long st_nlink = 1;
            long st_mtim = 0;
            long st_size = 0;
            if (vf.isDirectory()) {
                List<VFile> files = vf.listFiles();
                if (files != null) {
                    st_nlink += files.size();
                }
            }
            st_mtim = vf.getLastUpdate() / 1000l;
            st_size = vf.size();
            JnetAttributes.setMode(jniEnv, vf.getMode());
            JnetAttributes.setTime(jniEnv, st_mtim);
            JnetAttributes.setLinks(jniEnv, st_nlink);
            JnetAttributes.setSize(jniEnv, st_size);
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return ENOENT;
        }
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
        debug("LIST\t" + path);
        try {
            VFile vf = VFile.find(path);
            List<VFile> files = vf.listFiles();
            int count = 0;
            if (files != null) {
                for (VFile f : files) {
                    JnetList.addName(jniEnv, count, f.getName());
                    count++;
                }
            }
            JnetList.setCount(jniEnv, count);
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return ENOENT;
        }
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
        debug("OPEN\t" + path);
        try {
            VFile vf = VFile.find(path);
            if (!files.containsKey(path)) {
                long handle = file_handle++;
                files.put(path, vf);
                handles.put(path, handle);
                JnetOpen.setHandle(jniEnv, handle);
                JnetOpen.setDirectIO(jniEnv, false);
                JnetOpen.setKeepCache(jniEnv, false);
            } else {
                JnetOpen.setHandle(jniEnv, handles.get(path));
                JnetOpen.setDirectIO(jniEnv, false);
                JnetOpen.setKeepCache(jniEnv, false);
            }
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return EACCES;
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
        long size = JnetRead.getSize(jniEnv);
        long offset = JnetRead.getOffset(jniEnv);
        try {
            VFile vf = VFile.find(path);
            if (!(vf instanceof VStream)) {
                return EACCES;
            }
            VStream vs = (VStream) vf;
            long maxlen = vf.size();
            if (offset >= maxlen) {
                return 0;
            }
            if (maxlen - offset < size) {
                size = maxlen - offset;
            }
            byte buffer[] = new byte[(int) size];
            vs.seek((int) offset);
            vs.read(buffer);
            JnetRead.setData(jniEnv, buffer);
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return EACCES;
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
        int size = (int) JnetWrite.getSize(jniEnv);
        try {
            VFile vf = VFile.find(path);
            if (!Filter.NAME.equals(vf.getName())) {
                return EIO;
            }
            if (!(vf instanceof VStream)) {
                return EACCES;
            }
            long offset = JnetWrite.getOffset(jniEnv);
            VStream vs = (VStream) vf;
            vs.seek((int) offset);
            vs.write(JnetWrite.getDate(jniEnv));
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return EACCES;
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
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("RELEASE\t" + path);
        try {
            VFile vf = VFile.find(path);
            if (!(vf instanceof VStream)) {
                return EACCES;
            }
            VStream vs = (VStream) files.remove(path);
            if (vs != null) {
                vs.seek(0);
            }
            handles.remove(path);
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return EACCES;
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
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("TOUCH\t" + path);
        try {
            VFile.find(path).touch();
        } catch (IOException ex) {
            debug("Access " + path + " error.");
            return EACCES;
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
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("TRUNCATE\t" + path);
        try {
            VFile vf = VFile.find(path);
            long offset = JnetTruncate.getOffset(jniEnv);
            vf.size((int) offset);
        } catch (IOException ex) {
            debug("Access " + path + " error.");
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
        if (path == null) {
            return ENOENT;
        }
        debug("STATFS\t" + path);
        long count = Runtime.getRuntime().freeMemory() / 512;
        JnetStatfs.setNameMaxLen(jniEnv, 255);
        JnetStatfs.setBlockSize(jniEnv, 512);
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
}
