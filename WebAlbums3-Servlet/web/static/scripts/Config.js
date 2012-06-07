function updateLocation(lat, lng) {
    $("#latID").val(lat) ;
    $("#lngID").val(lng) ;

    $("#latID_2").val(lat) ;
    $("#lngID_2").val(lng) ;
}

function checkValidity(listId, validateBtId) {
    if ($("#"+listId).val() == -1) {
        $("#"+validateBtId).prop('disabled', 'disabled');
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
        $(this).val("Dead!").prop('disabled', 'disabled');
        callURL("http://"+window.location.hostname+$(this).prop('rel')) ;
    }) ;
}

var last_point = null;
function init_map() {
    var map = loadMap("map_search")
    
    var markers = new OpenLayers.Layer.Markers("Tags");
    map.addLayer(markers);
    
    map.events.register("click", map , function(e){
        if (last_point != null)
            markers.removeMarker(last_point)
        var opx = map.getLonLatFromPixel(e.xy) ;
        var marker = new OpenLayers.Marker(opx);
        markers.addMarker(marker);
        last_point = marker
        
        var pos = map.getLonLatFromViewPortPx(e.xy)
        pos.transform( map.projection,map.displayProjection);
        updateLocation(pos.lat, pos.lon)
    });
}

$(function() {
    init_buttons()
    init_map()
})