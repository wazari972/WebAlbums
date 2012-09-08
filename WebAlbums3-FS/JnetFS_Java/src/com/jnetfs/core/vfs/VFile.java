/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.vfs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jnetfs.core.Code;

public class VFile implements Serializable {

    private static final long serialVersionUID = -160198696276463321L;
    // Dir/File const  see linux/stat.h
    protected static final int S_IFDIR = Code.S_IFDIR;
    protected static final int S_IFREG = Code.S_IFREG;
    //file size;
    protected int size = 0;
    //disk file
    protected File file = null;
    //root directory
    private static VFile root;
    //file links
    private List<VFile> hardLink = new LinkedList<VFile>();
    private Map<String, VFile> softLink = new HashMap<String, VFile>();
    //load time
    private long loadTime = System.currentTimeMillis();
    //last update file
    private long lastUpdate = loadTime;
    //access rights
    private int mode = 0444;
    //name of file
    private String name;
    //parent file
    private VFile parent = null;
    //load file
    private boolean loaded = false;

    static {
        try {
            root = new VFile("/");
        } catch (IOException ex) {
        }
    }

    /**
     * default constructor
     *
     * @param name file name
     */
    private VFile(String name) throws IOException {
        this(null, name);
    }

    /**
     * default constructor
     *
     * @param parent parent file
     * @param name file name
     */
    public VFile(VFile parent, String name) throws IOException {
        this(parent, name, true);
    }

    /**
     * default constructor
     *
     * @param parent parent file
     * @param name name
     * @param directory dir/file
     */
    protected VFile(VFile parent, String name, boolean directory)
            throws IOException {
        if (!"/".equals(name)) {
            if (parent == null || parent instanceof VStream) {
                throw new IllegalArgumentException();
            }
        }
        this.name = name;
        if (directory) {
            this.mode |= S_IFDIR;
        } else {
            this.mode |= S_IFREG;
        }
        if (parent != null) {
            parent.add(this);
        }
    }

    /**
     * return parent file
     *
     * @return VFile
     */
    public synchronized VFile getParent() {
        if (this == root) {
            return root;
        }
        return parent;
    }

    /**
     * add a vfile to current node
     *
     * @param file vfile
     * @throws IOException
     */
    public synchronized void add(VFile file) throws IOException {
        add(file.name, file);
    }

    /**
     * add a vfile to current node
     *
     * @param file vfile
     */
    public synchronized void add(String alias, VFile file) throws IOException {
        if (file == root) {
            throw new IOException();
        }
        if (isFile()) {
            throw new IOException();
        }
        file.addNotify();
        if (!isLoaded()) {
            lazyLoad();
        }
        if (!hardLink.contains(file)
                && !softLink.keySet().contains(alias)) {
            if (file.parent == null) {
                file.parent = this;
                hardLink.add(file);
            } else {
                softLink.put(alias, file);
            }
            touch();
        }
    }

    /**
     * remove file from current node
     *
     * @param file vfile
     * @throws IOException
     */
    public synchronized void remove(VFile file) throws IOException {
        remove(file.name, file);
    }

    /**
     * remove all sub-file
     *
     * @throws IOException
     */
    public synchronized void removeAll() throws IOException {
        while (hardLink.size() > 0) {
            remove(hardLink.remove(0));
        }
        while (softLink.size() > 0) {
            remove(softLink.remove(0));
        }
    }

    /**
     * remove notify
     *
     * @throws IOException
     */
    protected void removeNotify() throws IOException {
    }

    /**
     * add notify
     *
     * @throws IOException
     */
    protected void addNotify() throws IOException {
    }

    /**
     * remove a file from node
     *
     * @param alias alias
     * @param file Vfile
     * @throws IOException
     */
    public synchronized void remove(String alias, VFile file) throws IOException {
        if (file == root) {
            throw new IOException();
        }
        if (isFile()) {
            throw new IOException();
        }
        removeNotify();
        if (hardLink.contains(file)) {
            hardLink.remove(file);
            file.parent = null;
        } else if (softLink.containsKey(alias)) {
            softLink.remove(alias);
        }
        touch();
    }

