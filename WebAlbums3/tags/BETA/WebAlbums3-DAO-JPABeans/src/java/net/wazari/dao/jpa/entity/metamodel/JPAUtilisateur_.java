package net.wazari.dao.jpa.entity.metamodel;

import net.wazari.dao.jpa.entity.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAUtilisateur.class)
public abstract class JPAUtilisateur_ {

    public static volatile SingularAttribute<JPAUtilisateur, Integer> id;
    public static volatile ListAttribute<JPAUtilisateur, JPAAlbum> jPAAlbumList;
    public static volatile SingularAttribute<JPAUtilisateur, String> nom;
}

