package util.google ;

import util.XmlBuilder ;

public abstract class GoogleMap {
  public abstract String getInitCode() ;
  public abstract boolean isEmpty() ;
  public abstract String getMapName() ;
  public String getFunctions() {return "";} ;
  
  public static XmlBuilder getBody() {
    XmlBuilder output = new XmlBuilder ("body") ;
    output.add("onload", "initialize()");
    output.add("onunload", "GUnload()") ;
    return output.validate() ;
  }
}