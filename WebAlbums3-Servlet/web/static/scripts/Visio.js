current = null
function nextprev(do_prev) {
    if (do_prev == undefined)
        do_prev = false
    //page just get loaded
    if (current == null) {
        if (window.location.hash == "") {
            img_lnk = $(".visio_img:first")
        } else {
            id = ""+window.location.hash.substring(1)+""
            img_lnk = $(".visio_img[rel = '"+id+"']")
        }
    } else {
        found = false
        found_on_prev = false
        img_next = undefined
        img_prev = undefined
        $(".visio_img").each(function() {
            if (found_on_prev) {
                img_next = $(this)
                found_on_prev = false
            }
            
            if ($(this).attr("rel") == current.attr("rel")) {
                found = true
                found_on_prev = true
            }
            
            if (!found)
                img_prev = $(this)
        })
        
        img_lnk = do_prev ? img_prev : img_next
        
        if (img_lnk == undefined) {
            if (do_prev)
                img_lnk = $(".page_previ")
            else
                img_lnk = $(".page_nexti")
            
            if (img_lnk.attr("href") == undefined) {
                alert("no more page!")
                return
            }
                
            window.location = img_lnk.attr("href")
            return
        }
    }
    img_lnk.first().click()
    current = img_lnk
}
 
 
function updateFullImage(id) {
    $("body").css("cursor", "wait");
    img = "Image__"+id ;
    
    imgTag = $('#largeImg').prop("src", img)
        .css("max-width", $("body").width())
        .css("max-height", $("body").height()).load(function() {  
          $("body").css("cursor", "auto")
        })
    
    
}
function init_visio () {
    $(".visio_img").click(function () {
        current = $(this)
        updateFullImage($(this).prop("rel"))
        return false
    })
    $("#visio_preview").hide()
    document.body.style.overflow = 'hidden';
    nextprev()
    
    jwerty.key('s/n/→/↓', function () { nextprev()});
    jwerty.key('p/←/↑', function () {nextprev(true)});
    
}

$(function() {
    init_visio()
})