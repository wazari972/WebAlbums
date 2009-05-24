package entity;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.classic.Session;
import org.hibernate.Transaction;

import util.HibernateUtil;
import entity.base.BaseTag;

/**
 * This is the object class that relates to the Tag table.
 * Any customizations belong here.
 */
public class Tag extends BaseTag {
  private static final long serialVersionUID = 1L;
  
  /*[CONSTRUCTOR MARKER BEGIN]*/
  public Tag () {
    super();
  }
  
  /**
   * Constructor for primary key
   */
  public Tag (java.lang.Integer _iD) {
    super(_iD);
  }
}