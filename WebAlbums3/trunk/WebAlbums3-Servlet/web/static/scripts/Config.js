
addLoadEvent(loadMaps("map_search", ""));
function loadGoogleMap() {
    if (GBrowserIsCompatible()) {
        // Creates a marker at the given point
        // Clicking the marker will hide it
        var map = new GMap2(document.getElementById('map_search'));
        map.addControl(new GSmallMapControl());
        map.addControl(new GMapTypeControl());
        var center = new GLatLng(45.1942765, 5.7316335);
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

function showGoogleAddress(address) {
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



var mapstraction;
var geocoder;
var address ;
function loadMapstration() {
    mapstraction = new Mapstraction('map_search','openlayers');
    mapstraction.setCenterAndZoom(new LatLonPoint(0,0), 1);

    // initialise the map with your choice of API
    geocoder = new MapstractionGeocoder(geocode_return, 'google');

    address = new Object();
    address.street = "1600 Pennsylvania Ave.";
    address.locality = "Washington";
    address.region = "DC";
    address.country = "US";

    geocoder.geocode(address);
}
$(document).ready(function() {
    loadMap() ;
});

function geocode_return(geocoded_location) {

    // display the map centered on a latitude and longitude (Google zoom levels)
    mapstraction.setCenterAndZoom(geocoded_location.point, 15);

    mapstraction.addControls({
        pan: true,
        zoom: 'small',
        map_type: true
    });
    // create a marker positioned at a lat/lon
    geocode_marker = new Marker(geocoded_location.point);

    var address = geocoded_location.street + ", "
    + geocoded_location.locality + ", "
    + geocoded_location.region;
    geocode_marker.setLabel("A");
    geocode_marker.setInfoBubble(geocoded_location.address);

    // display marker
    mapstraction.addMarker(geocode_marker);

    // open the marker
    geocode_marker.openBubble();
}

function user_submit() {
    var address = new Object();
    address.address = document.getElementById('newTag').value;
    
    showGoogleAddress(address.address)
    //geocoder.geocode(address);
}
$("#btGoto").bind("click", user_submit) ;

function checkValidity() {
    if ($("#lstNewTag").val() == 3) {
        $("#btGoto").show() ;
    } else {
        $("#btGoto").hide() ;
    }

    if ($("#lstNewTag").val() == -1) {
	$("#valNewTag").attr('disabled', 'disabled');
    } else {
	$("#valNewTag").attr('disabled', '');
    }
}
$("#lstNewTag").change(checkValidity) ;

