function updateAffichage(option) {
    if (option == 'edition') {
	callURL('Config?special=UPDATE&edition=SWAP');
    } else if (option == 'maps') {
	alert("not implemented");
    } else if (option == 'details') {
	callURL('Config?special=UPDATE&details=SWAP');
    } else {
	alert("Unknown option...");
    }
    window.location.reload();
}

function loadMaps() {
    var script = document.createElement("script");
    script.setAttribute("src", "http://maps.google.com/maps?file=api&v=2.x&key="+
			"ABQIAAAANkbb-SNf1VDzX-W2M-MKGBTBfUk9TZrBRaIteybtnU2KziHEpRRfEtLc0rJCxsohWtD3KULc_ECH6w"+
			"&c&async=2&callback=loadMap");
    script.setAttribute("type", "text/javascript");
    document.documentElement.firstChild.appendChild(script);
 
    document.getElementById('mapLoader').style.visibility = "hidden";
}

function loadCloud() {
    loadExernals('cloudLoader', 'Tags?special=CLOUD', 'cloud') ;
}

function loadPersons() {
    loadExernals('personsLoader', 'Tags?special=PERSONS', 'persons') ;
}

function loadPlaces() {
    loadExernals('placesLoader', 'Tags?special=PLACES', 'places') ;
}

function loadAlbums() {
    loadExernals('albumsLoader', 'Albums?special=TOP5', 'albums') ;
}

function callURL(url) {
    if(window.XMLHttpRequest) {// Firefox 
	xhr_object_XML = new XMLHttpRequest();
	xhr_object_XSL = new XMLHttpRequest();
    }
    else if(window.ActiveXObject) {// Internet Explorer
	xhr_object_XML = new ActiveXObject("Microsoft.XMLHTTP");
	xhr_object_XSL = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else { // XMLHttpRequest non supporté par le navigateur
	alert("Votre navigateur ne supporte pas les objets XMLHTTPRequest...");
	return;
    }

    // Get the XSLT from the server.
    xhr_object_XSL.open("GET", url, false);
    xhr_object_XSL.send(null);
    var xsl_doc = xhr_object_XSL.responseXML;
}

function loadExernals(btId, address, divId) {
    var xhr_object_XML = null;
    var xhr_object_XSL = null;

    
    var bt = document.getElementById(btId) ;
    if (bt != null) bt.style.visibility = "hidden";

    if(window.XMLHttpRequest) {// Firefox 
	xhr_object_XML = new XMLHttpRequest();
	xhr_object_XSL = new XMLHttpRequest();
    }
    else if(window.ActiveXObject) {// Internet Explorer
	xhr_object_XML = new ActiveXObject("Microsoft.XMLHTTP");
	xhr_object_XSL = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else { // XMLHttpRequest non supporté par le navigateur
	alert("Votre navigateur ne supporte pas les objets XMLHTTPRequest...");
	return;
    }

    // Get the XSLT from the server.
    xhr_object_XSL.open("GET", "static/Empty.xsl", false);
    xhr_object_XSL.send(null);
    var xsl_doc = xhr_object_XSL.responseXML;

    // Get the XML from the server.
    xhr_object_XML.open("GET", address, false);
    xhr_object_XML.send(null);
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
    }
    else if (typeof xml_doc.transformNode != "undefined") {
        div.innerHTML = xml_doc.transformNode (xsl_doc);
    }
    else {
        div.innerHTML = "XSLT not supported in browser.";
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
addLoadEvent(loadCloud())