package net.wazari.libvfs.vfs;

import com.jnetfs.core.Code;
import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.List;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IDirectory;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.ILink;
import net.wazari.libvfs.inteface.SLink;

public class LibVFS extends JnetFSAdapter {
    private long clientcount = 0;

    static class FileInfo {
        public String fullname = null;
        public String shortname = null;
        public long length = 0;
        public long lastupdate = 0;
        public long reference = 0;
    }

    public static Resolver resolver = null;
    
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
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            debug("ATTRIBUTES no file");
            return ENOENT;
        }
        
        File.Access[] access = file.getAccess();
        
        int mode = 0;
        
        if (Arrays.asList(access).contains(File.Access.R)) {
            debug("ATTRIBUTES R");
            mode |= 1 << 2; 
        }
        if (Arrays.asList(access).contains(File.Access.W)) {
            debug("ATTRIBUTES W");
            mode |= 1 << 1; 
        }
        if (Arrays.asList(access).contains(File.Access.X)) {
            debug("ATTRIBUTES X");
            mode |= 1;
        }
        mode = (mode << 6) | (mode << 3);
        if (file instanceof IDirectory) {
            debug("ATTRIBUTES DIR");
            mode |= Code.S_IFDIR;
        } else if (file instanceof ILink) {
            debug("ATTRIBUTES LNK");
            mode |= Code.S_IFLNK;
        } else {
            
            debug("ATTRIBUTES REG");
            mode |= Code.S_IFREG;
        }
        debug("ATTRIBUTES "+mode);
        JnetAttributes.setMode(jniEnv, mode);
        JnetAttributes.setTime(jniEnv, file.getTime() / 1000L);
        JnetAttributes.setSize(jniEnv, file.getSize());
        
        JnetAttributes.setLinks(jniEnv, 1);

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
        
        IFile file = resolver.getFile(path) ;
        if (file == null || !(file instanceof IDirectory)) {
            debug("LIST\t EACCES " + EACCES);
            return EACCES;
        }
        IDirectory dir = (IDirectory) file;
        int i = 0;
        List<IFile> files = dir.listFiles();
        for (IFile inFile : files) {
            String name = inFile.getShortname();
            JnetList.addName(jniEnv, i++, name);
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
        
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            debug("OPEN\t" + path + "(ENOENT)");
            return ENOENT;
        }
        
        long flags = JnetOpen.getFlags(jniEnv);
        
        debug("OPEN\t" + path + "("+flags+")");
        flags &= O_ACCMODE;
        if (!file.supports(flags)) {
            debug("OPEN\t" + path + "(EACCES)");
            return EACCES;
        }
        
        file.incReference();
        file.open();
        JnetOpen.setHandle(jniEnv, file.getHandle());
        
        JnetOpen.setDirectIO(jniEnv, false);
        JnetOpen.setKeepCache(jniEnv, false);
        debug("Opened with success");
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
        IFile file = resolver.getFile(path) ;
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
        IFile file = resolver.getFile(path) ;
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
        IFile file = resolver.getFile(path) ;
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
    
    @Override
    public int write(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            debug("WRITE\tENOENT");
            return ENOENT;
        }
        
        debug("WRITE\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        
        return ENOSYS;
    }
    
    @Override
    public int create(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("CREATE\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        return ENOSYS;
    }

    @Override
    public int mkdir(JnetJNIConnector jniEnv) throws JnetException {
        debug("MKDIR");
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("MKDIR\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        
        return ENOSYS;
    }

    @Override
    public int delete(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("DELETE\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ENOSYS;
    }

    @Override
    public int rmdir(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("RMDIR\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ENOSYS;
    }

    @Override
    public int rename(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("RENAME\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ENOSYS;
    }

    @Override
    public int touch(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("TOUCH\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ENOSYS;
    }

    @Override
    public int symlink(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("SYMLINK\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ENOSYS;
    }

    @Override
    public int readlink(JnetJNIConnector jniEnv) throws JnetException {
        debug("READLINK");
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("READLINK\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null || !(file instanceof SLink)) {
            return ENOENT;
        }
        
        JnetReadLink.setRealPath(jniEnv, ((SLink) file).getTarget());
        
        return ESUCCESS;
    }
    
    @Override
    public int truncate(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("TRUNCATE\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ENOSYS;
    }
    
    @Override
    public int chmod(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(JnetFSImpl.PATH);
        if (path == null) {
            return ENOENT;
        }
        
        debug("CHMOD\t" + path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return ENOENT;
        }
        return ESUCCESS;
    }
}
