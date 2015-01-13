package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPATagPhoto.class)
public abstract class JPATagPhoto_ {

    public static volatile SingularAttribute<JPATagPhoto, Long> id;
    public static volatile SingularAttribute<JPATagPhoto, JPATag> tag;
    public static volatile SingularAttribute<JPATagPhoto, JPAPhoto> photo;
}

