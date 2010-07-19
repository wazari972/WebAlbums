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

    public String getNouveau();

    public Integer getTag();

    public String getLng();

    public String getLat();

    public boolean getVisible();

    public String getImportTheme();

    public String getNom();

    public Integer getType();
}
