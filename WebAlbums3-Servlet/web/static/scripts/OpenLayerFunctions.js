zoom = 1
    function get_mm_bikeTracks(bounds) {

        llbounds = new OpenLayers.Bounds();
        llbounds.extend(OpenLayers.Layer.SphericalMercator.inverseMercator(bounds.left,bounds.bottom));
        llbounds.extend(OpenLayers.Layer.SphericalMercator.inverseMercator(bounds.right,bounds.top));
        url = "http://mm-lbserver.dnsalias.com/mm-mapserver_v2/wms/wms.php?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=MM_BIKETRACKS&STYLES=&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&SRS=EPSG:4326&BBOX="
        url = url + llbounds.toBBOX() + "&WIDTH=256&HEIGHT=256"
        return url
      }

      function sliderInit() {
        var slider = YAHOO.widget.Slider.getHorizSlider("sliderbg", "sliderthumb", -16, 220+16-4-1);
        slider.getRealValue = function() {
          return Math.round((this.getValue()-16)*100/(220-4-1))/100; 
        }
        slider.setValue(220*0.5+16-2);
        slider.subscribe("change", function(offsetFromStart) {
          for (var i = map.layers.length-1; i >= 0; i--) {
            if (!map.layers[i].isBaseLayer && !map.layers[i].noOpaq) {
              map.layers[i].setOpacity(slider.getRealValue());
            }
          }
        });
      }
function init_osm_box(divName) {
    map = new OpenLayers.Map (divName, {
        controls:[
            new OpenLayers.Control.MouseDefaults(),
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.PanZoomBar(),
            new OpenLayers.Control.MousePosition(),
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.Attribution()],
        maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
        maxResolution: 156543.0399,
        numZoomLevels: 19,
        units: 'm',
        projection: new OpenLayers.Projection("EPSG:900913"),
        displayProjection: new OpenLayers.Projection("EPSG:4326")
    } );
    
    map.addLayers([
        new OpenLayers.Layer.OSM.Mapnik("OpenStreetMap"),
        //new OpenLayers.Layer.Google( "Google Streets", { 'sphericalMercator': true, attribution: ', Uses GeoLite data by <a href="http://maxmind.com/">MaxMind</a>', numZoomLevels:18, displayInLayerSwitcher: true, permalink: "goostr" } ),
        //new OpenLayers.Layer.Google( "Google MapMaker", { type: G_MAPMAKER_NORMAL_MAP, 'sphericalMercator': true, attribution: ', Uses GeoLite data by <a href="http://maxmind.com/">MaxMind</a>', numZoomLevels:18, permalink: "goomak" } ),
        //new OpenLayers.Layer.Google( "Google Aerial", { type: G_SATELLITE_MAP, 'sphericalMercator': true, attribution: ', Uses GeoLite data by <a href="http://maxmind.com/">MaxMind</a>', numZoomLevels:19, permalink: "goosat" } ),
        //new OpenLayers.Layer.Google( "Google Physical", { type: G_PHYSICAL_MAP, 'sphericalMercator': true, attribution: ', Uses GeoLite data by <a href="http://maxmind.com/">MaxMind</a>', numZoomLevels:16, permalink: "goophy" } ),

        new OpenLayers.Layer.OSM( "OSM Cycle Map", "http://tile.opencyclemap.org/cycle/${z}/${x}/${y}.png",
        { displayOutsideMaxExtent: true, 
            opacity: 0.5, isBaseLayer: false, visibility: false, numZoomLevels:17, permalink: "cycle" } ),

        new OpenLayers.Layer.OSM( "Hillshading", "http://toolserver.org/~cmarqu/hill/${z}/${x}/${y}.png",
        { displayOutsideMaxExtent: true, 
            opacity: 1, isBaseLayer: false, visibility: false, numZoomLevels:17, transparent: true, noOpaq: true, permalink: "hill" } ),

        new OpenLayers.Layer.OSM( "Hiking Map", "http://tile.lonvia.de/hiking/${z}/${x}/${y}.png",
        { displayOutsideMaxExtent: true, 
            opacity: 1, isBaseLayer: false, visibility: false, numZoomLevels:19, transitionEffect: "null", noOpaq: true, permalink: "hiking" } ),
    ]);

    map.addControl(new OpenLayers.Control.LayerSwitcher());
        

    map.setCenter(new OpenLayers.LonLat(0, 0), 0);

    return map
}

gpx_layers = []
function init_gpx_layer(map, name, file) {
    
    // Add the Layer with the GPX Track
    var lgpx = new OpenLayers.Layer.GML(name, file, {
            format: OpenLayers.Format.GPX,
            style: {strokeColor: "green", strokeWidth: 5, strokeOpacity: 0.5},
            projection: new OpenLayers.Projection("EPSG:4326")
    });

    map.addLayer(lgpx);
    
    lgpx.events.register("loadend", lgpx , function (e) {
        zoomTo(map, lgpx)
    });
    
    return lgpx
}

function zoomTo(map, layer) {
    map.zoomToExtent(layer.getDataExtent());    
}