//Use Fuse 2.6 v
#include "JnetFS.h"
#include <errno.h>
#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <jni.h>
#include <fuse/fuse.h>
#include <fuse/fuse_common.h>

////////////////////////////////////////////////////////////////////////////////
// Java VM cache
////////////////////////////////////////////////////////////////////////////////
/* Defined by native libraries. */
JavaVM *jvm;
jobject jnetfs;
jobject jniConnector; //pass data between java & C
jboolean jdebug = JNI_FALSE;
const char *CONNECTOR_CLASS = "com/jnetfs/core/relay/JnetJNIConnector";
const char *PATH = "OS_PATH";
const char *PATHTO = "OS_PATH_TO";

void inline DEBUG(char *format, ...) {
    if (jdebug) {
        char buffer[1024];
        va_list args;
        va_start(args, format);
        vsprintf(buffer, format, args);
        va_end(args);
        fprintf(stdout, "JNI :%s\n", buffer);
    }
}

/**
 * create connector object
 */
jobject getConnector(JNIEnv *env) {
    return jniConnector;
}

JNIEnv * getEnv() {
    JNIEnv *env;
    (*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL);
    (*env)->PushLocalFrame(env, 20);
    return env;
}

void ReleaseEnv(JNIEnv * env) {
    //clear object
    jclass class = (*env)->FindClass(env, CONNECTOR_CLASS);
    jmethodID method = (*env)->GetMethodID(env, class, "reset", "()V");
    (*env)->CallVoidMethod(env, jniConnector, method);
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionClear(env);
    }
    (*env)->DeleteLocalRef(env, class);
    //clear variables
    (*env)->PopLocalFrame(env, NULL);
    (*jvm)->DetachCurrentThread(jvm);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    jvm = vm;
    return JNETFS_JVM_VERSION;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env = getEnv();
    (*env)->DeleteWeakGlobalRef(env, jnetfs);
    (*env)->DeleteWeakGlobalRef(env, jniConnector);
    jnetfs = NULL;
    jniConnector = NULL;
    jvm = NULL;
}
////////////////////////////////////////////////////////////////////////////////
// Utility functions
////////////////////////////////////////////////////////////////////////////////

/**
 * call Java function
 */
jint callJava(JNIEnv *env, const char *method, jobject connector) {
    DEBUG("CALL JAVA: %s", method);
    //get class
    jclass clazz = (*env)->GetObjectClass(env, jnetfs);
    if (clazz == NULL) return -EACCES;
    //get method
    jmethodID jmethod = (*env)->GetMethodID(env, clazz, method,
            "(Lcom/jnetfs/core/relay/JnetJNIConnector;)I");
    if (jmethod == NULL) return -EACCES;
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        (*env)->DeleteLocalRef(env, clazz);
        return -EIO;
    }
    //call java method
    jint r = (*env)->CallIntMethod(env, jnetfs, jmethod, connector);
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        (*env)->DeleteLocalRef(env, clazz);
        return -EIO;
    }
    (*env)->DeleteLocalRef(env, clazz);
    DEBUG("JAVA RESULT: %d", r);
    return r;
}

jboolean _put(JNIEnv *env, jobject connector, const char *key, jobject val) {
    jboolean r = JNI_TRUE;
    jclass class = (*env)->FindClass(env, CONNECTOR_CLASS);
    jmethodID method = (*env)->GetMethodID(env, class, "setObject",
            "(Ljava/lang/String;Ljava/lang/Object;)V");
    jstring jstr = (*env)->NewStringUTF(env, key);
    if (jstr != NULL) {
        (*env)->CallObjectMethod(env, connector, method, jstr, val);
        (*env)->DeleteLocalRef(env, jstr);
        (*env)->DeleteLocalRef(env, val);
    } else {
        r = JNI_FALSE;
    }
    (*env)->DeleteLocalRef(env, class);
    return r;
}

