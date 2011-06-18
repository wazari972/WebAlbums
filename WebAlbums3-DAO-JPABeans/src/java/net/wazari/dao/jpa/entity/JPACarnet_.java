package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAAlbum.class)
public abstract class JPACarnet_ {

    public static volatile SingularAttribute<JPACarnet, Integer> picture;
    public static volatile SingularAttribute<JPACarnet, Integer> id;
    public static volatile ListAttribute<JPACarnet, JPAPhoto> jPAPhotoList;
    public static volatile SingularAttribute<JPACarnet, String> description;
    public static volatile SingularAttribute<JPACarnet, JPATheme> theme;
    public static volatile SingularAttribute<JPACarnet, String> date;
    public static volatile SingularAttribute<JPACarnet, JPAUtilisateur> droit;
    public static volatile SingularAttribute<JPACarnet, String> nom;
}

