var singlePage_cache ={
    "Choix": ""
} ;
var ANCHOR_PREFIX = "#pg=" ;

var inPlaceSinglePage = null;
var inPlaceSinglePage_lock = null;

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
        if (left != null)
            left.parentNode.removeChild(left) ;
        $(cache).show() ;
        cache.id = "left" ;
    }
}

function loadSinglePage(url) {
    inPlaceSinglePage_lock = 1 ;
    var oldURL = getCurrentPage() ;

    var left  ;
    if (singlePageCached(oldURL)) {
        left = document.getElementById ("left") ;
        var cache = document.getElementById(oldURL) ;
        if (cache == undefined) {
            //create the cache
            cache = document.createElement("div") ;
            cache.id = oldURL ;
            left.parentNode.insertBefore(cache, left) ;
        }   
        //cache.innerHTML = document.getElementById ("left").innerHTML ;

        //save the cache
        $(left).hide() ;
        left.id = cache.id ;
        
        //recreate a "left" block
        cache.id = "left" ;
        cache.innerHTML = "" ;
    }
    
    if (url == inPlaceSinglePage) {
        return ;
    }
    inPlaceSinglePage = url ;
    if (url != undefined) {
        inPlaceSinglePage = jQuery.trim(inPlaceSinglePage) ;
    }
    //left = document.getElementById ("left")
    //$(left).hide() ;
    //left.id = "left-loading" ;
    
    var loader = null ;
    //loader = document.getElementById ("loader") ;
    if (loader != null) {
        loader.id = "left" ;
        $(loader).fadeIn() ;
    } else {
        //alert("No loader to show") ;
    }
    $.ajax({
        url:url,
        success:function(data){
            loadSinglePageBottomEnd(data) ;
        },
        async:true
    });
}

function loadSinglePageBottomEnd(data) {
    var xml_doc = data;

    var left = document.getElementById ("left-loading");
    if (left == null) {
        left = document.getElementById ("left");
        if (left == null) return ;
    }

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
    } else {
        alert("underfined XSLT processor") ;
    }
    var loader = document.getElementById ("left") ;
    if (loader != null) {
        loader.id = "loader" ;
        $(loader).hide() ;
    } else {
        //alert("No loader to hide")
    }

    left.id = "left";
    $(left).fadeIn() ;
    enableSinglePage() ;
    inPlaceSinglePage_lock = null ;
    $(window).scrollTop(0) ;
}

function checkSinglePageAnchor() {
    var currentSinglePage = getCurrentSinglePage() ;
    if (inPlaceSinglePage_lock != null) return ;
    if (currentSinglePage != inPlaceSinglePage) {
        //alert("previous/next page detected "+inPlaceSinglePage+" -> "+ currentSinglePage)
        loadSinglePage(currentSinglePage) ;
    }
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
        if ($(this).attr("href").indexOf("#") == 0) return ;
        if ($(this).attr("rel") != undefined &&
            $(this).attr("rel").indexOf("shadowbox") == 0) return ;


        parents = $(this).parent("div") ;
        for (var i = -1; i < parents.length; i++) {

            if (i == -1) {
                parent = $(this) ;
            } else {
                parent = parents.get(i) ;
            }

            if (parent.attr != undefined &&
                parent.attr("rel") != undefined &&
                parent.attr("rel").indexOf("singlepage[no]") == 0) {
                return ;
            }
        }
        
        $(this).click(function() {
            var url = jQuery.trim($(this).attr("href")) ;
            if (singlePageCached(url)) {
                loadSinglePageCache(url) ;
            } else {
                loadSinglePage(url) ;
            }
            document.location.href = ANCHOR_PREFIX+url;
            return false ;
        }) ;
    });
}

enableSinglePage() ;
loadBookmarkedSinglePage() ;


$().ready(function(){
   setInterval("checkSinglePageAnchor()", 300);
});