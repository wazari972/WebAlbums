var singlePage_cache ={
    "Choix": ""
} ;
var ANCHOR_PREFIX = "#pg=" ;

var displayXsl = null ;
function prepareDisplayXSL() {
    $.ajax({
        url:"static/Display.xsl",
        success:function(html){
            displayXsl = html;
        },
        async:false
    });

}
prepareDisplayXSL() ;

function singlePageCached(url) {
    var cache = singlePage_cache[url] != undefined ;
    return cache ;
}


function getCurrentPage() {
    if (getCurrentSinglePage() != undefined) return getCurrentSinglePage() ;
    else {
        var url = window.location.href ;
        return url.substring(url.lastIndexOf("/")+1) ;
    }
}

function getCurrentSinglePage() {
    var pos = window.location.hash.indexOf(ANCHOR_PREFIX) ;
    if (pos != -1) {
        return window.location.hash.substring(ANCHOR_PREFIX.length) ;
    } else return undefined ;
}

function loadSinglePage(url) {
    var oldURL = getCurrentPage() ;
    
    if (singlePageCached(oldURL)) {
        var left = document.getElementById ("left") ;
        var cache = document.getElementById(oldURL) ;
        if (cache == undefined) {
            //create the cache
            cache = document.createElement("div") ;
            cache.id = oldURL ;
            left.parentNode.insertBefore(cache, left) ;
        }   
        //cache.innerHTML = document.getElementById ("left").innerHTML ;

        //save the cache
        left.style.display = "none" ;
        left.id = cache.id ;
        
        //recreate a "left" block
        cache.id = "left" ;
        cache.innerHTML = "" ;
        cache.style.display = '' ;
    }

    $.ajax({
        url:url,
        success:function(data){
            loadSinglePageBottomEnd(url, data) ;
        },
        async:true
    });
}

function Node_getElementById(node, id) {
    for (var i= 0; i<node.childNodes.length; i++) {
        var child= node.childNodes[i];
        if (child.nodeType!==1) // ELEMENT_NODE
            continue;
        if (child.id===id)
            return child;
        child= Node_getElementById(child, id);
        if (child!==null)
            return child;
    }
    return null;
}

function loadSinglePageCache(url) {
    var cache = document.getElementById(url)  ;
    if (cache == undefined) {
        loadSinglePage(url, false) ;
    } else {
        var left = document.getElementById ("left");
        left.parentNode.removeChild(left) ;
        cache.style.display = "block" ;
        cache.id = "left" ;
    }
}

function loadSinglePageBottomEnd(url, data) {
    var xml_doc = data;

    var left = document.getElementById ("left");
    if (left == null) return ;

    left.style.display = "" ;
    left.innerHTML = "";

    // Use object detection to find out if we have
    // Firefox/Mozilla/Opera or IE XSLT support.
    if (typeof XSLTProcessor != "undefined") {
        var xsl_proc = new XSLTProcessor ();
        xsl_proc.importStylesheet (displayXsl);

        var node = xsl_proc.transformToFragment (xml_doc, document);
        var page = Node_getElementById(node, "left") ;
        var parent = left.parentNode ;
        parent.insertBefore(page, left) ;
        parent.removeChild(left) ;
    } else if (typeof xml_doc.transformNode != "undefined") {
        left.innerHTML = xml_doc.transformNode (displayXsl);
    } else {
        left.innerHTML = xhr_object_XML.responseText ;
    }

    enableSinglePage() ;
}

function loadBookmarkedSinglePage() {
    var currentSinglePage = getCurrentSinglePage() ;
    if (currentSinglePage != undefined) {
        loadSinglePage(currentSinglePage) ;
    }
}
function enableSinglePage() {
    $('a').each(function(index) {
        if ($(this).attr("SinglePaged")) return ;
        $(this).attr("SinglePaged", true) ;

        if ($(this).attr("href") == undefined) return ;
        if ($(this).attr("href").indexOf("javascript") == 0) return ;
        if ($(this).attr("rel").indexOf("shadowbox") == 0) return ;
        if ($(this).attr("rel").indexOf("singlepage[no]") == 0) return ;
    
        $(this).click(function() {
            var url = $(this).attr("href") ;
            if (singlePageCached(url)) {
                loadSinglePageCache(url) ;
            } else {
                loadSinglePage(url) ;
            }

            document.location.href = jQuery.trim(ANCHOR_PREFIX+url);
            return false ;
        }) ;
    });
}

enableSinglePage() ;
loadBookmarkedSinglePage() ;