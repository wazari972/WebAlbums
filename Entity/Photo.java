package Entity;

import Entity.base.BasePhoto;

/**
 * This is the object class that relates to the Photo table.
 * Any customizations belong here.
 */
public class Photo extends BasePhoto {

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Photo () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Photo (java.lang.Integer _iD) {
		super(_iD);
	}
/*[CONSTRUCTOR MARKER END]*/
}