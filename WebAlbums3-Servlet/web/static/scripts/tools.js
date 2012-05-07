function loadMap(divName) {
    return init_osm_box(divName)
}

function callURL(url) {
    return $.get(url)
}

function loadExernals(btId, url, divId, callback, async) {
    if (async == undefined) {
        async = true
    }
    if (btId != null)
        $("#"+btId).hide()
    
    $.ajax({
        url:url,
        success:function(data){
            loadExernalsBottomEnd(data, divId, callback)
        },
        async:async
    })
}


var emptyXsl = null
function prepareEmptyXSL() {
    $.ajax({
      url:"static/Empty.xsl",
      success:function(html){emptyXsl = html},
      async:false
     })

}

function loadExernalsBottomEnd(data, divId, callback) {
    var xml_doc = data

    var div = document.getElementById (divId)
    if (div == null) {
        alert("div "+divId+" not found.")
        return
    }
    
    // Use object detection to find out if we have
    // Firefox/Mozilla/Opera or IE XSLT support.
    if (typeof XSLTProcessor != "undefined") {
        var xsl_proc = new XSLTProcessor ()
        xsl_proc.importStylesheet (emptyXsl)
    
        var node = xsl_proc.transformToFragment (xml_doc, document)
        div.innerHTML = ""
        div.appendChild (node)
    } else if (typeof xml_doc.transformNode != "undefined") {
        div.innerHTML = xml_doc.transformNode (emptyXsl)
    } else {
        div.innerHTML = xhr_object_XML.responseText
    }
    
    $(div).fadeIn() 
    if (typeof enableSinglePage == 'function')
        enableSinglePage()
    
    if (callback != undefined) callback() 
}

function updateBackground(id) {
    if (document.body){
        document.body.style.backgroundImage = "url(Images?id="+id+"&mode=SHRINK&width=1280)"
    }
}

try {
    nothing = callbacks
} catch(e) {
    callbacks = []
}
function add_callback (hook, func) {
    var original = callbacks[hook]
    if (!original)
        original = function(){}
    
    callbacks[hook] = function (x) { return func(original(x)) }
}

try {
    nothing = saved
} catch(e) {
    saved = []
}

function save_data_page(key, value) {
    saved[key] = value
}

function get_data_page(key, value) {
    try {
        return saved[key]
    } catch(e) {
        return undefined
    }
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]")
    var regexS = "[\\?&]" + name + "=([^&#]*)"
    var regex = new RegExp(regexS)
    var results = regex.exec(window.location.search)
    if(results == null)
        return ""
    else {
        return decodeURIComponent(results[1].replace(/\+/g, " "))
    }
}


$(function() {
    prepareEmptyXSL() 
})