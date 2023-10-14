package dev.lbuddyboy.samurai.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class CC {

    public final String STAR = SymbolUtil.UNICODE_ARROW_RIGHT;
    public static final ArrayList<ChatColor> woolColors = new ArrayList<>(Arrays.asList(ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
            ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY,
            ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.RESET,
            ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.BLACK));

    public final String BLACK = ChatColor.BLACK.toString();
    public final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public final String DARK_RED = ChatColor.DARK_RED.toString();
    public final String MAIN = translate("&g&l");
    public final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public final String GOLD = ChatColor.GOLD.toString();
    public final String GRAY = ChatColor.GRAY.toString();
    public final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public final String BLUE = ChatColor.BLUE.toString();
    public final String GREEN = ChatColor.GREEN.toString();
    public final String AQUA = ChatColor.AQUA.toString();
    public final String RED = ChatColor.RED.toString();
    public final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public final String YELLOW = ChatColor.YELLOW.toString();
    public final String WHITE = ChatColor.WHITE.toString();
    public final String MAGIC = ChatColor.MAGIC.toString();
    public final String BOLD = ChatColor.BOLD.toString();
    public final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public final String ITALIC = ChatColor.ITALIC.toString();
    public final String RESET = ChatColor.RESET.toString();
    private static final int SERVER_VERSION = Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]
    );
    public static final String UNICODE_VERTICAL_BAR = dev.lbuddyboy.flash.util.bukkit.CC.GRAY + StringEscapeUtils.unescapeJava("\u2503");
    public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
    public static final String UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
    public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("▶");
    public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
    public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");
    public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
    public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45);
    public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45);
    public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 16);

    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}}");

    private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");

    public String translate(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String code = message.substring(matcher.start(), matcher.end());
            net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(code);

            message = message.replace(
                    code,
                    color + ""
            );

            matcher = pattern.matcher(
                    message
            );
        }

        return ChatColor.translateAlternateColorCodes('&', message
                .replaceAll("&h", "&x&f&b&c&b&4&f")
                .replaceAll("&g", "&x&f&b&9&4&4&a"));
    }

    public static String translate(String string, Object... format) {
        for (int i = 0; i < format.length; i += 2) {
            string = string.replace((String) format[i], String.valueOf(format[i + 1]));
        }
        return translate(string);
    }

    public void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(translate(message));
        }
    }

    public static List<String> translate(List<String> lore) {
        ArrayList<String> toAdd = new ArrayList();

        for (String lor : lore) {
            toAdd.add(translate(lor));
        }

        return toAdd;
    }

    public static List<String> translate(List<String> lore, Object... objects) {
        ArrayList<String> toAdd = new ArrayList();

        for (String lor : lore) {
            toAdd.add(translate(lor, objects));
        }

        return toAdd;
    }

    public static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<G:([0-9A-Fa-f]{6})>(.*?)</G:([0-9A-Fa-f]{6})>");

    /**
     * Applies a gradient pattern to the provided String.
     * Output might me the same as the input if this pattern is not present.
     *
     * @param string The String to which this pattern should be applied to
     * @return The new String with applied pattern
     */
    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(3);
            String content = matcher.group(2);
            string = string.replace(matcher.group(), color(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
        }
        return string;
    }

    /**
     * Colors a String.
     *
     * @param string The string we want to color
     * @param color  The color we want to set it to
     * @since 1.0.0
     */
    public static String color(String string, Color color) {
        return net.md_5.bungee.api.ChatColor.of(color) + string;
    }

    /**
     * Colors a String with a gradiant.
     *
     * @param string The string we want to color
     * @param start  The starting gradiant
     * @param end    The ending gradiant
     * @since 1.0.0
     */
    public static String color(String string, Color start, Color end) {
        String originalString = string;

        net.md_5.bungee.api.ChatColor[] colors = createGradient(start, end, withoutSpecialChar(string).length());
        return apply(originalString, colors);
    }

    private static String apply(String source, net.md_5.bungee.api.ChatColor[] colors) {
        StringBuilder specialColors = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        String[] characters = source.split("");
        int outIndex = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].equals("&") || characters[i].equals("§")) {
                if (i + 1 < characters.length) {
                    if (characters[i + 1].equals("r")) {
                        specialColors.setLength(0);
                    } else {
                        specialColors.append(characters[i]);
                        specialColors.append(characters[i + 1]);
                    }
                    i++;
                } else
                    stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
            } else
                stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * Returns a gradient array of chat colors.
     *
     * @param start The starting color.
     * @param end   The ending color.
     * @param step  How many colors we return.
     * @author TheViperShow
     * @since 1.0.0
     */

    private static net.md_5.bungee.api.ChatColor[] createGradient(Color start, Color end, int step) {
        net.md_5.bungee.api.ChatColor[] colors = new net.md_5.bungee.api.ChatColor[step];
        int stepR = Math.abs(start.getRed() - end.getRed()) / (step - 1);
        int stepG = Math.abs(start.getGreen() - end.getGreen()) / (step - 1);
        int stepB = Math.abs(start.getBlue() - end.getBlue()) / (step - 1);
        int[] direction = new int[]{
                start.getRed() < end.getRed() ? +1 : -1,
                start.getGreen() < end.getGreen() ? +1 : -1,
                start.getBlue() < end.getBlue() ? +1 : -1
        };

        for (int i = 0; i < step; i++) {
            Color color = new Color(start.getRed() + ((stepR * i) * direction[0]), start.getGreen() + ((stepG * i) * direction[1]), start.getBlue() + ((stepB * i) * direction[2]));
            colors[i] = net.md_5.bungee.api.ChatColor.of(color);
        }

        return colors;
    }

    private static String withoutSpecialChar(String source) {
        String workingString = source;
        for (String color : SPECIAL_COLORS) {
            if (workingString.contains(color)) {
                workingString = workingString.replace(color, "");
            }
        }
        return workingString;
    }

    public static ItemStack getCustomHead(String name, int amount, String url, List<String> lore) {
        ItemStack stack = getCustomHead(name, amount, url);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(translate(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getCustomHead(String name, int amount, String url, String... lore) {
        ItemStack stack = getCustomHead(name, amount, url);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(translate(Arrays.asList(lore)));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getCustomHead(String name, int amount, String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        assert skullMeta != null;

        if (url.length() < 16) {
            skullMeta.setOwner(url);
            skullMeta.setDisplayName(translate(name));
            skull.setItemMeta(skullMeta);
            return skull;
        } else {
            StringBuilder s_url = new StringBuilder();
            s_url.append("https://textures.minecraft.net/texture/").append(url);
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) null);
            byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());
            gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

            try {
                Field field = skullMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(skullMeta, gameProfile);
            } catch (Exception var9) {
                var9.printStackTrace();
            }

            skullMeta.setDisplayName(translate(name));
            skull.setItemMeta(skullMeta);
            return skull;
        }
    }

    public static ChatColor getColor(String path) {
        byte var2 = -1;
        switch (path.hashCode()) {
            case 1226:
                if (path.equals("&0")) {
                    var2 = 9;
                }
                break;
            case 1227:
                if (path.equals("&1")) {
                    var2 = 0;
                }
                break;
            case 1228:
                if (path.equals("&2")) {
                    var2 = 1;
                }
                break;
            case 1229:
                if (path.equals("&3")) {
                    var2 = 2;
                }
                break;
            case 1230:
                if (path.equals("&4")) {
                    var2 = 3;
                }
                break;
            case 1231:
                if (path.equals("&5")) {
                    var2 = 4;
                }
                break;
            case 1232:
                if (path.equals("&6")) {
                    var2 = 5;
                }
                break;
            case 1233:
                if (path.equals("&7")) {
                    var2 = 6;
                }
                break;
            case 1234:
                if (path.equals("&8")) {
                    var2 = 7;
                }
                break;
            case 1235:
                if (path.equals("&9")) {
                    var2 = 8;
                }
            case 1236:
            case 1237:
            case 1238:
            case 1239:
            case 1240:
            case 1241:
            case 1242:
            case 1243:
            case 1244:
            case 1245:
            case 1246:
            case 1247:
            case 1248:
            case 1249:
            case 1250:
            case 1251:
            case 1252:
            case 1253:
            case 1254:
            case 1255:
            case 1256:
            case 1257:
            case 1258:
            case 1259:
            case 1260:
            case 1261:
            case 1262:
            case 1263:
            case 1264:
            case 1265:
            case 1266:
            case 1267:
            case 1268:
            case 1269:
            case 1270:
            case 1271:
            case 1272:
            case 1273:
            case 1274:
            default:
                break;
            case 1275:
                if (path.equals("&a")) {
                    var2 = 10;
                }
                break;
            case 1276:
                if (path.equals("&b")) {
                    var2 = 11;
                }
                break;
            case 1277:
                if (path.equals("&c")) {
                    var2 = 12;
                }
                break;
            case 1278:
                if (path.equals("&d")) {
                    var2 = 13;
                }
                break;
            case 1279:
                if (path.equals("&e")) {
                    var2 = 14;
                }
                break;
            case 1280:
                if (path.equals("&f")) {
                    var2 = 15;
                }
        }

        switch (var2) {
            case 0:
                return ChatColor.DARK_BLUE;
            case 1:
                return ChatColor.DARK_GREEN;
            case 2:
                return ChatColor.DARK_AQUA;
            case 3:
                return ChatColor.DARK_RED;
            case 4:
                return ChatColor.DARK_PURPLE;
            case 5:
                return ChatColor.GOLD;
            case 6:
                return ChatColor.GRAY;
            case 7:
                return ChatColor.DARK_GRAY;
            case 8:
                return ChatColor.BLUE;
            case 9:
                return ChatColor.BLACK;
            case 10:
                return ChatColor.GREEN;
            case 11:
                return ChatColor.AQUA;
            case 12:
                return ChatColor.RED;
            case 13:
                return ChatColor.LIGHT_PURPLE;
            case 14:
                return ChatColor.YELLOW;
            case 15:
                return ChatColor.WHITE;
            default:
                return ChatColor.WHITE;
        }
    }

    public static ItemStack getCustomSkull(String url) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta)head.getItemMeta();

        if (url.length() < 16) {
            skullMeta.setOwner(url);
        } else {
            StringBuilder s_url = new StringBuilder();
            s_url.append("https://textures.minecraft.net/texture/").append(url);
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) null);
            byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());
            gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

            try {
                Field field = skullMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(skullMeta, gameProfile);
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }
        head.setItemMeta(skullMeta);

        return head;
    }


    public static int toDyeColor(ChatColor color) {
        if(color == ChatColor.DARK_RED) color = ChatColor.RED;
        if (color == ChatColor.DARK_BLUE) color = ChatColor.BLUE;

        return woolColors.indexOf(color);
    }
}