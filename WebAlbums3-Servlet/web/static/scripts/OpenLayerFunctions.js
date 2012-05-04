zoom = 1

function init_osm_box(divName) {
    map = new OpenLayers.Map (divName, {
        controls:[
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

    // Define the map layer
    // Here we use a predefined layer that will be kept up to date with URL changes
    layerMapnik = new OpenLayers.Layer.OSM.Mapnik("Mapnik");
    map.addLayer(layerMapnik);
    layerCycleMap = new OpenLayers.Layer.OSM.CycleMap("CycleMap");
    map.addLayer(layerCycleMap);
    layerMarkers = new OpenLayers.Layer.Markers("Markers");
    map.addLayer(layerMarkers);

    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.setCenter(new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34).getCenterLonLat(), zoom)

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