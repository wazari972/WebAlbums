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
import net.wazari.libvfs.inteface.VFSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibVFS extends JnetFSAdapter {
    private static final Logger log = LoggerFactory.getLogger(LibVFS.class.getCanonicalName());
    
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
        log.info("INIT");
        clientcount++;
        
        return Code.ESUCCESS;
        
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
        log.info("DESTROY");
        clientcount--;

        return Code.ESUCCESS;
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
        String path = jniEnv.getString(Code.PATH);
        IFile file = path == null || path.length() < 1 ? null : resolver.getFile(path) ;        
        if (file == null) {
            log.debug("ATTRIBUTES File not found: {}", path);
            return Code.ENOENT;
        }
        
        File.Access[] access = file.getAccess();
        
        int mode = 0;
        if (Arrays.asList(access).contains(File.Access.R)) {
            log.debug("ATTRIBUTES R");
            mode |= 1 << 2; 
        }
        if (Arrays.asList(access).contains(File.Access.W)) {
            log.debug("ATTRIBUTES W");
            mode |= 1 << 1; 
        }
        if (Arrays.asList(access).contains(File.Access.X)) {
            log.debug("ATTRIBUTES X");
            mode |= 1;
        }
        mode = (mode << 6) | (mode << 3);
        if (file instanceof IDirectory) {
            log.debug("ATTRIBUTES DIR");
            mode |= Code.S_IFDIR;
        } else if (file instanceof ILink && !((ILink) file).forceFile()) {
            log.debug("ATTRIBUTES LNK");
            mode |= Code.S_IFLNK;
        } else {
            log.debug("ATTRIBUTES REG");
            mode |= Code.S_IFREG;
        }
        log.debug("ATTRIBUTES {}", mode);
        JnetAttributes.setMode(jniEnv, mode);
        JnetAttributes.setTime(jniEnv, file.getTime() / 1000L);
        JnetAttributes.setSize(jniEnv, file.getSize());
        
        JnetAttributes.setLinks(jniEnv, 1);

        return Code.ESUCCESS;
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
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        log.info("LIST\t {}", path);
        
        IFile file = resolver.getFile(path) ;
        if (file == null || !(file instanceof IDirectory)) {
            log.info("LIST\t EACCES {}", Code.EACCES);
            return Code.EACCES;
        }
        IDirectory dir = (IDirectory) file;
        int i = 0;
        List<IFile> files = dir.listFiles();
        for (IFile inFile : files) {
            String name = inFile.getShortname();
            JnetList.addName(jniEnv, i++, name);
        }
        JnetList.setCount(jniEnv, i);
        
        return Code.ESUCCESS;
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
        String path = jniEnv.getString(Code.PATH);
        if (path == null || path.length() < 1) {
            return Code.ENOENT;
        }
        
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            log.info("OPEN\t {}(ENOENT)", path);
            return Code.ENOENT;
        }
        
        long flags = JnetOpen.getFlags(jniEnv);
        
        log.info("OPEN\t {} ({})", path, flags);
        flags &= Code.O_ACCMODE;
        if (!file.supports(flags)) {
            log.info("OPEN\t {} (EACCES)");
            return Code.EACCES;
        }
        
        file.incReference();
        file.open();
        JnetOpen.setHandle(jniEnv, file.getHandle());
        
        JnetOpen.setDirectIO(jniEnv, false);
        JnetOpen.setKeepCache(jniEnv, false);
        log.info("Opened with success");
        return Code.ESUCCESS;
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
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("READ\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return Code.ENOENT;
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
            return Code.EIO;
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
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("RELEASE\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return Code.ENOENT;
        }
        
        try {
            log.info("RELEASE\t do release");
            file.release();
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.info("RELEASE\t failed {}", e);
            e.printStackTrace();
            return Code.EFAULT;
        }
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
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("CLOSE\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return Code.ENOENT;
        }
        
        try {
            log.info("CLOSE\t do release");
            file.close();
            file.decReference();
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.info("CLOSE\t failed {}", e);
            e.printStackTrace();
            return Code.EFAULT;
        }
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
        String path = jniEnv.getString(Code.PATH);
        log.info("STATFS\t {}", path);
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
        return Code.ESUCCESS;
    }
    
    @Override
    public int write(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            log.info("WRITE\tENOENT");
            return Code.ENOENT;
        }
        
        log.info("WRITE\t {}", path);
        IFile file = resolver.getFile(path) ;
        
        if (file == null) {
            return Code.ENOENT;
        }
        
        long SIZE = JnetWrite.getSize(jniEnv);
        long OFFSET = JnetWrite.getOffset(jniEnv);
        byte[] DATA = JnetWrite.getData(jniEnv);
        
        log.info("SIZE {}",SIZE);
        log.info("OFFSET {}", OFFSET);
        log.info("DATA {}", new String(DATA));
        log.info("buffer {}", file.getContent());
        
        byte[] buffer = file.getContent().getBytes();
        
        byte[] target;
        //if target array is too short
        if (OFFSET + SIZE > buffer.length) {
            //create target as a larger copy
            target = new byte[(int) (OFFSET + SIZE)];
            for (int i = 0; i < buffer.length; i++) {
                target[i] = buffer[i];
            }
        } else {
            target = buffer;
        }

        for (int i = 0; i < SIZE; i++) {
            target[(int)(i + OFFSET)] = DATA[i];
        }
        log.info("wrte {}", new String(target));
        file.write(new String(target));
        
        return (int) SIZE;
    }
    
    @Override
    public int create(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("CREATE\t {}", path);
        
        IFile file = resolver.getFile(path) ;
        if (file != null) {
            return Code.EEXIST;
        }
        
        String dirname = path.substring(0, path.lastIndexOf("/"));
        String filename = path.substring(path.lastIndexOf("/") + 1);
        
        file = resolver.getFile(dirname) ;
        if (file == null || !(file instanceof IDirectory)) {
            return Code.ENOENT;
        }
        try {
            log.info("CREATE\t do create {}", path);
            ((IDirectory) file).create(filename);
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.warn("CREATE {} failed: {}", path, e);
            e.printStackTrace();
            return Code.EFAULT;
        }
    }

    @Override
    public int mkdir(JnetJNIConnector jniEnv) throws JnetException {
        log.info("MKDIR");
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        String dirname = path.substring(0, path.lastIndexOf("/"));
        String filename = path.substring(path.lastIndexOf("/") + 1);
        
        IFile file = resolver.getFile(dirname) ;
        if (file == null || !(file instanceof IDirectory)) {
            return Code.ENOENT;
        }
        
        try {
            log.info("MKDIR\t do create {}", path);
            ((IDirectory) file).mkdir(filename);
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.warn("MKDIR {} failed: {}", path, e);
            e.printStackTrace();
            return Code.EFAULT;
        }
    }

    @Override
    public int delete(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("DELETE\t{}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null || (file instanceof IDirectory)) {
            return Code.ENOENT;
        }
        
        try {
            log.info("DELETE\t do delete {}", path);
            file.unlink();
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.warn("DELETE {} failed: {}", path, e);
            e.printStackTrace();
            return Code.EFAULT;
        }
    }

    @Override
    public int rmdir(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("RMDIR\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null || !(file instanceof IDirectory)) {
            return Code.ENOENT;
        }
        
        try {
            log.info("RMDIR\t do rmdir {}", path);
            ((IDirectory) file).rmdir();
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.warn("RMDIR {} failed: {}", path, e);
            e.printStackTrace();
            return Code.EFAULT;
        }
    }
    
    @Override
    public int rename(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        IFile srcFile = path == null ? null : resolver.getFile(path) ;
        if (srcFile == null) {
            return Code.ENOENT;
        }
        
        String to = jniEnv.getString(Code.TO);
        log.info("RENAME\t {} into {}", path, to);
        
        String dirname = to.substring(0, to.lastIndexOf("/"));
        String filename = to.substring(to.lastIndexOf("/") + 1);
        
        IFile target = resolver.getFile(dirname) ;
        log.info("RENAME\t targetdir is {}", target);
        log.info("RENAME\t source file is {}", srcFile);
        if (target == null) {
            return Code.ENOENT;
        } else if (!(target instanceof IDirectory)) {
            return Code.ENOTDIR;
        }
        IDirectory targetDir = (IDirectory) target;
        IFile targetFile = resolver.getFile((IDirectory) targetDir, filename, path);
        
        log.info("RENAME\t targetfile is {}. Already exists ? {}", filename, targetFile);
        if (targetFile != null) {
            return Code.EEXIST;
        }
        try {
            targetDir.moveIn(srcFile, filename);
            
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.info("RENAME\t failed.", e);
            return Code.EFAULT;
        }
    }

    @Override
    public int touch(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("TOUCH\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return Code.ENOENT;
        }
        
        try {
            log.info("TOUCH\t do touch");
            file.touch();
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.info("TOUCH\t failed {}", e);
            e.printStackTrace();
            return Code.EFAULT;
        }
    }

    @Override
    public int symlink(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        IFile file = path == null ? null : resolver.getFile(path) ;
        if (file == null) {
            log.info("SYMLINK\t {} not found", path);
            return Code.ENOENT;
        }
        /* FIND A WAY TO ACKNOWLEDGE THE REQUESTED FILENAME */
        String to = jniEnv.getString(Code.TO);
        log.info("SYMLINK\t {} --> {}", path, to);
        
        String dirname = to.substring(0, to.lastIndexOf("/"));
        String filename = to.substring(to.lastIndexOf("/") + 1);
        
        IFile target = resolver.getFile(dirname) ;
        if (target == null) {
            return Code.ENOENT;
        } else if (!(target instanceof IDirectory)) {
            return Code.ENOTDIR;
        }
        IDirectory targetDir = (IDirectory) target;
        try {
            targetDir.acceptNewFile(file, filename);
            log.info("SYMLINK\t SUCCESS");
            return Code.ESUCCESS;
        } catch (VFSException ex) {
            log.info("SYMLINK\t failed {}", ex);
            return Code.EFAULT;
        }
    }

    @Override
    public int readlink(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        IFile file = path == null ? null : resolver.getFile(path) ;
        if (file == null || !(file instanceof SLink)) {
            log.info("READLINK\t File not found {}", path);
            return Code.ENOENT;
        }
        
        try {
            String target = ((SLink) file).getTarget();
            JnetReadLink.setRealPath(jniEnv, target);
            log.debug("READLINK\t do readlink from {} gave {}", file, target);
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.warn("READLINK\t readlink from {} FAILED", file, e);
            return Code.EFAULT;
        }
    }
    
    @Override
    public int truncate(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("TRUNCATE\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return Code.ENOENT;
        }
        
        try {
            log.info("TRUNCATE\t do truncate");
            file.truncate();
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.info("TRUNCATE\t failed",e);
            return Code.EFAULT;
        }
    }
    
    @Override
    public int chmod(JnetJNIConnector jniEnv) throws JnetException {
        String path = jniEnv.getString(Code.PATH);
        if (path == null) {
            return Code.ENOENT;
        }
        
        log.info("CHMOD\t {}", path);
        IFile file = resolver.getFile(path) ;
        if (file == null) {
            return Code.ENOENT;
        }
        
        try {
            log.info("CHMOD\t do chmod");
            /***/
            return Code.ESUCCESS;
        } catch (Exception e) {
            log.info("CHMOD\t failed {}",e);
            e.printStackTrace();
            return Code.EFAULT;
        }
    }
}
