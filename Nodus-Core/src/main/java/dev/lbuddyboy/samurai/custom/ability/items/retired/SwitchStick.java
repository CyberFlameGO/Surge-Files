package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class SwitchStick extends AbilityItem {

    public SwitchStick() {
        super("SwitchStick");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            ItemStack item = attacker.getItemInHand();
            boolean partnerItem = isPartnerItem(item);

            if (!partnerItem) {
                return;
            }

            if (DTRBitmask.KOTH.appliesAt(attacker.getLocation()) || DTRBitmask.CITADEL.appliesAt(attacker.getLocation())) {
                event.setCancelled(true);
                attacker.sendMessage(CC.RED + "Ability items cannot be used in koths/citadels!");
                return;
            }

            if (DTRBitmask.KOTH.appliesAt(entity.getLocation()) || DTRBitmask.CITADEL.appliesAt(entity.getLocation())) {
                event.setCancelled(true);
                attacker.sendMessage(CC.RED + "Ability items cannot be used in koths/citadels!");
                return;
            }

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            Location loc = entity.getLocation();
            loc.setYaw(loc.getYaw() + 180);
            entity.teleport(loc);

            setCooldown(attacker);
            consume(attacker, item);

            sendActivationMessages(attacker,
                    new String[]{
                            "Successfully hit " + CC.MAIN + entity.getName() + CC.WHITE + " with a " + getName() + CC.WHITE + "!"
                    },
                    entity,
                    new String[]{
                            CC.MAIN + attacker.getName() + " has hit you " + CC.WHITE + " with a " + getName() + CC.WHITE + "!"
                    });
        }
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 15L : 30L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.STICK)
                .name(CC.translate(getName()))
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRotates your opponent by 180 degrees",
                        " "
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lSwitch Stick");
    }

    @Override
    public int getAmount() {
        return 4;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }
}
