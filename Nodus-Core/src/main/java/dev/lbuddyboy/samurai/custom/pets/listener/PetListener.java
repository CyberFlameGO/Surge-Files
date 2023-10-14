package dev.lbuddyboy.samurai.custom.pets.listener;

import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.custom.pets.event.PetActivateEvent;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PetListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getHand() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        for (IPet pet : Samurai.getInstance().getPetHandler().getPets().values()) {
            if (!pet.isClickable()) continue;
            if (!pet.isThisPet(item)) continue;

            if (Feature.PETS.isDisabled()) {
                player.sendMessage(CC.translate("&cPets are currently disabled."));
                return;
            }

            event.setCancelled(true);

            if (pet.getCooldown().onCooldown(player)) {
                player.sendMessage(CC.translate("&cYou cannot use this pet for " + pet.getCooldown().getRemaining(player) + "."));
                continue;
            }

            Bukkit.getPluginManager().callEvent(new PetActivateEvent(player, pet));

            if (Samurai.getInstance().getBattlePassHandler() != null) {
                Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                    progress.setPetsUsed(progress.getPetsUsed() + 1);
                    progress.setUsePet(true);
                    progress.requiresSave();

                    Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                });
            }

            int levelBefore = pet.getLevel(item);
            pet.proc(player, levelBefore);

            if (levelBefore >= pet.getMaxLevel()) {
                player.getInventory().setItem(event.getHand(), pet.addXP(item, ThreadLocalRandom.current().nextInt(100, 500)));
                int level = pet.getLevel(item);
                if (level > levelBefore) {
                    if (Samurai.getInstance().getBattlePassHandler() != null) {
                        Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                            progress.setPetsLeveledUp(progress.getPetsUsed() + 1);
                            progress.setLevelUpPet(true);
                            progress.requiresSave();

                            Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                        });
                    }
                }
            }

            player.sendMessage(CC.translate("&6&lPETS &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &aYou have just used the " + pet.getDisplayName() + "&a!"));
            break;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (IPet pet : Samurai.getInstance().getPetHandler().getPets().values()) {
            pet.getCooldown().cleanUp();
        }
    }

    @EventHandler
    public void onDrag(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = player.getItemOnCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (cursor == null || cursor.getType() == Material.AIR) return;
        if (cursor.getType() != Samurai.getInstance().getPetHandler().getPetCandy().getType()) return;
        if (!cursor.isSimilar(Samurai.getInstance().getPetHandler().getPetCandy())) return;
        if (currentItem == null) return;
        if (currentItem.getType() != Material.PLAYER_HEAD) return;

        for (IPet pet : Samurai.getInstance().getPetHandler().getPets().values()) {
            if (!pet.isClickable()) continue;
            if (!pet.isThisPet(currentItem)) continue;

            if (Feature.PETS.isDisabled()) {
                player.sendMessage(CC.translate("&cPets are currently disabled."));
                return;
            }

            int level = pet.getLevel(currentItem);
            if (level >= pet.getMaxLevel()) {
                player.sendMessage(CC.translate("&cThat pet is already maxed level!"));
                return;
            }

            player.setItemOnCursor(ItemUtils.takeItem(cursor));
            event.setCancelled(true);

            if (Samurai.getInstance().getBattlePassHandler() != null) {
                Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                    progress.setPetCandyUsed(progress.getPetsUsed() + 1);
                    progress.setUsePetCandy(true);
                    progress.requiresSave();

                    Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                });
            }

            event.setCurrentItem(pet.setLevel(currentItem, level + 1));
            player.sendMessage(CC.translate("&6&lPETS &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &aYou have just leveled up the " + pet.getDisplayName() + "&a pet!"));
            break;
        }
    }

}
