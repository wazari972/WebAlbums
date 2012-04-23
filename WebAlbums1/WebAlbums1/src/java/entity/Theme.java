package entity;

import entity.base.BaseTheme;

/**
 * This is the object class that relates to the Theme table.
 * Any customizations belong here.
 */
public class Theme extends BaseTheme {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public Theme () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Theme (java.lang.Integer _iD) {
		super(_iD);
	}

/*[CONSTRUCTOR MARKER END]*/
}