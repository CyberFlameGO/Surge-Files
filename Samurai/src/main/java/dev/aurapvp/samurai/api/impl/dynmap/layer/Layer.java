package dev.aurapvp.samurai.api.impl.dynmap.layer;

import lombok.Getter;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import static dev.aurapvp.samurai.api.impl.dynmap.layer.LayerConfig.LayerField.*;

@Getter
public abstract class Layer {

    public String id;
    public LayerConfig config;
    public MarkerAPI markerAPI;
    public MarkerSet markerSet;

    public Layer(String id, LayerConfig config, MarkerAPI markerAPI) {
        this.id = id;
        this.config = config;
        this.markerAPI = markerAPI;

        markerSet = markerAPI.getMarkerSet(id) == null ? markerAPI.createMarkerSet(id, config.getString(LABEL), null, true) : markerAPI.getMarkerSet(id);

        markerSet.setMarkerSetLabel(config.getString(LABEL));
        markerSet.setLayerPriority(config.getInt(PRIORITY));
        markerSet.setHideByDefault(config.getBoolean(HIDDEN));
        markerSet.setMinZoom(config.getInt(MINZOOM));
    }
}