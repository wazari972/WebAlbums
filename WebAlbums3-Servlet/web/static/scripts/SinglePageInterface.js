var singlePage_cache = {"Choix": ""} ;
//var ANCHOR_PREFIX = "#pg=" ;

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

function singlePageCached(url) {
    var cache = singlePage_cache[url] != undefined ;
    return cache ;
}


function getCurrentPage() {
    return getCurrentSinglePage() ;
}

function getCurrentSinglePage() {
    return ""+window.location+""
    /*
    var pos = window.location.hash.indexOf(ANCHOR_PREFIX) ;
    if (pos != -1) {
         var page = window.location.hash.substring(ANCHOR_PREFIX.length)  ;
         page.replace(/^(\s*<br\s*\/?>)*\s*|\s*(<br\s*\/?>\s*)*$/g, '')
        return $.trim(page) ;
    } else return undefined ;
    */
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

cached = {}
counter = 0
function reloadSinglePage(event) {
    if (event.state != null)
        data = cached[event.state]
    else
        data = cached["first"]
    
    if (data != undefined) {
        left = $("#left")
        left.after(data)
        left.remove()
        $(window).scrollTop(0) ;
    } else {
        loadSinglePage(""+window.location+"")
    }
}

function loadSinglePage(url, dont_scroll, force) {
    if (dont_scroll == undefined)
        dont_scroll = false
    
    if (force == undefined)
        force = false
    
    if (url == undefined) {
        url = getCurrentPage() ;
        var pos = url.indexOf('?') ;
        if (pos != -1) {
            url = url.substring(0, pos) ;
        }

    }
    inPlaceSinglePage_lock = 1 ;

    if (!force && url == inPlaceSinglePage) {
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
        $(left).hide()
    }
    //document.location.href = ANCHOR_PREFIX+url;
    url = url.replace(/\n/g, '') ;
    url = url.replace(/\r/g, '') ;
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
    
    $("body").css("cursor", "wait");
    $.ajax({
        url:url,
        success:function(data){
            //$("body").css("cursor", "auto");
            loadSinglePageBottomEnd(data, dont_scroll, url) ;
        },
        complete:function() {;$("body").css("cursor", "auto");},
        statusCode: {
                500: function() {alert('Glassfish error ...');},
		404: function() {alert('page not found');}
        },
        async:true
    });
}

function loadSinglePageBottomEnd(xml_doc, dont_scroll, url) {    
    var left = document.getElementById ("left");
    if (left == null) {
        alert("could not locate the left div ...")
        return ;
    }
    
    if (counter == 0) {
        cached["first"] = $("#left")
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
    
    $("#gen_time").text(($(xml_doc).find("time").text()))
    
    $(left).fadeIn() ;
    enableSinglePage() ;
    inPlaceSinglePage_lock = null ;
    //url = getCurrentSinglePage();
    
    cached[counter] = $(left)
    history.pushState(counter, /*title*/ null, url);
    counter += 1
    
    
    if (dont_scroll) {
        //nothing to do
    } else if (url.indexOf("#") > -1) {
        anchor = url.substring(url.indexOf("#")+1)
        if ($("#anchor_"+anchor) && $("#anchor_"+anchor).offset())
            window.scrollTo(0, ($("#anchor_"+anchor).offset().top))
    } else {
        $(window).scrollTop(0) ;
    }
    
    if (callbacks["SinglePage"] != undefined)
        callbacks["SinglePage"]()
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


        var parents = $(this).parent("div") ;
        for (var i = -1; i < parents.length; i++) {
            var parent
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
function init_singlepage() {
    prepareDisplayXSL() ;
    enableSinglePage() ;
}


$(function(){
    // Revert to a previously saved state
    window.addEventListener('popstate', function(event) {
      console.log('popstate fired!');
      reloadSinglePage(event)
    });
    init_singlepage()
});
