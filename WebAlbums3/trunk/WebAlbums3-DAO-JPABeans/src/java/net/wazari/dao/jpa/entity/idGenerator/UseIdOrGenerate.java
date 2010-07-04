/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa.entity.idGenerator;

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
public class UseIdOrGenerate extends IdentityGenerator {
    private static final Logger log = Logger.getLogger(UseIdOrGenerate.class.getName());

    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
        if (obj == null) throw new HibernateException(new NullPointerException()) ;
        if ((((EntityWithId) obj).getId()) == null) {

            return super.generate(session, obj);
        } else {
            return ((EntityWithId) obj).getId();

        }
    }


}
