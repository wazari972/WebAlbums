function pointToContent(point) {
    return "<div class='map_content'>"
          +"  <h1><a href='Tag__"+point.id+"__"+"point.name"+"'>"+point.name+"</a></h1>\n"
          +"  <center><img src='Miniature__"+point.picture+".png' /></center>\n"
          +"</div>"
}

AutoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
    'autoSize': true
});

function populateMap(map) {    
    var markers = new OpenLayers.Layer.Markers("Geo Tags");
    map.addLayer(markers);
    
    $.getJSON("Choix?special=MAP&type=JSON",
        function(data) {
            $.each(data, function(key, point) {
                addMarker(map, markers, point, pointToContent)
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

function printDate(strDate) {
    var dDate = new Date(parseInt(strDate)) ;
    var dateOut = dDate.toString() ;
    return dateOut.substring(0, dateOut.length - 24) ;
}

//triggered when SELECT widget is loaded
function do_init_slider(data) {
    $("#fromDate").text(printDate(data.fromDate));
    $("#toDate").text(printDate(data.toDate));
    
    var NOW = new Date().getTime() ;
    var sliderOption = {
      range: true,
      min: data.fromDate,
      max: data.toDate+1,
      step: 100000000,
      values: [data.fromDate, data.toDate],
      stop: function(event, ui) {
          $("#fromDate").text(printDate(ui.values[0]))
          $("#toDate").text(printDate(ui.values[1]))
          //trimAlbums(ui.values[0], ui.values[1], $("#albmName").val())
          return true
      }
     } ;
     
     $("#slider-range").prop("rel", "singlepage[no]");
     $("#slider-range").slider(sliderOption);
     
     $("#albmName").keyup(
        function(){
           trimAlbums($("#slider-range").slider("option", "range", "min"),
                      $("#slider-range").slider("option", "range", "max"),
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