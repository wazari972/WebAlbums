var current = null;
function nextprev(do_prev, preload) {
    if (do_prev === undefined)
        do_prev = false;
    
    var img_lnk;
    var id;
    //page just get loaded
    if (current === null) {
        if (window.location.hash === "") {
            img_lnk = $(".visio_img:first");
        } else {
            id = ""+window.location.hash.substring(1)+"";
            
            if (id === "last")
                img_lnk = $(".visio_img:last");
            else if (id === "first")
                img_lnk = $(".visio_img:first");
            else
                img_lnk = $(".visio_img[rel = '"+id+"']");
        }
    } else {
        var found = false;
        var found_on_prev = false;
        var img_next = undefined;
        var img_prev = undefined;
        $(".visio_img").each(function() {
            if (found_on_prev) {
                img_next = $(this);
                found_on_prev = false;
            }
            
            if ($(this).attr("rel") === current.attr("rel")) {
                found = true;
                found_on_prev = true;
            }
            
            if (!found)
                img_prev = $(this);
        });
        
        img_lnk = do_prev ? img_prev : img_next;
        
        if (img_lnk === undefined) {
            if (preload)
                return;
            
            img_lnk = do_prev ? $(".page_previ") : $(".page_nexti");
            
            if (img_lnk.attr("href") === undefined)
                alert("no more page!");
            else 
                window.location = img_lnk.attr("href") + (do_prev ? "#last" : "#first");
            
            return;
        }
    }
    
    var target = img_lnk.first();
    
    if (!preload) {
        target.click();
        current = img_lnk;
    } else {
        doPreload(target.prop("href"));
    }
}

function doPreload(href) {
    var img = new Image();
    img.src = href;
    //alert("preload "+href);
}

function updateFullImage(href, id) {
    $("body").css("cursor", "wait");
    
    $('#largeImg').prop("src", href)
                  .css("max-width", $("body").width())
                  .css("max-height", $("body").height()).load(function() {
          $("body").css("cursor", "auto");
          window.location.hash = id;
    });
    
}
function init_visio () {
    $(".visio_img").click(function () {
        current = $(this);
        updateFullImage($(this).prop("href"), $(this).attr("rel"));
        return false;
    });
    $("#visio_preview").hide();
    document.body.style.overflow = 'hidden';
    nextprev();
    
    jwerty.key('s/n/→/↓', function () {nextprev(false); nextprev(false, true);});
    jwerty.key('p/←/↑', function () {nextprev(true); nextprev(true, true);});
    
    jwerty.key('f', function(){toggleFullScreen();});
}

$(function() {
    init_visio();
});