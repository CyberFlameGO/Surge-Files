package dev.lbuddyboy.samurai.commands.menu.help;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
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
public class GeneralHelpMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&aGeneral Help");
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(13, new BackButton(new MainHelpMenu()));

		int i = 26;
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.RAIL)
				.displayName(CC.translate("&eClaiming"))
				.lore(CC.translate(Arrays.asList(
						"&fWhere to claim for action?",
						"&7 * &eSouth Road&f is where all of the action is at.",
						"&f",
						"&fWhere to claim at if you're new to HCF?",
						"&7 * &eNorth, East, or West Road&f are isolated usually and less populated."
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.BARRIER)
				.displayName(CC.translate("&4Deathban"))
				.lore(CC.translate(Arrays.asList(
						"&fWhat is a &4Deathban&f?",
						"&7 * &4Deathban&f's are not actual bans, but they are",
						"&7 * &fhcf specific bans. The way to get a deaht ban is",
						"&7 * &fvery simple. You just die! If you do not have a rank",
						"&7 * &fwith a lowered deathban time, then you have to wait",
						"&7 * &41 hour&f to be able to join the server again, unless",
						"&7 * &fyou have lives.",
						"&f",
						"&fWhat are &4Lives&f?",
						"&7 * &fLives are a deathban specific currency of some sort.",
						"&7 * &fIf you have lives, then you won't need to wait out",
						"&7 * &fyour deathban time. Just rejoin the server while you",
						"&7 * &fare deathbanned and you'll use one life successfully.",
						"&7 * &f&oCheck your lives by doing &4/lives"
						)
				))
				.build()));

		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.GREEN_DYE)
				.displayName(CC.translate("&aSOTW"))
				.lore(CC.translate(Arrays.asList(
						"&fWhat is SOTW?",
						"&7 * &aSOTW&f is a 2 hour long part of our weekly maps.",
						"&f",
						"&fSOTW is an abbreviation for &a'Start of the World'",
						"&7 * &fWhen this timer is active. You cannot pvp",
						"&7 * &for take damage. You can also &a/spawn & /shop",
						"&7 * &fYou should take the time to get",
						"&7 * &fa claim and setup an accessible base.",
						"&7 * &fAdditionally you can run &a/sotw enable&f to enable pvp & damage for yourself."
				)))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.RED_DYE)
				.displayName(CC.translate("&cEOTW"))
				.lore(CC.translate(Arrays.asList(
								"&fWhat is EOTW?",
								"&7 * &cEOTW&f is an event at the end of the map.",
								"&7 * &fwhere all factions are set raidable and",
								"&7 * &fwhoever/whoever's faction is the last one",
								"&7 * &fstanding. They will be awarded usually a",
								"&7 * &fbuycraft voucher of $5-10. There is also",
								"&7 * &foccasionally a FFA (Free For All).",
								"&f",
								"&fEOTW is an abbreviation for &c'End of the World'",
								"&7 * &fWhen this timer is active. If you die it",
								"&7 * &fis &c&lPERMANENT&f, until next map. You",
								"&7 * &fwant to try to stay alive for the longest",
								"&7 * &ftime possible. The border also shrinks every",
								"&7 * &f5-10 minutes. There is also occasional teleport",
								"&7 * &falls, so it can be less annoying to play."
						)
						))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.DIAMOND_SWORD)
				.displayName(CC.translate("&9KoTH"))
				.lore(CC.translate(Arrays.asList(
						"&fWhat is a KoTH?",
						"&7 * &9KoTH&f's are a server event that if you manage",
						"&7 * &fto stand in the cap area for the",
						"&7 * &fcap time on the scoreboard. You'll",
						"&7 * &freceive a crate key that give you above",
						"&7 * &fmap kit armor & weapons.",
						"&f",
						"&fHow to Capture The KoTH?",
						"&7 * &fYou can start capturing the KoTH by standing",
						"&7 * &fwithin the bounds of the red wool/clay outlining",
						"&7 * &fthe capzone. This will start a sequence that if you",
						"&7 * &fcan monitor on the scoreboard. If it does hit 0 seconds",
						"&7 * &fyou will be rewarded a few koth keys, as well as a sign",
						"&7 * &ftelling you that you captured the koth, but if you happen",
						"&7 * &fto walk outside of the capzone or get git out you will",
						"&7 * &flose all progress of the koth capture.",
						"&7 * &f&oThis is usually meant for big factions, so capture at your own risk.",
						"&f",
						"&9KoTH&f is an abbreviation for &a'King of The Hill'",
						"&7 * &fWhen this timer is active. You are able to",
						"&7 * &frun the command /f i <koth-name>. This will",
						"&7 * &fgive you a location of where to go, so you",
						"&7 * &fcan start progress on capturing the KoTH.",
						"&7 * &f&oIf you are on LunarClient a waypoint will",
						"&7 * &fappear for everyone on the server."
						)
				))
				.build()));
		buttons.put(++i, Button.fromItem(new ItemBuilder(Material.PURPLE_SHULKER_BOX)
				.displayName(CC.translate("&x&6&5&0&0&d&d&lCITADEL"))
				.lore(CC.translate(Arrays.asList(
						"&fWhat is a Citadel?",
						"&7 * &x&6&5&0&0&d&d&lCitadel&f is a server event that if you manage",
						"&7 * &fto stand in the cap area for &x&6&5&0&0&d&d&l15 minutes&f.",
						"&7 * &fYou'll receive access to all the chests in the claim of the",
						"&7 * &x&6&5&0&0&d&d&lCitadel&f. You can check out the loot by doing &x&6&5&0&0&d&d/citadel loot",
						"&f",
						"&7 * &fThis event is essentially a more dominant KoTH."
						)
				))
				.build()));
		return buttons;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		int size = super.size(buttons) + 9;
		return (Math.min(size, 54));
	}
}
