package dev.lbuddyboy.bunkers.nametag.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.nametag.NametagInfo;
import dev.lbuddyboy.bunkers.nametag.NametagProvider;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.SymbolUtil;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import net.minecraft.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BunkersNametagProvider extends NametagProvider {

	public BunkersNametagProvider() {
		super("Bunkers Provider", 10);
	}

	@Override
	public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
		Team viewerTeam = Bunkers.getInstance().getTeamHandler().getTeam(refreshFor);
		NametagInfo nametagInfo = null;

		if (viewerTeam != null) {
			nametagInfo = createNametag(toRefresh, "" + viewerTeam.getColor(), "", EnumChatFormat.valueOf(viewerTeam.getColor().name()));
			if (toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				nametagInfo = createNametag(toRefresh, "", "", EnumChatFormat.valueOf("BLACK"));
			}
		}

		return (nametagInfo == null ? createNametag(toRefresh, "" + ChatColor.RED, "", EnumChatFormat.valueOf("RED")) : nametagInfo);
	}

	private NametagInfo createNametag(Player displayed, String prefix, String suffix, EnumChatFormat color) {
		String invis = displayed.hasMetadata("invisible") ? ChatColor.GRAY + "*" : "";
		prefix = invis + prefix;

		return createNametagNoPlayer(prefix, suffix, color);
	}

	public static void updateLunarTag(Player toRefresh, Player refreshFor) {
		boolean runningLunar = Bukkit.getPluginManager().getPlugin("LunarClient-API") != null;

		if (runningLunar) {
			List<String> tags = new ArrayList<>();
			Team refreshTeam = Bunkers.getInstance().getTeamHandler().getTeam(toRefresh);
			Team refreshForTeam = Bunkers.getInstance().getTeamHandler().getTeam(refreshFor);

			if (toRefresh.hasMetadata("modmode")) {
				tags.add(CC.translate("&g[Mod Mode]"));
			}
			ChatColor color;
			if (refreshTeam != null) {
				if (refreshForTeam != null && refreshTeam == refreshForTeam) {
					color = ChatColor.GREEN;
				} else {
					color = ChatColor.RED;
				}
				tags.add(ChatColor.GOLD + "[" + color + refreshTeam.getName() + ChatColor.GRAY + " " + SymbolUtil.STICK + " " + refreshTeam.getDTRFormatted() + ChatColor.GOLD + "]");
			}

			LunarClientAPI.getInstance().overrideNametag(toRefresh, tags, refreshFor);
		}
	}

}