package entity;

import entity.base.BaseGeolocalisation;

/**
 * This is the object class that relates to the Geolocalisation table.
 * Any customizations belong here.
 */
public class Geolocalisation extends BaseGeolocalisation {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public Geolocalisation () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Geolocalisation (java.lang.Integer _tag) {
		super(_tag);
	}

/*[CONSTRUCTOR MARKER END]*/
}