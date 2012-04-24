package net.wazari.dao.jpa.entity.metamodel;

import net.wazari.dao.jpa.entity.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAPhoto.class)
public abstract class JPAPhoto_ {

    public static volatile SingularAttribute<JPAPhoto, String> exposure;
    public static volatile SingularAttribute<JPAPhoto, String> focal;
    public static volatile SingularAttribute<JPAPhoto, String> flash;
    public static volatile SingularAttribute<JPAPhoto, String> model;
    public static volatile SingularAttribute<JPAPhoto, String> iso;
    public static volatile SingularAttribute<JPAPhoto, JPAAlbum> album;
    public static volatile SingularAttribute<JPAPhoto, String> width;
    public static volatile SingularAttribute<JPAPhoto, String> date;
    public static volatile SingularAttribute<JPAPhoto, String> type;
    public static volatile SingularAttribute<JPAPhoto, Integer> droit;
    public static volatile SingularAttribute<JPAPhoto, Integer> id;
    public static volatile SingularAttribute<JPAPhoto, String> height;
    public static volatile ListAttribute<JPAPhoto, JPATagPhoto> jPATagPhotoList;
    public static volatile SingularAttribute<JPAPhoto, String> description;
    public static volatile SingularAttribute<JPAPhoto, String> path;
}