jboolean putInt(JNIEnv *env, jobject connector, const char *key, jint val) {
    jboolean r = JNI_TRUE;
    jclass class = (*env)->FindClass(env, "java/lang/Integer");
    jmethodID method = (*env)->GetMethodID(env, class, "<init>", "(I)V");
    jobject obj = (*env)->NewObject(env, class, method, val);
    if (obj != NULL) {
        r = _put(env, connector, key, obj);
    }
    (*env)->DeleteLocalRef(env, class);
    return r;
}

jboolean putLong(JNIEnv *env, jobject connector, const char *key, jlong val) {
    jboolean r = JNI_FALSE;
    jclass class = (*env)->FindClass(env, "java/lang/Long");
    jmethodID method = (*env)->GetMethodID(env, class, "<init>", "(J)V");
    jobject obj = (*env)->NewObject(env, class, method, val);
    if (obj != NULL) {
        r = _put(env, connector, key, obj);
    }
    (*env)->DeleteLocalRef(env, class);
    return r;
}

jboolean putChars(JNIEnv *env, jobject connector, const char *key, const char *val, size_t size) {
    jboolean r = JNI_FALSE;
    int len = (int) size;
    jbyteArray bytes = (*env)->NewByteArray(env, len);
    if (bytes != NULL) {
        (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *) val);
        r = _put(env, connector, key, bytes);
    }
    return r;
}

jboolean putString(JNIEnv *env, jobject connector, const char *key, const char *val) {
    jboolean r = JNI_FALSE;
    jstring jstr = (*env)->NewStringUTF(env, val);
    if (jstr != NULL) {
        r = _put(env, connector, key, jstr);
    }
    return r;
}

jobject _get(JNIEnv *env, jobject connector, const char *key) {
    jclass class = (*env)->FindClass(env, CONNECTOR_CLASS);
    jmethodID method = (*env)->GetMethodID(env, class, "getObject",
            "(Ljava/lang/String;)Ljava/lang/Object;");
    jstring jstr = (*env)->NewStringUTF(env, key);
    jobject r = (*env)->CallObjectMethod(env, connector, method, jstr);
    (*env)->DeleteLocalRef(env, class);
    return r;
}

jint getInt(JNIEnv *env, jobject connector, const char *key) {
    jclass class = (*env)->FindClass(env, "java/lang/Integer");
    jmethodID method = (*env)->GetMethodID(env, class, "intValue", "()I");
    jobject obj = _get(env, connector, key);
    if (obj != NULL) {
        jint r = (*env)->CallIntMethod(env, obj, method);
        (*env)->DeleteLocalRef(env, class);
        return r;
    }
    return 0;
}

jlong getLong(JNIEnv *env, jobject connector, const char *key) {
    jclass class = (*env)->FindClass(env, "java/lang/Long");
    jmethodID method = (*env)->GetMethodID(env, class, "longValue", "()J");
    jobject obj = _get(env, connector, key);
    if (obj != NULL) {
        jlong r = (*env)->CallLongMethod(env, obj, method);
        (*env)->DeleteLocalRef(env, class);
        return r;
    }
    return 0;
}

void fill(JNIEnv *env, const jobject connector, const char *key, char *buf, size_t size) {
    jarray ja = _get(env, connector, key);
    if (ja == NULL) return;
    jint len = (*env)->GetArrayLength(env, ja);
    (*env)->GetByteArrayRegion(env, ja, 0, len, (jbyte *) buf);
    (*env)->DeleteLocalRef(env, ja);
}
////////////////////////////////////////////////////////////////////////////////
// Fuse implement
////////////////////////////////////////////////////////////////////////////////

/**
 * Initialize filesystem
 *
 * The return value will passed in the private_data field of
 * fuse_context to all file operations and as a parameter to the
 * destroy() method.
 *
 * Introduced in version 2.3
 * Changed in version 2.6
 */
void * jnetfs_init(struct fuse_conn_info *conn) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putInt(env, connector, "async_read", (*conn).async_read);
    int r = callJava(env, "init", connector);
    if (r == 0) {
        conn->async_read = getInt(env, connector, "async_read");
    }
    ReleaseEnv(env);
    return NULL;
}

