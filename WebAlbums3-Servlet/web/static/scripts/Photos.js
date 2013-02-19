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

function set_tags(photoid) {
    var tags = $("#fastedit_tag_"+photoid)
    var target = tags.parents(".options").find(".tags")
    var to_click = tags.parents(".options").find(".fastedit_tag_bt")
    
    var index = $('.fastedit_tag_bt').index(to_click);
    var to_click_2nd = $('.fastedit_tag_bt').slice(index+1,index+2);
    
    $.post("Photos?special=FASTEDIT&tagAction=SET&"+tags.serialize(), 
        {id : photoid},
        function() {
            // need to ensure that it's OK ...
            var newHtml = ""
            $("#fastedit_tag_"+photoid+" option:selected").each(function() {
                newHtml += $(this).text() + " "
            })
            target.html(newHtml)
            to_click.click()
            
            if (get_editionMode() == 'INTENSIVE EDIT') {
                to_click_2nd.click()
            }
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
        var id = $(this).attr('rel');
        var div_fast_tag = $("#fastedit_div_tag_"+id)
        div_fast_tag.toggle("fast")
        div_fast_tag.parents(".options").children(".tags").toggle("fast")
        div_fast_tag.parents(".edit").toggleClass("edit_visible")
        div_fast_tag.find("input").focus()
        
    }) ;
    $(".fastedit_desc_bt").click(function () {
        var id = $(this).attr('rel');
        $("#desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).parents(".edit").toggleClass("edit_visible")
    }) ;
    $(".fastedit_stars").click(function () {
        var photoid = $(this).attr('rel').split('/')[0];
        var stars = $(this).attr('rel').split('/')[1];

        set_stars(photoid, parseInt(stars))
        $(this).prevAll("img").removeClass("star_off").addClass("star_on")
        $(this).removeClass("star_off").addClass("star_on")
        $(this).nextAll("img").removeClass("star_on").addClass("star_off")
    }) ;

    $(".fastedit_settags").click(function () {
        var photoid = $(this).attr('rel');
        
        set_tags(photoid)
    })

    $(".fastedit_desc").click(function () {
        var photoid = $(this).attr('rel');
        var desc = $("#fastedit_desc_"+photoid)
        var photodesc = desc.val()
        var target = desc.parents(".options").find(".description")
        var to_click = desc.parents(".options").find(".fastedit_desc_bt")
        
        $.post("Photos?special=FASTEDIT", 
            {id : photoid, desc:photodesc},
            function() {
                var lines  = photodesc.split("\n")
                var i;
                
                target.text("")
                for (i = 0; i < lines.length; ++i) {
                    target.append("<p>"+lines[i]+"</p>")
                }                
                to_click.click()
            }
         );
    }) ;
    $(".details").each(function() {
        var list = $(this).find(".fastedit_tag")
        $(this).find(".tag_link").each(function() {
            list.find("option[value = '"+$(this).attr("rel")+"']").attr("selected", "true")
        })
    })
    $(".fastedit_tag").chosen();

    $("#massTagList").chosen();   
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
        
        var marker = addMarker(map, markers, point, function(x){return x.name}, 
                                                    json_point_to_lonlat(point))
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
        var layer = $(this).data("layer")
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
        var marker = $(this).data("marker")
        map.setCenter(marker.lonlat)
    })
}

$(function() {
    init_tooltip()
    init_massedit()
    init_fastedit()
    init_gpx()
})
