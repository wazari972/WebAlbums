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
                if (loadSinglePage != undefined) {
                    loadSinglePage(getCurrentPage(), true, true)
                } else
                    alert("Please reload the page to refresh")
            }
         );
    })
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
    value = get_editionMode()
    set_editionMode(value)
    $("#mode_edition").text(value)
    if (value == 'VISITE') {
        $(".edit").hide()
        $(".edit_visible").hide()
    } else if (value == 'EDITION') {
        
    } else
        alert('unknown edition mode value: '+value)
}

function body_mouseenter() {
    if (get_editionMode() == 'EDITION')
        $(this).find(".edit").show()
}

function body_mouseleave() {
    $(this).find(".edit").hide()
    $(this).find(".edit_visible").show()
}

function do_init_mouse_hover() {
    $(".details").hover(body_mouseenter, body_mouseleave)
    $(".edit").hide()
    $(".edit_visible").show()
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
    mode = get_editionMode() 
    
    if (mode == 'VISITE') {
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

function get_qos_Stars_only() {
    return $.cookie('QOS_STARS_ONLY') == "yes"
}

function activate_qosStars_only() {
    $("#qos_stars_only").click(function(){
        if ($(this).prop("checked"))
            $.cookie('QOS_STARS_ONLY', "yes")
        else
            $.cookie('QOS_STARS_ONLY', null)
        refresh_qos_stars()
    })
    if ($.cookie('QOS_STARS_ONLY') == null)
        return
    $("#qos_stars_only").prop("checked", true)
}

function set_qosStars(value) {
    $.cookie('QOS_STARS', value, {path: '/'});
}

function get_qosStars() {
    if ($.cookie('QOS_STARS') == null)
       set_qosStars(1)
    return parseInt($.cookie('QOS_STARS'));
}

function refresh_qos_stars() {
    stars = get_qosStars()
    only = get_qos_Stars_only()
    for(var i=1; i<=5; i++) {
        if (i <= stars)
            $("#qos_stars_"+i).attr("src", "static/images/star.on.png")
        else
            $("#qos_stars_"+i).attr("src", "static/images/star.off.png")
    }
    $(".photo_item").each(function(){
        c_stars = parseInt($(this).attr("rel"))
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
    stars = get_qosStars()
    for(var i = 1; i <= 5; i++) {
        $("#qos_stars").append("<img rel='"+i+"'id='qos_stars_"+i+"'src='static/images/star.off.png'/>")

        $("#qos_stars_"+i).click(function(){
            set_qosStars($(this).attr("rel"))
            refresh_qos_stars()
        })
    }
    refresh_qos_stars()
}

function init_qos_starts() {
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
    activate_qosStars_only()
    init_qos_starts()
    init_details()
})