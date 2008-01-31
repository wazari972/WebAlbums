$(".fullscreen").click(function () {
    callURL($(this).attr('rel').trim()) ;
}) ;

$(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});

addLoadEvent(loadCloud())

function refresh_editionMode() {
    value = get_editionMode()
    set_editionMode(value)
    
    $("#mode_edition").text(value)
    if (value == 'VISITE') {
        $(".edit").hide()
    } else if (value == 'EDITION') {
        $(".edit").show()
    } else
        alert('unknown edition mode value: '+value)
}

function set_editionMode(value) {
    $.cookie('EDITION_MODE', value, { path: '/' });
}

function get_editionMode() {
    if ($.cookie('EDITION_MODE') == null)
       set_editionMode('VISITE')
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

refresh_editionMode()

$("#mode_edition").click(toogle_editionMode) ;


add_singlePageCallback(refresh_editionMode)