package net.wazari.service.entity.util ;

import java.util.ArrayList;
import java.util.List;

public class MapPoint {
  private List<Point> points = new ArrayList<Point>();
  
  static public class Point {
      public String name ;
      public int id ;
      public Integer photoId ;
      public String photoPath ;
      public String lat ;
      public String lng ;
    
    
    public Point(String name, int id, String lat, String lng, Integer photoId, String photoPath) {
        this.name = name ;
        this.id = id ;
        this.lat = lat ;
        this.lng = lng ;
        this.photoId = photoId ;
        this.photoPath = photoPath ;
    }
  }
  
  public boolean isEmpty() {
      return points.isEmpty();
  }
  
  public void addPoint(Point p) {
    try {
      Double.parseDouble(p.lat) ;
      Double.parseDouble(p.lng) ;
      points.add(p);
    } catch (RuntimeException e) {}
  }

  
  public String getJSon() {

    StringBuilder str = new StringBuilder(150) ;

    str.append("[\n");
    int i = points.size();
    for (Point p : points) {
        str.append("  ");
        str.append("{\"name\":\"").append(p.name).append("\",");
        str.append(" \"id\":\"").append(p.id).append("\",");
        str.append(" \"lat\":\"").append(p.lat).append("\",");
        str.append(" \"lng\":\"").append(p.lng).append("\",");
        str.append(" \"picture\":\"").append(p.photoId).append("\",");
        str.append(" \"picturePath\":\"").append(p.photoPath).append("\"}");
        i--;
        if (i != 0)
            str.append(",\n");    
        else
            str.append("\n");        
    }
    str.append("]\n");
        
    return str.toString() ;
  }
}