/**
 * Clean up filesystem
 *
 * Called on filesystem exit.
 *
 * Introduced in version 2.3
 */
void jnetfs_destroy(void * data) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    callJava(env, "destroy", connector);
    ReleaseEnv(env);
}

/** Get file attributes.
 *
 * Similar to stat().  The 'st_dev' and 'st_blksize' fields are
 * ignored.	 The 'st_ino' field is ignored except if the 'use_ino'
 * mount option is given.
 */
int jnetfs_getattr(const char *path, struct stat *stbuf) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    memset(stbuf, 0, sizeof (struct stat));
    int r = callJava(env, "attributes", connector);
    if (r == 0) {
        struct fuse_context *ctx = fuse_get_context();
        stbuf->st_mode = (__mode_t) getInt(env, connector, "st_mode");
        stbuf->st_size = getLong(env, connector, "st_size");
        stbuf->st_nlink = getLong(env, connector, "st_nlink");
        stbuf->st_gid = ctx->gid;
        stbuf->st_uid = ctx->uid;
        struct timespec tspce;
        tspce.tv_sec = (time_t) getLong(env, connector, "st_mtim");
        tspce.tv_nsec = 0;
        stbuf->st_atim = tspce;
        stbuf->st_mtim = stbuf->st_atim;
        stbuf->st_ctim = stbuf->st_atim;
    }
    ReleaseEnv(env);
    return r;
}

/** Read directory
 *
 * This supersedes the old getdir() interface.  New applications
 * should use this.
 *
 * The filesystem may choose between two modes of operation:
 *
 * 1) The readdir implementation ignores the offset parameter, and
 * passes zero to the filler function's offset.  The filler
 * function will not return '1' (unless an error happens), so the
 * whole directory is read in a single readdir operation.  This
 * works just like the old getdir() method.
 *
 * 2) The readdir implementation keeps track of the offsets of the
 * directory entries.  It uses the offset parameter and always
 * passes non-zero offset to the filler function.  When the buffer
 * is full (or an error happens) the filler function will return
 * '1'.
 *
 * Introduced in version 2.3
 */
int jnetfs_readdir(const char *path, void *buf, fuse_fill_dir_t filler,
        off_t offset, struct fuse_file_info *fi) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    filler(buf, ".", NULL, 0);
    filler(buf, "..", NULL, 0);
    int r = callJava(env, "list", connector);
    if (r == 0) {
        jint count = getInt(env, connector, "file_count");
        int i;
        char key[32];
        for (i = 0; i < count; i++) {
            memset(key, 0, sizeof (key));
            sprintf(key, "file_%i", i);
            jstring jname = _get(env, connector, key);
            if (jname != NULL) {
                const char *name = (*env)->GetStringUTFChars(env, jname, JNI_FALSE);
                if (name != NULL) {
                    filler(buf, name, NULL, 0);
                    (*env)->ReleaseStringUTFChars(env, jname, name);
                }
                (*env)->DeleteLocalRef(env, jname);
            }
        }
    }
    ReleaseEnv(env);
    return r;
}

/** File open operation
 *
 * No creation, or truncation flags (O_CREAT, O_EXCL, O_TRUNC)
 * will be passed to open().  Open should check if the operation
 * is permitted for the given flags.  Optionally open may also
 * return an arbitrary filehandle in the fuse_file_info structure,
 * which will be passed to all file operations.
 *
 * Changed in version 2.2
 */
int jnetfs_open(const char *path, struct fuse_file_info *fi) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, ".flags", fi->flags);
    putLong(env, connector, ".writepage", fi->writepage);
    putLong(env, connector, ".direct_io", fi->direct_io);
    putLong(env, connector, ".keep_cache", fi->keep_cache);
    putLong(env, connector, ".flush", fi->flush);
    putLong(env, connector, ".fh", fi->fh);
    putLong(env, connector, ".lock_owner", fi->lock_owner);
    int r = callJava(env, "open", connector);
    if (r == 0) {
        fi->fh = (uint64_t) getLong(env, connector, ".fh");
        fi->lock_owner = getuid();
        fi->direct_io = getLong(env, connector, ".direct_io");
        fi->keep_cache = getLong(env, connector, ".keep_cache");
    }
    ReleaseEnv(env);
    return r;
}

