package dev.aurapvp.samurai.api.impl.dynmap.layer.impl;

import dev.aurapvp.samurai.api.impl.dynmap.Helper;
import dev.aurapvp.samurai.api.impl.dynmap.IconStorage;
import dev.aurapvp.samurai.api.impl.dynmap.layer.Layer;
import dev.aurapvp.samurai.api.impl.dynmap.layer.LayerConfig;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionHandler;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.faction.claim.ClaimHandler;
import lombok.Getter;
import org.bukkit.Location;
import org.dynmap.markers.*;

import java.util.List;

@Getter
public class ClaimLayer extends Layer {

    private final FactionHandler factionHandler;
    private final ClaimHandler claimHandler;
    private final LayerConfig layerConfig;
    private final MarkerAPI markerAPI;

    public ClaimLayer(FactionHandler factionHandler, ClaimHandler claimHandler, LayerConfig layerConfig, MarkerAPI markerAPI) {
        super("samurai.layers.claim", layerConfig, markerAPI);
        this.factionHandler = factionHandler;
        this.claimHandler = claimHandler;
        this.layerConfig = layerConfig;
        this.markerAPI = markerAPI;

        markerSet.getAreaMarkers().forEach(AreaMarker::deleteMarker);
        getFactionsWithClaims().forEach(this::upsertMarker);
    }

    public void upsertMarker(Faction faction) {
        String label = Helper.getFactionLabel(this.layerConfig, faction);

        for (Claim claim : faction.getClaims()) {
            String worldName = claim.getCuboid().getWorld().getName();
            AreaMarker marker = markerSet.findAreaMarker(claim.getUuid().toString());
            Location lower = claim.getCuboid().getLowerNE(), upper = claim.getCuboid().getUpperSW();
            double[] first = new double[]{lower.getX(), upper.getX()};
            double[] second = new double[]{lower.getZ(), upper.getZ()};

            if (marker == null)
                marker = markerSet.createAreaMarker(faction.getName(), label, true, worldName, first, second, true);

            var fillColor = config.getString("style.fill.color", "#57b356");
            var lineColor = config.getString("style.line.color", "#2d682d");
            if (config.getBoolean("style.based-on-tag", false)) {
                fillColor = Helper.HEXColor.of(faction.getType().getColor()).getCode();
                lineColor = fillColor;
            }

            double fillOpacity = config.getDouble("style.fill.opacity", 0.35);
            double lineOpacity = config.getDouble("style.line.opacity", 0.8);
            int lineWeight = config.getInt("style.line.weight", 3);

            marker.setLabel(label, true);
            marker.setDescription("Need to implement");
            marker.setFillStyle(fillOpacity, Integer.valueOf(fillColor.replace("#", ""), 16));
            marker.setLineStyle(lineWeight, lineOpacity, Integer.valueOf(lineColor.replace("#", ""), 16));
            marker.setCornerLocations(first, second);
        }
    }

    public void deleteMarker(Claim claim) {
        Marker marker = markerSet.findMarker(claim.getUuid().toString());

        if (marker == null) return;

        marker.deleteMarker();
    }

    public List<Faction> getFactionsWithClaims() {
        return this.factionHandler.getFactions().values().stream().filter(faction -> !faction.getClaims().isEmpty()).toList();
    }

}
