/* Might be overriden in Display.xsl */
var mapCenter = {
    lon: 5.7779693603516,
    lat: 45.212268217196,
    zoom: 10
};

function add_osm_layers(map) {
    map.addLayer(new OpenLayers.Layer.OSM.Mapnik("OpenStreetMap"));
    
    var openCycle = new OpenLayers.Layer.OSM( "OSM Cycle Map", "http://tile.opencyclemap.org/cycle/${z}/${x}/${y}.png",
        {displayOutsideMaxExtent: true, isBaseLayer: true, visibility: false, numZoomLevels:17, permalink: "cycle" });
        
    map.addLayer(openCycle);
    
    var mapbox = new ol.layer.Tile({
      source: new ol.source.TileJSON({
        url: 'http://api.tiles.mapbox.com/v3/' +
            'mapbox.natural-earth-hypso-bathy.jsonp',
        crossOrigin: 'anonymous'
      })
    });
    map.addLayer(mapbox);
}

function add_google_layers(map) {
    /**/
    if (google.maps.MapTypeId === undefined) {
        /* HUGLY hack, but Google Map script tries to 
         * document.write to load its inner script, which
         * is not allowed in XTHML/XSL. We just do it manually
         * here :) 
         * */
        
        var reload_google_layers = function() {
            if (have_google()) {
                add_google_layers(map);
            } else {
                alert("Couldn't load Google maps ...");
            }
        };

        $.getScript("http://maps.gstatic.com/intl/en_us/mapfiles/api-3/9/14/main.js", reload_google_layers);
        
        return;
    }
    
    var gphy = new OpenLayers.Layer.Google(
        "Google Physical",
        {type: google.maps.MapTypeId.TERRAIN, numZoomLevels: 20}
    );
    map.addLayer(gphy);
    
    var gmap = new OpenLayers.Layer.Google("Google Plan", {visibility: false, numZoomLevels: 20});
    map.addLayer(gmap);
}

function add_geoportail_layers(map) {
    var apiKEYs = {
        //'localhost:8080': 'r6muu3lqjvkg22q8dw893k7z'
        "localhost:8080":"f4ezpirs8jd63cwbhkstbkqd"
    };
    var apiKEY = "nq2serx7okjnv1zjpvdlgrz9"; //apiKEYs[window.location.host];
    
    var options = {
        name: "Cartes IGN",
        url: "http://gpp3-wxs.ign.fr/" + apiKEY + "/wmts",
        layer: "GEOGRAPHICALGRIDSYSTEMS.MAPS",
        matrixSet: "PM",
        style: "normal",
        attribution: '&copy;IGN <a href="http://www.geoportail.fr/" target="_blank"><img src="http://api.ign.fr/geoportail/api/js/latest/theme/geoportal/img/logo_gp.gif"></a> <a href="http://www.geoportail.gouv.fr/depot/api/cgu/licAPI_CGUF.pdf" alt="TOS" title="TOS" target="_blank">Terms of Service</a>'
    };
    var ign = new OpenLayers.Layer.WMTS(options);
    
    ign.setName("Geoportail IGN");
    map.addLayer(ign);
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
    
    if (intensity !== undefined) {
        src.intensity = intensity;
    }
}

function have_heatmap() {
    return (typeof Heatmap !== 'undefined');
}

function add_marker_layer(map, name) {
    var markers = new OpenLayers.Layer.Markers(name);
    map.addLayer(markers);
    
    return markers;
}
function have_google() {
    return (typeof google !== 'undefined' && google.maps.MapTypeId !== undefined);
}

function have_osm() {
    return (typeof OpenLayers !== 'undefined');
}

function init_osm_box(divName) {
    if (!have_heatmap()) {
        return null;
    }
    
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
    
    var layers = [add_osm_layers 
        /*add_geoportail_layers, 
        add_google_layers*/
        ];
    for (var i = 0; i < layers.length; i++) {
        try {
            layers[i](map);
        } catch(e) {
            window.console.log(e);
        }
    }
    
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.setCenter(transformLonLat(new OpenLayers.LonLat(mapCenter.lon, mapCenter.lat)), mapCenter.zoom);

    map.div.style[OpenLayers.String.camelize('background-image')]= 'none';
   
    return map;
}

