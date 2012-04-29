/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.*;
import net.wazari.dao.jpa.entity.*;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
public class WebAlbumsXML {
    public static final Class<?>[] clazzez = new Class<?>[]
    {WebAlbumsXML.class, JPATheme.class, JPAUtilisateur.class,
    JPAAlbum.class, JPATag.class, JPATagTheme.class, JPATagPhoto.class /*JPAPhoto.class/*,
     */} ;

    @XmlElementWrapper(name="Themes")
    @XmlElement(name="Theme")
    private List<JPATheme> Themes ;
    @XmlElementWrapper(name="Utilisateurs")
    @XmlElement(name="Utilisateur")
    private List<JPAUtilisateur> Utilisateurs ;
    @XmlElementWrapper(name="Tags")
    @XmlElement(name="Tag")
    private List<JPATag> Tags ;
    @XmlElementWrapper(name="Carnets")
    @XmlElement(name="Carnet")
    private List<JPACarnet> Carnets;

    private WebAlbumsXML(){}
    
    public WebAlbumsXML(List<Theme> themes, List<Utilisateur> utilisateurs, 
                        List<Tag> tags, List<Carnet> carnets)
    {
        this.Themes = (List) themes;
        this.Utilisateurs = (List) utilisateurs;
        this.Tags = (List) tags;
        this.Carnets = (List) carnets;
    }
/*
    public List<JPATag> getTags() {
        return (List) Tags.Tag;
    }

    public List<JPATheme> getThemes() {
        return (List) Themes.Theme;
    }

    public List<JPAUtilisateur> getUtilisateurs() {
        return (List) Utilisateurs.Utilisateur ;
    }
*/
}
