/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionConfig extends ViewSession{
    boolean getMinor();
    
    String getNouveau();

    Integer getTag();

    String getLng();

    String getLat();

    boolean getVisible();

    String getImportTheme();

    String getNom();

    Integer getType();

    Integer getParentTag();

    Integer[] getSonTags();

    String getBirthdate();
    
    String getContact();
}
