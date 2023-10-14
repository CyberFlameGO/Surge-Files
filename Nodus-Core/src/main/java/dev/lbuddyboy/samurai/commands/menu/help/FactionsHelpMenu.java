package dev.lbuddyboy.samurai.commands.menu.help;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/12/2021 / 3:39 AM
 * SteelHCF-main / com.steelpvp.hcf.command.menu.help
 */
public class FactionsHelpMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&aFactions Help");
    }

    @Override
    public Map<Integer, Button> getButtons(Player var1) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(13, new BackButton(new MainHelpMenu()));

        int i = 26;
        buttons.put(++i, Button.fromItem(new ItemBuilder(Material.GREEN_BANNER)
                .displayName(CC.translate("&aFaction"))
                .lore(CC.translate("&fWhat is a &aFaction&f?",
                        "&7 * &fA &aFaction&f is a team full of your friends or players ",
                        "&7 * &fthat combine as one whole and fight other teams on the server.",
                        "&7 * &fThese factions also have a few things that you can see in this menu.",
                        "&7 * &fSome things they have is DTR, DTR Freeze, Max Members, Max Allies, Points, and Homes."
                ))
                .build()));
        buttons.put(++i, Button.fromItem(new ItemBuilder(Material.RED_BANNER)
                .displayName(CC.translate("&cFaction DTR"))
                .lore(CC.translate(
                        Arrays.asList(
                                "&fWhat is &cFaction DTR&f?",
                                "&7 * &cFaction DTR&f is a number that represents how many more deaths",
                                "&7 * &fin your team that you can have, until you go raidable.",
                                "&f",
                                "&fWhat is &cDTR Freeze&f?",
                                "&7 * &cDTR Freeze&f is a state of your team where no one can join",
                                "&7 * &f or you cannot disband your team. This is a &c&n25-30 minute",
                                "&7 * &fcooldown, but whenever the time hits zero your &cDTR&f will be",
                                "&7 * &fset back to the maximum amount.",
                                "&f",
                                "&fHow is your &cMax DTR&f determined?",
                                "&7 * &fyour teams &cMax DTR&f is determined by each member, so",
                                "&7 * &f1 player equals 1 DTR.",
                                "&f",
                                "&fWhat is &cRaidable&f?",
                                "&7 * &fRaidable is the state of your team once your &cDTR&f as explained",
                                "&7 * &fabove is less than or equal to 0.",
                                "&f",
                                "&fHow do you go &cRaidable&f?",
                                "&7 * &fBasically, every death in your team you lose &c&n1 DTR&f.",
                                "&7 * &fThen, if your team goes below or equal to &c&n0 DTR&f. Your",
                                "&7 * &ffactions current state will be set to &cRaidable&f for &e&n30 minutes&f.",
                                "&f",
                                "&fWhat happens if you do you go &cRaidable&f?",
                                "&7 * &fIf you do manage to go &cRaidable&f. Your base will be able to be",
                                "&7 * &fblocks can be broken, interacted, and placed with."
                        )
                ))
                .build()));
        buttons.put(++i, Button.fromItem(new ItemBuilder(Material.YELLOW_BANNER)
                .displayName(CC.translate("&eFaction Home"))
                .lore(CC.translate(Arrays.asList(
                                "&fWhat is a &eFaction Home&f?",
                                "&7 * &fA &eFaction Home&f is a location where you can teleport to from anywhere,",
                                "&7 * &fbesides the end.",
                                "",
                                "&fHow do I teleport to my &eFaction Home&f?",
                                "&7 * &e/f home&f is a command that teleports you to your team home",
                                "&7 * &e/f sethome&f is a command that sets your teams home &7(Captain+)"
                        )
                )).build()));
        buttons.put(++i, Button.fromItem(new ItemBuilder(Material.PURPLE_BANNER)
                .displayName(CC.translate("&5Faction Rally"))
                .lore(CC.translate(Arrays.asList(
                        "&fWhat is a &5Faction Rally&f?",
                        "&7 * &5Faction Rally&f is a command that shows your team",
                        "&7 * &fmembers your location using the Lunar Client API Waypoints"
                )))
                .build()));
        buttons.put(++i, Button.fromItem(new ItemBuilder(Material.PINK_BANNER)
                .displayName(CC.translate("&dFaction Focus"))
                .lore(CC.translate(Arrays.asList(
                        "&fWhat is a &dFaction Focus&f?", "&7 * &dFaction Focus&f is a command that shows your team", "&7 * &fmembers a Lunar Client API Waypoint with a designated faction.", "&7 * &fThis also shows the designated faction's information."
                )))
                .build()));
        buttons.put(++i, Button.fromItem(new ItemBuilder(Material.CYAN_BANNER)
                .displayName(CC.translate("&3Faction Top"))
                .lore(CC.translate(Arrays.asList(
                        "&fWhat is &3Faction Top&f?", "&7 * &3Faction Top&f is a system that sorts the factions", "&7 * &fwith the most faction points. Get a kill to gain a point", "&7 * &f, but die and lose a point"
                )))
                .build()));
//		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.LIGHT_GRAY_BANNER)
//				.displayName(CC.translate("&7Faction Settings"))
//				.lore(CC.translate("&fWhat is &7Faction Settings&f?", "&7 * &7Faction Settings&f is a system that allows them to have", "&7 * &fto have more interaction & convenience with your claim."))
//				.build()));

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        int size = super.size(buttons) + 9;
        return (Math.min(size, 54));
    }
}
