function loadMap() {
    if (GBrowserIsCompatible()) {
	// Creates a marker at the given point
	// Clicking the marker will hide it
        var map = new GMap2(document.getElementById('map_search'));
        map.addControl(new GSmallMapControl());
        map.addControl(new GMapTypeControl());
        var center = new GLatLng(51.5001524, -0.1262362);
        map.setCenter(center, 15);
        geocoder = new GClientGeocoder();
        var marker = new GMarker(center, {draggable: false});
        map.addOverlay(marker);
        document.getElementById('latID').value = center.lat().toFixed(7);
        document.getElementById('lngID').value = center.lng().toFixed(7);

	document.getElementById('latID_2').value = center.lat().toFixed(7);
        document.getElementById('lngID_2').value = center.lng().toFixed(7);
        
	GEvent.addListener(map, 'moveend', function() {
	    map.clearOverlays();
            var center = map.getCenter();
	    var marker = new GMarker(center, {draggable: false});
	    map.addOverlay(marker);
	    document.getElementById('latID').value = center.lat().toFixed(7);
       	    document.getElementById('lngID').value = center.lng().toFixed(7);
	    
	    document.getElementById('latID_2').value = center.lat().toFixed(7);
       	    document.getElementById('lngID_2').value = center.lng().toFixed(7);
	});
	GEvent.addListener(map, 'moveend', function() {
	    map.clearOverlays();
	    var center = map.getCenter();
	    var marker = new GMarker(center, {draggable: true});
	    map.addOverlay(marker);
	    document.getElementById('latID').value = center.lat().toFixed(7);
	    document.getElementById('lngID').value = center.lng().toFixed(7);
	    
	    document.getElementById('latID_2').value = center.lat().toFixed(7);
       	    document.getElementById('lngID_2').value = center.lng().toFixed(7);
	    
	    GEvent.addListener(marker, 'dragend', function() {
		var pt = marker.getPoint();
		map.panTo(pt);
		document.getElementById('latID').value = pt.lat().toFixed(7);
		document.getElementById('lngID').value = pt.lng().toFixed(7);
		
		document.getElementById('latID_2').value = center.lat().toFixed(7);
       		document.getElementById('lngID_2').value = center.lng().toFixed(7);
	    });
	});
    }
}

function showAddress(address) {
    var map = new GMap2(document.getElementById('map_search'));
    map.addControl(new GSmallMapControl());
    map.addControl(new GMapTypeControl());
    if (geocoder) {
        geocoder.getLatLng(
            address,
            function(point) {
		if (!point) {
		    alert(address + ' not found');
		} else {
		    document.getElementById('latID').value = point.lat().toFixed(7);
		    document.getElementById('lngID').value = point.lng().toFixed(7);
		    map.clearOverlays();
		    map.setCenter(point, 14);
		    var marker = new GMarker(point, {draggable: true});
		    map.addOverlay(marker);
		    GEvent.addListener(marker, 'dragend', function() {
			var pt = marker.getPoint();
			map.panTo(pt);
			document.getElementById('latID').value = pt.lat().toFixed(7);
			document.getElementById('lngID').value = pt.lng().toFixed(7);

			document.getElementById('latID_2').value = center.lat().toFixed(7);
       			document.getElementById('lngID_2').value = center.lng().toFixed(7);
		    });
        
        	    GEvent.addListener(map, 'moveend', function() {
			map.clearOverlays();
			var center = map.getCenter();
			var marker = new GMarker(center, {draggable: true});
			map.addOverlay(marker);
			document.getElementById('latID').value = center.lat().toFixed(7);
			document.getElementById('lngID').value = center.lng().toFixed(7);
			
			document.getElementById('latID_2').value = center.lat().toFixed(7);
       			document.getElementById('lngID_2').value = center.lng().toFixed(7);

			GEvent.addListener(marker, 'dragend', function() {
			    var pt = marker.getPoint();
			    map.panTo(pt);
			    document.getElementById('latID').value = pt.lat().toFixed(7);
			    document.getElementById('lngID').value = pt.lng().toFixed(7);

			    document.getElementById('latID_2').value = center.lat().toFixed(7);
       			    document.getElementById('lngID_2').value = center.lng().toFixed(7);
			});
		    });
		}
            }
        );
    }
}

function checkValidity(buttonName, listName) {
    var button = document.getElementById(buttonName)
    var list = document.getElementById(listName)

    if (list.value == -1) {
	button.disabled = true 
    } else {
	button.disabled = false 
    }

}

addLoadEvent(loadMaps);