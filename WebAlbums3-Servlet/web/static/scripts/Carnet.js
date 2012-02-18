function convertImage(id, url, alt_text, title) {
    return "<br/><center><a href=\"Images?id="+url+"&amp;mode=GRAND\" title=\"" + alt_text + "\">"
         + "<img src=\"Images?id=" + url + "&amp;mode=PETIT\" alt=\"" + alt_text + "\""
         + "></a></center><br/>";
}
function convertLink(id, url, title, link_text) {
    var result = "<a href=\"Photos?album=" + url + "\"";
    if (title != "")
        result += " title=\"" + title + "\"";
    result += ">" + link_text + "</a>";
    
    return result
}

function init_markdown() {
    var converter = Markdown.getSanitizingConverter();
    converter.hooks.chain("plainLinkText", function (url) {
        return "This is a link to " + url.replace(/^https?:\/\//, "");
    });
    converter.hooks.chain("convertImage", convertImage);
    converter.hooks.chain("convertLink", convertLink);
    converted = converter.makeHtml($("#carnet_text").text())
    
    //TODO: rewrite with JQuery
    document.getElementById("carnet_text").innerHTML = converted
}

$(function() {
    init_markdown()
})