/** Read data from an open file
 *
 * Read should return exactly the number of bytes requested except
 * on EOF or error, otherwise the rest of the data will be
 * substituted with zeroes.	 An exception to this is when the
 * 'direct_io' mount option is specified, in which case the return
 * value of the read system call will reflect the return value of
 * this operation.
 *
 * Changed in version 2.2
 */
int jnetfs_read(const char *path, char *buf, size_t size, off_t offset,
        struct fuse_file_info *fi) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, ".flags", fi->flags);
    putLong(env, connector, ".writepage", fi->writepage);
    putLong(env, connector, ".direct_io", fi->direct_io);
    putLong(env, connector, ".keep_cache", fi->keep_cache);
    putLong(env, connector, ".flush", fi->flush);
    putLong(env, connector, ".fh", fi->fh);
    putLong(env, connector, ".lock_owner", fi->lock_owner);
    putLong(env, connector, "size", size);
    putLong(env, connector, "offset", offset);
    int r = callJava(env, "read", connector);
    memset(buf, size, 0);
    //write back data
    if (r > 0) {
        fill(env, connector, "buffer", buf, size);
    }
    ReleaseEnv(env);
    return r;
}

/** Write data to an open file
 *
 * Write should return exactly the number of bytes requested
 * except on error.	 An exception to this is when the 'direct_io'
 * mount option is specified (see read operation).
 *
 * Changed in version 2.2
 */
int jnetfs_write(const char *path, const char *buf, size_t size, off_t offset,
        struct fuse_file_info *fi) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, ".flags", fi->flags);
    putLong(env, connector, ".writepage", fi->writepage);
    putLong(env, connector, ".direct_io", fi->direct_io);
    putLong(env, connector, ".keep_cache", fi->keep_cache);
    putLong(env, connector, ".flush", fi->flush);
    putLong(env, connector, ".fh", fi->fh);
    putLong(env, connector, ".lock_owner", fi->lock_owner);
    putLong(env, connector, "size", size);
    putLong(env, connector, "offset", offset);
    putChars(env, connector, "buffer", buf, size);
    int r = callJava(env, "write", connector);
    ReleaseEnv(env);
    return r;
}

/** Release an open file
 *
 * Release is called when there are no more references to an open
 * file: all file descriptors are closed and all memory mappings
 * are unmapped.
 *
 * For every open() call there will be exactly one release() call
 * with the same flags and file descriptor.	 It is possible to
 * have a file opened more than once, in which case only the last
 * release will mean, that no more reads/writes will happen on the
 * file.  The return value of release is ignored.
 *
 * Changed in version 2.2
 */
int jnetfs_release(const char *path, struct fuse_file_info *fi) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, ".flags", fi->flags);
    putLong(env, connector, ".writepage", fi->writepage);
    putLong(env, connector, ".direct_io", fi->direct_io);
    putLong(env, connector, ".keep_cache", fi->keep_cache);
    putLong(env, connector, ".flush", fi->flush);
    putLong(env, connector, ".fh", fi->fh);
    putLong(env, connector, ".lock_owner", fi->lock_owner);
    int r = callJava(env, "release", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_truncate(const char *path, off_t offset) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, "offset", offset);
    int r = callJava(env, "truncate", connector);
    ReleaseEnv(env);
    return r;
}

