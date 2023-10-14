package dev.lbuddyboy.samurai.team.menu.button;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.commands.menu.EditConfigurationMenu;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ConversationBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.chat.listeners.ChatListener;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RollbackButton extends Button {

    @NonNull private Team team;

    
    @Override
	public String getName(Player player) {
        return CC.translate("&eRollback Faction");
    }

    
    @Override
	public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        lore.add(CC.translate("&7Click to start the rollback process."));

        return lore;
    }

    
    @Override
	public Material getMaterial(Player player) {
        return Material.DIAMOND_PICKAXE;
    }

    
    @Override
	public void clicked(Player player, int i, ClickType clickType) {
        if (!player.hasPermission("foxtrot.manage.rollback")) {
            player.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        if (team.getClaims().size() == 0) {
            player.sendMessage(CC.translate("The faction needs to have a home to do this."));
            return;
        }

        for (Claim claim : team.getClaims()) {
            player.teleport(getCenter(claim.getMaximumPoint(), claim.getMinimumPoint()));
            break;
        }

        player.sendTitle(CC.translate("&2&lFACTION ROLLBACK"), CC.translate("&aPlease enter the amount of time you want to rollback. (Ex: 5m, 10m, etc, etc)"), 20, 20, 20);
        player.closeInventory();
        Conversation conversation = new ConversationBuilder(player, new ConversationFactory(Flash.getInstance()))
                .closeableStringPrompt(CC.translate("&aPlease enter the amount of time you want to rollback. (Ex: 5m, 10m, etc, etc)"), (conversationContext, value) -> {

                    for (Claim claim : team.getClaims()) {
                        Location lowerNE = claim.getMinimumPoint().clone();
                        lowerNE.setY(RollbackButton.getCenter(claim.getMinimumPoint(), claim.getMaximumPoint()).getY());

                        player.chat("/co rollback t:" + value + " r:" + ((int) lowerNE.distance(RollbackButton.getCenter(claim.getMinimumPoint(), claim.getMaximumPoint()))));
                        break;
                    }
                    player.sendTitle(CC.translate("&2&lFACTION ROLLBACK"), CC.translate("&aRollback was a success. If it is missing some areas. I suggest doing it manually, but first do /co undo"), 20, 20, 20);

                    return Prompt.END_OF_CONVERSATION;
                })
                .echo(false).build();

        player.beginConversation(conversation);
    }

    public static Location getCenter(Location loc1, Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x1 = Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1;
        int y1 = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
        int z1 = Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1;

        return new Location(Bukkit.getWorld("world"), minX + (x1 - minX) / 2.0D, minY + (y1 - minY) / 2.0D, minZ + (z1 - minZ) / 2.0D);
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack stack = super.getButtonItem(player);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }
}
