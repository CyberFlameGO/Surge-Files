package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public final class BackInTime extends AbilityItem {

    private static Cooldown cooldown = new Cooldown();

    public BackInTime() {
        super("BackInTime");
    }

    private double defaultCD;

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 90L : TimeUnit.MINUTES.toSeconds(3);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.HORN_CORAL).data((short) 1)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click this to have no cooldown",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fon any of your hits for 8 seconds.",
                        " "
                ).modelData(11).build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lBack in Time");
    }

    @Override
    public int getAmount() {
        return 3;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        setGlobalCooldown(player);
        setCooldown(player);

        double original = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue();

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(0);

        defaultCD = original;

        cooldown.applyCooldown(player, 8);

        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(original);
            player.sendMessage(CC.translate("&cYour back in time has worn off."));
        }, 20 * 8);

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!"
                }, null, null);
        return true;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (cooldown.onCooldown(event.getPlayer())) {
            event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(defaultCD);
        }
    }

    @EventHandler
    public void onLeave(PlayerDeathEvent event) {
        if (cooldown.onCooldown(event.getEntity())) {
            event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(defaultCD);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (cooldown.onCooldown(player)) {
                    if (ArcherClass.isMarked(player)) return;

                    double attr = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();

                    event.setDamage((attr) * 1.20);
                }
            }
        }
    }

}
