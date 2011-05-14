package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPATag.class)
public abstract class JPATag_ {

    public static volatile SingularAttribute<JPATag, Integer> id;
    public static volatile SingularAttribute<JPATag, Integer> tagType;
    public static volatile SingularAttribute<JPATag, JPAGeolocalisation> jPAGeolocalisation;
    public static volatile ListAttribute<JPATag, JPATagPhoto> jPATagPhotoList;
    public static volatile SingularAttribute<JPATag, String> nom;
    public static volatile ListAttribute<JPATag, JPATagTheme> jPATagThemeList;
    public static volatile SingularAttribute<JPATag, JPATag> parent;
    public static volatile ListAttribute<JPATag, JPATag> sonList;
}

