package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class LastChance extends AbilityItem {

    public LastChance() {
        super("LastChance");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!",
                        "Your pearl cooldown has been reset."
                }, null, null);

        EnderpearlCooldownHandler.clearEnderpearlTimer(player);
        thirySecondPearl.add(player.getUniqueId());

        return true;
    }

    private List<UUID> thirySecondPearl = new ArrayList<>();
    
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (event.getEntity().getShooter() instanceof Player) {
                if (thirySecondPearl.contains(((Player) event.getEntity().getShooter()).getUniqueId())) {
                    Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                        EnderpearlCooldownHandler.getEnderpearlCooldown().put(event.getEntity().getName(), System.currentTimeMillis() + 30_000L);
                    }, 5);
                    thirySecondPearl.remove(event.getEntity().getUniqueId());
                }
            }
        }
    }

    @Override
    public boolean isPartnerItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return false;

        if (!itemStack.hasItemMeta())
            return false;

        ItemMeta partnerItemMeta = partnerItem.getItemMeta();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!partnerItemMeta.getDisplayName().equalsIgnoreCase(itemMeta.getDisplayName()))
            return false;

        return Arrays.equals(partnerItemMeta.getLore().toArray(), itemMeta.getLore().toArray());
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : TimeUnit.MINUTES.toSeconds(1);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.CLOCK)
                .name(CC.translate("&g&lLastChance"))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon right-clicking your ender pearl cooldown will be",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &freset and if you pearl after that you will receive",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fa 30 second pearl cooldown.",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lLastChance");
    }

    @Override
    public int getAmount() {
        return 3;
    }
}
