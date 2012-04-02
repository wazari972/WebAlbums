function convertImage(id, url, alt_text, title) {
    // __ in this function messes up the markdown generation
    // ## will be converted later
    
    return '<br/><center><a href="'+url+'" title="' + alt_text + '">'
         + '<img src="'+ url + '" alt="' + alt_text + '" />'
         + '</a></center><br/>';
}
function convertLink(id, url, title, link_text) {
    var result = "<a href=\"Photos__" + url + "_p0_pa__\"";
    if (title != "")
        result += " title=\"" + title + "\"";
    result += ">" + link_text + "</a>";
    
    return result
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
    converted = converter.makeHtml($("#carnet_text").text())
    
    converted = converted.replace("Image##", "Image__")
    converted = converted.replace("Miniature##", "Miniature__")
    
    //TODO: rewrite with JQuery
    document.getElementById("carnet_text").innerHTML = converted
    $("#carnet_text a").each(function() {
        href = $(this).attr("href")
        if (href.indexOf("Photos") == -1) {
            if (!directAccess)
            $(this).attr("href", "Images__"+href)
        else
            $(this).attr("href", root_path+photo_folder+carnet_static_lookup[href])
        }
    })
    
    $("#carnet_text img").each(function() {
        src = $(this).attr("src")
        if (!directAccess)
            $(this).attr("src", "Miniature__"+src+".png")
        else
            $(this).attr("src", root_path+mini_folder+carnet_static_lookup[src]+".png")
    })
}

function init_toc() {
    toc = $("#carnet_toc")
    ol = $(document.createElement('ol'))
    toc.append(ol)
    $("#carnet_text h1").each(function() {
        li = $(document.createElement('li'))
        ol.append(li)
        li.text($(this).text())
        title = $(this)
        li.click(function() {
            window.scrollTo(0, (title.offset().top))
        })
    })
    
    
}

function init_page() {
    init_markdown()
    init_toc()
}

$(function() {
    if (get_data_page("carcnet_inited"))
        return
    save_data_page("carcnet_inited", true)
    
    init_page()
    add_callback("SinglePage", init_page)
})
