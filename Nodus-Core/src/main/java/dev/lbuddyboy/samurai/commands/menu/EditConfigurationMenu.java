package dev.lbuddyboy.samurai.commands.menu;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.server.menu.ServersMenu;
import dev.lbuddyboy.flash.server.packet.ServerCommandPacket;
import dev.lbuddyboy.samurai.util.ConversationBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/12/2021 / 2:59 PM
 * SteelHCF-main / com.steelpvp.hcf.command.menu
 */
public class EditConfigurationMenu extends PaginatedMenu implements Listener {

	public static Map<Player, FoxtrotConfiguration> configurationMap = new HashMap<>();

	@Override
	public String getPrePaginatedTitle(Player var1) {
		return CC.translate("&bConfig Editor");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = -1;
		for (FoxtrotConfiguration config : FoxtrotConfiguration.values()) {
			buttons.put(++i, new Button() {
				@Override
				public String getName(Player var1) {
					return config.name();
				}

				@Override
				public List<String> getDescription(Player var1) {
					return CC.translate(Arrays.asList(
							"",
							"&bSection&7: &f" + config.getSection(),
							"&bDefault Value&7: &f" + config.getDefaultValue(),
							"&bCurrent Value&7: &f" + Samurai.getInstance().getConfig().get(config.getSection()),
							"",
							"&7Click to update the value"
					));
				}

				@Override
				public Material getMaterial(Player var1) {
					return Material.PAPER;
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					if (config.getDefaultValue() instanceof ArrayList || config.getDefaultValue() instanceof List)  {
						player.sendMessage(CC.translate("&cThis configuration setting isn't editable at this time."));
						return;
					}
					player.closeInventory();
					Conversation conversation = new ConversationBuilder(player, new ConversationFactory(Flash.getInstance()))
							.closeableStringPrompt(CC.translate("&aType the new value of this configuration setting."), (conversationContext, value) -> {
								FoxtrotConfiguration config = EditConfigurationMenu.configurationMap.get(player);
								if (config.getDefaultValue() instanceof Integer) {
									config.update(Integer.parseInt(value));
								} else if (config.getDefaultValue() instanceof String) {
									config.update(value);
								} else if (config.getDefaultValue() instanceof Double) {
									config.update(Double.parseDouble(value));
								} else if (config.getDefaultValue() instanceof Boolean) {
									config.update(Boolean.parseBoolean(value));
								} else {
									player.sendMessage(CC.translate("&cCould not parse that value. Try again."));
									return Prompt.END_OF_CONVERSATION;
								}
								EditConfigurationMenu.configurationMap.remove(player);
								player.sendMessage(CC.translate("&aValue updated."));
								new EditConfigurationMenu().openMenu(player);
								return Prompt.END_OF_CONVERSATION;
							})
							.echo(false).build();

					player.beginConversation(conversation);
				}
			});
		}

		return buttons;
	}

	@Override
	public int getMaxItemsPerPage(Player player) {
		return 45;
	}

}
