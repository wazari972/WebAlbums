package Entity;

import Entity.base.BaseTagAlbum;

/**
 * This is the object class that relates to the TagAlbum table.
 * Any customizations belong here.
 */
public class TagAlbum extends BaseTagAlbum {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public TagAlbum () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TagAlbum (java.lang.Integer _iD) {
		super(_iD);
	}
	public String toString() {
		return "["+getClass()+"*"+getID()+"*, "+getAlbumID()+"=>"+getTagID()+"]";
	}
/*[CONSTRUCTOR MARKER END]*/
}