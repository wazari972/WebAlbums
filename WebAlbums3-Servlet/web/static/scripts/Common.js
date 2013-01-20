function prepareTagsTooltipsDiv(content) {
    prepareTooltipsDiv(content, "Tags") ;
}
function prepareAlbumsTooltipsDiv(content) {
    prepareTooltipsDiv(content, "Albums") ;
}

function prepareTooltipsDiv(content, what) {
    var targetEl = document.getElementById(content.attr('id')) ;
    if (targetEl.innerHTML == "" || targetEl.innerHTML == null) {
        loadExernals(null, what+"?special=ABOUT&id="+content.attr('rel'), content.attr('id'), null, false) ;
    }
}

function prepareCloudTooltips() {
    $(".cloud-tag").ezpz_tooltip({stayOnContent: true, beforeShow: prepareTagsTooltipsDiv});
}

function loadCloud() {
    if (!staticAccess)
        loadExernals('cloudLoader', 'Tags__Cloud', 'cloud', prepareCloudTooltips) ;
}

function init_photoalbum_size() {
    $("#nbPhotoAlbum").change(function(){
        $.post("Albums?special=PHOTOALBUM_SIZE", 
            {photoAlbumSize : $(this).val()},
            function(data) {
                pleaseReload()
            }
         );
    })
}

function pleaseReload() {
    if (typeof reloadSinglePage != 'undefined') {
        reloadSinglePage()
    } else
        alert("Please reload the page to refresh")
}

function init_fullscreen() {
    $(".fullscreen").click(function () {
        callURL($(this).attr('rel').trim()) ;
    }) ;
}

function init_common() {
    init_photoalbum_size()
    init_fullscreen()
    add_callback("SinglePage", init_fullscreen)
    $(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});

    loadCloud()
}
/******************************************/

function refresh_editionMode() {
    var value = get_editionMode()
    set_editionMode(value)
    $("#mode_edition").text(value)
    if (value == 'VISITE') {
        $(".edit").css('visibility', 'hidden')
        $(".edit_visible").css('visibility', 'hidden')
        callURL("Photos?special=FASTEDIT")
    } else if (value == 'EDITION') {
        //nothing to do
    } else if (value == 'INTENSIVE EDIT') {
        $(".edit").css('visibility', 'visible')
        $(".edit_visible").css('visibility', 'visible')
        $(".optional").css('visibility', 'visible')
    } else
        alert('unknown edition mode value: '+value)
}

function body_mouseenter() {
    if (get_editionMode() == 'EDITION')
        $(this).find(".edit").css('visibility', 'visible')
    $(this).find(".optional").css('visibility', 'visible')
}

function body_mouseleave() {
    var value = get_editionMode()
    if (value == 'INTENSIVE EDIT') 
        return
    
    //check if a 'fastedit_tag_' is currently selected, 
    //see below why
    var limited = false
    try {
        var selected_id = $(document.activeElement).attr("id")
        if (selected_id != undefined) {
            if (selected_id.substring(0, "fastedit_tag_".length) == "fastedit_tag_") {
                limited = true
            }
        }
    } catch (e) {
        alert(e)
    }
    
    $(this).find(".edit").css('visibility', 'hidden')
    //this is a bug I can't understand today,
    //if we click on the 'tag select' and try to go down,
    //this one will make the list disappear!
    if (!limited)
        $(this).find(".optional").css('visibility', 'hidden')
    $(this).find(".edit_visible").css('visibility', 'visible')
}

function do_init_mouse_hover() {
    $(".item").hover(body_mouseenter, body_mouseleave)
    var value = get_editionMode()
    if (value != 'INTENSIVE EDIT') {
        $(".edit").css('visibility', 'hidden')
        $(".optional").css('visibility', 'hidden')
    }
    $(".edit_visible").css('visibility', 'visible')
}

function init_mouse_hover() {
    add_callback("SinglePage", do_init_mouse_hover)
    do_init_mouse_hover()
}


function set_editionMode(value) {
    $.cookie('EDITION_MODE', value, {path: '/'});
}

function get_editionMode() {
    if ($.cookie('EDITION_MODE') == null)
       set_editionMode('EDITION')
    return $.cookie('EDITION_MODE')
}

