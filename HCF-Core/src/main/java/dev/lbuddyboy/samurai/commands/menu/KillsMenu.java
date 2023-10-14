package dev.lbuddyboy.samurai.commands.menu;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import dev.lbuddyboy.samurai.commands.staff.LastInvCommand;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
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
public class KillsMenu extends PaginatedMenu {

    private List<DBObject> objects;

    private final static DateFormat FORMAT = new SimpleDateFormat("M dd yyyy h:mm a");

    @Override
    public String getPrePaginatedTitle(Player var1) {
        return CC.translate("&cKills Menu");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player var1) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = -1;

        for (DBObject object : objects) {

            BasicDBObject basicDBObject = (BasicDBObject) object;

            String cause = null;
            if (object.get("uuid") != null) {
                cause = ChatColor.GRAY + " killed " + ChatColor.RED + FrozenUUIDCache.name(UUIDfromString(object.get("uuid").toString()));
            }

            String finalCause = cause;
            buttons.put(++i, new Button() {
                @Override
                public String getName(Player var1) {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.setTimeZone(TimeZone.getTimeZone("EST"));
                    String formattedDate = sdf.format(((System.currentTimeMillis() - basicDBObject.getDate("when").getTime())));
                    return CC.translate("&g" + formattedDate);
                }

                @Override
                public List<String> getDescription(Player var1) {
                    return CC.translate(Arrays.asList(
                            " ",
                            "&g&lDeathban Information",
                            " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gLogger&7: " + (basicDBObject.containsField("combatLogger") ? "&aYes" : "&cNo"),
                            " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gDead&7: &f" + TimeUtils.formatIntoDetailedString((int) ((System.currentTimeMillis() - basicDBObject.getDate("when").getTime()) / 1000)) + " Ago",
                            " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gVictim&7: &f " + FrozenUUIDCache.name(UUIDfromString(object.get("uuid").toString())),
                            " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gKiller&7: &f" + FrozenUUIDCache.name(UUIDfromString(object.get("killerUUID").toString())),
                            " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCause&7: &f" + finalCause,
                            " ",
                            "&7Left Click to teleport to the death location.",
                            "&7Right Click to view the inventory of the dead.",
                            "&7Click Q to view the inventory of the killer.",
                            " "
                    ));
                }

                @Override
                public Material getMaterial(Player var1) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType == ClickType.LEFT) {
                        Location location = LocationSerializer.deserialize((BasicDBObject) basicDBObject.get("location"));
                        player.chat("/tppos " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
                    }
                    if (clickType == ClickType.DROP) {
                        DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");
                        DBObject dbObject = mongoCollection.findOne(object.get("_id").toString());

                        if (dbObject == null) return;

                        BasicDBObject basicDBObject = (BasicDBObject) dbObject;

                        ItemStack[] contents = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("killerInventory"));
                        ItemStack[] armor = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("killerArmor"));

                        LastInvCommand.cleanLoot(contents);
                        LastInvCommand.cleanLoot(armor);

                        new Menu() {

                            @Override
                            public String getTitle(Player player) {
                                return "Killer Inventory View";
                            }

                            @Override
                            public Map<Integer, Button> getButtons(Player player) {
                                Map<Integer, Button> buttons = new HashMap<>();

                                buttons.put(8, new BackButton(KillsMenu.this));

                                int i = -1;
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
                    if (clickType == ClickType.RIGHT) {
                        DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");
                        DBObject dbObject = mongoCollection.findOne(object.get("_id").toString());

                        if (dbObject == null) return;
                        BasicDBObject basicDBObject = (BasicDBObject) dbObject;

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

                                buttons.put(8, new BackButton(KillsMenu.this));

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