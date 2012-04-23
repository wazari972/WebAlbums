package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPATagTheme.class)
public abstract class JPATagTheme_ {

    public static volatile SingularAttribute<JPATagTheme, Integer> id;
    public static volatile SingularAttribute<JPATagTheme, Boolean> isVisible;
    public static volatile SingularAttribute<JPATagTheme, JPATag> tag;
    public static volatile SingularAttribute<JPATagTheme, JPATheme> theme;
    public static volatile SingularAttribute<JPATagTheme, Integer> photo;
}

