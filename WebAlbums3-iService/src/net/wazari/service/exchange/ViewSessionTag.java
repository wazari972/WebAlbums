/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionTag extends ViewSessionPhoto, ViewSessionAlbum {

    Integer[] getTagAsked();

    boolean getWantTagChildren();

    boolean getWantUnusedTags();

}
