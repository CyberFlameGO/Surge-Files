package dev.lbuddyboy.samurai.commands.menu;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import dev.lbuddyboy.samurai.commands.staff.LastInvCommand;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@AllArgsConstructor
public class DeathsMenu extends PaginatedMenu {

    private List<DBObject> objects;

    private final static DateFormat FORMAT = new SimpleDateFormat("M dd yyyy h:mm a");

    @Override
    public String getPrePaginatedTitle(Player var1) {
        return CC.translate("&cDeaths Menu");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player var1) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = -1;

        for (DBObject object : objects) {

            BasicDBObject basicDBObject = (BasicDBObject) object;

            String cause;
            if (object.get("killerUUID") != null) {
                cause = ChatColor.GRAY + " died to " + ChatColor.RED + FrozenUUIDCache.name(UUIDfromString(object.get("killerUUID").toString()));
            } else {
                if (object.get("reason") != null) {
                    cause = ChatColor.GRAY + " died from " + object.get("reason").toString().toLowerCase() + " damage.";
                } else {
                    cause = ChatColor.GRAY + " died from unknown causes.";
                }
            }

            buttons.put(++i, new Button() {
                @Override
                public String getName(Player var1) {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.setTimeZone(TimeZone.getTimeZone("EST"));
                    String formattedDate = sdf.format(new Date(basicDBObject.getDate("when").getTime()));
                    return CC.translate("&g" + formattedDate);
                }

                @Override
                public List<String> getDescription(Player var1) {
                    if (basicDBObject.containsKey("refundedBy")) {

                        SimpleDateFormat sdf = new SimpleDateFormat();
                        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
                        String formattedDate = sdf.format(new Date(System.currentTimeMillis() - basicDBObject.getLong("refundedAt")));

                        return CC.translate(Arrays.asList(
                                " ",
                                "&g&lDeathban Information",
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gDied&7: &f" + TimeUtils.formatIntoDetailedString((int) ((System.currentTimeMillis() - basicDBObject.getDate("when").getTime()) / 1000)) + " Ago",
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gKiller&7: &f" + (object.get("killerUUID") == null ? "None" : FrozenUUIDCache.name(UUIDfromString(object.get("killerUUID").toString()))),
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCause&7: &f" + cause,
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gLogger&7: " + (basicDBObject.containsField("combatLogger") ? "&aYes" : "&cNo"),
                                " ",
                                "&g&lRefund Information",
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gRefunded By&7: &f" + basicDBObject.containsKey("refundedBy"),
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gRefunded At&7: &f" + TimeUtils.formatIntoDetailedString((int) ((System.currentTimeMillis() - basicDBObject.getLong("refundedAt")) / 1000)) + " Ago &7(" + formattedDate + ")",
                                " ",
                                "&7Left Click to teleport to the death location.",
                                "&7Right Click to view the inventory of the death.",
                                "&7Middle Click to load this as your inventory.",
                                "&7Shift Right to remove the items from the players deaths claim.",
                                " "
                        ));
                    } else {
                        return CC.translate(Arrays.asList(
                                " ",
                                "&g&lDeathban Information",
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gDied&7: &f" + TimeUtils.formatIntoDetailedString((int) ((System.currentTimeMillis() - basicDBObject.getDate("when").getTime()) / 1000)) + " Ago",
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gKiller&7: &f" + (object.get("killerUUID") == null ? "None" : FrozenUUIDCache.name(UUIDfromString(object.get("killerUUID").toString()))),
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCause&7: &f" + cause,
                                " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gLogger&7: " + (basicDBObject.containsField("combatLogger") ? "&aYes" : "&cNo"),
                                " ",
                                "&7Left Click to teleport to the death location.",
                                "&7Right Click to view the inventory of the death.",
                                "&7Drop to refund the player.",
                                "&7Shift Left to add the items to the players death claim.",
                                "&7Shift Right to remove the items from the players deaths claim.",
                                " "
                        ));
                    }
                }

                @Override
                public Material getMaterial(Player var1) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType == ClickType.DROP) {
                        player.chat("/deathrefund " + object.get("_id").toString());
                    } else if (clickType == ClickType.SHIFT_LEFT) {
                        player.chat("/deathclaimadd " + object.get("_id").toString());
                    } else if (clickType == ClickType.SHIFT_RIGHT) {
                        player.chat("/deathclaimremove " + object.get("_id").toString());
                    } else if (clickType == ClickType.MIDDLE) {
                        ItemStack[] contents = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerInventory"));
                        ItemStack[] armor = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerArmor"));

                        player.getInventory().setArmorContents(armor);
                        player.getInventory().setContents(contents);
                    } else if (clickType == ClickType.LEFT) {
                        Location location = LocationSerializer.deserialize((BasicDBObject) basicDBObject.get("location"));
                        player.chat("/tppos " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
                    } else if (clickType == ClickType.RIGHT) {

                        ItemStack[] contents = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerInventory"));
                        ItemStack[] armor = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerArmor"));

                        LastInvCommand.cleanLoot(contents);
                        LastInvCommand.cleanLoot(armor);

                        new Menu() {

                            @Override
                            public String getTitle(Player player) {
                                return "Dead Inventory View";
                            }

                            @Override
                            public Map<Integer, Button> getButtons(Player player) {
                                Map<Integer, Button> buttons = new HashMap<>();

                                int i = -1;

                                buttons.put(8, new BackButton(new DeathsMenu(objects)));

                                for (ItemStack stack : armor) {
                                    buttons.put(++i, Button.fromItem(stack));
                                }
                                int e = 8;
                                for (ItemStack stack : contents) {
                                    buttons.put(++e, Button.fromItem(stack));
                                }

                                return buttons;
                            }

                        }.openMenu(player);
                    }
                }
            });

        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    private static UUID UUIDfromString(String string) {
        return UUID.fromString(
                string.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }

}