var gpx_layers = [];
function init_gpx_layer(map, name, file_id, ready_callback) {
    file = "GPX__"+file_id+".gpx";
    
    // Add the Layer with the GPX Track
    var lgpx = new OpenLayers.Layer.Vector(name+" "+file_id, {
        protocol: new OpenLayers.Protocol.HTTP({
            url: file,
            format: new OpenLayers.Format.GPX
        }),
        projection: new OpenLayers.Projection("EPSG:4326"),
        style: {strokeColor: "red", strokeWidth: 5, strokeOpacity: 1},
        strategies: [new OpenLayers.Strategy.Fixed()]
    });

    map.addLayer(lgpx);
    
    lgpx.events.register("loadend", lgpx , function (e) {
        if (ready_callback === undefined) {
            zoomTo(map, lgpx, false);
        } else {
            ready_callback(map, lgpx);
        }
    });
    
    return lgpx;
}

function zoomTo(map, layer, closest) {
    if (map === null) {
        return;
    }
    map.zoomToExtent(layer.getDataExtent(), closest);
}

function zoom_to_layer(map, layer) {
    if (map === null) {
        return;
    }
    
    map.zoomToExtent(layer.getDataExtent());
}

function transformLonLat(lonlat) {
    if (!lonlat || !lonlat.transform) {
        return lonlat;
    } else {
        if (!have_osm()) {
            return null;
        }
        return lonlat.transform(
            new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
            new OpenLayers.Projection("EPSG:900913") // to Spherical Mercator Projection
        );
    }
}

function lng_lat_to_lonlat(lng, lat) {
    if (!have_osm()) {
        return null;
    }
    return transformLonLat(new OpenLayers.LonLat(lng, lat));
}

var currentPopup = null;
function addMarker(map, markers, point, pointToContent_p, lnglat) {
    if (!have_osm()) {
        return null;
    }
    
    var feature = new OpenLayers.Feature(markers, lnglat);
    feature.closeBox = true;
    feature.popupClass = OpenLayers.Class(OpenLayers.Popup.Anchored, {});
    feature.data.popupContentHTML = pointToContent_p(point);
    feature.data.overflow = "hidden";

    var marker = new OpenLayers.Marker(lnglat);
    marker.feature = feature;

    var markerClick = function(evt) {
        if (this.popup === null) {
            this.popup = this.createPopup(this.closeBox);
            map.addPopup(this.popup);
            this.popup.show();
        } else {
            this.popup.toggle();
        }
        
        if (currentPopup !== null) {
            currentPopup.hide();
        }
    
        currentPopup = this.popup;
        currentPopup.show();
        OpenLayers.Event.stop(evt);
    };
    
    marker.events.register("mousedown", feature, markerClick);

    markers.addMarker(marker);
    
    return marker;
}

var geoCodeURL = "http://nominatim.openstreetmap.org/search";
function geocode(address, map) {
    $.ajax({
        url: geoCodeURL,
        data: {
            format: "json",
            q: address
        },
        async: false,
        success: function ( data ) {
            var json_to_obj = function( item ) {
                if (!item) return null;
                return {
                    label: item.display_name,
                    lat: item.lat,
                    lon: item.lon
                }
            }
            var item = json_to_obj(data[0])
            if (item === null) {
                return;
            }
            var longlat = new OpenLayers.LonLat(item.lon, item.lat);
            map.setCenter(transformLonLat(longlat), map.getZoom());
        }   
    })
}

function json_pointToContent(point) {
    return "<div class='map_content'>"
          +"  <h3><a taget='_blank' href='Tag__"+point.id+"__"+point.name+"'>"+point.name+"</a></h3>\n"
          +"  <center><img src='Miniature__"+point.picture+".png' /></center>\n"
          +"</div>";
}


function json_point_to_lonlat(point) {
    return lng_lat_to_lonlat(point.lng, point.lat);
}

function populateMapFromJSON(url, map, headCloud) {
    var heatmap = null;
    if (headCloud && have_heatmap()) {
        heatmap = add_heatmap_layer(map, "Heatmap");
    }
    
    var markers = add_marker_layer(map, "Geo Tags");
    
    $.getJSON(url,
        function(data) {
            var hasData = false;
            $.each(data, function(key, point) {
                var lonlat = json_point_to_lonlat(point);
                
                addMarker(map, markers, point, json_pointToContent, lonlat);
                hasData = true;
                if (heatmap === null) {
                    return;
                }
                var intensity = headCloud.find("tag[id=" +point.id+ "]").attr("nb");
                heat_add_src(heatmap, lonlat, intensity);
            });
            
            if (hasData) {
                zoom_to_layer (map, heatmap !== null ? heatmap : markers);
            }
            $("body").css("cursor", "auto");
            /* BUG: this div blocks my TOC in Carnet page ... so I get rid of it here !*/
            $(".olForeignContainer").remove();
        }
    ).error(function(e, textStatus) { alert("populateMapFromJSON FAILED --"+url+" -- "+e +" -- "+textStatus); $("body").css("cursor", "auto");});
}