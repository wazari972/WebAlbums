package Entity;



import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import util.HibernateUtil;
import Entity.base.BaseTag;

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

/*[CONSTRUCTOR MARKER END]*/
	public static void main (String[] args) {
		try {
			Session session = HibernateUtil.currentSession();
			Transaction tx = session.beginTransaction() ;
			Tag t = new Tag () ;
			t.setNom("Jaune") ;
			session.save(t) ;
			System.out.println(t.getID());
			Tag t2 = new Tag();
			t2.setNom("Jaune") ;
			session.save(t2) ;
			tx.commit();
			System.out.println(t2.getID());
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}