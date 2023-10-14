package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class SkybridgeGrace extends AbilityItem {

    public SkybridgeGrace() {
        super("SkybridgeGrace");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().isAir()) {
            player.sendMessage(CC.translate("&cYou need to be in the air to do this."));
            return false;
        }

        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!",
                        "You received Resistance 5 for 3 seconds."
                }, null, null);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 3, 4));

        return true;
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
        return ItemBuilder.of(Material.FEATHER)
                .name(CC.translate("&g&lSkyBridge Grace"))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon right-clicking you will receive resistance ",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &f5 for 3 seconds. This item can only be used in the air.",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lSkyBridge Grace");
    }

    @Override
    public int getAmount() {
        return 3;
    }
}
