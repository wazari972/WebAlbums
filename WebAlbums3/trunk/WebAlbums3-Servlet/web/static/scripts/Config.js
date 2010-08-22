//Useful links:
// http://code.google.com/apis/maps/documentation/javascript/reference.html#Marker
// http://code.google.com/apis/maps/documentation/javascript/services.html#Geocoding
// http://jqueryui.com/demos/autocomplete/#remote-with-cache

var geocoder;
var map;
var marker;

function initialize(){
    //MAP
    var latlng = new google.maps.LatLng(41.659,-4.714);
    var options = {
	zoom: 16,
	center: latlng,
	mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("map_search"), options);

    //GEOCODER
    geocoder = new google.maps.Geocoder();

    marker = new google.maps.Marker({
	map: map,
	draggable: true
  });

}

$(document).ready(function() {

    initialize();

    $(function() {
	$("#newTag").autocomplete({
	    //This bit uses the geocoder to fetch address values
	    source: function(request, response) {
		geocoder.geocode( {'newTag': request.term }, function(results, status) {
		    response($.map(results, function(item) {
			return {
			    label:  item.formatted_address,
			    value: item.formatted_address,
			    latitude: item.geometry.location.lat(),
			    longitude: item.geometry.location.lng()
			}
		    }));
		})
	    },
	    //This bit is executed upon selection of an address
	    select: function(event, ui) {
		$("#latID").val(ui.item.latitude);
		$("#lngID").val(ui.item.longitude);
		var location = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);
		marker.setPosition(location);
		map.setCenter(location);
	    }
	});
    });

    //Add listener to marker for reverse geocoding
    google.maps.event.addListener(marker, 'drag', function() {
	geocoder.geocode({'latLng': marker.getPosition()}, function(results, status) {
	    if (status == google.maps.GeocoderStatus.OK) {
		if (results[0]) {
		    $('#newTag').val(results[0].formatted_address);
		    $('#latID').val(marker.getPosition().lat());
		    $('#lngID').val(marker.getPosition().lng());
		}
	    }
	});
    });

});

function checkValidity(buttonName, listName) {
    var button = document.getElementById(buttonName)
    var list = document.getElementById(listName)

    if (list.value == -1) {
	button.disabled = true 
    } else {
	button.disabled = false 
    }

}