/** Possibly flush cached data
 *
 * BIG NOTE: This is not equivalent to fsync().  It's not a
 * request to sync dirty data.
 *
 * Flush is called on each close() of a file descriptor.  So if a
 * filesystem wants to return write errors in close() and the file
 * has cached dirty data, this is a good place to write back data
 * and return any errors.  Since many applications ignore close()
 * errors this is not always useful.
 *
 * NOTE: The flush() method may be called more than once for each
 * open().	This happens if more than one file descriptor refers
 * to an opened file due to dup(), dup2() or fork() calls.	It is
 * not possible to determine if a flush is final, so each flush
 * should be treated equally.  Multiple write-flush sequences are
 * relatively rare, so this shouldn't be a problem.
 *
 * Filesystems shouldn't assume that flush will always be called
 * after some writes, or that if will be called at all.
 *
 * Changed in version 2.2
 */
int jnetfs_flush(const char *path, struct fuse_file_info *fi) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, ".flags", fi->flags);
    putLong(env, connector, ".writepage", fi->writepage);
    putLong(env, connector, ".direct_io", fi->direct_io);
    putLong(env, connector, ".keep_cache", fi->keep_cache);
    putLong(env, connector, ".flush", fi->flush);
    putLong(env, connector, ".fh", fi->fh);
    putLong(env, connector, ".lock_owner", fi->lock_owner);
    int r = callJava(env, "flush", connector);
    ReleaseEnv(env);
    return r;
}

/** Create a file node
 *
 * This is called for creation of all non-directory, non-symlink
 * nodes.  If the filesystem defines a create() method, then for
 * regular files that will be called instead.
 */
int jnetfs_mknod(const char *path, mode_t mode, dev_t dt) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, "mode", mode);
    int r = callJava(env, "create", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_mkdir(const char *path, mode_t mode) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, "mode", mode);
    int r = callJava(env, "mkdir", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_unlink(const char *path) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    int r = callJava(env, "delete", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_rmdir(const char *path) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    int r = callJava(env, "rmdir", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_rename(const char *path, const char *to) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putString(env, connector, PATHTO, to);
    int r = callJava(env, "rename", connector);
    ReleaseEnv(env);
    return r;
}

/**
 * Change the access and modification times of a file with
 * nanosecond resolution
 *
 * Introduced in version 2.6
 */
int jnetfs_utimens(const char *path, const struct timespec tv[2]) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putLong(env, connector, "timespec_0.tv_sec", tv[0].tv_sec);
    putLong(env, connector, "timespec_0.tv_nsec", tv[0].tv_nsec);
    putLong(env, connector, "timespec_1.tv_sec", tv[1].tv_sec);
    putLong(env, connector, "timespec_1.tv_nsec", tv[1].tv_nsec);
    int r = callJava(env, "touch", connector);
    ReleaseEnv(env);
    return r;
}

/** Get file system statistics
 *
 * The 'f_frsize', 'f_favail', 'f_fsid' and 'f_flag' fields are ignored
 *
 * Replaced 'struct statfs' parameter with 'struct statvfs' in
 * version 2.5
 */
int jnetfs_statfs(const char *path, struct statvfs *stat) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    int r = callJava(env, "statfs", connector);
    if (r == 0) {
        stat->f_bavail = getLong(env, connector, "f_bavail");
        stat->f_bfree = getLong(env, connector, "f_bfree");
        stat->f_blocks = getLong(env, connector, "f_blocks");
        stat->f_bsize = getLong(env, connector, "f_bsize");
        stat->f_favail = getLong(env, connector, "f_favail");
        stat->f_ffree = getLong(env, connector, "f_ffree");
        stat->f_files = getLong(env, connector, "f_files");
        stat->f_frsize = getLong(env, connector, "f_frsize");
        stat->f_fsid = getLong(env, connector, "f_fsid");
        stat->f_namemax = getLong(env, connector, "f_namemax");
    }
    ReleaseEnv(env);
    return r;
}

int jnetfs_chmod(const char *path, mode_t t) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putInt(env, connector, "mode_t", t);
    int r = callJava(env, "chmod", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_symlink(const char *path, const char *to) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    putString(env, connector, PATHTO, to);
    int r = callJava(env, "symlink", connector);
    ReleaseEnv(env);
    return r;
}

