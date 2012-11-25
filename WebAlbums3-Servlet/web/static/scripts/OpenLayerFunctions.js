/* Might be overriden in Display.xsl */
var mapCenter = {
    lon: 5.7779693603516,
    lat: 45.212268217196,
    zoom: 10
}

function add_stamen_layers(map) {
    var toner = new OpenLayers.Layer.Stamen("toner")
    toner.setName("Stamen Toner")
    var water = new OpenLayers.Layer.Stamen("watercolor")
    water.setName("Stamen Watercolor")
    //var terrain = new OpenLayers.Layer.Stamen("terrain")
    //terrain.setName("Stamen Terrain")
    
    map.addLayers([toner, water]);
}

function add_osm_layers(map) {
    map.addLayer(new OpenLayers.Layer.OSM.Mapnik("OpenStreetMap"));
    
    var openCycle = new OpenLayers.Layer.OSM( "OSM Cycle Map", "http://tile.opencyclemap.org/cycle/${z}/${x}/${y}.png",
        {displayOutsideMaxExtent: true, isBaseLayer: true, visibility: false, numZoomLevels:17, permalink: "cycle" })
        
    map.addLayer(openCycle);
}

function add_google_layers(map) {
    /**/
    if (google.maps.MapTypeId == undefined) {
        /* HUGLY hack, but Google Map script tries to 
         * document.write to load its inner script, which
         * is not allowed in XTHML/XSL. We just do it manually
         * here :) 
         * */
        
        var reload_google_layers = function() {
            if (google.maps.MapTypeId == undefined) {
                alert("Couldn't load Google maps ...")
            } else {
                add_google_layers(map)
            }
        }

        $.getScript("http://maps.gstatic.com/intl/en_us/mapfiles/api-3/9/14/main.js", reload_google_layers)
        
        return
    }
    
    var gphy = new OpenLayers.Layer.Google(
        "Google Physical",
        {type: google.maps.MapTypeId.TERRAIN, numZoomLevels: 20}
    );
    map.addLayer(gphy)
    
    var gmap = new OpenLayers.Layer.Google("Google Plan", {visibility: false, numZoomLevels: 20});
    map.addLayer(gmap)
}

function add_geoportail_layers(map) {
    var apiKEYs = {
        //'localhost:8080': 'r6muu3lqjvkg22q8dw893k7z'
        "localhost:8080":"f4ezpirs8jd63cwbhkstbkqd"
    };
    var apiKEY = apiKEYs[window.location.host];
    
    var options = {
        name: "Cartes IGN",
        url: "http://gpp3-wxs.ign.fr/" + apiKEY + "/wmts",
        layer: "GEOGRAPHICALGRIDSYSTEMS.MAPS",
        matrixSet: "PM",
        style: "normal",
        attribution: '&copy;IGN <a href="http://www.geoportail.fr/" target="_blank"><img src="http://api.ign.fr/geoportail/api/js/latest/theme/geoportal/img/logo_gp.gif"></a> <a href="http://www.geoportail.gouv.fr/depot/api/cgu/licAPI_CGUF.pdf" alt="TOS" title="TOS" target="_blank">Terms of Service</a>'
    };
    var ign = new OpenLayers.Layer.WMTS(options)
    
    ign.setName("Geoportail IGN")
    map.addLayer(ign)
}

function add_heatmap_layer(map, name) {
    if (!have_heatmap())
        return null;
    
    var heatmap = new Heatmap.Layer(name);
    map.addLayer(heatmap);
    
    return heatmap;
}

function heat_add_src(heatmap, latlng, intensity) {
    if (!have_heatmap())
        return;
    
    var src = new Heatmap.Source(latlng);
    heatmap.addSource(src);
    
    if (intensity != undefined) {
        src.intensity = intensity
    }
}

function have_heatmap() {
    return (typeof Heatmap != 'undefined')
}

function add_marker_layer(map, name) {
    var markers = new OpenLayers.Layer.Markers(name);
    map.addLayer(markers);
    
    return markers
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
    
    var layers = [add_osm_layers, add_geoportail_layers, add_google_layers, add_stamen_layers]
    for (var i = 0; i < layers.length; i++) {
        try {
            layers[i](map)
        } catch(e) {
            window.console.log(e)
        }
    }
    
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

function zoom_to_layer(map, layer) {
    map.zoomToExtent(layer.getDataExtent());
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

function lng_lat_to_lonlat(lng, lat) {
    return transformLonLat(new OpenLayers.LonLat(lng, lat))
}

var currentPopup = null;
function addMarker(map, markers, point, pointToContent_p, lnglat) {
    var feature = new OpenLayers.Feature(markers, lnglat);
    feature.closeBox = true;
    feature.popupClass = OpenLayers.Class(OpenLayers.Popup.AnchoredBubble, {minSize: new OpenLayers.Size(300, 180) } );
    feature.data.popupContentHTML = pointToContent_p(point);
    feature.data.overflow = "hidden";

    var marker = new OpenLayers.Marker(lnglat);
    marker.feature = feature;

    var markerClick = function(evt) {
        if (this.popup == null) {
            this.popup = this.createPopup(this.closeBox);
            map.addPopup(this.popup);
            this.popup.show();
        } else {
            this.popup.toggle();
        }
        
        if (currentPopup != null)
            currentPopup.hide()
    
        currentPopup = this.popup;
        currentPopup.show()
        OpenLayers.Event.stop(evt);
    };
    
    marker.events.register("mousedown", feature, markerClick);

    markers.addMarker(marker);
    
    return marker
}

function geocode(address, map) {
    var geocoder = new google.maps.Geocoder();
    
    if (geocoder) {
        geocoder.geocode({'address': address }, function (results, status) {
          if (status == google.maps.GeocoderStatus.OK) {
             
             var longlat = new OpenLayers.LonLat(results[0].geometry.location.lng(), results[0].geometry.location.lat())
             map.setCenter(transformLonLat(longlat), map.getZoom())

          } else {
             alert("Geocoding failed: " + status);
          }

       });
    } else {
        alert("No geocoder ...");
    }
}