package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.Pair;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class Turtle extends AbilityItem {

    private static final int ACTIVE_TIME = 10; // how long it's active for

    private final Map<Player, Pair<Instant, Integer>> activationMap = new ConcurrentHashMap<>();

    public Turtle() {
        super("Turtle");
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Pair<Instant, Integer> active = activationMap.get(entity);
            if (active == null)
                return;

            int hits = active.second;

            activationMap.put(entity, new Pair<>(active.first, ++hits));
        }
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!",
                        "You will receive up to 5 hearts of absorption."
                }, null, null);
        activationMap.put(player, new Pair<>(Instant.now().plusSeconds(ACTIVE_TIME), 0));

        Bukkit.getScheduler().runTaskLaterAsynchronously(Samurai.getInstance(), () -> {

            int hits = Math.min(activationMap.get(player).second, 10);

            float absorption = hits == 0 ? 0.0f : hits / 2.0f;

            if (absorption > 0) {
                setAbsorption(player, absorption);
                player.sendMessage(ChatColor.RED + "You got " + absorption + " hearts of absorption");
            }
        }, 20 * 5);

        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activationMap.remove(event.getPlayer());
    }

    private void setAbsorption(Player player, float hearts) {
        EntityPlayer entity = ((CraftPlayer) player).getHandle();
        entity.getBukkitEntity().setAbsorptionAmount((hearts * 2.0f) + entity.getBukkitEntity().getAbsorptionAmount());
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
        return SOTWCommand.isPartnerPackageHour() ? 90L : TimeUnit.MINUTES.toSeconds(3);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.SCUTE)
                .name(CC.translate("&g&lTurtle"))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon right-clicking, each hit received ",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fwill equate to half a heart of absorption",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fafter a 5 second period (Max 5 Hearts).",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lTurtle");
    }

    @Override
    public int getAmount() {
        return 3;
    }
}
