var mapCenter = {
    lon: 5.7779693603516,
    lat: 45.212268217196,
    zoom: 10
}

OL_GEOPORTAIL_VISIBILITY = false
OL_GEOPORTAIL_KEYS = ['1454623408623333731', '1711091050407331029'/*192.*/]
inited = true;
function init_geoportail_EU_layer(map) {
    alert("inited")
    var setGeoRM = function () {
        return Geoportal.GeoRMHandler.addKey(
            gGEOPORTALRIGHTSMANAGEMENT.apiKey,
            gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey[0]].tokenServer.url,
            gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey[0]].tokenServer.ttl,
            map);
    }
        
    var cat = new Geoportal.Catalogue(map, gGEOPORTALRIGHTSMANAGEMENT);
    var zon = cat.getTerritory('EUE');
    
    // get Geoportail layer's parameters :
    var europeanMapOpts = cat.getLayerParameters(zon, 'GEOGRAPHICALGRIDSYSTEMS.MAPS');
    // overwrite some :
    europeanMapOpts.options.opacity = 1.0;
    // link with GeoRM :
    europeanMapOpts.options["GeoRM"] = setGeoRM();
    
    // build map :
    var europeanMap= new europeanMapOpts.classLayer(
        "Geoportail",
        europeanMapOpts.url,
        europeanMapOpts.params,
        europeanMapOpts.options);
    // reproject maxExtent (Geoportal's API standard and extended do it automagically :
    //europeanMapOpts.options.maxExtent.transform(europeanMapOpts.options.projection, map.getProjection(), true);
    // add it to the map :
    //europeanMap.isBaseLayer = true
    europeanMap.visibility = OL_GEOPORTAIL_VISIBILITY
    return europeanMap
}

function init_geoportail_base_layer() {
    var layer = new OpenLayers.Layer(
        '__PlateCarre__',
        {
            isBaseLayer: true,
            displayInLayerSwitcher: false,
            projection: new OpenLayers.Projection('EPSG:4326'),
            units: 'degrees',
            maxResolution: 1.40625,
            numZoomLevels: 21,
            maxExtent: new OpenLayers.Bounds(-180, -90, 180, 90),
            minZoomLevel:5,
            maxZoomLevel:20,
            territory:'EUE'
        })
    return layer;
}

function add_geoportail_layer(map) {
    var options = {
        name: "Cartes IGN",
        url: "http://gpp3-wxs.ign.fr/sg68l6zf6zemkg3cdr0bknay/wmts",
        layer: "GEOGRAPHICALGRIDSYSTEMS.MAPS",
        matrixSet: "PM",
        style: "normal",
        numZoomLevels: 19,
	attribution: 'Map base: &copy;IGN <a href="http://www.geoportail.fr/" target="_blank"><img src="http://api.ign.fr/geoportail/api/js/2.0.0beta/theme/geoportal/img/logo_gp.gif"></a> <a href="http://www.geoportail.gouv.fr/depot/api/cgu/licAPI_CGUF.pdf" alt="TOS" title="TOS" target="_blank">Terms of Service</a>'
    };
    var cartes = new OpenLayers.Layer.WMTS(options);
    
    options.name = "Photos IGN";
    options.layer = "ORTHOIMAGERY.ORTHOPHOTOS";
    options.numZoomLevels = 20;
    var photos = new OpenLayers.Layer.WMTS(options);                
    
    options.name = "Cassini";
    options.layer = "GEOGRAPHICALGRIDSYSTEMS.CASSINI";
    options.numZoomLevels = 16;
    var cassini = new OpenLayers.Layer.WMTS(options);                
    
    options.name = "Etat Major";
    options.layer = "GEOGRAPHICALGRIDSYSTEMS.ETATMAJOR40";
    var major = new OpenLayers.Layer.WMTS(options);         
    
    map.addLayers([cartes, photos, cassini, major]);
}

