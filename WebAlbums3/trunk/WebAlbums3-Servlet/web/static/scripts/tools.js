

function loadMaps() {
    var script = document.createElement("script");
    script.setAttribute("src", "http://maps.google.com/maps/api/js?sensor=false&callback=loadGoogleMap");
    script.setAttribute("type", "text/javascript");
    document.documentElement.firstChild.appendChild(script);
}


function pleaseConfirm(form) {
    if (confirm("Really ?")) {
        document.getElementById(form).submit() ;
    }
}

function updateAffichage(option) {
    if (option == 'edition') {
        callURL('Other/Display?action=NEXT_EDITION');
    } else if (option == 'maps') {
        alert("not implemented");
    } else if (option == 'details') {
        callURL('Other/Display?action=SWAP_DETAILS');
    } else {
        alert("Unknown option...");
    }
    window.location.reload();
}

function loadCloud() {
    loadExernals('cloudLoader', 'Tags?special=CLOUD', 'cloud') ;
}

function callURL(url) {
    $.get(url);
}


var xsl_doc = null ;
function prepareXSL() {
    $.ajax({
      url:"static/Empty.xsl",
      success:function(html){xsl_doc = html;},
      async:false
     });

}
prepareXSL() ;

function loadExernals(btId, url, divId) {

    var bt = document.getElementById(btId) ;
    if (bt != null) bt.style.visibility = "hidden";

    $.get(url, function(data){
        loadExernalsBottomEnd(data, divId) ;
    }) ;
}


function loadExernalsBottomEnd(data, divId) {
    var xml_doc = data;

    var div = document.getElementById (divId);
    if (div == null) return ;

    div.style.display = "" ;
    div.innerHTML = "";
    // Use object detection to find out if we have
    // Firefox/Mozilla/Opera or IE XSLT support.
    if (typeof XSLTProcessor != "undefined") {
        var xsl_proc = new XSLTProcessor ();
        xsl_proc.importStylesheet (xsl_doc);

        var node = xsl_proc.transformToFragment (xml_doc, document);
        div.appendChild (node);
    } else if (typeof xml_doc.transformNode != "undefined") {
        div.innerHTML = xml_doc.transformNode (xsl_doc);
    } else {
        div.innerHTML = xhr_object_XML.responseText ;
    }
}

function updateFullImage(id) {
    var img = document.getElementById("largeImg");
    img.src = "Images?id="+id+"&mode=GRAND" ;
}

function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            if (oldonload) {
                oldonload();
            }
            func();
        }
    }
}
function updateBackground(id) {
    document.getElementById("body").style.backgroundImage = "url(Images?id="+id+"&mode=SHRINK&width=1280)"
}

$(".fullscreen").click(function () {
    callURL($(this).attr('rel').trim()) ;
}) ;

addLoadEvent(loadCloud())