package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/03/2022 / 9:14 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands
 */

@CommandAlias("texturepack|textures")
public class TexturePackCommand extends BaseCommand {

	@Default
	public static void texturepack(Player sender) {
		sender.setResourcePack(Samurai.getInstance().getConfig().getString("texture-pack"));
		sender.sendMessage(CC.translate("&aProcessing textures..."));
	}

}
