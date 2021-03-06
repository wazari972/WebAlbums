package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPATheme.class)
public abstract class JPATheme_ {

    public static volatile SingularAttribute<JPATheme, Integer> id;
    public static volatile ListAttribute<JPATheme, JPAAlbum> jPAAlbumList;
    public static volatile SingularAttribute<JPATheme, String> password;
    public static volatile SingularAttribute<JPATheme, String> nom;
    public static volatile ListAttribute<JPATheme, JPATagTheme> jPATagThemeList;
    public static volatile SingularAttribute<JPATheme, Integer> picture;
    public static volatile SingularAttribute<JPATheme, Integer> background;
    public static volatile ListAttribute<JPATheme, JPACarnet> jPACarnetList;
    public static volatile SingularAttribute<JPATheme, String> longitude;
    public static volatile SingularAttribute<JPATheme, String> lat;
}

