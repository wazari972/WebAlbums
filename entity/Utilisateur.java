package entity;

import entity.base.BaseUtilisateur;

/**
 * This is the object class that relates to the Utilisateur table.
 * Any customizations belong here.
 */
public class Utilisateur extends BaseUtilisateur {

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

/*[CONSTRUCTOR MARKER END]*/
}