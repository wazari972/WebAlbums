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

    google.maps.event.addListener(map, 'click', function(event) {
        deleteOverlays();
        placeMarker(event.latLng);
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

function updateLocation(lat, lng) {
    $("#latID").val(lat) ;
    $("#lngID").val(lng) ;

    $("#latID_2").val(lat) ;
    $("#lngID_2").val(lng) ;
}

function checkValidity(listId, validateBtId) {
    if ($("#"+listId).val() == -1) {
        $("#"+validateBtId).attr('disabled', 'disabled');
    } else {
        $("#"+validateBtId).removeAttr('disabled');
    }
}

function pleaseConfirm(form) {
    if (confirm("Really ?")) {
        document.getElementById(form).submit() ;
    }
}

function init_buttons() {
    $("#lstModGeo").change(function () {
        checkValidity("lstModGeo", "valModGeo")
    }) ;
    checkValidity("lstModGeo", "valModGeo")

    $("#lstNewTag").change(function () {
        checkValidity("lstNewTag", "valNewTag")
    }) ;
    checkValidity("lstNewTag", "valNewTag")

    $("#lstModTag").change(function () {
        checkValidity("lstModTag", "valModTag")
    }) ;
    checkValidity("lstModTag", "valModTag")

    $("#lstParentTag").change(function () {
        checkValidity("lstParentTag", "valLinkTag")
    }) ;
    checkValidity("lstParentTag", "valLinkTag")

    $("#lstModVis").change(function () {
        checkValidity("lstModVis", "valModVis")
    }) ;
    checkValidity("lstModVis", "valModVis")

    $("#lstDelTag").change(function () {
        checkValidity("lstDelTag", "valDelTag")
    }) ;
    checkValidity("lstDelTag", "valDelTag")

    $("#importBt").click(function () {
        pleaseConfirm("formImport")
    }) ;

    $("#delThemeBt").click(function () {
        pleaseConfirm("formDelTheme")
    }) ;

    $("#btKill").click(function () {
        alert("Bye-bye");
        $(this).val("Dead!").attr('disabled', 'disabled');
        callURL("http://"+window.location.hostname+$(this).attr('rel')) ;
    }) ;
    
    $("#btGoto").bind("click", user_submit) ;
}

$(function() {
    loadMaps()
    init_buttons()
})