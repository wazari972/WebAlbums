package entity.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the Photo table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="Photo"
 */
public abstract class BasePhoto  implements Serializable {

	public static String PROP_FOCAL = "Focal";
	public static String PROP_ALBUM = "Album";
	public static String PROP_ISO = "Iso";
	public static String PROP_MODEL = "Model";
	public static String PROP_FLASH = "Flash";
	public static String PROP_PATH = "Path";
	public static String PROP_HEIGHT = "Height";
	public static String PROP_DATE = "Date";
	public static String PROP_ID = "ID";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_CLICK = "Click";
	public static String PROP_EXPOSURE = "Exposure";
	public static String PROP_WIDTH = "Width";
	public static String PROP_TYPE = "Type";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _iD;

	// fields
	private java.lang.String _description;
	private java.lang.Integer _album;
	private java.lang.String _path;
	private java.lang.Integer _click;
	private java.lang.String _model;
	private java.lang.String _date;
	private java.lang.String _iso;
	private java.lang.String _exposure;
	private java.lang.String _focal;
	private java.lang.String _flash;
	private java.lang.String _height;
	private java.lang.String _width;
	private java.lang.String _type ;


	// constructors
	public BasePhoto () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePhoto (java.lang.Integer _iD) {
		this.setID(_iD);
		initialize();
	}

	protected void initialize () {}



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="increment"
     *  column="ID"
     */
	public java.lang.Integer getID () {
		return _iD;
	}

	/**
	 * Set the unique identifier of this class
	 * @param _iD the new ID
	 */
	public void setID (java.lang.Integer _iD) {
		this._iD = _iD;
		this.hashCode = Integer.MIN_VALUE;
	}


	/**
	 * Return the value associated with the column: Description
	 */
	public java.lang.String getDescription () {
		return _description;
	}

	/**
	 * Set the value related to the column: Description
	 * @param _description the Description value
	 */
	public void setDescription (java.lang.String _description) {
		this._description = _description;
	}


	/**
	 * Return the value associated with the column: Album
	 */
	public java.lang.Integer getAlbum () {
		return _album;
	}

	/**
	 * Set the value related to the column: Album
	 * @param _album the Album value
	 */
	public void setAlbum (java.lang.Integer _album) {
		this._album = _album;
	}


	/**
	 * Return the value associated with the column: Path
	 */
	public java.lang.String getPath () {
		return _path;
	}

	/**
	 * Set the value related to the column: Path
	 * @param _path the Path value
	 */
	public void setPath (java.lang.String _path) {
	    setClick(0) ;
	    this._path = _path;
	}

	/**
	 * Return the value associated with the column: Type
	 */
	public java.lang.String getType () {
		return _type;
	}

	/**
	 * Set the value related to the column: Type
	 * @param _type the Path value
	 */
	public void setType (java.lang.String _type) {
		this._type = _type;
	}

	/**
	 * Return the value associated with the column: Click
	 */
	public java.lang.Integer getClick () {
		return _click;
	}

	/**
	 * Set the value related to the column: Click
	 * @param _click the Click value
	 */
	public void setClick (java.lang.Integer _click) {
		this._click = _click;
	}


	/**
	 * Return the value associated with the column: Model
	 */
	public java.lang.String getModel () {
		return _model;
	}

	/**
	 * Set the value related to the column: Model
	 * @param _model the Model value
	 */
	public void setModel (java.lang.String _model) {
		this._model = _model;
	}


	/**
	 * Return the value associated with the column: Date
	 */
	public java.lang.String getDate () {
		return _date;
	}

	/**
	 * Set the value related to the column: Date
	 * @param _date the Date value
	 */
	public void setDate (java.lang.String _date) {
		this._date = _date;
	}


	/**
	 * Return the value associated with the column: Iso
	 */
	public java.lang.String getIso () {
		return _iso;
	}

	/**
	 * Set the value related to the column: Iso
	 * @param _iso the Iso value
	 */
	public void setIso (java.lang.String _iso) {
		this._iso = _iso;
	}


	/**
	 * Return the value associated with the column: Exposure
	 */
	public java.lang.String getExposure () {
		return _exposure;
	}

	/**
	 * Set the value related to the column: Exposure
	 * @param _exposure the Exposure value
	 */
	public void setExposure (java.lang.String _exposure) {
		this._exposure = _exposure;
	}


	/**
	 * Return the value associated with the column: Focal
	 */
	public java.lang.String getFocal () {
		return _focal;
	}

	/**
	 * Set the value related to the column: Focal
	 * @param _focal the Focal value
	 */
	public void setFocal (java.lang.String _focal) {
		this._focal = _focal;
	}


	/**
	 * Return the value associated with the column: Flash
	 */
	public java.lang.String getFlash () {
		return _flash;
	}

	/**
	 * Set the value related to the column: Flash
	 * @param _flash the Flash value
	 */
	public void setFlash (java.lang.String _flash) {
		this._flash = _flash;
	}


	/**
	 * Return the value associated with the column: Height
	 */
	public java.lang.String getHeight () {
		return _height;
	}

	/**
	 * Set the value related to the column: Height
	 * @param _height the Height value
	 */
	public void setHeight (java.lang.String _height) {
		this._height = _height;
	}


	/**
	 * Return the value associated with the column: Width
	 */
	public java.lang.String getWidth () {
		return _width;
	}

	/**
	 * Set the value related to the column: Width
	 * @param _width the Width value
	 */
	public void setWidth (java.lang.String _width) {
		this._width = _width;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof entity.base.BasePhoto)) return false;
		else {
			entity.base.BasePhoto mObj = (entity.base.BasePhoto) obj;
			if (null == this.getID() || null == mObj.getID()) return false;
			else return (this.getID().equals(mObj.getID()));
		}
	}


	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getID()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getID().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}

}