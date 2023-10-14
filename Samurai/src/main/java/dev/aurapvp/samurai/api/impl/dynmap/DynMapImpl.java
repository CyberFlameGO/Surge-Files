package dev.aurapvp.samurai.api.impl.dynmap;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.api.impl.dynmap.layer.impl.ClaimLayer;
import dev.aurapvp.samurai.api.impl.dynmap.layer.impl.FactionLayer;
import dev.aurapvp.samurai.api.impl.dynmap.layer.LayerConfig;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;

import java.util.Objects;

@Getter
public class DynMapImpl implements IModule {

    private final Plugin plugin;
    private DynmapAPI dynmapApi;
    private MarkerAPI markerApi;
    private FactionLayer factionLayer;
    private ClaimLayer claimLayer;

    public DynMapImpl(Samurai samurai) {
        plugin = samurai.getServer().getPluginManager().getPlugin("dynmap");
        if (plugin == null) {
            samurai.getLogger().severe("Cannot find dynmap!");
            return;
        }
        dynmapApi = (DynmapAPI) plugin; /* Get API */
        markerApi = dynmapApi.getMarkerAPI();
        if (markerApi == null) {
            throw new IllegalStateException("'markers' component has not been configured in DynMap! Disabling...");
        }
    }

    @Override
    public String getId() {
        return "dynmap-api";
    }

    @Override
    public void load(Samurai samurai) {
        Config factionsConfig = samurai.getFactionHandler().getFactionsLang();
        ConfigurationSection clanHomesSection = Objects.requireNonNull(factionsConfig.getConfigurationSection("layer.homes"));
        ConfigurationSection landsSection = Objects.requireNonNull(factionsConfig.getConfigurationSection("layer.claims"));

        String defaultHomeIcon = factionsConfig.getString("layer.homes.default-icon", IconStorage.DefaultIcons.FACTIONHOME.getName());
        IconStorage homesIcons = new IconStorage(samurai, "/images/factionhome", defaultHomeIcon, markerApi);

        try {
            factionLayer = new FactionLayer(samurai.getFactionHandler(), homesIcons, new LayerConfig(factionsConfig, clanHomesSection), markerApi);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        try {
            claimLayer = new ClaimLayer(samurai.getFactionHandler(), samurai.getFactionHandler().getClaimHandler(), new LayerConfig(factionsConfig, landsSection), markerApi);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void unload(Samurai plugin) {

    }
}
