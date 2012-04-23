package entity;

import entity.base.BaseUserPhoto;

/**
 * This is the object class that relates to the UserPhoto table.
 * Any customizations belong here.
 */
public class UserPhoto extends BaseUserPhoto {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public UserPhoto () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public UserPhoto (java.lang.Integer _iD) {
		super(_iD);
	}

/*[CONSTRUCTOR MARKER END]*/
}