package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Crawler extends AbilityItem {

    public Crawler() {
        super("Crawler");
    }

    public static List<String> blockedMaterials = Arrays.asList("BEDROCK", "CHEST", "_GATE", "MOB_SPAWNER", "_DOOR", "WATER", "LAVA");

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 30L : 45L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.RABBIT_FOOT).data((short) 1)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click this to spawn a glass",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fblock above your head to put you in",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fa crawling animation to provide more",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fperspective.",
                        " ",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &4&lBLOCKED BLOCKS&c: " + StringUtils.join(blockedMaterials.stream().map(String::toLowerCase).map(s -> WordUtils.capitalize(s.replace("_", ""))).collect(Collectors.toList()), "s, "),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cYou cannot use this ability on these blocks.",
                        " "
                ).modelData(10).build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lCrawler");
    }

    @Override
    public int getAmount() {
        return 3;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Location loc = player.getEyeLocation();
        for (String mat : blockedMaterials) {
            if (loc.getBlock().getType().name().contains(mat)) {
                player.sendMessage(CC.translate("&cCould not use this ability item due to a blocked material being on your body."));
                return false;
            }
        }

        setGlobalCooldown(player);
        setCooldown(player);

        Material before = loc.getBlock().getType();
        BlockData blockData = loc.getBlock().getBlockData().clone();
        loc.getBlock().setType(Material.GLASS);

        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getBlock().setBlockData(blockData);
                loc.getBlock().setType(before);
            }
        }.runTaskLater(Samurai.getInstance(), 20 * 5);

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!"
                }, null, null);
        return true;
    }
}