int jnetfs_readlink(const char *path, char *buff, size_t size) {
    JNIEnv *env = getEnv();
    jobject connector = getConnector(env);
    putString(env, connector, PATH, path);
    int r = callJava(env, "readlink", connector);
    if (r == 0) {
        memset(buff, 0, size);
        jstring jname = _get(env, connector, "realPath");
        const char *name = (*env)->GetStringUTFChars(env, jname, JNI_FALSE);
        if (name != NULL) {
            memcpy(buff, name, size - 1);
            (*env)->ReleaseStringUTFChars(env, jname, name);
        }
        (*env)->DeleteLocalRef(env, jname);
    }
    ReleaseEnv(env);
    return r;
}
////////////////////////////////////////////////////////////////////////////////
// Fuse Man entrance
////////////////////////////////////////////////////////////////////////////////
struct fuse_operations jnetfs_oper = {
    .getattr = jnetfs_getattr,
    .readdir = jnetfs_readdir,
    .init = jnetfs_init,
    .destroy = jnetfs_destroy,
    .open = jnetfs_open,
    .read = jnetfs_read,
    .write = jnetfs_write,
    .release = jnetfs_release,
    .truncate = jnetfs_truncate,
    .flush = jnetfs_flush,
    .mknod = jnetfs_mknod,
    .mkdir = jnetfs_mkdir,
    .unlink = jnetfs_unlink,
    .rmdir = jnetfs_rmdir,
    .rename = jnetfs_rename,
    .utimens = jnetfs_utimens,
    .statfs = jnetfs_statfs,
    .chmod = jnetfs_chmod,
    .symlink = jnetfs_symlink,
    .readlink = jnetfs_readlink,
};

/*
 * Class:     com_jnetfs_client_JnetFS
 * Method:    mount
 * Signature: ([Ljava/lang/String;Z)I
 */
JNIEXPORT jint JNICALL Java_com_jnetfs_core_JnetFS_mount
(JNIEnv *env, jobject jfs, jobjectArray args, jboolean debug) {
    //local resources
    jnetfs = (*env)->NewWeakGlobalRef(env, jfs);
    //connector
    jobject class = (*env)->FindClass(env, CONNECTOR_CLASS);
    jmethodID method = (*env)->GetMethodID(env, class, "<init>", "()V");
    jobject connector = (*env)->NewObject(env, class, method);
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        return -1;
    }
    (*env)->DeleteLocalRef(env, class);
    jniConnector = (*env)->NewWeakGlobalRef(env, connector);
    //debug mode
    jdebug = debug;
    //setup fuse-parameters
    int argc = (*env)->GetArrayLength(env, args) + 1;
    char *argv[argc];
    argv[0] = "JnetFS";
    int i;
    for (i = 1; i < argc; i++) {
        jstring jArg = (*env)->GetObjectArrayElement(env, args, i - 1);
        const char *arg = (*env)->GetStringUTFChars(env, jArg, NULL);
        char *fuseArg = (char *) malloc(strlen(arg) + 1);
        if (fuseArg == NULL) return -EOVERFLOW;
        strcpy(fuseArg, arg);
        argv[i] = fuseArg;
        (*env)->ReleaseStringUTFChars(env, jArg, arg);
        (*env)->DeleteLocalRef(env, jArg);
    }
    int r = fuse_main(argc, argv, &jnetfs_oper, NULL);
    //free memory
    for (i = 1; i < argc; i++) {
        free((void*) argv[i]);
    }
    DEBUG("FUSE RETURN: %d", r);
    return r;
}

/*
 * Class:     com_jnetfs_client_JnetFS
 * Method:    umount
 * Signature: (Ljava/lang/String;)
 */
JNIEXPORT void JNICALL Java_com_jnetfs_core_JnetFS_umount
(JNIEnv *env, jobject jfs, jstring mountpoint) {
    const char *point = (*env)->GetStringUTFChars(env, mountpoint, NULL);
    if (point == NULL) return;
    struct fuse_context *ctx = fuse_get_context();
    if (ctx != NULL) {
        fuse_unmount_compat22(point);
    }
    (*env)->ReleaseStringUTFChars(env, mountpoint, point);
}
