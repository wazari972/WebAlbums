package engine ;

public class GoogleSearch extends GoogleMap {
    private String lng, lat ;
    public GoogleSearch(String lng, String lat) {
	this.lng = lng ;
	this.lat = lat ;
    }
    public String getInit() {
	String init = 
"        var map = new GMap2(document.getElementById('"+getMapName()+"'));\n"+
"        map.addControl(new GSmallMapControl());\n"+
"        map.addControl(new GMapTypeControl());\n"+
"        var center = new GLatLng(44.9357622, 1.6835690);\n"+
"        map.setCenter(center, 15);\n"+
"        geocoder = new GClientGeocoder();\n"+
"        var marker = new GMarker(center, {draggable: true});\n"+  
"        map.addOverlay(marker);\n"+
"        document.getElementById('"+lat+"').value = center.lat().toFixed(7);\n"+
"        document.getElementById('"+lng+"').value = center.lng().toFixed(7);\n"+
"        \n"+
"	  GEvent.addListener(marker, 'dragend', function() {\n"+
"       var point = marker.getPoint();\n"+
"	      map.panTo(point);\n"+
"       document.getElementById('"+lat+"').value = point.lat().toFixed(7);\n"+
"       document.getElementById('"+lng+"').value = point.lng().toFixed(7);\n"+
"       \n"+
"        });\n"+
"       \n"+
"       \n"+
"       GEvent.addListener(map, 'moveend', function() {\n"+
"		  map.clearOverlays();\n"+
"                  var center = map.getCenter();\n"+
"		  var marker = new GMarker(center, {draggable: true});\n"+
"		  map.addOverlay(marker);\n"+
"		  document.getElementById('"+lat+"').value = center.lat().toFixed(7);\n"+
"       	  document.getElementById('"+lng+"').value = center.lng().toFixed(7);\n"+
"     \n"+
"        });\n"+
"      \n"+
"      GEvent.addListener(marker, 'dragend', function() {\n"+
"      var point =marker.getPoint();\n"+
"	     map.panTo(point);\n"+
"            document.getElementById('"+lat+"').value = point.lat().toFixed(7);\n"+
"	     document.getElementById('"+lng+"').value = point.lng().toFixed(7);});\n" ;
	return init ;
    }
    
    public String getSearch(String champs) {
	return "showAddress(document."+champs+".value); return false" ;
    }
    
    public String getFunctions() {
	String fct = 
" function showAddress(address) {\n"+
"      var map = new GMap2(document.getElementById('"+getMapName()+"'));\n"+
"       map.addControl(new GSmallMapControl());\n"+
"       map.addControl(new GMapTypeControl());\n"+
"       if (geocoder) {\n"+
"        geocoder.getLatLng(\n"+
"          address,\n"+
"          function(point) {\n"+
"            if (!point) {\n"+
"              alert(address + ' not found');\n"+
"            } else {\n"+
"		  document.getElementById('"+lat+"').value = point.lat().toFixed(7);\n"+
"	   document.getElementById('"+lng+"').value = point.lng().toFixed(7);\n"+
"		 map.clearOverlays();\n"+
"			map.setCenter(point, 14);\n"+
"   var marker = new GMarker(point, {draggable: true});\n"+  
"		 map.addOverlay(marker);\n"+
"		GEvent.addListener(marker, 'dragend', function() {\n"+
"      var pt = marker.getPoint();\n"+
"	     map.panTo(pt);\n"+
"      document.getElementById('"+lat+"').value = pt.lat().toFixed(7);\n"+
"	     document.getElementById('"+lng+"').value = pt.lng().toFixed(7);\n"+
"        });\n"+
"        \n"+
"        \n"+
"	 GEvent.addListener(map, 'moveend', function() {\n"+
"		  map.clearOverlays();\n"+
"    var center = map.getCenter();\n"+
"		  var marker = new GMarker(center, {draggable: true});\n"+
"		  map.addOverlay(marker);\n"+
"		  document.getElementById('"+lat+"').value = center.lat().toFixed(7);\n"+
"	   document.getElementById('"+lng+"').value = center.lng().toFixed(7);\n"+
"    \n"+
"	 GEvent.addListener(marker, 'dragend', function() {\n"+
"     var pt = marker.getPoint();\n"+
"	    map.panTo(pt);\n"+
"    document.getElementById('"+lat+"').value = pt.lat().toFixed(7);\n"+
"	   document.getElementById('"+lng+"').value = pt.lng().toFixed(7);\n"+
"        });\n"+
"        });\n"+
"\n"+
"            }\n"+
"          }\n"+
"        );\n"+
"      }\n"+
"    }\n" ;
	return fct ;
    } ;
    public boolean isEmpty() {
	return true;
    }
    public String getMapName() {
	return "map_search" ;
    }
}