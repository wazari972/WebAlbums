function convertImage(id, url, alt_text, title) {
    // __ in this function messes up the markdown generation
    // ## will be converted later
    return "<br/><center><a href=\"Image##"+url+"\" title=\"" + alt_text + "\">"
         + "<img src=\"Miniature##" + url + ".png\" alt=\"" + alt_text + "\">"
         + "</a></center><br/>";
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
    
    converted = converted.replace("Image\\#\\#", "Image__")
    converted = converted.replace("Miniature##", "Miniature__")
    
    //TODO: rewrite with JQuery
    document.getElementById("carnet_text").innerHTML = converted
}

$(function() {
    init_markdown()
    add_callback("SinglePage", init_markdown)
})
