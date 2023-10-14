package dev.lbuddyboy.samurai.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.flash.user.model.Prefix;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class GSONUtils {

    @Getter public static Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().enableComplexMapKeySerialization().create();

    public static final Type PREFIX = new TypeToken<Prefix>() {}.getType();
    public static final Type UUID = new TypeToken<List<UUID>>() {}.getType();

}
