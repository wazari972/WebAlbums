package entity;

import entity.base.BaseUtilisateur;

/**
 * This is the object class that relates to the Utilisateur table.
 * Any customizations belong here.
 */
@SuppressWarnings("unchecked")
public class Utilisateur extends BaseUtilisateur  implements Comparable  {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public Utilisateur () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Utilisateur (java.lang.Integer _iD) {
		super(_iD);
	}

  public int compareTo(Object o) {
    if (!(o instanceof Utilisateur)) return -1 ;
    Utilisateur u = (Utilisateur) o ;
    return u.getID() - getID() ;
  }
/*[CONSTRUCTOR MARKER END]*/
}