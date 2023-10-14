package dev.aurapvp.samurai.api.impl.dynmap.layer.impl;

import com.google.gson.JsonParser;
import dev.aurapvp.samurai.api.impl.dynmap.Helper;
import dev.aurapvp.samurai.api.impl.dynmap.IconStorage;
import dev.aurapvp.samurai.api.impl.dynmap.layer.Layer;
import dev.aurapvp.samurai.api.impl.dynmap.layer.LayerConfig;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionHandler;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

import java.util.List;

@Getter
public class FactionLayer extends Layer {

    private final FactionHandler factionHandler;
    private final IconStorage iconStorage;
    private final LayerConfig layerConfig;
    private final MarkerAPI markerAPI;

    public FactionLayer(FactionHandler factionHandler, IconStorage iconStorage, LayerConfig layerConfig, MarkerAPI markerAPI) {
        super("samurai.layers.home", layerConfig, markerAPI);
        this.factionHandler = factionHandler;
        this.iconStorage = iconStorage;
        this.layerConfig = layerConfig;
        this.markerAPI = markerAPI;

        markerSet.getMarkers().forEach(Marker::deleteMarker);
        getFactionsWithHome().forEach(this::upsertMarker);
    }

    public void upsertMarker(Faction faction) {
        Location home = faction.getHome();
        String worldName = home.getWorld().getName();
        String iconName = iconStorage.getDefaultIconName();
        MarkerIcon icon = iconStorage.getIcon(iconName);
        String label = Helper.getFactionLabel(this.layerConfig, faction);
        Marker marker = markerSet.findMarker(faction.getUniqueId().toString());

        if (marker == null) marker = markerSet.createMarker(faction.getName(), label, true, worldName, home.getX(), home.getY(), home.getZ(), icon, true);

        marker.setLocation(worldName, home.getX(), home.getY(), home.getZ());
        marker.setLabel(label);
        marker.setMarkerIcon(icon);
    }

    public void deleteMarker(Faction faction) {
        Marker marker = markerSet.findMarker(faction.getUniqueId().toString());

        if (marker == null) return;

        marker.deleteMarker();
    }

    public List<Faction> getFactionsWithHome() {
        return this.factionHandler.getFactions().values().stream().filter(faction -> faction.getHome() != null).toList();
    }

}
