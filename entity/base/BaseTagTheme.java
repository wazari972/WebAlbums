package entity.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the TagTheme table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="TagTheme"
 */
public abstract class BaseTagTheme  implements Serializable {

  public static String PROP_ID = "ID";
  public static String PROP_TAG = "Tag";
  public static String PROP_THEME = "Theme";
  public static String PROP_PHOTO = "Photo";
  public static String PROP_ISVISIBLE = "isVisible";

  private int hashCode = Integer.MIN_VALUE;
  
  // primary key
  private java.lang.Integer _iD;
  
  // fields
  private java.lang.Integer _photo;
  private java.lang.Integer _theme;
  private java.lang.Integer _tag;
  private java.lang.Boolean _isvisble;

  // constructors
  public BaseTagTheme () {
    this._isvisble = true ;
    initialize();
  }
  
  /**
   * Constructor for primary key
   */
  public BaseTagTheme (java.lang.Integer _iD) {
    this.setID(_iD);
    this._isvisble = true ;
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
   * Return the value associated with the column: Theme
   */
  public java.lang.Integer getTheme () {
    return _theme;
  }

  /**
   * Set the value related to the column: Theme
   * @param _photo the Theme value
   */
  public void setTheme (java.lang.Integer _theme) {
    this._theme = _theme;
  }
  public void setTheme (java.lang.String _theme) {
    this._theme = Integer.parseInt(_theme);
  }

  /**
   * Return the value associated with the column: Tag
   */
  public java.lang.Integer getTag () {
    return _tag;
  }

  /**
   * Set the value related to the column: Tag
   * @param _tag the Tag value
   */
  public void setTag (java.lang.Integer _tag) {
    this._tag = _tag;
  }
  public void setTag (java.lang.String _tag) {
    this._tag = Integer.parseInt(_tag);
  }
  
  /**
   * Return the value associated with the column: Photo
   */
  public java.lang.Integer getPhoto () {
    return _photo;
  }

  /**
   * Set the value related to the column: Photo
   * @param _tag the Tag value
   */
  public void setPhoto (java.lang.Integer _photo) {
    this._photo = _photo;
  }

  /**
   * Return the value associated with the column: isVisible
   */
  public java.lang.Boolean getIsVisible () {
    return _isvisble;
  }

  /**
   * Set the value related to the column: Tag
   * @param _tag the Tag value
   */
  public void setIsVisible (java.lang.Boolean _isvisble) {
    this._isvisble = _isvisble;
  }
  
  public boolean equals (Object obj) {
    if (null == obj) return false;
    if (!(obj instanceof entity.base.BaseTagTheme)) return false;
    else {
      entity.base.BaseTagTheme mObj = (entity.base.BaseTagTheme) obj;
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