package dev.lbuddyboy.lmotd.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CC {

    public static TextComponent translate(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

}