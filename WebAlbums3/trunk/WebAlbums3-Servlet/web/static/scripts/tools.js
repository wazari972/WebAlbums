
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
    xhr_object_XSL = getNewHTTPObject();

    // Get the XSLT from the server.
    xhr_object_XSL.open("GET", url, false);
    xhr_object_XSL.send(null);
}

function getNewHTTPObject() {
    var xmlhttp;
    /** Special IE only code ... */
    /*@cc_on
          @if (@_jscript_version >= 5)
              try {
                  xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
              } catch (e) {
                  try {
                      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                  } catch (E) {
                      xmlhttp = false;
                  }
             }
          @else
             xmlhttp = false;
        @end @*/

    /** Every other browser on the planet */
    if (!xmlhttp && typeof XMLHttpRequest != 'undefined') {
        try {
            xmlhttp = new XMLHttpRequest();
        } catch (e) {
            xmlhttp = false;
        }
    }
    return xmlhttp;

}

var xsl_doc = null ;
function prepareXSL() {
    // Get the XSLT from the server.
    var xhr_object_XSL = getNewHTTPObject();
    xhr_object_XSL.open("GET", "static/Empty.xsl", false);
    xhr_object_XSL.send(null);
    xsl_doc = xhr_object_XSL.responseXML;
}
prepareXSL() ;

function loadExernals(btId, address, divId) {
    var xhr_object_XML = getNewHTTPObject();

    var bt = document.getElementById(btId) ;
    if (bt != null) bt.style.visibility = "hidden";


    // Get the XML from the server.
    xhr_object_XML.open("GET", address, true);

    xhr_object_XML.onreadystatechange = function() {
        if (xhr_object_XML.readyState != 4) {
            return;
        }
        loadExernalsBottomEnd(xhr_object_XML, divId) ;
    };

    xhr_object_XML.send(null);
}


function loadExernalsBottomEnd(xhr_object_XML, divId) {
    var xml_doc = xhr_object_XML.responseXML;

    var div = document.getElementById (divId);
    div.style.display = "" ;

    // Use object detection to find out if we have
    // Firefox/Mozilla/Opera or IE XSLT support.
    if (typeof XSLTProcessor != "undefined") {
        var xsl_proc = new XSLTProcessor ();

        xsl_proc.importStylesheet (xsl_doc);
        var node = xsl_proc.transformToFragment (xml_doc, document);
        div.innerHTML = "";
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

//addLoadEvent(loadCloud())