    /**
     * get absolute path
     *
     * @return path
     */
    public synchronized String getPath() {
        if (root == this) {
            return "/";
        }
        String r = name + (isDirectory() ? "/" : "");
        if (parent != null) {
            r = parent.getPath() + r;
        }
        return r;
    }

    /**
     * check is updated or not since last load
     *
     * @return true/false
     */
    public synchronized boolean isUpdated() {
        return lastUpdate != loadTime;
    }

    /**
     * return Vfile tree root
     *
     * @return Vfile
     */
    public static VFile getRoot() {
        return root;
    }

    /**
     * find file by absolute path
     *
     * @param path absolute path
     * @return Vfile
     */
    public static VFile find(String path) throws IOException {
        VFile vf = find(getRoot(), getRoot().getPath(), path);
        if (vf == null) {
            vf = find(getRoot(), getRoot().getPath(), path + "/");
        }
        if (vf == null) {
            throw new IOException();
        }
        return vf;
    }

    /**
     * find sub files
     *
     * @param file vfile
     * @param subPath sub path
     * @return VFile
     */
    public static VFile find(VFile file, String subPath) throws IOException {
        VFile vf = find(file, file.getPath(), file.getPath() + subPath);
        if (vf == null) {
            vf = find(file, file.getPath(), file.getPath() + subPath + "/");
        }
        if (vf == null) {
            throw new IOException();
        }
        return vf;
    }

    /**
     * find file by absolute path
     *
     * @param vf VFile
     * @param path absolute path
     * @return Vfile
     */
    private static VFile find(VFile vf, String linkpath, String path) throws IOException {
        //load object
        if (vf.isDirectory() && !vf.isLoaded()) {
            vf.lazyLoad();
        }
        String vfp = linkpath;
        if (path.equals(vfp)) {
            return vf;
        }
        if (path.startsWith(vfp)) {
            //seek hard links
            for (VFile v : vf.hardLink) {
                String lp = linkpath + v.getName() + (v.isDirectory() ? "/" : "");
                VFile r = find(v, lp, path);
                if (r != null) {
                    return r;
                }
            }
            //seek soft links
            for (String s : vf.softLink.keySet()) {
                VFile v = vf.softLink.get(s);
                String lp = linkpath + s + (v.isDirectory() ? "/" : "");
                VFile r = find(v, lp, path);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * load tree to vfile root
     *
     * @param froot start points
     */
    public static void loadFS(File froot) throws IOException {
        getRoot().load(froot, getRoot());
    }

    /**
     * load vfile from file system
     *
     * @param file root path
     * @param vfile vfile
     */
    public synchronized void load(File file, VFile vfile) throws IOException {
        vfile.file = file;
        vfile.reset();
        if (!vfile.isLoaded()) {
            vfile.lazyLoad();
        }
    }

    /**
     * free memory
     *
     * @throws IOException IOException
     */
    public synchronized void reset() throws IOException {
        for (VFile v : hardLink) {
            if (!v.isUpdated()) {
                hardLink.remove(v);
            }
        }
        this.softLink.clear();
        this.loaded = false;
    }

    /**
     * lazy load object
     *
     * @throws IOException IOException
     */
    protected synchronized void lazyLoad() throws IOException {
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    VFile vf = null;
                    if (f.isDirectory()) {
                        vf = new VFile(this, f.getName());
                    } else {
                        vf = newVStream(this, f);
                    }
                    vf.file = f;
                    vf.size = (int) f.length();
                    vf.loadTime = vf.lastUpdate = f.lastModified();
                }
            }
        }
    }

    /**
     * set loaded flag
     *
     * @param loaded loaded
     */
    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * return the status of load
     *
     * @return true/false
     */
    protected boolean isLoaded() {
        return this.loaded;
    }

    /**
     * create a new vfile
     *
     * @param parent VFile
     * @param file file
     * @return MemFile
     * @throws IOException IOException
     */
    protected synchronized VStream newVStream(VFile parent, File file) throws IOException {
        return new MStream(parent, file.getName());
    }

    /**
     * Save to real file
     *
     * @param real file
     * @throws IOException IOException
     */
    protected synchronized void saveOut(File real) throws IOException {
        real.mkdirs();
    }

