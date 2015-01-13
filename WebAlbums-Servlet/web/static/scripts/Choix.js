function populateChoixMap(map) {        
    var xmlCloud;
                
    $.ajax( {
        async: false,
        type: "GET",
        url: "Tags?special=CLOUD",
        dataType: "xml",
        success: 
          function(data) {
            xmlCloud = $(data);
        }});
    
    populateMapFromJSON("Choix_map.json", map, xmlCloud);
    
}

function createGpxesMap() {
    var map = loadMap("gpxChoix");
    $("#gpxChoix").data("map", map);
    
    $(".gpxTrack").each(function (index) {
        $(this).data("ready", false);
        var label = $(this).text();
        if (label === "") {
            label = "Track "+index;
            $(this).text(label);
        }
        
        $(this).data("label", label);
    });

    $(".gpxTrack").click(function() {
        var this_track = $(this); /*because ready_callback loose 'this' */
        
        var do_show = function() {
            map = $("#gpxChoix").data("map");
            layer.setVisibility(true);
            zoomTo(map, layer, false);
            this_track.text("+ " + this_track.data("label"));
        };
        if (!$(this).data("ready")) {
            var ready_callback = function() {
                this_track.data("ready", true);
                do_show();
            };
            var layer = init_gpx_layer(map, $(this).text(), $(this).attr("rel"), ready_callback);
            $(this).data("layer", layer);

            $(this).text($(this).data("label")+" ...");
            return;
        }
        var layer = $(this).data("layer");
        if (layer.getVisibility()) {
            layer.setVisibility(false);
            $(this).text($(this).data("label"));
        } else {
            do_show();
        }
    });
    
    $("#gpxSelection select").chosen();
    $("#gpxSelection select").change(
            function() {
                $("#gpxSelection select option").each(function() {
                    var show = $(this).attr('selected') === 'selected';
                    
                    if (show) 
                        $("#gpxSelection p." + $(this).attr("value")).show();
                    else
                        $("#gpxSelection p." + $(this).attr("value")).hide();
                });
            }
            );
}

var map;
function init_loader() {
    $("#albumsLoader").click(function () {
        loadExernals('albumsLoader', 'Albums?special=TOP5', 'albums');
    }) ;


    $("#carnetsLoader").click(function () {
        loadExernals('carnetsLoader', 'Carnets?special=TOP5', 'carnets');
    }) ;

    $("#personsLoader").click(function () {
        loadExernals('personsLoader', 'Tags?special=PERSONS', 'persons');
    }) ;

    $("#placesLoader").click(function () {
        loadExernals('placesLoader', 'Tags?special=PLACES', 'places');
    }) ;
    
    $("#timeagoLoader").click(function () {
        loadExernals('timeagoLoader', 'Albums?special=AGO', 'timesago');
    }) ;
    $("#alltimeagoLoader").click(function () {
        loadExernals('alltimeagoLoader', 'Albums?special=AGO&all=y', 'timesago');
        $("#timeagoLoader").hide();
    }) ;

    $("#tagShower").click(function () {
        $("#tagShower").fadeOut();
        $("#tags").fadeIn();
    }) ;

    $("#randPictLoader").click(function () {
        function refresh() {
            refresh_details();
            refresh_editionMode();
        }
        loadExernals(null, 'Photos?special=RANDOM', 'randPict', refresh);
    }) ;

    $("#yearsLoader").click(function () {
        loadExernals(null, 'Albums?special=YEARS', 'years');
    }) ;

    $("#selectLoader").click(function () {        
        loadExernals('selectLoader', 'Albums?special=SELECT', 'select');
    }) ;
    
    $("#gpsLoader").click(function () {        
        loadExernals('gpsLoader', 'Albums?special=GPX', 'gpsChoix', createGpxesMap);
    }) ;
    
    $("#tagGraphLoader").click(function () {
        var data = $("#tagChoix").serialize();
        loadExernals(null, 'Albums?special=GRAPH&'+data, 'tagGraph', draw_graph, true, data);
    }) ;

    $("#mapLoader").click(function () {
        $(this).fadeOut();
        $("#theMapChoix").addClass("mapChoix");
        var map = loadMap("theMapChoix");
        $("#theMapChoix").data("map", map);
        $("body").css("cursor", "wait");
        populateChoixMap(map);
    }) ;
}

function trimAlbums(name) {
    $('.selectAlbum').each(function() {
        if ($(this).text().toUpperCase().indexOf(name.toUpperCase()) === -1) {
            $(this).hide();
        } else {
            $(this).show();
        }
    });
}

function printDate(strDate) {
    var dDate = new Date(parseInt(strDate));
    var dateOut = dDate.toString();
    return dateOut.substring(0, dateOut.length - 24);
}

//triggered when SELECT widget is loaded
function init_selecter() {
     $("#albmName").keyup(
        function() {
           trimAlbums($(this).val());
        }
     );
     if (!staticAccess)
        $(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});
}

function save_theme() {
    var themeId = getParameterByName("themeId");
    if (themeId === "") {
        return;
    }
    
    $.cookie("themeId", themeId);
}

/****************************************************/

function draw_graph() {
    ykeys_len = my_ykeys.length;
    data_len = graphData.length;
    for (_j = 0; _j < ykeys_len; _j++) {
        ykey = my_ykeys[_j];
        prev = 0;
        for (_i = data_len-1; _i >= 0; _i--) {
            graphData[_i][ykey] += prev;
            prev = graphData[_i][ykey];
        }
    }
    
    my_labels = $.map(my_ykeys, function (d) {
        if (d === "album")
            return "Albums";
        else
            return d.split("__")[2];
    });

    Morris.Line({
      element: 'graph',
      data: graphData,
      xkey: 'q',
      ykeys: my_ykeys,
      labels: my_labels
    });
}

$(function() {
    save_theme();
    init_loader();
    $("#tagChoix").chosen();
});