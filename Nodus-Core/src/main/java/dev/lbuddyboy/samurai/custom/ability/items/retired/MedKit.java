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
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MedKit extends AbilityItem {

    private static final int ACTIVATE_DELAY = 5; // how long it's active for

    private final List<Player> activationMap = new ArrayList<>();

    public MedKit() {
        super("MedKit");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!",
                        "You will receive full health in 5 seconds."
                }, null, null);
        activationMap.add(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (activationMap.contains(player)) {
                    player.setHealth(20);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 1));
                    activationMap.remove(player);

                    sendActivationMessages(player,
                            new String[]{
                                    "You have activated " + getName() + CC.WHITE + "!",
                                    "You are now full health."
                            }, null, null);
                }
            }
        }.runTaskLater(Samurai.getInstance(), 20 * ACTIVATE_DELAY);

        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activationMap.remove(event.getPlayer());
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : 60;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.PAPER)
                .name(CC.translate("&g&lMed Kit"))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon right-clicking, after 5 ",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fseconds. You will be granted ",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &ffull health. ",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .modelData(3)
                .build();
    }

    @Override
    public ShapedRecipe getRecipe() {
        NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace("'", "").replace(" ", "_")));
        ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

        recipe.shape("AAA", "BBB", "AAA");
        recipe.setIngredient('A', Material.GHAST_TEAR);
        recipe.setIngredient('B', Material.PAPER);

        return recipe;
    }

    @Override
    public List<Material> getRecipeDisplay() {
        return Arrays.asList(
                Material.GHAST_TEAR,
                Material.GHAST_TEAR,
                Material.GHAST_TEAR,

                Material.PAPER,
                Material.PAPER,
                Material.PAPER,

                Material.GHAST_TEAR,
                Material.GHAST_TEAR,
                Material.GHAST_TEAR
        );
    }

    @Override
    public String getName() {
        return CC.translate("&g&lMed Kit");
    }

    @Override
    public int getAmount() {
        return 3;
    }
}
