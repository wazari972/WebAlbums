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

function loadSinglePage(url) {
    
    if (url == undefined) {
        url = getCurrentPage() ;
        var pos = url.indexOf('?') ;
        if (pos != -1) {
            url = url.substring(0, pos) ;
        }

    }
    inPlaceSinglePage_lock = 1 ;

    if (url == inPlaceSinglePage) {
        return ;
    }

    var oldURL = getCurrentPage() ;
    
    var left = document.getElementById ("left") ;
    if (left == null) {
        alert("No left block available, please reload the page")
        return ;
    }
    //save the left block if necessary
    if (singlePageCached(oldURL)) {
        var cacheTmp = document.getElementById("cacheTmp") ;
        if (cacheTmp == undefined) {
            cacheTmp = document.createElement("div") ;
            cacheTmp.id = "cacheTmp" ;
            left.parentNode.insertBefore(cacheTmp, left) ;
        }
        //duplicate left into cacheTmp
        cacheTmp.innerHTML = left.innerHTML ;
        cacheTmp.id = "left" ;
        //save the old left as old url cache
        left.id = oldURL ;
    }
    document.location.href = ANCHOR_PREFIX+url;
    inPlaceSinglePage = $.trim(url) ;
    
    if (singlePageCached(url)) {
        var cache = document.getElementById(url) ;
        if (cache != undefined) {
            $(left).hide() ;
            left.id = "dromadaire" ;
            cache.id = 'left' ;
            $(cache).show() ;
            $("#dromadaire").remove() ;
            
            return ;
        }
    }
    $("body").css("cursor", "progress");
    $.ajax({
        url:url,
        success:function(data){
            $("body").css("cursor", "auto");
            loadSinglePageBottomEnd(data) ;
        },
        async:true
    });
}

function loadSinglePageBottomEnd(data) {
    var xml_doc = data;

    var left = document.getElementById ("left");
    if (left == null) {
        alert("could not locate the left div ...")
        return ;
    }
    
    // Use object detection to find out if we have
    // Firefox/Mozilla/Opera or IE XSLT support.
    if (typeof XSLTProcessor != "undefined") {
        var xsl_proc = new XSLTProcessor ();
        xsl_proc.importStylesheet (displayXsl);

        var newPage = xsl_proc.transformToFragment (xml_doc, document);
        var newLeft = Node_getElementById(newPage, "left") ;
        var parent = left.parentNode ;
        parent.insertBefore(newLeft, left) ;
        parent.removeChild(left) ;
        left = newLeft ;
    } else {
        alert("underfined XSLT processor") ;
    }
    
    $(left).fadeIn() ;
    enableSinglePage() ;
    inPlaceSinglePage_lock = null ;
    $(window).scrollTop(0) ;
}

function checkSinglePageAnchor() {
    if (inPlaceSinglePage_lock != null) return ;
    
    var currentSinglePage = getCurrentSinglePage() ;
    //alert("test prev/next")
    if (currentSinglePage != inPlaceSinglePage) {
        alert("previous/next page detected "+inPlaceSinglePage+" -> "+ currentSinglePage)
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
            loadSinglePage(url) ;
            return false ;
        }) ;
    });
}

enableSinglePage() ;
loadBookmarkedSinglePage() ;


$().ready(function(){
   setInterval("checkSinglePageAnchor()", 30000);
});