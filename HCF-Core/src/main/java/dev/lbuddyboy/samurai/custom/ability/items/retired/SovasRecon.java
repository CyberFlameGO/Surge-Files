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
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SovasRecon extends AbilityItem {

    public SovasRecon() {
        super("SovasRecon");
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (event.getBow() == null) return;
        if (!event.getBow().isSimilar(getPartnerItem())) return;

        Player player = (Player) event.getEntity();

        if (player.getWorld().getEnvironment() == World.Environment.THE_END || player.getWorld().getEnvironment() == World.Environment.NETHER)
            return;

        setCooldown(player);
        setGlobalCooldown(player);

        event.getProjectile().setMetadata("recon", new FixedMetadataValue(Samurai.getInstance(), true));

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!",
                        "Whenever your arrow lands you will mark",
                        "anyone within 15 blocks the glowing effect."
                }, null, null);

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (event.getHitBlock() == null) return;
        if (!projectile.hasMetadata("recon")) return;

        List<Player> affected = new ArrayList<>();
        for (Entity entity : projectile.getNearbyEntities(15, 15, 15)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                affected.add(target);
            }
        }

        for (Player player : affected) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 5, 1));
        }

    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : 60;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.BOW)
                .name(CC.translate("&g&lSova's Recon"))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon shooting this bow, the arrow",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fthat hits the ground. Anyone within",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &f15 blocks of it will be given the",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fglowing affect.",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .modelData(8)
                .data((short) (Material.BOW.getMaxDurability() - 25))
                .build();
    }

    @Override
    public ShapedRecipe getRecipe() {
        NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace("'", "").replace(" ", "_")));
        ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.COMPASS);
        recipe.setIngredient('B', Material.BOW);

        return recipe;
    }

    @Override
    public List<Material> getRecipeDisplay() {
        return Arrays.asList(
                Material.COMPASS,
                Material.COMPASS,
                Material.COMPASS,

                Material.COMPASS,
                Material.BOW,
                Material.COMPASS,

                Material.COMPASS,
                Material.COMPASS,
                Material.COMPASS
        );
    }

    @Override
    public String getName() {
        return CC.translate("&g&lSova's Recon");
    }

    @Override
    public int getAmount() {
        return 1;
    }
}
