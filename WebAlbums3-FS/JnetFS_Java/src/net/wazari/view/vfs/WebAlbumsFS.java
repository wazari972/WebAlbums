package net.wazari.view.vfs;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebAlbumsFS extends JnetFSAdapter {
    // opened files

    private FileInfo fileInfo = null;
    //last file handle
    private long file_handle = 1000;
    private long clientcount = 0;
    private int retry = 5;

    static class FileInfo {
        public String fullname = null;
        public String shortname = null;
        public long length = 0;
        public long lastupdate = 0;
        public long reference = 0;
    }

    /**
     * init file system
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    @Override
    public int init(JnetJNIConnector jniEnv) throws JnetException {
        debug("INIT");
        clientcount++;
        try {
            if (fileInfo == null) {
                URLConnection conn = new URL(JnetEnv.getJnetRoot()).openConnection();
                conn.addRequestProperty("Range", "bytes=0-");
                conn.setDoInput(true);
                conn.connect();
                String response = conn.getHeaderField(0);
                if (response.indexOf("HTTP/1.1") == -1) {
                    throw new IOException("server doesn't support HTTP/1.1");
                }
                if (response.indexOf("206") == -1) {
                    throw new IOException("server doesn't support download partially, or bad url!");
                }
                int length = conn.getContentLength();
                fileInfo = new FileInfo();
                fileInfo.fullname = JnetEnv.getJnetRoot();
                String shortname = fileInfo.fullname;
                int idx = shortname.lastIndexOf("/");
                if (idx != -1) {
                    shortname = shortname.substring(idx + 1).trim();
                } else {
                    shortname = "web.data";
                }
                fileInfo.shortname = shortname;
                fileInfo.length = length;
                fileInfo.lastupdate = conn.getLastModified();
                try {
                    int times = Integer.parseInt(JnetEnv.getPropoerty("jnet.http.retry"));
                    if (times > 0) {
                        retry = times;
                    }
                } catch (NumberFormatException ex) {
                }
            }
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
    @Override
    public int destroy(JnetJNIConnector jniEnv) throws JnetException {
        debug("DESTROY");
        clientcount--;
        if (clientcount <= 0) {
            fileInfo = null;
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
    @Override
    public int attributes(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null || path.length() < 1) {
            return ENOENT;
        }
        debug("ATTRIBUTES\t" + path);
        int st_mode;
        long st_nlink = 1;
        long st_mtim;
        long st_size;
        if ("/".equals(path)) {
            st_size = 1 << 12;
            st_mode = getRigtsMask(true);
        } else if (fileInfo.shortname.equals(path.substring(1))) {
            st_size = fileInfo.length;
            st_mode = getRigtsMask(false);
        } else {
            return EACCES;
        }
        st_mtim = fileInfo.lastupdate / 1000L;
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
    @Override
    public int list(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("LIST\t" + path);
        if (!"/".equals(path)) {
            return EACCES;
        }
        JnetList.addName(jniEnv, 0, fileInfo.shortname);
        JnetList.setCount(jniEnv, 1);
        return ESUCCESS;
    }

    /**
     * open a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    @Override
    public int open(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null || path.length() < 1) {
            return ENOENT;
        }
        debug("OPEN\t" + path);
        if (!fileInfo.shortname.equals(path.substring(1))) {
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
        fileInfo.reference++;
        JnetOpen.setHandle(jniEnv, file_handle);
        JnetOpen.setDirectIO(jniEnv, false);
        JnetOpen.setKeepCache(jniEnv, false);
        return ESUCCESS;
    }

    /**
     * read files
     *
     * @param jniEnv JnetJNIConnector
     * @return count
     * @throws JnetException JnetException
     */
    @Override
    public int read(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("READ\t" + path);
        if (!fileInfo.shortname.equals(path.substring(1))) {
            return ENOENT;
        }
        long size = JnetRead.getSize(jniEnv);
        long offset = JnetRead.getOffset(jniEnv);
        long maxlen = fileInfo.length;
        if (offset >= maxlen) {
            return 0;
        }
        if (maxlen - offset < size) {
            size = maxlen - offset;
        }
        byte buffer[] = new byte[(int) size];
        for (int i = 0; i < retry; i++) {
            try {
                URLConnection conn = new URL(fileInfo.fullname).openConnection();
                conn.setConnectTimeout(30 * 1000);
                String range = "bytes=" + offset + "-" + (offset + size - 1);
                conn.addRequestProperty("Range", range);
                conn.setDoInput(true);
                conn.connect();
                String response = conn.getHeaderField(0);
                if (response.indexOf("HTTP/1.1") == -1) {
                    throw new IOException("server doesn't support HTTP/1.1");
                }
                if (response.indexOf("206") == -1) {
                    throw new IOException("server doesn't support download partially, or bad url!");
                }
                int count = 0;
                InputStream is = conn.getInputStream();
                while (count < buffer.length) {
                    int c = is.read(buffer, count, buffer.length - count);
                    count += c;
                }
                is.close();
                JnetRead.setDate(jniEnv, buffer);
                
                return (int) size;
            } catch (IOException ex) {
            }
        }
        return EIO;
    }

    /**
     * release file
     *
     * @param jniEnv JnetJNIConnector
     * @return OK
     * @throws JnetException JnetException
     */
    @Override
    public int release(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        debug("RELEASE\t" + path);
        return ESUCCESS;
    }

    /**
     * Close a file
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException JnetException
     */
    @Override
    public int flush(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        debug("CLOSE\t" + path);
        fileInfo.reference--;
        return ESUCCESS;
    }

    /**
     * statistic of fs
     *
     * @param jniEnv JnetJNIConnector
     * @return status
     * @throws JnetException
     */
    @Override
    public int statfs(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        debug("STATFS\t" + path);
        long count = 1;
        JnetStatfs.setNameMaxLen(jniEnv, 255);
        JnetStatfs.setBlockSize(jniEnv, 1 << 12);
        JnetStatfs.setBlocks(jniEnv, count / (1 << 12));
        JnetStatfs.setFreeBlocks(jniEnv, 0);
        JnetStatfs.setAvailableBlocks(jniEnv, 0);
        JnetStatfs.setFreeSize(jniEnv, 0);
        JnetStatfs.setFiles(jniEnv, 1);
        JnetStatfs.setFileFree(jniEnv, 0);
        JnetStatfs.setFileAvailable(jniEnv, 0);
        JnetStatfs.setFileSID(jniEnv, 0);
        return ESUCCESS;
    }

    /**
     * get file rights
     *
     * @param file File
     * @return rights
     */
    protected int getRigtsMask(boolean root) {
        int r = 0;
        r |= 1 << 2; //read-only
        r = (r << 6) | (r << 3);
        if (root) {
            r |= S_IFDIR;
        } else {
            r |= S_IFREG;
        }
        return r;
    }
}
