package dev.lbuddyboy.samurai.custom.ability.items.bard;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.TeamHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class PortableBard extends AbilityItem {

    static final int EFFECT_RANGE = 32;

    public static final List<PortableBardEffect> PORTABLE_BARD_EFFECTS = new ArrayList<>();
    public static final Map<String, PortableBardEffect> ITEM_STACK_PORTABLE_BARD_EFFECT_MAP = new HashMap<>();

    public PortableBard() {
        super("PortableBard");

        this.name = "portable-bard";
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        if (item.getItemMeta().getDisplayName() == null) return false;

        PortableBardEffect bardEffect = ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.get(item.getItemMeta().getDisplayName().toLowerCase());

        // if it's null then the only other thing it could be was the main item
        if (bardEffect == null) {
            new PortableBardMenu(event.getItem()).openMenu(player);
            return false;
        }

        // manually handle this here because portable bard is
        // special and allows you to open the menu but not use effects
        if (isOnCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage(getCooldownMessage(player));
            return false;
        }

        TeamHandler handler = Samurai.getInstance().getTeamHandler();
        Team team = handler.getTeam(player);
        List<Player> toEffect = new ArrayList<>();
        if (team != null) {
            for (Player onlineMember : team.getOnlineMembers()) {
                if (onlineMember.getWorld() != player.getWorld()) continue;
                if (player != onlineMember && player.getLocation().distance(onlineMember.getLocation()) <= EFFECT_RANGE) {
                    toEffect.add(onlineMember);
                }
            }
        }

        PotionEffect potionEffect = bardEffect.getPotionEffect();

        player.addPotionEffect(potionEffect, true);
        toEffect.forEach(mate -> mate.addPotionEffect(potionEffect, true));

        setCooldown(player);
        setGlobalCooldown(player);
        consume(player, item);

        player.sendMessage(CC.WHITE + "Successfully applied " + ItemUtils.getName(bardEffect.getStack()) + CC.WHITE + " to your team!");

        return true;
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(this.material)
                .name(getName())
                .setLore(CC.translate(this.lore))
                .modelData(4)
                .build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lPortable Bard");
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public boolean isPartnerItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
            return false;
        }

        PortableBardEffect effect = ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.get(item.getItemMeta().getDisplayName().toLowerCase());
        return effect != null || super.isPartnerItem(item);
    }

    @Override
    public void reload(File folder) {
        super.reload(folder);

        load();
    }

    public void load() {
        ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.clear();
        PORTABLE_BARD_EFFECTS.clear();

        for (String key : this.config.getConfigurationSection("portable-bards").getKeys(false)) {
            String displayName = config.getString("portable-bards." + key + ".display-name");
            List<String> lore = config.getStringList("portable-bards." + key + ".lore");
            Material material = Material.getMaterial(config.getString("portable-bards." + key + ".material"));
            String effectType = config.getString("portable-bards." + key + ".effect");
            int customModelData = config.getInt("portable-bards." + key + ".custom-model-data");
            int defaultAmount = config.getInt("portable-bards." + key + ".default-amount");
            int effectTime = config.getInt("portable-bards." + key + ".effect-time");
            int amplifier = config.getInt("portable-bards." + key + ".amplifier");
            PotionEffectType potionEffectType = PotionEffectType.getByName(effectType);
            if (potionEffectType == null) potionEffectType = PotionEffectType.SPEED;

            PORTABLE_BARD_EFFECTS.add(new PortableBardEffect(
                    ItemBuilder.of(material).name(CC.translate(displayName)).setLore(CC.translate(lore)).modelData(customModelData).amount(defaultAmount).build(),
                    potionEffectType,
                    effectTime,
                    effectTime,
                    amplifier
            ));
        }
        for (PortableBardEffect effect : PORTABLE_BARD_EFFECTS) {
            ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.put(effect.toItemStack().getItemMeta().getDisplayName().toLowerCase(), effect);
        }
    }

}
