package dev.lbuddyboy.hub.util;

import dev.lbuddyboy.hub.general.GeneralSettingsHandler;
import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.placeholder.PlaceholderType;
import dev.lbuddyboy.spoof.lSpoof;
import dev.lbuddyboy.spoof.model.Server;

import java.text.NumberFormat;
import java.util.Locale;

public class SpoofUtil {

    public static int getSpoofedCounts() {
        int count = 0;

        for (Server server : lSpoof.getInstance().getServers()) {
            count += server.getSpoofedPlayers().size() + server.getOnlinePlayers();
        }

        return count;
    }

    public void loadPlaceholders(GeneralSettingsHandler settingsHandler) {
        for (Server server : lSpoof.getInstance().getServers()) {
            settingsHandler.getPlaceholders().add(new Placeholder("%fake-online-" + server.getName() + "%", PlaceholderType.FAKE_ONLINE));
        }
    }

    public void replacePlaceholder(Placeholder placeholder) {
        if (placeholder.getType() == PlaceholderType.FAKE_ONLINE) {
            Server server = lSpoof.getInstance().getServer(placeholder.getHolder().replaceAll("%fake-online-", "").replaceAll("%", ""));
            int count = server.getSpoofedPlayers().size() + server.getOnlinePlayers();

            placeholder.setReplacement(NumberFormat.getInstance(Locale.ENGLISH).format(count));
        }
    }

}
