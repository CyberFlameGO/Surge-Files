package dev.aurapvp.samurai.enchants;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.enchants.command.ArmorSetsCommand;
import dev.aurapvp.samurai.enchants.command.completion.ArmorSetCompletion;
import dev.aurapvp.samurai.enchants.command.context.ArmorSetContext;
import dev.aurapvp.samurai.enchants.set.ArmorSet;
import dev.aurapvp.samurai.enchants.set.impl.GladiatorArmorSet;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArmorSetHandler implements IModule {

    private final Map<String, ArmorSet> armorSets;

    public ArmorSetHandler() {
        this.armorSets = new HashMap<>();
    }

    @Override
    public String getId() {
        return "armor-sets";
    }

    @Override
    public void load(Samurai plugin) {
        this.armorSets.put("gladiator", new GladiatorArmorSet());

        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("armorsets", new ArmorSetCompletion());
        Samurai.getInstance().getCommandManager().getCommandContexts().registerContext(ArmorSet.class, new ArmorSetContext());
        Samurai.getInstance().getCommandManager().registerCommand(new ArmorSetsCommand());
    }

    @Override
    public void unload(Samurai plugin) {

    }
}
