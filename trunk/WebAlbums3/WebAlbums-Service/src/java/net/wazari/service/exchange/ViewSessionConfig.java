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

    public Boolean getVisible();

    public String getImportTheme();

    public String getPassword();

    public String getNom();

    public Integer getType();

    public Boolean getSure();
}
