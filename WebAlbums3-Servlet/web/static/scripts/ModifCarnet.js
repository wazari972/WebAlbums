function saveCarnet(silent) {
    if (silent == undefined)
        silent = false
    
    id = getParameterByName("carnet")
    
    $.post("Carnets?action=SAVE", 
        {carnet : id,
         nom : $("#carnetNom").val(),
         date : $("#carnetDate").val(),
         carnetRepr : $("#carnetRepr").val(),
         carnetPhoto : $("#carnetPhoto").val(),
         carnetAlbum : $("#carnetAlbum").val(),
         user: $("#carnetUser").val(),
         desc: $("#carnetDesc").val(),
         carnetText: $("#wmd-input").val()
        },
        function(xml) {
            xml = $(xml)
            
            setTimeout(function(){$(".carnetSave").val("Enregistrer")}, 5000);
            if (xml.find("exception").text() != "") {
                if (!silent)
                    alert(">"+xml.find("exception").text()+"<")
                $(".carnetSave").val("Error...")
            }
            
            $(".carnetSave").val("Saved!")
        }
     );
}

autosave_timer = undefined
function toggleAutoSaveCarnet() {
    if ($(this).attr("checked") == "checked") {
        $(".carnetAutoSave").attr("checked", "checked")
        if (autosave_timer == undefined) {
            autosave_timer = setInterval(saveCarnet, 5*60*1000)
        }
    } else {
        $(".carnetAutoSave").removeAttr("checked")
        if (autosave_timer != undefined) {
            clearInterval(autosave_timer)
        }
    }
}

function init_buttons() {
    $(".carnetAutoSave").click(toggleAutoSaveCarnet)
    
    $(".carnetSave").click(saveCarnet) ;

    $("#carnetRepr").change(function () {
        $("#carnetReprImg").attr("src", "Images?id="+$(this).val()+"&amp;mode=PETIT)");
    }) ;
}

function beginConvert() {
    $("#carnetPhoto").val("")
}
function convertImage(id, url, alt_text, title) {
    
    if (isNaN(url))
        return "<strong>Image #"+id+" is invalide Image id (not a number)</strong>"
    $("#carnetPhoto").val($("#carnetPhoto").val()+"-"+url)
    
    return "<br/><center><a href=\"Images?id="+url+"&amp;mode=GRAND\" title=\"" + alt_text + "\">"
         + "<img src=\"Images?id=" + url + "&amp;mode=PETIT\" alt=\"" + alt_text + "\""
         + "></a></center><br/>";
}

function convertLink(id, url, title, link_text) {
    if (isNaN(url))
        return "<strong>Link #"+id+" is not a valide Album id (not a number)</strong>"
    
    var result = "<a href=\"Photos?album=" + url + "\"";
    if (title != "")
        result += " title=\"" + title + "\"";
    result += ">" + link_text + "</a>";
    
    return result
}

function init_markdown_edit() {
    var help = function () {alert("Exemple:\n\
Gros titre\n\
==========\n\
Sous-titre\n\
----------\n\
\n\
Texte en **gras** ou en *italique*.\n\
\n\
Une photo: ![et sa description][1]\n\
Un trait de séparation:\n\
\n\
--------\n\
Un lien [vers un album][2].\n\
\n\
Une liste:\n\
\n\
* 1er point\n\
* 2eme point\n\
\n\
et une liste ordonnée:\n\
\n\
1. first\n\
2. second\n\
\n\
Une citation:\n\
\n\
> voilà la citation\n\
\n\
ou du `code en ligne` ou en block:\n\
\n\
    ici\n\
\n\
\n\
  [1]: 1007\n\
  [2]: 25\n\
");}

    var converter = Markdown.getSanitizingConverter();

    converter.hooks.chain("plainLinkText", function (url) {
        return "This is a link to " + url.replace(/^https?:\/\//, "");
    });

    converter.hooks.chain("convertImage", convertImage);

    converter.hooks.chain("convertLink", convertLink);

    converter.hooks.chain("beginConvert", beginConvert);
    
    var editor = new Markdown.Editor(converter, "", {handler: help});
    editor.run();
}

$(function() {
    init_buttons()
    init_markdown_edit()
})

