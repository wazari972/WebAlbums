
addLoadEvent(loadMaps("map_search", ""));


var geocoder;
var map;
var markersArray = [];

function loadGoogleMap() {
    geocoder = new google.maps.Geocoder();
    var latlng = new google.maps.LatLng(45.194276, 5.7316335);
    var myOptions = {
        zoom: 8,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP

    //MapTypeId.
    //MapTypeId.SATELLITE
    //MapTypeId.HYBRID
    //MapTypeId.TERRAIN
    }
    map = new google.maps.Map(document.getElementById("map_search"), myOptions);
    // affichage infobulle

    //fin affichage info bulle
    google.maps.event.addListener(map, 'click', function(event) {
        deleteOverlays();
        placeMarker(event.latLng);
    //map.setZoom(18);
    //alert(map.getCenter().lat() + "-" + map.getCenter().lng());
    });
}

function placeMarker(location) {
    var marker = new google.maps.Marker({
        position: location,
        map: map
    });

    markersArray.push(marker);
    map.setCenter(location);
    var center = map.getCenter();
    updateLocation(center.lat().toFixed(16), center.lng().toFixed(16)) ;
}



function deleteOverlays() {
    if (markersArray) {
        for (i in markersArray) {
            markersArray[i].setMap(null);
        }
        markersArray.length = 0;
    }
}


function codeAddress(address) {
    if (geocoder) {
        geocoder.geocode({
            'address': address
        }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                map.setCenter(results[0].geometry.location);
                var marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location
                });
                map.setZoom(16);
                deleteOverlays();
                markersArray.push(marker);
                var center = map.getCenter();

                updateLocation(center.lat().toFixed(16), center.lng().toFixed(16)) ;
            } else {
                alert("la géolocalisation a échoué: " + status);
            }
        });
    }
}


function user_submit() {
    codeAddress(document.getElementById('newTag').value) ;
}
$("#btGoto").bind("click", user_submit) ;

function updateLocation(lat, lng) {
    $("#latID").val(lat) ;
    $("#lngID").val(lng) ;

    $("#latID_2").val(lat) ;
    $("#lngID_2").val(lng) ;
}
function checkValidity() {
    if ($("#lstNewTag").val() == -1) {
        $("#valNewTag").attr('disabled', 'disabled');
    } else {
        $("#valNewTag").attr('disabled', '');
    }
}
$("#lstNewTag").change(checkValidity) ;

