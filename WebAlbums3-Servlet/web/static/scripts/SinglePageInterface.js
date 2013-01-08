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

function getCurrentPage() {
    return getCurrentSinglePage() ;
}

function getCurrentSinglePage() {
    return ""+window.location+""
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

var cached = {}
var counter = 0
function reloadSinglePage(event) {
    var data, left
    if (event != undefined) {
        if (event.state != null)
            data = cached[event.state]
        else
            data = cached["first"]
    }
    
    if (data != undefined) {
        left = $("#left")
        left.after(data)
        left.remove()
        $(window).scrollTop(0) ;
    } else {
        loadSinglePage(getCurrentPage(), true, true)
    }
}

function loadSinglePage(url, dont_scroll, force, async) {
    if (dont_scroll == undefined)
        dont_scroll = false
    
    if (force == undefined)
        force = false
    
    if (async == undefined)
        async = false
    
    if (url == undefined) {
        url = getCurrentPage() ;
        var pos = url.indexOf('?') ;
        if (pos != -1) {
            url = url.substring(0, pos) ;
        }
    }
    
    //remove itermediate #s
    if (url.indexOf("#") != url.lastIndexOf("#")) {
        url = url.substring(0, url.indexOf("#")) + url.substring(url.lastIndexOf("#"))
    }
    inPlaceSinglePage_lock = 1 ;

    if (!force && url == inPlaceSinglePage) {
        return ;
    }

    inPlaceSinglePage = $.trim(url) ;

    
    $("body").css("cursor", "wait");
    $.ajax({
        url:url,
        success:function(data){
            loadSinglePageBottomEnd(data, dont_scroll, url) ;
        },
        complete:function() {$("body").css("cursor", "auto")},
        statusCode: {
                500: function() {alert('Glassfish error 500 ...');},
		404: function() {alert('Page not found 404 ...');}
        },
        async:async
    });
}

function loadSinglePageBottomEnd(xml_doc, dont_scroll, url) {    
    var left = $("#left");
    if (counter == 0) {
        cached["first"] = $("#left")
    }
    
    var xsl_proc = new XSLTProcessor ();
    xsl_proc.importStylesheet (displayXsl);
    var newDocument = xsl_proc.transformToDocument (xml_doc);
    
    $(newDocument).ready(function() {    
        var newLeft = $(Node_getElementById(newDocument, "left"))
        left.hide()
        left.after(newLeft)
        left.hide()
        left.remove()
        //newLeft.show()
        left = newLeft ;

        $("#gen_time").text(($(xml_doc).find("time").text()))
        
        enableSinglePage() ;
        inPlaceSinglePage_lock = null ;

        cached[counter] = left
        history.pushState(counter, /*title*/ null, url);
        counter += 1

        if (dont_scroll) {
            //nothing to do
        } else if (url.indexOf("#") > -1) {
            var anchor = url.substring(url.indexOf("#")+1)
            if ($("#anchor_"+anchor) && $("#anchor_"+anchor).offset())
                window.scrollTo(0, ($("#anchor_"+anchor).offset().top))
        } else {
            $(window).scrollTop(0) ;
        }

        if (callbacks["SinglePage"] != undefined)
            callbacks["SinglePage"]()
    })
}

function enableSinglePage() {
    $('a').each(function(index) {
        
        if ($(this).attr("SinglePaged")) return ;
        $(this).attr("SinglePaged", true) ;

        if ($(this).attr("href") == undefined) return ;

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
        reloadSinglePage(event)
    });
    init_singlepage()
});