function toogle_editionMode() {
    var mode = get_editionMode() 
    
    if (mode == 'VISITE') {
        set_editionMode('INTENSIVE EDIT')
    } else if (mode == 'INTENSIVE EDIT') {
        set_editionMode('EDITION')
    } else if (mode == 'EDITION') {
        set_editionMode('VISITE')
    }
        
    refresh_editionMode()
}

function init_edition() {
    refresh_editionMode()

    $("#mode_edition").click(toogle_editionMode) ;

    add_callback("SinglePage", refresh_editionMode)
}

/******************************************/

function activate_qosStars_only() {
    $("#qos_stars_only").click(function(){
        set_qosStars_only($(this).prop("checked"), false)
        refresh_qos_stars()
    })
    
    $("#qos_stars_only").prop("checked", $.cookie('QOS_STARS_ONLY') == "yes")
}

function get_qos_Stars_only() {
    return $.cookie('QOS_STARS_ONLY') == "yes"
}

function set_qosStars_only(only, front_only) {
    $.cookie('QOS_STARS_ONLY', only ? "yes" : null)
    
    if (!front_only) {
        set_qosStars(get_qosStars())
    }
}

function set_qosStars(value, front_only) {
    $.cookie('QOS_STARS', value, {path: '/'});
    
    if (!front_only) {
        var starlevel = (get_qos_Stars_only() ? "-":"")+value

        $.post("Photos?special=FASTEDIT&newStarLevel="+starlevel, null, function() {
            pleaseReload()
        })
    }
}

function get_qosStars() {
    return parseInt($.cookie('QOS_STARS'));
}

function refresh_qos_stars() {
    var stars = get_qosStars()
    var only = get_qos_Stars_only()
    
    for(var i=1; i<=5; i++) {
        if (i <= stars)
            $("#qos_stars_"+i).attr("src", "static/images/star.on.png")
        else
            $("#qos_stars_"+i).attr("src", "static/images/star.off.png")
    }
    
    $(".photo_item").each(function(){
        return
        var c_stars = parseInt($(this).attr("rel"))
        if ((!only && c_stars >= stars)
            ||only && c_stars == stars)
        {
            $(this).show()
        } else {
            $(this).hide()
        }
    })
}

function prepare_qos_stars() {
    for(var i = 1; i <= 5; i++) {
        $("#qos_stars").append("<img rel='"+i+"'id='qos_stars_"+i+"'src='static/images/star.off.png'/>")

        $("#qos_stars_"+i).click(function(){
            set_qosStars($(this).attr("rel"), false)
            refresh_qos_stars()
        })
    }

    var stars = parseInt($("#qos_stars").attr("rel"))
    var only = stars < 0

    set_qosStars_only(only, true)
    set_qosStars(only ? -stars : stars, true)
    activate_qosStars_only()
    refresh_qos_stars()
}

function init_qos_stars() {
    prepare_qos_stars()
    add_callback("SinglePage", refresh_qos_stars)
}

/******************************************/

function set_details(value) {
    $.cookie('EXIF_DETAILS', value, {path: '/'});
}

function get_details() {
    if ($.cookie('EXIF_DETAILS') == null)
       set_details("1")
    
    return parseInt($.cookie('EXIF_DETAILS'));
}

function toogle_details() {
    details = get_details() 
    
    set_details((details+1)%2)
    details = get_details() 
    
    refresh_details()
}

function refresh_details() {
    details = get_details()
    set_details(details)
    if (details == 1) {
        $(".exif_container").each(function(){
            exif_tooltip = $(this).data("exif_tooltip")
            if (exif_tooltip != undefined)
                exif_tooltip.appendTo($(this))
        })
        
        $("#mode_details").text("AVEC")
        $(".exif").show()
    } else {
        $(".exif_tooltip").each(function(){
            $(this).parent().data("exif_tooltip", $(this))
            $(this).detach()
        })
        $("#mode_details").text("SANS")
        $(".exif").hide()
    }
}

function init_details() {
    refresh_details()
    add_callback("SinglePage", refresh_details)
    $("#mode_details").click(toogle_details) ;
}

/**********************************************/
$(function () {
    init_common()
    init_edition()
    init_mouse_hover()
    init_qos_stars()
    init_details()
})