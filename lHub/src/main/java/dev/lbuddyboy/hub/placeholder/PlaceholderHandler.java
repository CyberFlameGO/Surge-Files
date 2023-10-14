package dev.lbuddyboy.hub.placeholder;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.placeholder.impl.FlashPH;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlaceholderHandler implements lModule {

    private final List<PlaceholderImpl> placeholderImpls;

    public PlaceholderHandler() {
        this.placeholderImpls = new ArrayList<>();
    }

    @Override
    public void load(lHub plugin) {
        PluginManager manager = lHub.getInstance().getServer().getPluginManager();
        if (manager.getPlugin("Flash") != null && manager.isPluginEnabled("Flash")) this.placeholderImpls.add(new FlashPH());
    }

    @Override
    public void unload(lHub plugin) {

    }
}