    /**
     * save to file system
     *
     * @throws IOException IOException
     */
    public synchronized void save() throws IOException {
        if (isUpdated()) {
            save(file);
        }
    }

    /**
     * save to real file system
     *
     * @param froot File
     * @throws IOException IOException
     */
    public synchronized void save(File froot) throws IOException {
        if (isUpdated()) {
            saveOut(froot);
        }
        for (VFile f : hardLink) {
            if (f.isUpdated()) {
                File real = new File(froot, f.getPath());
                if (isDirectory()) {
                    saveOut(new File(froot, getPath()));
                    f.save(real);
                }
                f.saveOut(real);
            }
        }
    }

    /**
     * get name of vfile
     *
     * @return name
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * get access mode
     *
     * @return mode
     */
    public synchronized int getMode() {
        return mode;
    }

    /**
     * set mode
     *
     * @param mode mode
     */
    public synchronized void setMode(int mode) {
        this.mode |= mode;
    }

    /**
     * return last update million-second
     *
     * @return million-second
     */
    public synchronized long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * check is a file
     *
     * @return true/false
     */
    public synchronized boolean isFile() {
        return (mode & S_IFREG) != 0;
    }

    /**
     * check is a directory
     *
     * @return true/false
     */
    public synchronized boolean isDirectory() {
        return (mode & S_IFDIR) != 0;
    }

    /**
     * check the update time
     */
    public synchronized void touch() {
        VFile p = this;
        while (p != null) {
            p.lastUpdate = System.currentTimeMillis();
            if (!p.isUpdated()) {
                p.lastUpdate++;
            }
            p = p.parent;
        }
    }

