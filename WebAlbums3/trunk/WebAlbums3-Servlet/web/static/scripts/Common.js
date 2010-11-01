$(".fullscreen").click(function () {
    callURL($(this).attr('rel').trim()) ;
}) ;

$(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});
