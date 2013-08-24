/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.annotation;

/**
 *
 * @author kevin
 */
public interface CanChange {
    /* Triggered when the owner has read the content. */
    void contentRead();
    /* Return True if the content has change since the last call to contentRead(). */
    boolean contentChanged();
}