function init_osm_box(divName) {
    var map = new OpenLayers.Map (divName, {
        controls:[
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.LayerSwitcher()
            ],
        maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
        maxResolution: 156543.0399,
        numZoomLevels: 19,
        units: 'm',
        projection: new OpenLayers.Projection("EPSG:900913"),
        displayProjection: new OpenLayers.Projection("EPSG:4326")
    } );

    map.addLayers([
        new OpenLayers.Layer.OSM.Mapnik("OpenStreetMap"),
        /*new OpenLayers.Layer.Google("Google Streets", {visibility: false}),*/

        new OpenLayers.Layer.OSM( "OSM Cycle Map", "http://tile.opencyclemap.org/cycle/${z}/${x}/${y}.png",
        { displayOutsideMaxExtent: true, 
            opacity: 0.5, isBaseLayer: true, visibility: false, numZoomLevels:17, permalink: "cycle" } ),
    ]);
    add_geoportail_layer(map)
    map.addControl(new OpenLayers.Control.LayerSwitcher());

    map.setCenter(transformLonLat(new OpenLayers.LonLat(mapCenter.lon, mapCenter.lat)), mapCenter.zoom);

    map.div.style[OpenLayers.String.camelize('background-image')]= 'none';
   
    return map
}

var gpx_layers = [];
function init_gpx_layer(map, name, file_id, ready_callback) {
    file = "GPX__"+file_id+".gpx"
    
    // Add the Layer with the GPX Track
    var lgpx = new OpenLayers.Layer.Vector(name+" "+file_id, {
                protocol: new OpenLayers.Protocol.HTTP({
                    url: file,
                    format: new OpenLayers.Format.GPX
                }),
                projection: new OpenLayers.Projection("EPSG:4326"),
                style: {strokeColor: "red", strokeWidth: 5, strokeOpacity: 1},
                strategies: [new OpenLayers.Strategy.Fixed()]
            })
    
    /*
    var lgpx = new OpenLayers.Layer.GML(name+" "+file_id, file, {
            format: OpenLayers.Format.GPX,
            style: {strokeColor: "red", strokeWidth: 5, strokeOpacity: 1},
            projection: new OpenLayers.Projection("EPSG:4326")
    });*/

    map.addLayer(lgpx);
    
    lgpx.events.register("loadend", lgpx , function (e) {
        if (ready_callback == undefined)
            zoomTo(map, lgpx, false)
        else
            ready_callback(map, lgpx)
    });
    
    return lgpx
}

function zoomTo(map, layer, closest) {
    map.zoomToExtent(layer.getDataExtent(), closest);
}

function get_mm_bikeTracks(bounds) {

    llbounds = new OpenLayers.Bounds();
    llbounds.extend(OpenLayers.Layer.SphericalMercator.inverseMercator(bounds.left,bounds.bottom));
    llbounds.extend(OpenLayers.Layer.SphericalMercator.inverseMercator(bounds.right,bounds.top));
    url = "http://mm-lbserver.dnsalias.com/mm-mapserver_v2/wms/wms.php?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=MM_BIKETRACKS&STYLES=&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&SRS=EPSG:4326&BBOX="
    url = url + llbounds.toBBOX() + "&WIDTH=256&HEIGHT=256"
    return url
}

function transformLonLat(lonlat) {
    if (!lonlat || !lonlat.transform)
        return lonlat
    else {
        return lonlat.transform(
        new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
        new OpenLayers.Projection("EPSG:900913") // to Spherical Mercator Projection
    );
    }
}

var currentPopup = null;
function addMarker(map, markers, point, pointToContent_p) {
    var lnglat = transformLonLat(new OpenLayers.LonLat(point.lng, point.lat))
        
    var feature = new OpenLayers.Feature(markers, lnglat);      
    var marker = feature.createMarker();
    feature.closeBox = true;
    feature.popupClass =  OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
        'autoSize': true
    });
    feature.data.popupContentHTML = pointToContent_p(point);
    feature.data.overflow = "hidden";
   
    marker = feature.createMarker();
 
    markerClick = function (evt) {
        if (this.popup == null) {
            alert("oups")
        } else {
            this.popup.toggle();
            if (currentPopup != null)
                currentPopup.hide()
        }
        currentPopup = this.popup;
        OpenLayers.Event.stop(evt);
    };
    marker.events.register("mousedown", feature, markerClick);
    markers.addMarker(marker);

    feature.popup = feature.createPopup(feature.closeBox);
    map.addPopup(feature.popup);
    feature.popup.hide();
    
    return marker;
}