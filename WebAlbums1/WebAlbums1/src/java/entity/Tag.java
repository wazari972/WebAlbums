package entity;

import entity.base.BaseTag;

/**
 * This is the object class that relates to the Tag table.
 * Any customizations belong here.
 */
@SuppressWarnings("unchecked")
public class Tag extends BaseTag implements Comparable {
  private static final long serialVersionUID = 1L;
  
  /*[CONSTRUCTOR MARKER BEGIN]*/
  public Tag () {
    super();
  }
  
  /**
   * Constructor for primary key
   */
  public Tag (java.lang.Integer _iD) {
    super(_iD);
  }

  public int compareTo(Object o) {
    if (!(o instanceof Tag)) return -1 ;
    Tag t = (Tag) o ;
    if (t.getTagType() < getTagType()) {
      return 1 ;
    } else if (t.getTagType() > getTagType()) {
      return -1 ;
    } else {
      return 0 - t.getNom().compareTo(getNom()) ;
    }
  }
}