package dev.lbuddyboy.samurai.custom.ability.crate;

import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PartnerCrateHandler implements Listener {

    private final FileConfig fileConfig = new FileConfig(Samurai.getInstance(), "partner_packages.yml");

    public PartnerCrateHandler() {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = fileConfig.getConfig();
        config.addDefault("package-crate-name", "&e&lAbility Package");
        config.addDefault("package-crate-lore", Arrays.asList(
                "",
                "&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " Right click to receive 1 set of a random ability item.",
                ""
        ));
        config.addDefault("package-commands", Arrays.asList("/raw %player% is raw", "/raw %player% is raw af"));

        config.options().copyDefaults(true);
        fileConfig.save();

        reloadConfig();
    }

    @Getter
    private ItemStack crateItem;

    @SneakyThrows
    public void reloadConfig() {
        FileConfiguration config = fileConfig.getConfig();
        config.load(fileConfig.getFile());

        crateItem = ItemBuilder.of(Material.ENDER_CHEST)
                .name(config.getString("package-crate-name"))
                .addToLore(config.getStringList("package-crate-lore").toArray(new String[0]))
                .build();

    }

    @EventHandler
    private void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null || item.getType() != Material.ENDER_CHEST) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (item.hasItemMeta() && crateItem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {

            event.setCancelled(true);

            InventoryUtils.removeAmountFromInventory(player.getInventory(), item, 1);

            List<AbilityItem> exclusives = Samurai.getInstance().getAbilityItemHandler().getAbilityItems()
                    .stream()
                    .filter(a -> !a.isExclusive()).toList();

            Firework fireWork = (Firework) player.getWorld().spawnEntity(player.getLocation().add(0, 3, 0), EntityType.FIREWORK);
            FireworkMeta fwMeta = fireWork.getFireworkMeta();

            fwMeta.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).
                    with(FireworkEffect.Type.BALL_LARGE)
                    .with(FireworkEffect.Type.STAR).withColor(Color.ORANGE).withColor(Color.YELLOW).withFade(Color.PURPLE).withFade(Color.RED).build());

            fireWork.setFireworkMeta(fwMeta);

            for (int i = 0; i < 2; i++) {
                AbilityItem partnerPackage = exclusives.get(ThreadLocalRandom.current().nextInt(exclusives.size()));
                if (!InventoryUtils.addAmountToInventory(player.getInventory(), partnerPackage.getPartnerItem())) {
                    player.getWorld().dropItemNaturally(player.getLocation(), partnerPackage.getPartnerItem());
                }
            }

            int random = ThreadLocalRandom.current().nextInt(100);

            if (random < 50) {
                List<ItemStack> BLOCKS = Arrays.asList(
                        new ItemStack(Material.DIAMOND_BLOCK, 16),
                        new ItemStack(Material.EMERALD_BLOCK, 16),
                        new ItemStack(Material.IRON_BLOCK, 16),
                        new ItemStack(Material.GOLD_BLOCK, 16)
                );

                ItemStack block = BLOCKS.get(ThreadLocalRandom.current().nextInt(0, BLOCKS.size()));
                if (!InventoryUtils.addAmountToInventory(player.getInventory(), block)) {
                    player.getWorld().dropItemNaturally(player.getLocation(), block);
                }
                return;
            }

            List<String> commands = Arrays.asList(
                    "crate give Alchemy %player% 1",
                    "crate give Enchantment %player% 1",
                    "crate give Silver %player% 1",
                    "crate give Gold %player% 1",
                    "crate give Diamond %player% 1",
                    "crate give Aura %player% 1",
                    "crate give Surge %player% 1"
            );
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.get(ThreadLocalRandom.current().nextInt(0, commands.size()))
                    .replaceAll("%player%", player.getName())
            );
        }

    }

}
