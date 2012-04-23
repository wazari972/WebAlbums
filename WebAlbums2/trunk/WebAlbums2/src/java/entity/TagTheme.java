package entity;

import entity.base.BaseTagTheme;

/**
 * This is the object class that relates to the TagTheme table.
 * Any customizations belong here.
 */
public class TagTheme extends BaseTagTheme {

/**
 * 
 */
  private static final long serialVersionUID = 1L;
  
  /*[CONSTRUCTOR MARKER BEGIN]*/
  public TagTheme () {
    super();
  }
  
  /**
   * Constructor for primary key
   */
  public TagTheme (java.lang.Integer _iD) {
    super(_iD);
  }

  public String toString() {
    return "TagTheme "+getID()+
      " (P"+getPhoto()+", Th"+getTheme()+", Ta"+getTag()+") "+
      getIsVisible() ;
  }
/*[CONSTRUCTOR MARKER END]*/
}