package engine ;

import java.util.List ;
import java.util.ArrayList ;

import constante.Path ;

public class GooglePoint extends GoogleMap {
  private static int current = 0 ;
  private List<Point> points = new ArrayList<Point>();
  private int id ;
  private boolean displayInfo = true ;
  private static final String MAP_NAME = "map_point" ;

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
  public boolean isEmpty() {
      return points.isEmpty();
  }
  public String getMapName() {
      return MAP_NAME+id ;
  }
  public GooglePoint () {
    this.id = GooglePoint.current ;
    GooglePoint.current++ ;
  }
  
  public void addPoint(Point p) {
    try {
      Double.parseDouble(p.lat) ;
      Double.parseDouble(p.lng) ;
      points.add(p);
    } catch (Exception e) {}
  }

  public void displayInfo(boolean info) {
    this.displayInfo = info ;
  }
  
  public String getInit() {
    if (points.isEmpty()) return "" ;
    else if (!Path.hasInternet()) return "" ;

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
		 "     map"+id+".addControl(new GMapTypeControl());");
    }
        
    return str.toString() ;
  }
}