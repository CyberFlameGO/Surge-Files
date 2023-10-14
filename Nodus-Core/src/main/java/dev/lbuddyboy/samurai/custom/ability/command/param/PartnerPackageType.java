package dev.lbuddyboy.samurai.custom.ability.command.param;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.AbilityItemHandler;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;

import java.util.stream.Collectors;

public final class PartnerPackageType implements ContextResolver<AbilityItem, BukkitCommandExecutionContext> {

	@Override
	public AbilityItem getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
		AbilityItemHandler handler = Samurai.getInstance().getAbilityItemHandler();

		AbilityItem partnerPackageByName = handler.getPartnerPackageByName(c.popFirstArg());
		if (partnerPackageByName == null) {
			String available = handler.getAbilityItems().stream()
					.map(AbilityItem::getName)
					.map(ChatColor::stripColor)
					.map(str -> str.replace("'", "").replace(" ", "_"))
					.map(String::toLowerCase)
					.collect(Collectors.joining(", "));
			c.getSender().sendMessage(CC.RED + "No package with that name found!");
			c.getSender().sendMessage(CC.RED + "Available: " + CC.YELLOW + available);
		}
		return partnerPackageByName;
	}
}
