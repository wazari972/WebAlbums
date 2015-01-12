package net.wazari.dao.jpa.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAGeolocalisation.class)
public abstract class JPAGeolocalisation_ {
    public static volatile SingularAttribute<JPAGeolocalisation, Integer> id;
    public static volatile SingularAttribute<JPAGeolocalisation, JPATag> tag;
    public static volatile SingularAttribute<JPAGeolocalisation, String> longitude;
    public static volatile SingularAttribute<JPAGeolocalisation, String> lat;
}

