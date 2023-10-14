package dev.lbuddyboy.samurai.events;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.util.CC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Event {

    String getName();

    boolean isActive();

    void tick();

    void setActive(boolean active);

    boolean isHidden();

    void setHidden(boolean hidden);

    boolean activate();

    boolean deactivate();

    EventType getType();
    
    default String getDisplayName() {
        String displayName = "";

        switch (getName()) {
            case "EOTW":
                displayName = CC.translate("&x&c&1&0&9&2&7&lEOTW");
                break;
            case "Nether-Citadel":
            case "Citadel":
            case "Overworld-Citadel":
                displayName = CC.translate("&x&c&4&1&8&f&b&lCitadel");
                break;
            default:
                displayName = CC.translate("&x&6&0&7&7&f&b&l" + getName() + " KoTH");
                break;
        }

        if (getType() == EventType.DTC) {
            displayName = "DTC";
        } else {
            if (getName().equals(VaultHandler.TEAM_NAME)) {
                displayName = CC.translate("&x&7&a&9&9&c&1&lVault Post");
            }
        }

        return displayName;
    }

    public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            List<String> completions = new ArrayList<>();
            Player player = context.getPlayer();
            String input = context.getInput();

            for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {

                if (StringUtils.startsWithIgnoreCase(event.getName(), input)) {
                    completions.add(event.getName());
                }
            }

            return (completions);
        }

    }

    public static class Type implements ContextResolver<Event, BukkitCommandExecutionContext> {

        @Override
        public Event getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
            Player sender = c.getPlayer();
            String source = c.popFirstArg();

            if (c.isOptional() && source.isEmpty() && c.hasFlag("active")) {
                for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
                    if (event.isActive() && !event.isHidden()) {
                        return event;
                    }
                }

                sender.sendMessage(ChatColor.RED + "There is no active Event at the moment.");

                return null;
            }

            Event event = Samurai.getInstance().getEventHandler().getEvent(source);

            if (event == null) {
                sender.sendMessage(ChatColor.RED + "No Event with the name " + source + " found.");
                return (null);
            }

            return (event);
        }
    }

}