    /**
     * return this size of vfile
     *
     * @return size
     */
    public synchronized int size() throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        return this.size;
    }

    /**
     * set the length of vfile
     *
     * @param len length
     * @throws IOException IOException
     */
    public synchronized void size(int len) throws IOException {
        size = len;
    }

    /**
     * return sub-files
     *
     * @return sub-files
     */
    public synchronized List<VFile> listFiles() throws IOException {
        List<VFile> r = new LinkedList<VFile>();
        //load object
        if (!isLoaded()) {
            lazyLoad();
        }
        r.addAll(this.hardLink);
        return r;
    }

    /**
     * return sub-files
     *
     * @return sub-files
     */
    public synchronized Map<String, VFile> listFiles(boolean withlink) throws IOException {
        if (isFile()) {
            return null;
        }
        Map<String, VFile> fs = new HashMap<String, VFile>();
        //load object
        if (!isLoaded()) {
            lazyLoad();
        }
        if (withlink) {
            fs.putAll(softLink);
        }
        for (VFile f : hardLink) {
            fs.put(f.name, f);
        }
        return fs;
    }

    /**
     * Indicates whether some other object is "equal to" this one. <p> The
     * <code>equals</code> method implements an equivalence relation on non-null
     * object references: <ul> <li>It is <i>reflexive</i>: for any non-null
     * reference value
     * <code>x</code>,
     * <code>x.equals(x)</code> should return
     * <code>true</code>. <li>It is <i>symmetric</i>: for any non-null reference
     * values
     * <code>x</code> and
     * <code>y</code>,
     * <code>x.equals(y)</code> should return
     * <code>true</code> if and only if
     * <code>y.equals(x)</code> returns
     * <code>true</code>. <li>It is <i>transitive</i>: for any non-null
     * reference values
     * <code>x</code>,
     * <code>y</code>, and
     * <code>z</code>, if
     * <code>x.equals(y)</code> returns
     * <code>true</code> and
     * <code>y.equals(z)</code> returns
     * <code>true</code>, then
     * <code>x.equals(z)</code> should return
     * <code>true</code>. <li>It is <i>consistent</i>: for any non-null
     * reference values
     * <code>x</code> and
     * <code>y</code>, multiple invocations of <tt>x.equals(y)</tt> consistently
     * return
     * <code>true</code> or consistently return
     * <code>false</code>, provided no information used in
     * <code>equals</code> comparisons on the objects is modified. <li>For any
     * non-null reference value
     * <code>x</code>,
     * <code>x.equals(null)</code> should return
     * <code>false</code>. </ul> <p> The <tt>equals</tt> method for class
     * <code>Object</code> implements the most discriminating possible
     * equivalence relation on objects; that is, for any non-null reference
     * values
     * <code>x</code> and
     * <code>y</code>, this method returns
     * <code>true</code> if and only if
     * <code>x</code> and
     * <code>y</code> refer to the same object
   * (
     * <code>x == y</code> has the value
     * <code>true</code>). <p> Note that it is generally necessary to override
     * the <tt>hashCode</tt> method whenever this method is overridden, so as to
     * maintain the general contract for the <tt>hashCode</tt> method, which
     * states that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return
     * <code>true</code> if this object is the same as the obj argument;
     * <code>false</code> otherwise.
     * @see #hashCode()
     * @see java.util.Hashtable
     */
    public boolean equals(Object obj) {
        boolean r = obj instanceof VFile;
        if (r) {
            r = name.equals(((VFile) obj).name);
        }
        return r;
    }

    /**
     * Returns a hash code value for the object. This method is supported for
     * the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>. <p> The general contract of
     * <code>hashCode</code> is: <ul> <li>Whenever it is invoked on the same
     * object more than once during an execution of a Java application, the
     * <tt>hashCode</tt> method must consistently return the same integer,
     * provided no information used in <tt>equals</tt> comparisons on the object
     * is modified. This integer need not remain consistent from one execution
     * of an application to another execution of the same application. <li>If
     * two objects are equal according to the <tt>equals(Object)</tt> method,
     * then calling the
     * <code>hashCode</code> method on each of the two objects must produce the
     * same integer result. <li>It is <em>not</em> required that if two objects
     * are unequal according to the {@link java.lang.Object#equals(java.lang.Object)}
     * method, then calling the <tt>hashCode</tt> method on each of the two
     * objects must produce distinct integer results. However, the programmer
     * should be aware that producing distinct integer results for unequal
     * objects may improve the performance of hashtables. </ul> <p> As much as
     * is reasonably practical, the hashCode method defined by class
     * <tt>Object</tt> does return distinct integers for distinct objects. (This
     * is typically implemented by converting the internal address of the object
     * into an integer, but this implementation technique is not required by the
     * Java<font size="-2"><sup>TM</sup></font> programming language.)
     *
     * @return a hash code value for this object.
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.util.Hashtable
     */
    public int hashCode() {
        return super.hashCode() + name.hashCode();
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
    public synchronized String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPath());
        if (isDirectory()) {
            for (VFile f : hardLink) {
                buffer.append("\n").append(f.toString(f, f.name, false));
            }
            for (String s : softLink.keySet()) {
                VFile f = softLink.get(s);
                buffer.append("\n").append(f.toString(f, s, true));
            }
        } else {
            buffer.append('\n').append(toString(this, name, false));
        }
        return buffer.toString();
    }

    /**
     * print detail information
     *
     * @param vfile vfile
     * @param alias string
     * @param link true/false
     * @return string
     */
    private synchronized String toString(VFile vfile, String alias, boolean link) {
        StringBuffer buffer = new StringBuffer();
        if (link) {
            buffer.append('l');
        } else {
            buffer.append(isDirectory() ? "d" : "-");
        }
        buffer.append("rwxrw-r--");
        String len = String.valueOf(hardLink.size() + softLink.size() + 1);
        while (len.length() < 5) {
            len = " " + len;
        }
        buffer.append(len);
        try {
            len = String.valueOf(vfile.size());
        } catch (Exception ex) {
            len = "0";
        }
        while (len.length() < 10) {
            len = " " + len;
        }
        Date date = new Date();
        date.setTime(lastUpdate);
        buffer.append(len).append(" ");
        buffer.append(date).append(" ");
        if (link) {
            buffer.append(alias).append("->").append(vfile.getPath());
        } else {
            buffer.append(getName());
        }
        return buffer.toString();
    }

    /**
     * print tree
     *
     * @return string
     */
    public synchronized String tree() {
        try {
            if (!isLoaded()) {
                lazyLoad();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        StringBuffer buff = new StringBuffer(toString());
        for (VFile f : hardLink) {
            if (f.isDirectory()) {
                buff.append('\n').append(f.tree());
            }
        }
        return buff.toString();
    }
}
