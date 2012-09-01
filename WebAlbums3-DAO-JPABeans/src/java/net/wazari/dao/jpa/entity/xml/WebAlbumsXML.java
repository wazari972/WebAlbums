/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.jpa.entity.*;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
public class WebAlbumsXML {
    public static final Class<?>[] clazzez = new Class<?>[]
    {WebAlbumsXML.class, JPATheme.class, JPAUtilisateur.class,
    JPAAlbum.class, JPATag.class, JPATagTheme.class, JPATagPhoto.class} ;

    @XmlElementWrapper(name="Themes")
    @XmlElement(name="Theme")
    public List<JPATheme> Themes ;
    @XmlElementWrapper(name="Utilisateurs")
    @XmlElement(name="Utilisateur")
    public List<JPAUtilisateur> Utilisateurs ;
    @XmlElementWrapper(name="Tags")
    @XmlElement(name="Tag")
    public List<JPATag> Tags ;
    @XmlElementWrapper(name="Carnets")
    @XmlElement(name="Carnet")
    public List<JPACarnet> Carnets;

    private WebAlbumsXML(){}
    
    public WebAlbumsXML(List<Theme> themes, List<Utilisateur> utilisateurs, 
                        List<Tag> tags, List<Carnet> carnets)
    {
        this.Themes = (List) themes;
        this.Utilisateurs = (List) utilisateurs;
        this.Tags = (List) tags;
        this.Carnets = (List) carnets;
    }
}
