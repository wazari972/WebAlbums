package util.google ;

import java.util.List ;
import java.util.ArrayList ;


import constante.Path ;

public class GoogleHeader {
  public static final String googleKey =
//    "ABQIAAAANkbb-SNf1VDzX-W2M-MKGBTBfUk9TZrBRaIteybtnU"+"2KziHEpRRfEtLc0rJCxsohWtD3KULc_ECH6w" ;
    "ABQIAAAANkbb-SNf1VDzX-W2M-MKGBT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSS17ctsLu1MnOWWvXRfyA77AyruA";

  private List<GoogleMap> maps = null ;

  public void addMap (GoogleMap map) {
    if (maps == null) maps = new ArrayList<GoogleMap> () ;
    maps.add(map) ;
  }

  public static String getKey() {
    if (!Path.hasInternet()) return "" ;
    return "http://maps.google.com/maps?file=api&amp;v=2&amp;"+
      "key="+googleKey ;
  }
  
  public String script () {
    if (maps == null) return "" ;
    else if (!Path.hasInternet()) return "" ;

    StringBuilder str = new StringBuilder() ;
    str.append(
      "function initialize() {\n"+
      "  if (GBrowserIsCompatible()) {\n"+
      "     // Creates a marker at the given point\n"+
      "     // Clicking the marker will hide it\n");
      
    for (GoogleMap m : maps) {
      str.append(m.getInitCode());
    }
    str.append(
      "     \n"+
      "  }\n"+
      "}\n" );
    str.append("\n\n");
    for (GoogleMap m : maps) {
	str.append(m.getFunctions());
    }
    
    maps = new ArrayList<GoogleMap> () ;
    return str.toString();
  }

  public String bodyAttributes () {
    if (maps == null) return "" ;
    else if (!Path.hasInternet()) return "" ;

    return "onload='initialize()' onunload='GUnload()'" ;
  }
}