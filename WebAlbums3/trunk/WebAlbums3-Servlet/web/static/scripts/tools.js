

function loadMaps() {
    var script = document.createElement("script");
    script.setAttribute("src", "http://maps.google.com/maps/api/js?sensor=false&callback=loadGoogleMapWrapper");
    script.setAttribute("type", "text/javascript");
    document.documentElement.firstChild.appendChild(script);
}

function loadGoogleMapWrapper() {
    loadGoogleMap() ;
    if (enableSinglePage != undefined) {
        setTimeout("enableSinglePage() ;", 3000);
    }
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
    loadExernals('cloudLoader', 'Tags?special=CLOUD', 'cloud', prepareCloudTooltips) ;
}

function callURL(url) {
    $.get(url);
}


var emptyXsl = null ;
function prepareEmptyXSL() {
    $.ajax({
      url:"static/Empty.xsl",
      success:function(html){emptyXsl = html;},
      async:false
     });

}
prepareEmptyXSL() ;


function loadExernals(btId, url, divId, callback, async) {
    if (async == undefined) {
        async = true ;
    }
    var bt = document.getElementById(btId) ;
    if (bt != null) bt.style.visibility = "hidden";

    $.ajax({
        url:url,
        success:function(data){
            loadExernalsBottomEnd(data, divId, callback) ;
        },
        async:async
    });
}


function loadExernalsBottomEnd(data, divId, callback) {
    var xml_doc = data;

    var div = document.getElementById (divId);
    if (div == null) return ;

    $(div).hide() ;
    div.innerHTML = "";
    // Use object detection to find out if we have
    // Firefox/Mozilla/Opera or IE XSLT support.
    if (typeof XSLTProcessor != "undefined") {
        var xsl_proc = new XSLTProcessor ();
        xsl_proc.importStylesheet (emptyXsl);

        var node = xsl_proc.transformToFragment (xml_doc, document);
        div.appendChild (node);
    } else if (typeof xml_doc.transformNode != "undefined") {
        div.innerHTML = xml_doc.transformNode (emptyXsl);
    } else {
        div.innerHTML = xhr_object_XML.responseText ;
    }

    $(div).fadeIn() ;
    if (enableSinglePage != undefined) enableSinglePage() ;
    if (callback != undefined) callback() ;
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
function prepareTagsTooltipsDiv(content) {
    prepareTooltipsDiv(content, "Tags") ;
}
function prepareAlbumsTooltipsDiv(content) {
    prepareTooltipsDiv(content, "Albums") ;
}

function prepareTooltipsDiv(content, what) {
    targetEl = document.getElementById(content.attr('id')) ;
    if (targetEl.innerHTML == "" || targetEl.innerHTML == null) {
        loadExernals(null, what+"?special=ABOUT&id="+content.attr('rel'), content.attr('id'), null, false) ;
    }
}

function prepareCloudTooltips() {
    $(".cloud-tag").ezpz_tooltip({stayOnContent: true,beforeShow: prepareTagsTooltipsDiv});
}

addLoadEvent(loadCloud())