package dev.lbuddyboy.pcore.essential.rollback.menu.sub;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.locator.ItemCache;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.essential.offline.menu.OfflineEditorMenu;
import dev.lbuddyboy.pcore.essential.rollback.PlayerDeath;
import dev.lbuddyboy.pcore.essential.rollback.menu.DeathsMenu;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ConversationBuilder;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.fanciful.FancyMessage;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class InventoryViewMenu extends Menu {

    private PlayerDeath death;
    private List<PlayerDeath> deaths;
    private UUID target;
    private ItemStack[] armor, contents;

    @Override
    public String getTitle(Player player) {
        return Bukkit.getOfflinePlayer(this.target).getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (ItemStack stack : this.armor) {
            buttons.add(new ItemButton(i++, stack));
        }

        i = 9;
        for (int j = 0; j < 9; j++) {
            buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 1).setName(" ").create(), ++i));
        }

        i = 19;
        for (ItemStack stack : this.contents) {
            buttons.add(new ItemButton(i++, stack));
        }

        buttons.add(new CopyInventoryButton());
        buttons.add(new RefundButton(player));
        buttons.add(new EditProofButton(this));
        buttons.add(new BackButton(9, new DeathsMenu(Bukkit.getOfflinePlayer(this.death.getVictim()), deaths)));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 54;
    }

    @AllArgsConstructor
    public class ItemButton extends Button {

        private int i;
        private ItemStack stack;

        @Override
        public int getSlot() {
            return i;
        }

        @Override
        public ItemStack getItem() {
            if (stack == null || stack.getType() == Material.AIR) return new ItemStack(Material.AIR);

            ItemCache itemCache = pCore.getInstance().getLocatorHandler().fetchCache(stack);

            if (itemCache == null || itemCache.getLocation() == null) {
                return new ItemBuilder(this.stack.clone())
                        .addLoreLines(
                                " ",
                                "&7&oThis item was despawned or is declared useless to the economy..."
                        )
                        .create();
            } else if (itemCache.getLocation().getType() == LocationType.BLOCK_INVENTORY) {
                if (itemCache.getLocation().getLocation() != null) {
                    Block block = itemCache.getLocation().getLocation().getBlock();

                    return new ItemBuilder(this.stack.clone())
                            .addLoreLines(
                                    " ",
                                    "&7&oThis item is located in a " + block.getType(),
                                    "&7&oLocation: " + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ(),
                                    "&aClick to teleport"
                            )
                            .create();
                }
            } else if (itemCache.getLocation().getType() == LocationType.DOUBLE_CHEST) {
                return new ItemBuilder(this.stack.clone())
                        .addLoreLines(
                                " ",
                                "&7&oThis item is located in a double chest",
                                "&7&oLocation: " + itemCache.getLocation().getLocation().getBlockX() + ", " + itemCache.getLocation().getLocation().getBlockY() + ", " + itemCache.getLocation().getLocation().getBlockZ(),
                                "&aClick to teleport"
                        )
                        .create();
            } else if (itemCache.getLocation().getType() == LocationType.GROUND_ITEM) {
                if (itemCache.getLocation().getLocation() != null) {
                    Block block = itemCache.getLocation().getLocation().getBlock();

                    return new ItemBuilder(this.stack.clone())
                            .addLoreLines(
                                    " ",
                                    "&7&oThis item is located on the ground!",
                                    "&7&oApproximate Location: " + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ(),
                                    "&aClick to teleport"
                            )
                            .create();
                }
            } else if (itemCache.getLocation().getType() == LocationType.DESPAWNED) {
                return new ItemBuilder(this.stack.clone())
                        .addLoreLines(
                                " ",
                                "&7&oThis item was despawned"
                        )
                        .create();
            } else if (itemCache.getLocation().getType() == LocationType.BURNT) {
                return new ItemBuilder(this.stack.clone())
                        .addLoreLines(
                                " ",
                                "&7&oThis item was burnt"
                        )
                        .create();
            } else if (itemCache.getLocation().getType() == LocationType.ENDER_CHEST) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(itemCache.getLocation().getHolderUUID());

                return new ItemBuilder(this.stack.clone())
                        .addLoreLines(
                                " ",
                                "&7&oThis item is in " + player.getName() + "'s ender chest",
                                "&aClick to open"
                        )
                        .create();
            } else if (itemCache.getLocation().getType() == LocationType.HOVERING) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(itemCache.getLocation().getHolderUUID());

                return new ItemBuilder(this.stack.clone())
                        .addLoreLines(
                                " ",
                                "&7&oThis item is located on " + player.getName() + "'s cursor",
                                "&7&oThis means they're held on one click.",
                                "&aClick to view"
                        )
                        .create();
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(itemCache.getLocation().getHolderUUID());

            return new ItemBuilder(this.stack.clone())
                    .addLoreLines(
                            " ",
                            "&7&oThis item is located in " + player.getName() + "'s inventory",
                            "&7&oThis player is " + (player.isOnline() ? "online" : "offline"),
                            "&aClick to view"
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            ItemCache itemCache = pCore.getInstance().getLocatorHandler().fetchCache(stack);

            if (itemCache.getLocation().getType() == LocationType.BLOCK_INVENTORY) {
                if (itemCache.getLocation().getLocation() == null) return;

                player.teleport(itemCache.getLocation().getLocation());
            } else if (itemCache.getLocation().getType() == LocationType.DOUBLE_CHEST) {
                player.teleport(itemCache.getLocation().getLocation());
            } else if (itemCache.getLocation().getType() == LocationType.GROUND_ITEM) {
                player.teleport(itemCache.getLocation().getLocation());
            } else if (itemCache.getLocation().getType() == LocationType.ENDER_CHEST) {
                pCore.getInstance().getEnderchestHandler().openEnderChest(player, itemCache.getLocation().getHolderUUID());
            } else if (itemCache.getLocation().getType() == LocationType.PLAYER_INVENTORY) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(itemCache.getLocation().getHolderUUID());

                if (offlinePlayer.isOnline()) {
                    Player target = offlinePlayer.getPlayer();

                    player.openInventory(target.getInventory());
                    return;
                }

                OfflineData offlineCache = pCore.getInstance().getOfflineHandler().fetchCache(offlinePlayer.getUniqueId());

                new OfflineEditorMenu(offlinePlayer, offlineCache, InventoryViewMenu.this).openMenu(player);
            }
        }
    }

    @AllArgsConstructor
    public class RefundButton extends Button {

        private Player player;

        @Override
        public int getSlot() {
            return 8;
        }

        @Override
        public ItemStack getItem() {
            if (death.isResolved()) {
                if (player.hasPermission("pCore.command.refund.override")) {
                    return new ItemBuilder(Material.GOLD_INGOT)
                            .setName("&e&lRefund Inventory &7(REFUNDED ALREADY)")
                            .setLore("&c&lADMIN ONLY! &7Click to override and refund again.")
                            .create();
                }
                return new ItemBuilder(Material.GOLD_INGOT)
                        .setName("&e&lRefund Inventory &7(REFUNDED ALREADY)")
                        .create();
            }
            return new ItemBuilder(Material.GOLD_INGOT)
                    .setName("&e&lRefund Inventory")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();
            OfflinePlayer targetOffline = Bukkit.getPlayer(death.getVictim());

            if (targetOffline == null || !targetOffline.isOnline()) {
                player.sendMessage(CC.translate("&cThe player is not online."));
                return;
            }

            Player target = targetOffline.getPlayer();
            for (ItemStack content : target.getInventory().getContents()) {
                if (content != null && content.getType() != Material.AIR) {
                    player.sendMessage(CC.translate("&cThe player still has items in their inventory."));
                    return;
                }
            }
            for (ItemStack content : target.getInventory().getArmorContents()) {
                if (content != null && content.getType() != Material.AIR) {
                    player.sendMessage(CC.translate("&cThe player still has armor applied."));
                    return;
                }
            }

            target.getInventory().setArmorContents(death.getVictimArmor());
            target.getInventory().setContents(death.getVictimInventory());

            List<String> message = new ArrayList<>(Arrays.asList(
                    " ",
                    "&a&lINVENTORY REFUNDED",
                    "&eIssuer&7: &f" + player.getDisplayName(),
                    "&eProof&7:"
            ));

            List<String> finalMessage = message;
            death.getResolveProof().forEach(s -> finalMessage.add("&7- " + s));

            if (death.getResolveProof().isEmpty()) {
                message = new ArrayList<>(Arrays.asList(
                        " ",
                        "&a&lINVENTORY REFUNDED",
                        "&eIssuer&7: &f" + player.getDisplayName(),
                        " "
                ));
            }
            message.add(" ");

            message.forEach(s -> player.sendMessage(CC.translate(s)));

            death.setResolver(player.getUniqueId());
            death.setResolveTime(System.currentTimeMillis());

            MineUser victimPlayer = pCore.getInstance().getMineUserHandler().tryMineUserAsync(death.getVictim());

            victimPlayer.setPlayerDeaths(deaths);
            victimPlayer.flagUpdate();
            pCore.getInstance().getRollbackHandler().updateCache(death.getVictim(), deaths);
            death.performAntiDupe(contents);
            death.performAntiDupe(armor);
        }
    }

    @AllArgsConstructor
    public class CopyInventoryButton extends Button {

        @Override
        public int getSlot() {
            return 7;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.DIAMOND)
                    .setName("&a&lCopy Inventory")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            for (ItemStack content : player.getInventory().getContents()) {
                if (content != null && content.getType() != Material.AIR) {
                    player.sendMessage(CC.translate("&cThe player still has items in their inventory."));
                    return;
                }
            }
            for (ItemStack content : player.getInventory().getArmorContents()) {
                if (content != null && content.getType() != Material.AIR) {
                    player.sendMessage(CC.translate("&cThe player still has armor applied."));
                    return;
                }
            }

            player.getInventory().setArmorContents(death.getVictimArmor());
            player.getInventory().setContents(death.getVictimInventory());

            death.performAntiDupe(contents);
            death.performAntiDupe(armor);
        }
    }

    @AllArgsConstructor
    public class EditProofButton extends Button {

        private InventoryViewMenu menu;

        @Override
        public int getSlot() {
            return 6;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.PAINTING)
                    .setName("&a&lEdit Proof")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new EditProofMenu(menu).openMenu(player);
        }
    }

    @AllArgsConstructor
    public class EditProofMenu extends PagedMenu {

        private InventoryViewMenu menu;

        @Override
        public String getPageTitle(Player player) {
            return "Proof Editor";
        }

        @Override
        public List<Button> getPageButtons(Player player) {
            List<Button> buttons = new ArrayList<>();

            int i = 1;
            for (String s : menu.death.getResolveProof()) {
                buttons.add(new ProofButton(i++, s));
            }

            return buttons;
        }

        @Override
        public List<Button> getGlobalButtons(Player player) {
            List<Button> buttons = new ArrayList<>();

            buttons.add(new AddProofButton());
            buttons.add(new BackButton(6, menu));

            return buttons;
        }

        @Override
        public boolean autoUpdate() {
            return true;
        }

        @AllArgsConstructor
        public class ProofButton extends Button {

            private int i;
            private String proof;

            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(Material.PAPER)
                        .setName("&eProof #" + i)
                        .setLore(
                                "&7" + this.proof,
                                " ",
                                "&7&oLeft click to view this proof.",
                                "&7&oRight click to remove this proof."
                        )
                        .create();
            }

            @Override
            public void action(InventoryClickEvent event) throws Exception {
                if (!(event.getWhoClicked() instanceof Player)) return;
                Player player = (Player) event.getWhoClicked();

                if (event.getClick() == ClickType.RIGHT) {
                    menu.getDeath().getResolveProof().remove(this.i - 1);
                    MineUser victimPlayer = pCore.getInstance().getMineUserHandler().tryMineUserAsync(death.getVictim());

                    victimPlayer.setPlayerDeaths(deaths);
                    victimPlayer.flagUpdate();
                    pCore.getInstance().getRollbackHandler().updateCache(menu.getDeath().getVictim(), deaths);
                    return;
                }

                FancyMessage message = new FancyMessage(this.proof);
                message.tooltip("&7Click to open this link.");
                message.link(this.proof);
                message.send(player);
            }

            @Override
            public boolean clickUpdate() {
                return true;
            }
        }

        @AllArgsConstructor
        public class AddProofButton extends Button {

            @Override
            public int getSlot() {
                return 4;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(Material.EMERALD)
                        .setName("&a&lADD PROOF &7(CLICK)")
                        .create();
            }

            @Override
            public void action(InventoryClickEvent event) throws Exception {
                if (!(event.getWhoClicked() instanceof Player)) return;

                Player player = (Player) event.getWhoClicked();
                player.closeInventory();

                ConversationBuilder builder = new ConversationBuilder(player);
                player.beginConversation(builder.closeableStringPrompt(CC.translate("&aPlease type the proof to add to this players death. Type 'cancel' to stop this process."), (ctx, response) -> {
                            death.getResolveProof().add(response);
                            deaths.set(menu.getDeaths().indexOf(menu.getDeath()), menu.getDeath());

                            MineUser victimPlayer = pCore.getInstance().getMineUserHandler().tryMineUserAsync(menu.getDeath().getVictim());
                            victimPlayer.setPlayerDeaths(menu.getDeaths());
                            victimPlayer.flagUpdate();

                            pCore.getInstance().getRollbackHandler().updateCache(menu.getDeath().getVictim(), menu.getDeaths());

                            EditProofMenu.this.openMenu(player);
                            return Prompt.END_OF_CONVERSATION;
                        })
                        .echo(false)
                        .build());
            }
        }
    }

}
