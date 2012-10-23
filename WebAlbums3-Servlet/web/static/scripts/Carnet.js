function convertImage(id, url, alt_text, title) {
    // __ in this function messes up the markdown generation
    // ## will be converted later
    
    return '<center><a href="Image##'+url+'" title="' + alt_text + '">'
         + '<img class="photo" src="Miniature##'+ url + '.png" alt="' + alt_text + '" />'
         + '</a></center>';
}
function convertLink(id, url, title, link_text) {
    var result = "<a href=\"Photos__" + url + "_p0_pa__\"";
    if (title != "")
        result += " title=\"" + title + "\"";
    result += ">" + link_text + "</a>";
    
    return result
}

function finishConvert(converted) {
    
    converted = converted.replace(/Image##/g, "Image__")
    converted = converted.replace(/Miniature##/g, "Miniature__")
    
    return converted
}

function init_markdown() {
    if (document.getElementById("carnet_text") == null)
        return
    
    var converter = Markdown.getSanitizingConverter();
    converter.hooks.chain("plainLinkText", function (url) {
        return "This is a link to " + url.replace(/^https?:\/\//, "");
    });
    converter.hooks.chain("convertImage", convertImage);
    converter.hooks.chain("convertLink", convertLink);
    converter.hooks.chain("postConversion", finishConvert);
    
    var converted = converter.makeHtml($("#carnet_text").text())
    
    //TODO: rewrite with JQuery
    document.getElementById("carnet_text").innerHTML = converted
    if (directAccess) {
        $("#carnet_text a").each(function() {
            var href = $(this).attr("href")
            $(this).attr("href", root_path+photo_folder+carnet_static_lookup[href])
            
        })
        $("#carnet_text img").each(function() {
            var src = $(this).attr("src")
            $(this).attr("src", root_path+mini_folder+carnet_static_lookup[src]+".png")
        })   
    }
}

function init_toc() {
    var toc = $(".carnet_toc")
    //create the list holder in all the carnet_toc divs
    toc.each(function() {
        $(this).append(document.createElement('ol'))
    })
    //mark the first one as special: we don't want to scroll to top from first
    toc.children("ol:eq(0)").addClass("first")
    var ol = toc.children("ol")
    
    //for all the toc lists
    ol.each(function() {
        var thisOl = $(this)
        
        var sommLI = $(document.createElement('li'))
        sommLI.text("Table of Content")
        thisOl.append(sommLI)
        sommLI.addClass("toc")
        
        //create an item and add it to the list
        var toutLI = $(document.createElement('li'))
        toutLI.text("Tout")
        thisOl.append(toutLI)
        toutLI.addClass("all")
        
        toutLI.click(function() {
            ol.each(function() {
                $(this).children().removeClass("current")
            })
            $("#carnet_text").children().show()
            //if we're at the bottom of the chapter, scroll to the top'
            if (!$(this).parent().hasClass("first"))
                window.scrollTo(0, $("#carnet_head").offset().top)
        })
        
        //create an item and add it to the list
        var debutLI = $(document.createElement('li'))
        debutLI.text("Début")
        thisOl.append(debutLI)
        
        var from = $("#carnet_text").children().first();
        debutLI.click(function() {
            //on click, change the class to "current"
            var idx = debutLI.index()
            ol.each(function() {
                $(this).children().removeClass("current")
                $(this).children("li:eq("+idx+")").addClass("current")
            })
            from.nextAll().hide()
            from.nextUntil("h1").show()
            //if we're at the bottom of the chapter, scroll to the top'
            if (!$(this).parent().hasClass("first"))
                window.scrollTo(0, $("#carnet_head").offset().top)
        })
        
        //take all the headers,
        $("#carnet_text").children("h1").each(function() {
            var title = $(this)
        
            //create an item and add it to the list
            var li = $(document.createElement('li'))
            li.text(title.text())
            thisOl.append(li)
        
            li.click(function() {
                //on click, change the class to "current"
                var idx = li.index()
                ol.each(function() {
                    $(this).children().removeClass("current")
                    $(this).children("li:eq("+idx+")").addClass("current")
                })
                //and hide all the irrelevant chapters
                title.prevAll().hide()
                title.show()
                title.nextAll().hide()
                title.nextUntil("h1").show()
                //if we're at the bottom of the chapter, scroll to the top'
                if (!$(this).parent().hasClass("first"))
                    window.scrollTo(0, $("#carnet_head").offset().top)
            })
        })
    })    
}

function init_buttons() {
    $(".fullscreen").click(function() {
        $(".item").css({
            'position': 'absolute',
            'left': "0px",
            'top': "0px",
            'background': '#b2c01d',
            'width':$(document).width()+'px'
        })
        
    })
}

function init_page() {
    init_markdown()
    init_toc()
    init_buttons()
}

$(function() {
    if (get_data_page("carnet_inited")) {
        return
    }
    save_data_page("carnet_inited", true)
    
    init_page()
    add_callback("SinglePage", init_page)
})
