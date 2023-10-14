package dev.aurapvp.samurai.faction.logs.impl;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.aurapvp.samurai.economy.transaction.Transaction;
import dev.aurapvp.samurai.faction.logs.FactionLog;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ForceClaim extends FactionLog {

    private final UUID executor, faction;

    public ForceClaim(long sentAt, UUID executor, UUID faction) {
        this.sentAt = sentAt;
        this.executor = executor;
        this.faction = faction;
    }

    @Override
    public String getId() {
        return "FORCE_CLAIM";
    }

    @Override
    public Material getMaterial() {
        return Material.WOODEN_HOE;
    }

    @Override
    public byte getMaterialData() {
        return 0;
    }

    @Override
    public List<String> getLoreFormat(Object... replacements) {
        return CC.format(Arrays.asList(
                "&6Executor&7: &f" + Bukkit.getOfflinePlayer(this.executor).getName(),
                "&6Faction&7: &f" + Bukkit.getOfflinePlayer(this.faction).getName()
        ), replacements);
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

}
