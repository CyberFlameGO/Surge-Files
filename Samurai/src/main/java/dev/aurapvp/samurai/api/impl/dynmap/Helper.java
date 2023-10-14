package dev.aurapvp.samurai.api.impl.dynmap;

import dev.aurapvp.samurai.api.impl.dynmap.layer.LayerConfig;
import dev.aurapvp.samurai.faction.Faction;
import org.bukkit.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Helper {

    /**
     * The expression universally identifies a color code.
     *
     * <pre>
     * Format: "&<color_code><text>"
     * Valid examples: "&fHello", "Wo&1rld"
     * </pre>
     */
    private static final Pattern COLOR_CODE = Pattern.compile("&(?<color>[\\da-f])(?<text>[^&]+)");
    private static final String HTML_COLOR = "<span style='color: %s;'>%s</span>";

    // Suppresses default constructor, ensuring non-instantiability.
    private Helper() {
    }

    /**
     * Converts a string with color codes to {@literal <span>} with inline css, pipes to {@literal <br>}
     *
     * @return colored html {@literal <span>}
     */
    public static String colorToHTML(String string) {
        if (string == null) {
            return "";
        }
        string = string.trim().replace("|", "<br>");
        Matcher matcher = COLOR_CODE.matcher(string);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group("color");
            String text = matcher.group("text");
            ChatColor chatColor = ChatColor.getByChar(color);
            if (chatColor == null) {
                continue;
            }

            String htmlEncoded = String.format(HTML_COLOR, HEXColor.of(chatColor).getCode(), text);
            matcher.appendReplacement(sb, htmlEncoded);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String getFactionLabel(LayerConfig config, Faction faction) {
        String onlineMembers = String.format("%s/%s", faction.getOnlineMembers().size(), faction.getMembers().size());

        String label = config.getString("format", "{faction} &8(home)")
                .replace("{faction}", faction.getName())
                .replace("{member_count}", String.valueOf(faction.getMembers().size()))
                //.replace("{founded}", faction.getFoundedString())
                .replace("{deaths}", String.valueOf(faction.getDeaths()))
                //.replace("{kdr}", String.valueOf(faction.getTotalKDR()))
                .replace("{members_online}", onlineMembers)
                .replace("{leader}", faction.getLeader().getName());

        return Helper.colorToHTML(label);
    }

    public enum HEXColor {
        WHITE("#FFFFFF"),
        BLACK("#000000"),
        DARK_GRAY("#555555"),
        GRAY("#AAAAAA"),
        DARK_PURPLE("#AA00AA"),
        LIGHT_PURPLE("#FF55FF"),
        DARK_BLUE("#0000AA"),
        BLUE("#5555FF"),
        DARK_AQUA("#00AAAA"),
        AQUA("#55FFFF"),
        GREEN("#55FF55"),
        DARK_GREEN("#00AA00"),
        YELLOW("#FFFF55"),
        GOLD("#FFAA00"),
        RED("#FF5555"),
        DARK_RED("#AA0000");

        private final String code;

        HEXColor(String code) {
            this.code = code;
        }

        public static HEXColor of(ChatColor chatColor) {
            try {
                return HEXColor.valueOf(chatColor.name());
            } catch (IllegalArgumentException ex) {
//                debug(String.format("Error while trying to parse the hex color of %s: %s", chatColor.name(), ex.getMessage()));
                return HEXColor.WHITE;
            }
        }

        public static HEXColor of(String chatColorChar) {
            String color = chatColorChar.replace("§", "").replace("&", "");
            ChatColor chatColor = ChatColor.getByChar(color);
            return chatColor != null ? of(chatColor) : HEXColor.WHITE;
        }

        public String getCode() {
            return code;
        }
    }
}