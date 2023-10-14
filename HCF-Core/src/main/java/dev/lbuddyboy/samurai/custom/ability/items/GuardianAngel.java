package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class GuardianAngel extends AbilityItem {

    public GuardianAngel() {
        super("GuardianAngel");

        this.name = "guardian-angel";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        player.setHealth(20);
        player.setFoodLevel(player.getFoodLevel());

        MessageConfiguration.GUARDIAN_ANGEL_CLICKER.sendListMessage(player, "%ability-name%", this.getName());
        return true;
    }
}
