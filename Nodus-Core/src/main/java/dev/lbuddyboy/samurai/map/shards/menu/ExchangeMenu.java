package dev.lbuddyboy.samurai.map.shards.menu;

import dev.lbuddyboy.samurai.persist.maps.ShardMap;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.text.NumberFormat;
import java.util.*;

public class ExchangeMenu extends Menu {

    private final ShardMap gemMap = Samurai.getInstance().getShardMap();

    @Override
    public String getTitle(Player player) {
        return "Shard Exchange";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&fShard: &6◆" + gemMap.getShards(player) + " &f" + SymbolUtil.STICK + " &fBalance: &2$" + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(player.getUniqueId())));
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }
        });

        buttons.put(22, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&fExchange &6◆1");
            }

            @Override
            public List<String> getDescription(Player player) {
                return CC.translate(Arrays.asList("&7" + CC.STAR + " &fCost: &6$15,000", " ", "&7&oClick to exchange your balance."));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GOLD_NUGGET;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());

                if (balance < 15000) {
                    player.sendMessage(CC.translate("&cInsufficient funds."));
                    return;
                }

                FrozenEconomyHandler.setBalance(player.getUniqueId(), balance - 15000);
                gemMap.addShards(player.getUniqueId(), 1);
                player.sendMessage(CC.translate("&aPurchased &6◆1"));

            }
        });

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }


}
