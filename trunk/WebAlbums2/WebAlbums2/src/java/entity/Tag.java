package entity;

import entity.base.BaseTag;

import java.io.File ;
import engine.WebPage ;
import java.util.Iterator;
import engine.Users ;
import javax.servlet.http.HttpServletRequest;

import system.SystemTools ;

/**
 * This is the object class that relates to the Tag table.
 * Any customizations belong here.
 */
@SuppressWarnings("unchecked")
public class Tag extends BaseTag {
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

  public Geolocalisation getGeolocEnt () {
    if (getTagType() != 3) return null ;

    String rq = "FROM Geolocalisation WHERE Tag = '"+getID()+"'" ;
    Geolocalisation enrGeo =
      (Geolocalisation) WebPage.session.createQuery(rq).uniqueResult() ;
    return enrGeo ;
  }
}