package dev.lbuddyboy.samurai.team.roster.menu;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.server.menu.ServersMenu;
import dev.lbuddyboy.flash.server.packet.ServerCommandPacket;
import dev.lbuddyboy.flash.util.bukkit.CC;
import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.button.BackButton;
import dev.lbuddyboy.flash.util.menu.paged.PagedMenu;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.util.ConversationBuilder;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RosterRoleMenu extends PagedMenu {

    public List<UUID> roles;
    public String context;
    public ChatColor color;
    public Team team;

    public RosterRoleMenu(List<UUID> roles) {
        this.roles = roles;
    }

    @Override
    public String getPageTitle(Player player) {
        return context + "'s";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (UUID member : this.roles) {
            buttons.add(new MemberButton(i++, this, member));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new AddRosterButton(this));
        buttons.add(new BackButton(4, new RosterMenu(team)));

        return buttons;
    }

    @AllArgsConstructor
    private class AddRosterButton extends Button {

        public RosterRoleMenu menu;

        @Override
        public int getSlot() {
            return 6;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.LIME_BANNER).setName("&aAdd a player &7(Click)").setLore("&7Click to add a player to the " + menu.context + " group.").create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            player.closeInventory();
            Conversation conversation = new ConversationBuilder(player, new ConversationFactory(Flash.getInstance()))
                    .closeableStringPrompt(CC.translate("&aType the name of the player you would like to add to the " + menu.context + " group. Type 'cancel' to stop this process."), (conversationContext, value) -> {
                        UUID uuid = null;
                        try {
                            uuid = FrozenUUIDCache.uuid(value);
                        } catch (Exception ignored) {
                            player.sendMessage(dev.lbuddyboy.samurai.util.CC.translate("&cWe could not add that player to your roster! Try again!"));
                        }
                        String role = menu.context;

                        if (role.equalsIgnoreCase("co-leaders")) {
                            if (uuid != null) {
                                team.getRoster().getColeaders().add(uuid);
                                player.sendMessage(dev.lbuddyboy.samurai.util.CC.translate("&a" + value + " is now a part of your co leader group!"));
                            }
                            RosterRoleMenu menu = new RosterRoleMenu(team.getRoster().getColeaders());

                            menu.team = team;
                            menu.context = "Co-Leaders";
                            menu.color = ChatColor.AQUA;

                            Tasks.run(() -> menu.openMenu(player));
                        } else if (role.equalsIgnoreCase("captains")) {
                            if (uuid != null) {
                                team.getRoster().getCaptains().add(uuid);
                                player.sendMessage(dev.lbuddyboy.samurai.util.CC.translate("&a" + value + " is now a part of your captain group!"));
                            }
                            RosterRoleMenu menu = new RosterRoleMenu(team.getRoster().getCaptains());

                            menu.team = team;
                            menu.context = "Captains";
                            menu.color = ChatColor.DARK_AQUA;

                            Tasks.run(() -> menu.openMenu(player));
                        } else if (role.equalsIgnoreCase("members")) {
                            if (uuid != null) {
                                team.getRoster().getMembers().add(uuid);
                                player.sendMessage(dev.lbuddyboy.samurai.util.CC.translate("&a" + value + " is now a part of your member group!"));
                            }
                            RosterRoleMenu menu = new RosterRoleMenu(team.getRoster().getMembers());

                            menu.team = team;
                            menu.context = "Members";
                            menu.color = ChatColor.GOLD;

                            Tasks.run(() -> menu.openMenu(player));
                        }

                        team.getRoster().save();

                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false).build();

            player.beginConversation(conversation);
        }
    }

    @AllArgsConstructor
    private static class MemberButton extends Button {

        public int slot;
        public RosterRoleMenu menu;
        public UUID uuid;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            String name = FrozenUUIDCache.name(uuid);
            if (menu.context.equalsIgnoreCase("members")) {
                return new ItemBuilder(Material.PLAYER_HEAD)
                        .setOwner(name)
                        .setName(menu.color + name)
                        .setLore(Arrays.asList(
                                CC.MENU_BAR,
                                "",
                                "&fThis member will join the team in the " + menu.color + menu.context + " group.",
                                "",
                                "&7Left Click to promote their roster role.",
                                "&7Middle Click to remove them from the roster.",
                                CC.MENU_BAR
                        ))
                        .create();
            }
            if (menu.context.equalsIgnoreCase("co-leaders")) {
                return new ItemBuilder(Material.PLAYER_HEAD)
                        .setOwner(name)
                        .setName(menu.color + name)
                        .setLore(Arrays.asList(
                                CC.MENU_BAR,
                                "",
                                "&fThis member will join the team in the " + menu.color + menu.context + " group.",
                                "",
                                "&7Right Click to demote their roster role.",
                                "&7Middle Click to remove them from the roster.",
                                CC.MENU_BAR
                        ))
                        .create();
            }
            return new ItemBuilder(Material.PLAYER_HEAD)
                    .setOwner(name)
                    .setName(menu.color + name)
                    .setLore(Arrays.asList(
                            CC.MENU_BAR,
                            "",
                            "&fThis member will join the team in the " + menu.color + menu.context + " group.",
                            "",
                            "&7Left Click to promote their roster role.",
                            "&7Right Click to demote their roster role.",
                            "&7Middle Click to remove them from the roster.",
                            CC.MENU_BAR
                    ))
                    .create();
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            if (event.getClick().isRightClick()) {
                if (menu.context.equalsIgnoreCase("captains")) {
                    menu.team.getRoster().getCaptains().remove(uuid);
                    menu.team.getRoster().getMembers().add(uuid);
                    menu.team.getRoster().save();

                    player.sendMessage(CC.translate("&a" + FrozenUUIDCache.name(uuid) + " is now a member!"));
                } else if (menu.context.equalsIgnoreCase("co-leaders")) {
                    menu.team.getRoster().getColeaders().remove(uuid);
                    menu.team.getRoster().getCaptains().add(uuid);
                    menu.team.getRoster().save();

                    player.sendMessage(CC.translate("&a" + FrozenUUIDCache.name(uuid) + " is now a captain!"));
                }
                player.closeInventory();
                menu.openMenu(player);
                return;
            }

            if (event.getClick().isLeftClick()) {
                if (menu.context.equalsIgnoreCase("captains")) {
                    menu.team.getRoster().getCaptains().remove(uuid);
                    menu.team.getRoster().getColeaders().add(uuid);
                    menu.team.getRoster().save();

                    player.sendMessage(CC.translate("&a" + FrozenUUIDCache.name(uuid) + " is now a co-leader!"));
                } else if (menu.context.equalsIgnoreCase("members")) {
                    menu.team.getRoster().getMembers().remove(uuid);
                    menu.team.getRoster().getCaptains().add(uuid);
                    menu.team.getRoster().save();

                    player.sendMessage(CC.translate("&a" + FrozenUUIDCache.name(uuid) + " is now a captain!"));
                }
                player.closeInventory();
                menu.openMenu(player);
                return;
            }

            menu.team.getRoster().getColeaders().remove(uuid);
            menu.team.getRoster().getCaptains().remove(uuid);
            menu.team.getRoster().getMembers().remove(uuid);

            player.sendMessage(CC.translate("&a" + FrozenUUIDCache.name(uuid) + " is no longer on the team's roster!"));

            player.closeInventory();
            menu.openMenu(player);
        }
    }

}
