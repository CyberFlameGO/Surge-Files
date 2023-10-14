package dev.lbuddyboy.samurai.team.brew.menu;

import dev.lbuddyboy.samurai.deathmessage.util.MobUtil;
import dev.lbuddyboy.samurai.team.brew.button.InfoButton;
import dev.lbuddyboy.samurai.team.brew.button.PotionsBrewedButton;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.brew.BrewType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/08/2021 / 9:54 PM
 * HCTeams / dev.lbuddyboy.samurai.team.brew.menu
 */
public class BrewMenu extends Menu {

    private final Button FILLER_ITEM = new Button() {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).build();
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

        }
    };

    @Getter
    public static final List<Material> brewingMaterials = Arrays.asList(
            Material.NETHER_WART,
            Material.GLISTERING_MELON_SLICE,
            Material.GLOWSTONE_DUST,
            Material.GUNPOWDER,
            Material.SUGAR,
            Material.FERMENTED_SPIDER_EYE,
            Material.MAGMA_CREAM,
            Material.REDSTONE,
            Material.GLASS_BOTTLE,
            Material.GOLDEN_CARROT
    );
    private List<Integer> slots = Arrays.asList(29, 30, 31, 32, 33, 38, 39, 40, 41, 42);

    @Override
    public String getTitle(Player player) {
        return "Team Brew Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;

        for (Material material : getBrewingMaterials()) {
            buttons.put(slots.get(i), new BrewMaterialButton(material));
            ++i;
        }

        buttons.put(getSlot(2, 1), new PotionsBrewedButton(BrewType.HEALTHII));
        buttons.put(getSlot(3, 1), new PotionsBrewedButton(BrewType.SPEEDII));
        buttons.put(getSlot(4, 1), new InfoButton());
        buttons.put(getSlot(5, 1), new PotionsBrewedButton(BrewType.INVISI));
        buttons.put(getSlot(6, 1), new PotionsBrewedButton(BrewType.FRES));

        buttons.put(getSlot(0, 0), FILLER_ITEM);
        buttons.put(getSlot(0, 1), FILLER_ITEM);
        buttons.put(getSlot(0, 2), FILLER_ITEM);
        buttons.put(getSlot(0, 3), FILLER_ITEM);
        buttons.put(getSlot(0, 4), FILLER_ITEM);
        buttons.put(getSlot(0, 5), FILLER_ITEM);

        buttons.put(getSlot(8, 0), FILLER_ITEM);
        buttons.put(getSlot(8, 1), FILLER_ITEM);
        buttons.put(getSlot(8, 2), FILLER_ITEM);
        buttons.put(getSlot(8, 3), FILLER_ITEM);
        buttons.put(getSlot(8, 4), FILLER_ITEM);
        buttons.put(getSlot(8, 5), FILLER_ITEM);

        return buttons;
    }

    @AllArgsConstructor
    public static class BrewMaterialButton extends Button {

        private final Material material;

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());

            String matName = MobUtil.getItemName(material);
            return new ItemBuilder(this.material)
                    .lore(CC.translate(Arrays.asList(
                            "",
                            "&7Team's Balance: " + team.getBrewingMaterial(this.material),
                            "",
                            "&6&lDEPOSIT",
                            "&7Right Click to deposit x1 " + matName,
                            "&7Press Q to deposit ALL " + matName,
                            "",
                            "&6&lWITHDRAW",
                            "&7Left Click to withdraw x1 " + matName,
                            "&7Shift Left Click to withdraw ALL " + matName,
                            ""
                    )))
                    .displayName(CC.translate("&6" + matName)).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            String matName = MobUtil.getItemName(material);

            PlayerInventory inventory = player.getInventory();
            Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());
            if (clickType == ClickType.DROP) {
                int amount = getAmount(player);

                int i = -1;
                for (ItemStack content : player.getInventory().getContents()) {
                    ++i;
                    if (content == null || content.getType() == Material.AIR) continue;
                    if (content.getType() != material) continue;
                    if (!content.getEnchantments().isEmpty()) continue;

                    player.getInventory().setItem(i, null);
                }

                team.setBrewingMaterials(this.material, team.getBrewingMaterial(this.material) + amount);
                player.sendMessage(CC.translate("&aYou have successfully deposited x" + amount + " in to your team's " + matName + " balance"));
            } else if (clickType == ClickType.SHIFT_LEFT) {
                if (team.getBrewingMaterial(this.material) <= 0) {
                    player.sendMessage(CC.translate("&cYour team does not have the required materials for this action."));
                    return;
                }
                int amount = team.getBrewingMaterial(this.material);
                InventoryUtils.addAmountToInventory(player.getInventory(), new ItemStack(this.material, amount));
                team.setBrewingMaterials(this.material, 0);
            } else if (clickType == ClickType.RIGHT || clickType == ClickType.LEFT) {
                int toTake = 1;

                if (clickType == ClickType.RIGHT) {
                    if (getAmount(player) < 1) {
                        player.sendMessage(CC.translate("&cYou do not have the required materials for this action."));
                        return;
                    }
                    InventoryUtils.changeInventoryAmount(player.getInventory(), new ItemStack(this.material), 1, false, false);
                    team.setBrewingMaterials(this.material, team.getBrewingMaterial(this.material) + toTake);
                    player.sendMessage(CC.translate("&aYou have successfully deposited x" + toTake + " in to your team's " + matName + " balance"));
                } else {
                    if (team.getBrewingMaterial(this.material) <= 0) {
                        player.sendMessage(CC.translate("&cInsufficient materials."));
                        return;
                    }
                    player.getInventory().addItem(new ItemStack(this.material));
                    team.setBrewingMaterials(this.material, team.getBrewingMaterial(this.material) - toTake);
                    player.sendMessage(CC.translate("&aYou have successfully withdrawn x" + toTake + " from your team's " + matName + " balance"));
                }
            }
        }

        public int getAmount(Player player) {
            int count = 0;
            PlayerInventory inv = player.getInventory();
            for (ItemStack stack : inv.all(this.material).values()) {
                if (stack != null && stack.getType() == this.material) {
                    if (!stack.getEnchantments().isEmpty()) continue;

                    count = count + stack.getAmount();
                }
            }
            return count;
        }

        private void take(ItemStack item, int toTake) {
            item.setAmount(item.getAmount() - toTake);
        }
    }

}
