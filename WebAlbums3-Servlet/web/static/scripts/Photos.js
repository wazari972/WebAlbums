function init_massedit() {
    $("#selectAllBt").click(function () {
        selectAll() ;
    }) ;

    //set correctly the action address of the MASSEDIT form
    if (typeof getCurrentPage == 'function') {
        if($("#massEditForm").length != 0) {
            $("#massEditForm").get(0).setAttribute("action", getCurrentPage())
        }
    }

    $("#selectAllBt").click(function () {
        selectAll() ;
    }) ;
}

function reload_page_cb(data, photoid, cb) {
    if (loadSinglePage != undefined) {
        loadSinglePage(getCurrentPage()+"#"+photoid, /*dont_scoll*/true, /*force*/true, /*async*/true)
    } else
        alert("Please reload the page to refresh")
}

function add_rm_tag(photoid, tagact) {
    tagid = $("#fastedit_tag_"+id).val()
    if (!(tagid > 0)) {
        alert("No tag selected ...")
        return
    }
    $.post("Photos?special=FASTEDIT", 
        {id : photoid, tagAction:tagact, tag:tagid},
        function(data) {
            reload_page_cb(data, photoid);
        }
     );
}

function set_stars(photoid, stars) {
    if (stars < 0 || stars > 5) {
        alert("Le nombre d'étoiles doit être compris entre 0 et 5 ("+n+")")
        return
    }
    $("#stars_"+photoid+"_message").text("Settings the stars ...")
    $.post("Photos?special=FASTEDIT", 
        {id : photoid, stars:stars},
        function(data) {
            $("#stars_"+photoid+"_message").text("")
        }
     );
}

function init_fastedit() {
    $(".fastedit_tag_bt").click(function () {
        id = $(this).attr('rel');
        $("#fastedit_div_tag_"+id).toggle("fast")
        $("#fastedit_div_tag_"+id).parent(".edit").toggleClass("edit_visible")
    }) ;
    $(".fastedit_desc_bt").click(function () {
        id = $(this).attr('rel');
        $("#desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).parent(".edit").toggleClass("edit_visible")
    }) ;
    $(".fastedit_stars").click(function () {
        photoid = $(this).attr('rel').split('/')[0];
        stars = $(this).attr('rel').split('/')[1];

        set_stars(photoid, parseInt(stars))
        $(this).prevAll("img").removeClass("star_off").addClass("star_on")
        $(this).removeClass("star_off").addClass("star_on")
        $(this).nextAll("img").removeClass("star_on").addClass("star_off")
        
        
    }) ;

    $(".fastedit_addtag").click(function () {
        photoid = $(this).attr('rel');
        add_rm_tag(photoid, "ADD")
    }) ;

    $(".fastedit_rmtag").click(function () {
        photoid = $(this).attr('rel');
        add_rm_tag(photoid, "RM")
    }) ;

    $(".fastedit_desc").click(function () {
        photoid = $(this).attr('rel');
        photodesc = $("#fastedit_desc_"+photoid).val()

        $.post("Photos?special=FASTEDIT", 
            {id : photoid, desc:photodesc},
            function(data) {
                reload_page_cb(data, photoid);
            }
         );
    }) ;
}

function init_tooltip() {
    $(".exif").ezpz_tooltip({stayOnContent: true});
}

function get_tag_layer(map, do_zoom) {
    var layer = $("#showTagMap").data("layer")
    if (layer == undefined) {
        
        layer = init_tag_layer(map, do_zoom)
        $("#showTagMap").data("layer", layer)
    }
    return layer
}

function init_tag_layer (map, do_zoom) {
    var markers = new OpenLayers.Layer.Markers("Geo Tags");
    map.addLayer(markers);

    $(".tag_visu").each(function() {
        var point = {
            lat: $(this).attr('rel').split('/')[0],
            lng: $(this).attr('rel').split('/')[1],
            name: $(this).text()
        }
        marker = addMarker(map, markers, point, function(x){return x.name})
        $(this).data("marker", marker)
    })
    
    zoomTo(map, markers, do_zoom)
    return markers
}

function init_gpx() {
    var get_map = function() {
        var map = $("#gpx_box").data("map")
        if (map == undefined) {
            $("#gpx_box").show()
            map = init_osm_box("gpx_box")
            $("#gpx_box").data("map", map)
        }
        
        return map
    }
    
    $(".gpx_visu").click(function() {        
        var map = get_map()
        $("#tags_visu").show()
        $("#gpx_box").show()
        layer = $(this).data("layer")
        if (layer == undefined) {
            layer = init_gpx_layer(map, $(this).text(), $(this).attr("rel"))
            $(this).data("layer", layer)
        } else {
            zoomTo(map, layer, false)
        }
        get_tag_layer(map, false)
    })
    
    $("#showTagMap").click(function() {
       $("#tags_visu").toggle()
       $("#gpx_box").toggle()
       var map = get_map()
       get_tag_layer(map, true)
    });
    
    $(".tag_visu").click(function() {
        var map = get_map()
        get_tag_layer(map, true)
        marker = $(this).data("marker")
        map.setCenter(marker.lonlat)
    })
}

$(function() {
    init_tooltip()
    add_callback("SinglePage", init_tooltip)
    init_massedit()
    init_fastedit()
    init_gpx()
})
