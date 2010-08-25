package net.wazari.service.util.google ;

import java.util.List ;
import java.util.ArrayList ;

public class GooglePoint extends GoogleMap {
  private static int current = 0 ;

  private List<Point> points = new ArrayList<Point>();
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
      this.varName = net.wazari.common.util.StringUtil.toAscii(name) ;
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

  public String getInitFunction() {
    return "function loadGoogleMap() {\n"+
      getInitCode()+"\n"+
      "}\n" ;
  }
  
  public String getInitCode() {
    if (points.isEmpty()) return "//point list is empty\n" ;

    StringBuilder str = new StringBuilder(150) ;

    str.append("var imageBounds = new google.maps.LatLngBounds() ;\n") ;
    for (Point p : points) {
      str.append("var "+p.varName+" = new google.maps.LatLng("+p.lat+", "+p.lng+");\n");
      str.append("imageBounds.extend("+p.varName+") ;\n");
    }
    
    str.append("\nvar optionsCarte = {\n");
    str.append("    zoom: 13,\n");
    str.append("    center: imageBounds.getCenter(),\n");
    str.append("    mapTypeId: google.maps.MapTypeId.ROADMAP\n");
    str.append("};\n\n");
    
    str.append("var maCarte = new google.maps.Map(document.getElementById('"+getMapName()+"'), optionsCarte);\n");
    str.append("maCarte.fitBounds( imageBounds );\n\n") ;

    for (Point p : points) {
      str.append("var "+p.varName+"IW = new google.maps.InfoWindow({\n");
      str.append("    content: \""+p.msg+"\"\n");
      str.append("});\n");

      str.append("var "+p.varName+"OM = {\n");
      str.append("    position: "+p.varName+",\n");
      str.append("    map: maCarte\n");
      str.append("}\n");
      str.append("var "+p.varName+"M = new google.maps.Marker("+p.varName+"OM);\n");
      
      str.append("google.maps.event.addListener("+p.varName+"M, 'click', function() {\n");
      str.append("    "+p.varName+"IW.open(maCarte,"+p.varName+"M);\n");
      str.append("});\n\n");
    }
        
    return str.toString() ;
  }
}
