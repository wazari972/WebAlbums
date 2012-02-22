function pointToContent(point) {
    return "<div class='gmap_content'>"
          +"  <h1><a href='Tags?tagAsked="+point.id+"'>"+point.name+"</a></h1>\n"
          +"  <img src='Images?mode=PETIT&id="+point.picture+"' />\n"
          +"</div>"
}

function pointToMarker(point, imageBounds, mymap, markers) {
    var latlng = new google.maps.LatLng(point.lat, point.lng);
    imageBounds.extend(latlng) ;
    
    var IW = new google.maps.InfoWindow({
       content:pointToContent(point),
       maxWidth: 250,
       map: mymap
    });

    var M = new google.maps.Marker({position: latlng, title: "Toto"});
    
    google.maps.event.addListener(M, 'click', function() {
      IW.open(mymap, M);
    });
    
    markers.push(M);
}

function putMarkersOnMapSimple (map, markers) {
      $.each(markers, function(key, marker) {
        marker.setMap(map)
      })
}

function putMarkersOnMapGrouped (map, markers) {
      new MarkerClusterer(map, markers)
}

function init_maps() {
    $.getScript("static/scripts/google-maps-utility-library-v3/markerclusterer.js")
    putMarkersOnMap = putMarkersOnMapSimple
    //putMarkersOnMap = putMarkersOnMapGrouped
}

function loadGoogleMap() {
    var imageBounds = new google.maps.LatLngBounds();
    var markers = []
    var map = new google.maps.Map(document.getElementById('mapChoix'), {
      mapTypeId: google.maps.MapTypeId.ROADMAP
    });
    $.getJSON("Choix?special=map.json",
        function(data) {
              $.each(data, function(key, val) {
                pointToMarker(val, imageBounds, map, markers)
              })
              
              map.setCenter(imageBounds.getCenter())
              map.fitBounds(imageBounds)

              putMarkersOnMap(map, markers);
        }
    );
}

function printDate(strDate) {
    var dDate = new Date(parseInt(strDate)) ;
    var dateOut = dDate.toString() ;
    return dateOut.substring(0, dateOut.length - 24) ;
}

function trimAlbums(min, max, name) {
    $('.selectAlbum').each(function(index) {
        
        if (parseInt($(this).attr('rel'))  < min ) {
           $(this).hide() ;
        } else if (parseInt($(this).attr('rel'))  > max) {
            $(this).hide() ;
        } else if ($(this).text().toUpperCase().indexOf(name.toUpperCase()) == -1) {
            $(this).hide() ;
        } else {
            $(this).show() ;
        }
    });

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
        loadExernals(null, 'Photos?special=RANDOM', 'randPict') ;
    }) ;

    $("#yearsLoader").click(function () {
        loadExernals(null, 'Albums?special=YEARS', 'years') ;
    }) ;

    $("#selectLoader").click(function () {
        loadExernals('selectLoader', 'Albums?special=SELECT', 'select') ;
    }) ;

    $("#googleMapLoader").click(function () {
        $("#googleMapLoader").fadeOut() ;
        $("#mapChoix") ;
        loadMaps();
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
     $("#slider-range").attr("rel", "singlepage[no]");
     
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
         
     $(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});
}

$(function() {
    init_maps()
    init_loader()
})