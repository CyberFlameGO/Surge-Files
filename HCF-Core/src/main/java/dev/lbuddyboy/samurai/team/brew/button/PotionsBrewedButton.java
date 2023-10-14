package dev.lbuddyboy.samurai.team.brew.button;

import dev.lbuddyboy.samurai.deathmessage.util.MobUtil;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.brew.BrewType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/08/2021 / 10:20 PM
 * HCTeams / dev.lbuddyboy.samurai.team.brew.button
 */

@AllArgsConstructor
public class PotionsBrewedButton extends Button {

    private final BrewType type;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack stack = type.getDisplayItem().clone();
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.setBasePotionData(type.getPotionData());
        List<String> lore = CC.translate(Arrays.asList(
                "",
                "&6Time Until Brewed" + "&7: " + getBrewTime(player),
                "&6Total " + type.getId() + "&7: " + getBrewed(player),
                "",
                "&7Left Click to withdraw all the potions",
                "&7Right Click to toggle the brew process",
                "",
                "&6&lREQUIRED MATERIALS"
        ));
        for (Material material : type.getRequiredMaterials()) {
            String mat = MobUtil.getItemName(material);
            lore.add(CC.translate("&7- " + mat));
        }
        meta.setLore(lore);
        meta.setDisplayName(CC.translate("&6" + type.getId() + "'s Brewed"));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        if (clickType == ClickType.LEFT) {
            if (getBrewed(player) >= 1) {
                int potsGiven = 0;
                int i = 0;
                for (ItemStack stack : player.getInventory().getStorageContents()) {
                    if (stack == null || stack.getType() == Material.AIR) {
                        ItemStack potion = new ItemStack(type == BrewType.HEALTHII ? Material.SPLASH_POTION : Material.POTION, 1);
                        PotionMeta meta = (PotionMeta) potion.getItemMeta();
                        meta.setBasePotionData(type.getPotionData());
                        potion.setItemMeta(meta);

                        player.getInventory().setItem(i, potion);
                        potsGiven++;
                        if (getBrewed(player) - potsGiven <= 0)
                            break;
                    }
                    ++i;
                }
                if (type == BrewType.HEALTHII) {
                    team.setHealthPotsBrewed(team.getBrewedHealthPots() - potsGiven);
                } else if (type == BrewType.SPEEDII) {
                    team.setSpeedBrewed(team.getBrewedSpeedPots() - potsGiven);
                } else if (type == BrewType.FRES) {
                    team.setFresBrewed(team.getBrewedFresPots() - potsGiven);
                } else if (type == BrewType.INVISI) {
                    team.setInvisBrewed(team.getBrewedInvisPots() - potsGiven);
                }
            }
        } else if (clickType == ClickType.RIGHT) {
            toggleBrew(player);
        }
    }

    private void toggleBrew(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        if (type == BrewType.INVISI) {
            if (team.getStartedBrewingInvis() == 0) {
                team.setStartedBrewingInvis(System.currentTimeMillis() + 10_000L);
            } else {
                team.setStartedBrewingInvis(0);
            }
        } else if (type == BrewType.FRES) {
            if (team.getStartedBrewingFres() == 0) {
                team.setStartedBrewingFres(System.currentTimeMillis() + 10_000L);
            } else {
                team.setStartedBrewingFres(0);
            }
        } else if (type == BrewType.SPEEDII) {
            if (team.getStartedBrewingSpeeds() == 0) {
                team.setStartedBrewingSpeeds(System.currentTimeMillis() + 10_000L);
            } else {
                team.setStartedBrewingSpeeds(0);
            }
        } else if (type == BrewType.HEALTHII) {
            if (team.getStartedBrewingHealths() == 0) {
                team.setStartedBrewingHealths(System.currentTimeMillis() + 10_000L);
            } else {
                team.setStartedBrewingHealths(0);
            }
        }
    }

    private int getBrewed(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        if (type == BrewType.HEALTHII) {
            return team.getBrewedHealthPots();
        }
        if (type == BrewType.SPEEDII) {
            return team.getBrewedSpeedPots();
        }
        if (type == BrewType.FRES) {
            return team.getBrewedFresPots();
        }
        if (type == BrewType.INVISI) {
            return team.getBrewedInvisPots();
        }
        return 0;
    }

    private String getBrewTime(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        if (type == BrewType.HEALTHII) {
            long dura = ((team.getStartedBrewingHealths() - System.currentTimeMillis()) / 1000);
            if (team.getStartedBrewingHealths() <= 0 ) {
                return ChatColor.RED + "Off";
            }
            if (dura < 0) {
                return CC.RED + "Invalid Brewing Materials";
            }
            return "" + dura + "s";
        }
        if (type == BrewType.SPEEDII) {
            long dura = ((team.getStartedBrewingSpeeds() - System.currentTimeMillis()) / 1000);

            if (team.getStartedBrewingSpeeds() <= 0) {
                return ChatColor.RED + "Off";
            }
            if (dura < 0) {
                return CC.RED + "Invalid Brewing Materials";
            }
            return "" + dura + "s";
        }
        if (type == BrewType.FRES) {
            long dura = ((team.getStartedBrewingFres() - System.currentTimeMillis()) / 1000);

            if (team.getStartedBrewingFres() <= 0) {
                return ChatColor.RED + "Off";
            }
            if (dura < 0) {
                return CC.RED + "Invalid Brewing Materials";
            }
            return "" + dura + "s";
        }
        if (type == BrewType.INVISI) {
            long dura = ((team.getStartedBrewingFres() - System.currentTimeMillis()) / 1000);

            if (team.getStartedBrewingInvis() <= 0) {
                return ChatColor.RED + "Off";
            }
            if (dura < 0) {
                return CC.RED + "Invalid Brewing Materials";
            }
            return "" + dura + "s";
        }
        return "0";
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
}
