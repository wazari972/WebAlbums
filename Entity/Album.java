package Entity;

import Entity.base.BaseAlbum;

/**
 * This is the object class that relates to the Album table.
 * Any customizations belong here.
 */
public class Album extends BaseAlbum {

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Album () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Album (java.lang.Integer _iD) {
		super(_iD);
	}
/*[CONSTRUCTOR MARKER END]*/
}