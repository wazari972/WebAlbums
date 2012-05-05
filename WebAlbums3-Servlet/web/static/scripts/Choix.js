function pointToContent(point) {
    return "<div class='map_content'>"
          +"  <h1><a href='Tag__"+point.id+"__"+"point.name"+"'>"+point.name+"</a></h1>\n"
          +"  <center><img src='Miniature__"+point.picture+".png' /></center>\n"
          +"</div>"
}

function printDate(strDate) {
    var dDate = new Date(parseInt(strDate)) ;
    var dateOut = dDate.toString() ;
    return dateOut.substring(0, dateOut.length - 24) ;
}

function trimAlbums(min, max, name) {
    $('.selectAlbum').each(function(index) {
        
        if (parseInt($(this).prop('rel'))  < min ) {
           $(this).hide() ;
        } else if (parseInt($(this).prop('rel'))  > max) {
            $(this).hide() ;
        } else if ($(this).text().toUpperCase().indexOf(name.toUpperCase()) == -1) {
            $(this).hide() ;
        } else {
            $(this).show() ;
        }
    });
}

AutoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
    'autoSize': true
});
var currentPopup = null;
function addMarker(map, markers, point) {
    var lnglat = new OpenLayers.LonLat(point.lng, point.lat).transform(
        new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
        new OpenLayers.Projection("EPSG:900913") // to Spherical Mercator Projection
    );
        
    var feature = new OpenLayers.Feature(markers, lnglat);      
    var marker = feature.createMarker();
    feature.closeBox = true;
    feature.popupClass =  OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
        'autoSize': true
    });
    feature.data.popupContentHTML = pointToContent(point);
    feature.data.overflow = "hidden";
   
    marker = feature.createMarker();
 
    markerClick = function (evt) {
        if (this.popup == null) {
            alert("oups")
        } else {
            this.popup.toggle();
            if (currentPopup != null)
                currentPopup.hide()
        }
        currentPopup = this.popup;
        OpenLayers.Event.stop(evt);
    };
    marker.events.register("mousedown", feature, markerClick);
    markers.addMarker(marker);

    feature.popup = feature.createPopup(feature.closeBox);
    map.addPopup(feature.popup);
    feature.popup.hide();
    
    
}

function populateMap(map) {    
    var markers = new OpenLayers.Layer.Markers("Geo Tags");
    map.addLayer(markers);

    var size = new OpenLayers.Size(21,25);
    var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
    var icon = new OpenLayers.Icon('http://www.openlayers.org/dev/img/marker.png',size,offset);
    
    $.getJSON("Choix?special=MAP&type=JSON",
        function(data) {
            $.each(data, function(key, point) {
                addMarker(map, markers, point, icon.clone())
            })

            map.addControl(new OpenLayers.Control.LayerSwitcher());
            map.zoomToExtent(markers.getDataExtent());
            $("body").css("cursor", "auto");
        }
    ).error(function(e, textStatus) { alert("error"+e+textStatus); $("body").css("cursor", "auto");});
}

function init_loader() {
    $("#albumsLoader").click(function () {
        loadExernals('albumsLoader', 'Albums?special=TOP5', 'albums') ;
    }) ;


    $("#carnetsLoader").click(function () {
        loadExernals('carnetsLoader', 'Carnets?special=TOP5', 'carnets') ;
    }) ;

    $("#personsLoader").click(function () {
        loadExernals('personsLoader', 'Tags?special=PERSONS', 'persons') ;
    }) ;

    $("#placesLoader").click(function () {
        loadExernals('placesLoader', 'Tags?special=PLACES', 'places') ;
    }) ;

    $("#tagShower").click(function () {
        $("#tagShower").fadeOut() ;
        $("#tags").fadeIn() ;
    }) ;

    $("#randPictLoader").click(function () {
        function refresh() {
            refresh_details()
            refresh_editionMode()
        }
        loadExernals(null, 'Photos?special=RANDOM', 'randPict', refresh) ;
    }) ;

    $("#yearsLoader").click(function () {
        loadExernals(null, 'Albums?special=YEARS', 'years') ;
    }) ;

    $("#selectLoader").click(function () {        
        loadExernals('selectLoader', 'Albums?special=SELECT', 'select') ;
    }) ;
    
    $("#tagGraphLoader").click(function () {
        data = $("#tagChoix").serialize()
        loadExernals(null, 'Albums?special=GRAPH&'+data, 'tagGraph', draw_graph, true, data) ;
    }) ;

    $("#mapLoader").click(function () {
        $("#mapLoader").fadeOut() ;
        $("#mapChoix").addClass("mapChoix") ;
        var map = loadMap("mapChoix");
        $("body").css("cursor", "wait");
        populateMap(map)
    }) ;
}

//triggered when SELECT widget is loaded
function do_init_slider(data) {
    
    var NOW = new Date().getTime() ;
    var sliderOption = {
      range: true,
      min: 0,
      max: NOW,
      step: 100000000,
      slide: function(event, ui) {
          $("#fromDate").text(printDate(ui.values[0]));
          $("#toDate").text(printDate(ui.values[1]));
          trimAlbums(ui.values[0], ui.values[1], $("#albmName").val()) ;
      }
     } ;
     $("#slider-range").prop("rel", "singlepage[no]");
     
     $("#slider-range").slider(sliderOption);
     $("#fromDate").text(printDate(data.fromDate));
     $("#toDate").text(printDate(data.toDate));
     
     $("#slider-range").slider( "option", "max", data.toDate+$( "#slider-range" ).slider( "option", "step" ));
     $("#slider-range").slider( "option", "min", data.fromDate);

     $("#slider-range").slider( "option", "values", [data.fromDate, data.toDate]);
     $("#albmName").keyup(
        function(){
           trimAlbums($("#slider-range").slider( "option", "range", "min" ),
                      $("#slider-range").slider( "option", "range", "max" ),
                      $(this).val());

        }
     );
     if (!staticAccess)
        $(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});
}

function save_theme() {
    themeId = getParameterByName("themeId")
    if (themeId == "") 
        return
    
    $.cookie("themeId", themeId)
}

/****************************************************/

function draw_graph() {
    ykeys_len = my_ykeys.length
    data_len = graphData.length
    for (_j = 0; _j < ykeys_len; _j++) {
        ykey = my_ykeys[_j]
        prev = 0 ;
        for (_i = data_len-1; _i >= 0; _i--) {
            graphData[_i][ykey] += prev
            prev = graphData[_i][ykey]
        }
    }
    
    my_labels = $.map(my_ykeys, function (d) {
        if (d == "album")
            return "Albums"
        else
            return d.split("__")[2]
    })

    Morris.Line({
      element: 'graph',
      data: graphData,
      xkey: 'q',
      ykeys: my_ykeys,
      labels: my_labels
    });
}

$(function() {
    save_theme()
    init_loader()
})