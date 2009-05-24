package entity;

import entity.base.BaseTagPhoto;

/**
 * This is the object class that relates to the TagPhoto table.
 * Any customizations belong here.
 */
public class TagPhoto extends BaseTagPhoto {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public TagPhoto () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TagPhoto (java.lang.Integer _iD) {
		super(_iD);
	}

/*[CONSTRUCTOR MARKER END]*/
}