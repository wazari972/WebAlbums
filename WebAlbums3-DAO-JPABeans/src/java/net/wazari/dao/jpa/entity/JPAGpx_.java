package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAGpx.class)
public abstract class JPAGpx_ {
    public static volatile SingularAttribute<JPAGpx, Integer> id;
    public static volatile SingularAttribute<JPAGpx, String> gpxPath;
    public static volatile SingularAttribute<JPAGpx, JPAAlbum> album;
    public static volatile SingularAttribute<JPAGpx, String> description;
}

