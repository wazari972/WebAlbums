package util.google ;

import java.util.List ;
import java.util.ArrayList ;

import constante.Path ;

public class GooglePoint extends GoogleMap {
  private static int current = 0 ;

  private List<Point> points = new ArrayList<Point>();
  private String id = "" ;
  private boolean displayInfo = true ;
  private String name ;
  
  static public class Point {
    public String lat ;
    public String lng ;
    public String name ;
    public String varName ;
    public String msg ;
    public Point(String lat, String lng, String name) {
      this.lat = lat ;
      this.lng = lng ;
      this.name = name ;
      this.varName = util.StringUtil.toAscii(name) ;
    }
    public void setMsg(String msg) {
      this.msg = msg ;
    }
  }
  public GooglePoint(String name) {
    this.name = name ;
  }
  public GooglePoint(String name, boolean uniqueName) {
    this.name = name +(uniqueName ? GooglePoint.current++ : "") ;
  }
  
  public boolean isEmpty() {
      return points.isEmpty();
  }
  public String getMapName() {
    return name ;
  }
  
  public void addPoint(Point p) {
    try {
      Double.parseDouble(p.lat) ;
      Double.parseDouble(p.lng) ;
      points.add(p);
    } catch (RuntimeException e) {}
  }

  public void displayInfo(boolean info) {
    this.displayInfo = info ;
  }

  public String getInitFunction() {
    return "function loadMap() {\n"+
      "  if (GBrowserIsCompatible()) {\n"+
      getInitCode()+"\n"+
      "  }\n"+
      "}\n" ;
  }
  
  public String getInitCode() {
    if (points.isEmpty()) return "//point list is empty\n" ;
    else if (!Path.hasInternet()) return "//no internet\n" ;

    StringBuilder str = new StringBuilder() ;
    str.append(
      "     function createMarker"+id+"(latlng, message) {\n"+
      "         var marker = new GMarker(latlng);\n"+
      "         marker.value = 1;\n");
    if (displayInfo) {
      str.append(
	"         GEvent.addListener(marker,'click', function() {\n"+
	"             map"+id+".openInfoWindowHtml(latlng, message);\n"+
	"         });\n");
    }
    str.append(
      "         return marker;\n"+
      "     }\n");
    
    str.append(
      "     var map"+id+" "+
      "= new GMap2(document.getElementById('"+getMapName()+"'));\n"+
      "     \n"+
      "     var bounds"+id+" = new GLatLngBounds ();\n");
    for (Point p : points) {
      str.append("     var "+p.varName+id+" "+
		 "= new GLatLng("+p.lat+", "+p.lng+");\n");
      str.append("     bounds"+id+".extend("+p.varName+id+");\n");
    }
    str.append(
      "     map"+id+".setCenter(bounds"+id+".getCenter() , 13);\n"+
      "     var zoomLevel"+id+" = map"+id+".getBoundsZoomLevel(bounds"+id+");\n"+
      "     map"+id+".setZoom(zoomLevel"+id+");\n"+
      "     \n");
      
    for (Point p : points) {
      str.append("     map"+id+".addOverlay(createMarker"+id+"("+
		 p.varName+id+", \""+p.msg+"\"));\n");
    }
    if (displayInfo) {
      str.append(""+
		 "     map"+id+".addControl(new GSmallMapControl());\n"+
		 "     map"+id+".addControl(new GMapTypeControl());\n");
    }
        
    return str.toString() ;
  }
}