package net.wazari.view.vfs;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Map;
import net.wazari.libvfs.inteface.IDirectory;
import net.wazari.libvfs.inteface.IFile;

public class WebAlbumsFS extends JnetFSAdapter {
    private long clientcount = 0;

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
        
        return ESUCCESS;
        
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
        
        IFile file = Resolver.getFile(path) ;
        if (file == null) {
            debug("ATTRIBUTES no file\t" + path);
            return EACCES;
        }
        
        int st_mode;
        long st_mtim = file.getTime() / 1000L;
        long st_size;
        if (file instanceof IDirectory) {
            st_size = 1 << 12;
            st_mode = getFileDirMask(true);
        } else {
            st_size = file.getSize();
            st_mode = getFileDirMask(false);
        }
        
        JnetAttributes.setMode(jniEnv, st_mode);
        JnetAttributes.setTime(jniEnv, st_mtim);
        JnetAttributes.setLinks(jniEnv, 1);
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
        
        IFile file = Resolver.getFile(path) ;
        if (file == null || !(file instanceof IDirectory)) {
            return EACCES;
        }
        IDirectory dir = (IDirectory) file;
        int i = 0;
        Map<String, IFile> files = dir.listFiles();
        for (String inFilenames : files.keySet()) {
            JnetList.addName(jniEnv, i++, inFilenames);
        }
        JnetList.setCount(jniEnv, i);
        
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
        IFile file = Resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        long flags = JnetOpen.getFlags(jniEnv);
        flags &= O_ACCMODE;
        if (!file.supports(flags)) {
                return EACCES;
        }
        
        file.incReference();
        JnetOpen.setHandle(jniEnv, file.getHandle());
        
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
        IFile file = Resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        long SIZE = JnetRead.getSize(jniEnv);
        long OFFSET = JnetRead.getOffset(jniEnv);
        
        long maxlen = file.getSize();
        if (OFFSET >= maxlen) {
            return 0;
        }
        if (maxlen - OFFSET < SIZE) {
            SIZE = maxlen - OFFSET;
        }
        byte buffer[] = new byte[(int) SIZE];
        try {
            //String range = "bytes=" + OFFSET + "-" + (OFFSET + SIZE - 1);

            int count = 0;
            InputStream is = new StringBufferInputStream(file.getContent());
            while (count < buffer.length) {
                int c = is.read(buffer, count, buffer.length - count);
                count += c;
            }
            is.close();
            JnetRead.setData(jniEnv, buffer);

            return (int) SIZE;
        } catch (IOException ex) {
            return EIO;
        }
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
        IFile file = Resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        file.release();
        
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
        if (path == null) {
            return ENOENT;
        }
        
        debug("CLOSE\t" + path);
        IFile file = Resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        file.close();
        file.decReference();
        
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
        JnetStatfs.setNameMaxLen(jniEnv, 2550);
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
    protected int getFileDirMask(boolean isDir) {
        int r = 0;
        r |= 1 << 2; //read-only
        r = (r << 6) | (r << 3);
        if (isDir) {
            r |= S_IFDIR;
        } else {
            r |= S_IFREG;
        }
        return r;
    }
}
