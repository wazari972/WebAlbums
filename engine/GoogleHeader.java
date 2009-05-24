package engine ;

import java.util.List ;
import java.util.ArrayList ;

public class GoogleHeader implements Header {
  public static final String googleKey =
    "ABQIAAAANkbb-SNf1VDzX-W2M-MKGBTBfUk9TZrBRaIteybtnU"+
    "2KziHEpRRfEtLc0rJCxsohWtD3KULc_ECH6w" ;
    //"ABQIAAAANkbb-SNf1VDzX-W2M-MKGBT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSS17ctsLu1MnOWWvXRfyA77AyruA";

  private List<GoogleMap> maps = null ;

  public void addMap (GoogleMap map) {
    if (maps == null) maps = new ArrayList<GoogleMap> () ;
    maps.add(map) ;
  }
  
  public String header () {
    if (maps == null) return "" ;
    StringBuilder str = new StringBuilder() ;
    str.append(
      "<script "+
      "src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;"+
      "key="+googleKey+"\" type='text/javascript'></script>\n"+
      "<script type='text/javascript'>\n"+
      "function initialize() {\n"+
      "\n"+
      "  if (GBrowserIsCompatible()) {\n"+
      "     // Creates a marker at the given point\n"+
      "     // Clicking the marker will hide it\n");
      
    for (GoogleMap m : maps) {
      str.append(m.getInit());
    }
    str.append(
      "     \n"+
      "  }\n"+
      "}\n"+
      "</script>\n");
    
    maps = new ArrayList<GoogleMap> () ;
    return str.toString();
  }

  public String bodyAttributes () {
    if (maps == null) return "" ;
    return "onload='initialize()' onunload='GUnload()'" ;
  }
}