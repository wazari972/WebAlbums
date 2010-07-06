/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa.entity.idGenerators;

import java.io.Serializable;
import java.util.logging.Logger;
import net.wazari.dao.entity.facades.EntityWithId;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

/**
 *
 * @author kevinpouget
 */
public class UseIdOrGenerated extends IdentityGenerator {
    private static final Logger log = Logger.getLogger(UseIdOrGenerated.class.getName());

    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
        if (obj == null) throw new HibernateException(new NullPointerException()) ;

        if ((((EntityWithId) obj).getId()) == null) {
            Serializable id = super.generate(session, obj) ;
            log.info("==>"+id);
            new Exception("==>"+id).printStackTrace(); 
            return id;
        } else {
            return ((EntityWithId) obj).getId();

        }
    }


}
