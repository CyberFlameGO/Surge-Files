package dev.lbuddyboy.samurai.map.kits;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultKit extends Kit {

    @Getter
    @Setter
    private ItemStack icon = new ItemStack(Material.DIAMOND_SWORD);

    @Getter
    @Setter
    private String description = "Default Desc";

    @Getter
    @Setter
    private int order = 1;

    @Getter
    @Setter
    private List<ItemStack> editorItems = new ArrayList<>();

    public DefaultKit(String kitName) {
        super(kitName);
    }

    public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            List<String> completions = new ArrayList<>();
            Player player = context.getPlayer();
            String input = context.getInput();

            for (DefaultKit kit : Samurai.getInstance().getMapHandler().getKitManager().getDefaultKits()) {
                if (StringUtils.startsWith(kit.getName(), input)) {
                    completions.add(kit.getName());
                }
            }

            return completions;
        }

    }

    public static class Type implements ContextResolver<DefaultKit, BukkitCommandExecutionContext> {

        @Override
        public DefaultKit getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
            Player sender = c.getPlayer();
            String source = c.popFirstArg();
            DefaultKit kit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(source);

            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "Default kit '" + source + "' not found.");
                return null;
            }

            return kit;
        }
    }
}
