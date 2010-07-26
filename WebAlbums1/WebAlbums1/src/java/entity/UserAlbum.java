package entity;

import entity.base.BaseUserAlbum;

/**
 * This is the object class that relates to the UserAlbum table.
 * Any customizations belong here.
 */
public class UserAlbum extends BaseUserAlbum {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public UserAlbum () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public UserAlbum (java.lang.Integer _iD) {
		super(_iD);
	}

/*[CONSTRUCTOR MARKER END]*/
}