/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.io;

import com.jnetfs.core.Code;
import com.jnetfs.core.relay.IResponse;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JnetFile {
    // net context

    JnetContext context = null;
    //path
    String path = null;

    /**
     * create a JnetFile
     *
     * @param context
     */
    private JnetFile(JnetContext context) throws IOException {
        this(context, "");
    }

    /**
     * return JnetContext
     *
     * @return context
     */
    public JnetContext getContext() {
        return context;
    }

    /**
     * create a JnetFile
     *
     * @param context JnetFile
     * @param path JnetContext
     * @throws IOException
     */
    public JnetFile(JnetContext context, String path) throws IOException {
        this.context = context;
        path = normalize(path);
        if ("".equals(path)) {
            path = "/";
        }
        this.path = path;
        if (path == null || context == null) {
            throw new NullPointerException();
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException();
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
    }

    /**
     * create a JnetFile
     *
     * @param parent JnetFile
     * @param path String
     * @throws IOException
     */
    public JnetFile(JnetFile parent, String path) throws IOException {
        this(parent.context, parent.getPath() + "/" + path);
    }

    /**
     * create a JnetFile
     *
     * @param context
     * @return JnetFile
     * @throws IOException
     */
    public static JnetFile getRoot(JnetContext context) throws IOException {
        return new JnetFile(context);
    }

    /**
     * List the available filesystem roots.
     *
     * <p> A particular Java platform may support zero or more
     * hierarchically-organized file systems. Each file system has a
     * <code>root</code> directory from which all other files in that file
     * system can be reached. Windows platforms, for example, have a root
     * directory for each active drive; UNIX platforms have a single root
     * directory, namely
     * <code>"/"</code>. The set of available filesystem roots is affected by
     * various system-level operations such as the insertion or ejection of
     * removable media and the disconnecting or unmounting of physical or
     * virtual disk drives.
     *
     * <p> This method returns an array of
     * <code>File</code> objects that denote the root directories of the
     * available filesystem roots. It is guaranteed that the canonical pathname
     * of any file physically present on the local machine will begin with one
     * of the roots returned by this method.
     *
     * <p> The canonical pathname of a file that resides on some other machine
     * and is accessed via a remote-filesystem protocol such as SMB or NFS may
     * or may not begin with one of the roots returned by this method. If the
     * pathname of a remote file is syntactically indistinguishable from the
     * pathname of a local file then it will begin with one of the roots
     * returned by this method. Thus, for example,
     * <code>File</code> objects denoting the root directories of the mapped
     * network drives of a Windows platform will be returned by this method,
     * while
     * <code>File</code> objects containing UNC pathnames will not be returned
     * by this method.
     *
     * <p> Unlike most methods in this class, this method does not throw
     * security exceptions. If a security manager exists and its
     * <code>{@link
     * java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     * denies read access to a particular root directory, then that directory
     * will not appear in the result.
     *
     * @return JnetFile[]
     * @throws IOException
     */
    public List<JnetFile> list() throws IOException {
        List<JnetFile> r = new ArrayList<JnetFile>();
        if (!exists()) {
            return r;
        }
        IResponse rsp = getAttribute(true);
        if (!rsp.isOK()) {
            return r;
        }
        JnetJNIConnector jniEnv = rsp.getConnector();
        int st_mode = jniEnv.getInteger("st_mode");
        if ((st_mode & Code.S_IFDIR) == 0) {
            throw new IOException("file is not a directory");
        }
        jniEnv.setString(JnetFSImpl.PATH, getFullPath(this.path));
        rsp = JnetList.instance.operate(RequestImpl.getInstance(jniEnv));
        if (!rsp.isOK()) {
            return r;
        }
        jniEnv = rsp.getConnector();
        int count = jniEnv.getInteger("file_count");
        String prefix = this.path;
        if (!this.path.endsWith("/")) {
            prefix += "/";
        }
        for (int i = 0; i < count; i++) {
            r.add(new JnetFile(context, prefix + jniEnv.getString("file_" + i)));
        }
        return r;
    }

    /**
     * return JnetJNIConnector
     *
     * @return JnetJNIConnector
     */
    IResponse getAttribute(boolean readlink) throws IOException {
        String path = this.path;
        try {
            if (readlink) {
                this.path = readLink().path;
            }
            JnetJNIConnector jniEnv = context.getJniEnv();
            jniEnv.setString(JnetFSImpl.PATH, getFullPath(this.path));
            IResponse r = JnetAttributes.instance.operate(RequestImpl.getInstance(jniEnv));
            if (r.isOK()) {
                int mode = r.getConnector().getInteger("st_mode");
                if ((mode & Code.S_IFLNK) == Code.S_IFLNK) {
                    if (readlink) {
                        jniEnv.setString(JnetFSImpl.PATH, getFullPath(this.path));
                        r = JnetReadLink.instance.operate(RequestImpl.getInstance(jniEnv));
                        if (r.isOK()) {
                            this.path = normalize(r.getConnector().getString("realPath"));
                            jniEnv.setString(JnetFSImpl.PATH, getFullPath(this.path));
                            r = JnetAttributes.instance.operate(RequestImpl.getInstance(jniEnv));
                        }
                    }
                }
            }
            return r;
        } finally {
            this.path = path;
        }
    }

    /**
     * return full path
     *
     * @return path
     */
    String getFullPath(String path) {
        return "/" + JnetEnv.getServerIP() + path;
    }

    /**
     * Tests whether the application can read the file denoted by this abstract
     * pathname.
     *
     * @return
     * <code>true</code> if and only if the file specified by this abstract
     * pathname exists <em>and</em> can be read by the application;
     * <code>false</code> otherwise
     */
    public boolean canRead() throws IOException {
        IResponse r = getAttribute(true);
        if (!r.isOK()) {
            return false;
        }
        int mode = r.getConnector().getInteger("st_mode");
        return (mode & (1 << 5)) != 0;
    }

    /**
     * Tests whether the application can modify the file denoted by this
     * abstract pathname.
     *
     * @return
     * <code>true</code> if and only if the file system actually contains a file
     * denoted by this abstract pathname <em>and</em> the application is allowed
     * to write to the file;
     * <code>false</code> otherwise.
     */
    public boolean canWrite() throws IOException {
        IResponse r = getAttribute(true);
        if (!r.isOK()) {
            return false;
        }
        int mode = r.getConnector().getInteger("st_mode");
        return (mode & (1 << 4)) != 0;
    }

    /**
     * Tests whether the file or directory denoted by this abstract pathname
     * exists.
     *
     * @return
     * <code>true</code> if and only if the file or directory denoted by this
     * abstract pathname exists;
     * <code>false</code> otherwise
     */
    public boolean exists() throws IOException {
        return getAttribute(true).isOK();
    }

    /**
     * Returns the pathname string of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     *
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name sequence
     * except for the last. If the name sequence is empty then the pathname does
     * not name a parent directory.
     */
    public JnetFile getParent() throws IOException {
        if ("/".equals(path)) {
            return this;
        }
        int idx = path.lastIndexOf("/");
        if (idx == 0 && !"/".equals(path)) {
            return JnetFile.getRoot(context);
        }
        return new JnetFile(context, path.substring(0, idx));
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a directory.
     *
     * @return
     * <code>true</code> if and only if the file denoted by this abstract
     * pathname exists <em>and</em> is a directory;
     * <code>false</code> otherwise
     */
    public boolean isDirectory() throws IOException {
        IResponse r = getAttribute(true);
        if (!r.isOK()) {
            return false;
        }
        int mode = r.getConnector().getInteger("st_mode");
        return (mode & Code.S_IFDIR) == Code.S_IFDIR;
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a directory.
     *
     * @return
     * <code>true</code> if and only if the file denoted by this abstract
     * pathname exists <em>and</em> is a directory;
     * <code>false</code> otherwise
     */
    public boolean isLink() throws IOException {
        IResponse r = getAttribute(false);
        if (!r.isOK()) {
            return false;
        }
        int mode = r.getConnector().getInteger("st_mode");
        return (mode & Code.S_IFLNK) == Code.S_IFLNK;
    }

    /**
     * return real path
     *
     * @return String
     * @throws IOException
     */
    public JnetFile readLink() throws IOException {
        String real = "/";
        String rest = this.path.substring(1);
        while (rest.length() > 0) {
            int idx = rest.indexOf('/');
            if (idx == -1) {
                break;
            }
            String p = real + rest.substring(0, idx);
            JnetFile file = new JnetFile(context, p);
            if (file.isLink()) {
                JnetJNIConnector jniEnv = context.getJniEnv();
                jniEnv.setString(JnetFSImpl.PATH, getFullPath(p));
                IResponse r = JnetReadLink.instance.operate(RequestImpl.getInstance(jniEnv));
                if (!r.isOK()) {
                    throw new IOException("error:" + r.getErrCode());
                }
                p = r.getConnector().getString("realPath");
                if (!p.startsWith("/")) {
                    real += p;
                }
                rest = normalize(real + rest.substring(idx)).substring(1);
                real = "/";
                continue;
            } else {
                real += rest.substring(0, idx + 1);
            }
            rest = rest.substring(idx + 1);
        }
        real += rest;
        return new JnetFile(context, real);
    }

    /**
     * symbol link a file
     *
     * @param file JnetFile
     * @return true/false
     * @throws IOException
     */
    public boolean linkTo(JnetFile dest) throws IOException {
        if (!dest.context.equals(context)) {
            return false;
        }
        if (exists() || !dest.exists()) {
            return false;
        }
        JnetFile p1 = readLink();
        JnetFile p2 = dest;
        JnetFile root = getRoot(context);
        String s1 = p1.getPath();
        String s2 = p2.getPath();
        while (!p1.getParent().equals(root)) {
            if (s2.startsWith("/")) {
                s2 = ".." + s2;
            } else {
                s2 = "../" + s2;
            }
            p1 = p1.getParent();
        }
        JnetJNIConnector jniEnv = context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, s2);
        jniEnv.setString(JnetFSImpl.TO, getFullPath(s1));
        return JnetSymLink.instance.operate(RequestImpl.getInstance(jniEnv)).isOK();
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a normal
     * file. A file is <em>normal</em> if it is not a directory and, in
     * addition, satisfies other system-dependent criteria. Any non-directory
     * file created by a Java application is guaranteed to be a normal file.
     *
     * @return
     * <code>true</code> if and only if the file denoted by this abstract
     * pathname exists <em>and</em> is a normal file;
     * <code>false</code> otherwise
     */
    public boolean isFile() throws IOException {
        IResponse r = getAttribute(true);
        if (!r.isOK()) {
            return false;
        }
        int mode = r.getConnector().getInteger("st_mode");
        return (mode & Code.S_IFREG) == Code.S_IFREG;
    }

    /**
     * Returns the length of the file denoted by this abstract pathname. The
     * return value is unspecified if this pathname denotes a directory.
     *
     * @return The length, in bytes, of the file denoted by this abstract
     * pathname, or
     * <code>0L</code> if the file does not exist. Some operating systems may
     * return
     * <code>0L</code> for pathnames denoting system-dependent entities such as
     * devices or pipes.
     */
    public long length() throws IOException {
        IResponse r = getAttribute(true);
        if (!r.isOK()) {
            return 0l;
        }
        return r.getConnector().getLong("st_size");
    }

    /**
     * Creates the directory named by this abstract pathname.
     *
     * @return
     * <code>true</code> if and only if the directory was created;
     * <code>false</code> otherwise
     */
    public boolean mkdir() throws IOException {
        JnetFile f = readLink();
        JnetJNIConnector jniEnv = context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, getFullPath(f.getPath()));
        return JnetMkdir.instance.operate(RequestImpl.getInstance(jniEnv)).isOK();
    }

    /**
     * Creates the directory named by this abstract pathname, including any
     * necessary but nonexistent parent directories. Note that if this operation
     * fails it may have succeeded in creating some of the necessary parent
     * directories.
     *
     * @return
     * <code>true</code> if and only if the directory was created, along with
     * all necessary parent directories;
     * <code>false</code> otherwise
     */
    public boolean mkdirs() throws IOException {
        int idx = 0;
        while (true) {
            String p = this.path;
            if (!p.endsWith("/")) {
                p += "/";
            }
            idx = p.indexOf('/', idx);
            if (idx == -1) {
                break;
            }
            p = p.substring(0, idx);
            if (p.length() > 0) {
                JnetFile f = new JnetFile(context, p);
                if (f.isFile()) {
                    return false;
                }
                if (!f.exists()) {
                    if (!f.mkdir()) {
                        return false;
                    }
                }
            }
            idx++;
        }
        return true;
    }

    /**
     * Renames the file denoted by this abstract pathname.
     *
     * <p> Many aspects of the behavior of this method are inherently
     * platform-dependent: The rename operation might not be able to move a file
     * from one filesystem to another, it might not be atomic, and it might not
     * succeed if a file with the destination abstract pathname already exists.
     * The return value should always be checked to make sure that the rename
     * operation was successful.
     *
     * @param dest The new abstract pathname for the named file
     *
     * @return
     * <code>true</code> if and only if the renaming succeeded;
     * <code>false</code> otherwise
     */
    public boolean renameTo(JnetFile dest) throws IOException {
        if (!dest.context.equals(context)) {
            return false;
        }
        String p1 = readLink().path;
        String p2 = dest.readLink().path;
        JnetJNIConnector jniEnv = context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, getFullPath(p1));
        jniEnv.setString(JnetFSImpl.TO, getFullPath(p2));
        return JnetRename.instance.operate(RequestImpl.getInstance(jniEnv)).isOK();
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname. If this
     * pathname denotes a directory, then the directory must be empty in order
     * to be deleted.
     *
     * @return
     * <code>true</code> if and only if the file or directory is successfully
     * deleted;
     * <code>false</code> otherwise
     */
    public boolean delete() throws IOException {
        JnetJNIConnector jniEnv = context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, getFullPath(readLink().path));
        return JnetDelete.instance.operate(RequestImpl.getInstance(jniEnv)).isOK();
    }

    /**
     * Atomically creates a new, empty file named by this abstract pathname if
     * and only if a file with this name does not yet exist. The check for the
     * existence of the file and the creation of the file if it does not exist
     * are a single operation that is atomic with respect to all other
     * filesystem activities that might affect the file. <P> Note: this method
     * should <i>not</i> be used for file-locking, as the resulting protocol
     * cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock} facility should be used
     * instead.
     *
     * @return
     * <code>true</code> if the named file does not exist and was successfully
     * created;
     * <code>false</code> if the named file already exists
     *
     * @throws IOException If an I/O error occurred
     *
     * @throws SecurityException If a security manager exists and its
     * <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     * denies write access to the file
     *
     * @since 1.2
     */
    public boolean createNewFile() throws IOException {
        JnetJNIConnector jniEnv = context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, getFullPath(readLink().path));
        return JnetCreate.instance.operate(RequestImpl.getInstance(jniEnv)).isOK();
    }

    /**
     * Requests that the file or directory denoted by this abstract pathname be
     * deleted when the virtual machine terminates. Files (or directories) are
     * deleted in the reverse order that they are registered. Invoking this
     * method to delete a file or directory that is already registered for
     * deletion has no effect. Deletion will be attempted only for normal
     * termination of the virtual machine, as defined by the Java Language
     * Specification.
     *
     * <p> Once deletion has been requested, it is not possible to cancel the
     * request. This method should therefore be used with care.
     *
     * <P> Note: this method should <i>not</i> be used for file-locking, as the
     * resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock} facility should be used
     * instead.
     *
     * @throws SecurityException If a security manager exists and its
     * <code>{@link
     *          java.lang.SecurityManager#checkDelete}</code> method denies delete access
     * to the file
     *
     * @see #delete
     *
     * @since 1.2
     */
    public void deleteOnExit() {
        if (!context.removehook.contains(this)) {
            context.removehook.add(this);
        }
    }

    /**
     * Converts this abstract pathname into a pathname string. The resulting
     * string uses the {@link #separator default name-separator character} to
     * separate the names in the name sequence.
     *
     * @return The string form of this abstract pathname
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the name of the file or directory denoted by this abstract
     * pathname. This is just the last name in the pathname's name sequence. If
     * the pathname's name sequence is empty, then the empty string is returned.
     *
     * @return The name of the file or directory denoted by this abstract
     * pathname, or the empty string if this pathname's name sequence is empty
     */
    public String getName() {
        int idx = path.lastIndexOf("/");
        if (idx == 0) {
            return path;
        }
        return path.substring(idx + 1);
    }

    /**
     * path normalize
     *
     * @param path String
     * @return String
     * @throws IOException
     */
    private String normalize(String path) throws IOException {
        List<String> paths = new ArrayList<String>();
        while (true) {
            int idx = path.indexOf('/');
            if (idx == -1) {
                if (path.length() > 0) {
                    paths.add(path);
                }
                break;
            }
            String p = path.substring(0, idx);
            path = path.substring(idx + 1);
            if ("..".equals(p)) {
                if (paths.size() > 0) {
                    paths.remove(paths.size() - 1);
                }
            } else if (!".".equals(p) && !"".equals(p)) {
                paths.add(p);
            }
        }
        path = "";
        for (String p : paths) {
            path += "/" + p;
        }
        return path;
    }

    /**
     * Tests this abstract pathname for equality with the given object. Returns
     * <code>true</code> if and only if the argument is not
     * <code>null</code> and is an abstract pathname that denotes the same file
     * or directory as this abstract pathname. Whether or not two abstract
     * pathnames are equal depends upon the underlying system. On UNIX systems,
     * alphabetic case is significant in comparing pathnames; on Microsoft
     * Windows systems it is not.
     *
     * @param obj The object to be compared with this abstract pathname
     *
     * @return
     * <code>true</code> if and only if the objects are the same;
     * <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JnetFile) {
            return this.path.equals(((JnetFile) obj).path);
        }
        return false;
    }

    /**
     * Computes a hash code for this abstract pathname. Because equality of
     * abstract pathnames is inherently system-dependent, so is the computation
     * of their hash codes. On UNIX systems, the hash code of an abstract
     * pathname is equal to the exclusive <em>or</em> of the hash code of its
     * pathname string and the decimal value
     * <code>1234321</code>. On Microsoft Windows systems, the hash code is
     * equal to the exclusive <em>or</em> of the hash code of its pathname
     * string converted to lower case and the decimal value
     * <code>1234321</code>. Locale is not taken into account on lowercasing the
     * pathname string.
     *
     * @return A hash code for this abstract pathname
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that "textually represents"
     * this object. The result should be a concise but informative
     * representation that is easy for a person to read. It is recommended that
     * all subclasses override this method. <p> The
     * <code>toString</code> method for class
     * <code>Object</code> returns a string consisting of the name of the class
     * of which the object is an instance, the at-sign character `
     * <code>@</code>', and the unsigned hexadecimal representation of the hash
     * code of the object. In other words, this method returns a string equal to
     * the value of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return getPath();
    }
}
