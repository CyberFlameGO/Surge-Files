package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PackAPunch extends AbilityItem {

    public PackAPunch() {
        super("PackAPunch");
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 45L : 90L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.NETHER_STAR).data((short) 1)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click this while under 4",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fhearts to receive Strength II &",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fSpeed III for 6 seconds.",
                        " "
                ).build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lPack a Punch");
    }

    @Override
    public int getAmount() {
        return 3;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getHealth() >= 8) {
            player.sendMessage(ChatColor.RED + "You must have less than 4 hearts to use this item.");
            return false;
        }

        setGlobalCooldown(player);
        setCooldown(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1));

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!"
                }, null, null);
        return true;
    }
}
