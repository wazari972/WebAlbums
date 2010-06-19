package net.wazari.service.util.google ;

import net.wazari.service.exchange.Configuration;
import net.wazari.common.util.XmlBuilder ;

public abstract class GoogleMap {
  public abstract String getInitCode(Configuration conf) ;
  public abstract boolean isEmpty() ;
  public abstract String getMapName() ;
  public String getFunctions(Configuration conf) {return "";} ;
  
  public static XmlBuilder getBody() {
    XmlBuilder output = new XmlBuilder ("body") ;
    output.add("onload", "initialize()");
    output.add("onunload", "GUnload()") ;
    return output.validate() ;
  }
}