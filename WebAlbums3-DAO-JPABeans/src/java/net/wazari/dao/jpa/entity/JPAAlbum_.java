package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAAlbum.class)
public abstract class JPAAlbum_ {

    public static volatile SingularAttribute<JPAAlbum, Integer> picture;
    public static volatile SingularAttribute<JPAAlbum, Integer> id;
    public static volatile ListAttribute<JPAAlbum, JPAPhoto> jPAPhotoList;
    public static volatile ListAttribute<JPAAlbum, JPAGpx> jPAGpxList;
    public static volatile SingularAttribute<JPAAlbum, String> description;
    public static volatile SingularAttribute<JPAAlbum, JPATheme> theme;
    public static volatile SingularAttribute<JPAAlbum, String> date;
    public static volatile SingularAttribute<JPAAlbum, JPAUtilisateur> droit;
    public static volatile SingularAttribute<JPAAlbum, String> nom;
}

