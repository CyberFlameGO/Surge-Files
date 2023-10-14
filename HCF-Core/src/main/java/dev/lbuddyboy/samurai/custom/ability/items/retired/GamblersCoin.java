package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class GamblersCoin extends AbilityItem {

    private final List<PotionEffect> POSITIVE_EFFECTS = Arrays.asList(
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1),
            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 1)
    );

    private final List<PotionEffect> NEGATIVE_EFFECTS = Arrays.asList(
            new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0),
            new PotionEffect(PotionEffectType.POISON, 20 * 5, 0)
    );

    public GamblersCoin() {
        super("GamblersCoin");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);

        int random = ThreadLocalRandom.current().nextInt(100);

        if (random <= 50) {
            for (PotionEffect effect : NEGATIVE_EFFECTS) {
                player.addPotionEffect(effect);
            }
            sendActivationMessages(player,
                    new String[]{
                            "You have activated " + getName() + CC.WHITE + "!",
                            "You have been given the negative effects."
                    }, null, null);
        } else {
            for (PotionEffect effect : POSITIVE_EFFECTS) {
                player.addPotionEffect(effect);
            }
            sendActivationMessages(player,
                    new String[]{
                            "You have activated " + getName() + CC.WHITE + "!",
                            "You have been given the positive effects."
                    }, null, null);
        }

        return true;
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : 60;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.GOLD_NUGGET)
                .name(CC.translate("&g&lGamblers Coin"))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon right-clicking, you have",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fa 50/50 chance to get either",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fStrength II & Resistance II for",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &f5 seconds or Poison & Slowness",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &ffor 5 seconds.",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .modelData(12)
                .build();
    }

    @Override
    public ShapedRecipe getRecipe() {
        NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace("'", "").replace(" ", "_")));
        ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

        recipe.shape("AAA", "BBB", "AAA");
        recipe.setIngredient('A', Material.EMERALD);
        recipe.setIngredient('B', Material.SPIDER_EYE);

        return recipe;
    }

    @Override
    public List<Material> getRecipeDisplay() {
        return Arrays.asList(
                Material.EMERALD,
                Material.EMERALD,
                Material.EMERALD,

                Material.SPIDER_EYE,
                Material.SPIDER_EYE,
                Material.SPIDER_EYE,

                Material.EMERALD,
                Material.EMERALD,
                Material.EMERALD
        );
    }

    @Override
    public String getName() {
        return CC.translate("&g&lGamblers Coin");
    }

    @Override
    public int getAmount() {
        return 2;
    }
}
