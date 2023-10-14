package dev.aurapvp.samurai.util.loottable;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.IModule;
import dev.aurapvp.samurai.util.loottable.command.LootTableCommand;
import dev.aurapvp.samurai.util.loottable.command.completions.LootTableCommandCompletion;
import dev.aurapvp.samurai.util.loottable.command.completions.LootTableCompletion;
import dev.aurapvp.samurai.util.loottable.command.completions.LootTableItemsCompletion;
import dev.aurapvp.samurai.util.loottable.command.contexts.LootTableContext;
import dev.aurapvp.samurai.util.loottable.command.contexts.LootTableItemContext;
import dev.aurapvp.samurai.util.loottable.editor.ItemEditor;
import dev.aurapvp.samurai.util.loottable.editor.impl.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LootTableHandler implements IModule {

    private List<LootTable> lootTables = new ArrayList<>();
    private List<ItemEditor> editors = new ArrayList<>();

    @Override
    public String getId() {
        return "loot-tables";
    }

    @Override
    public void load(Samurai samurai) {
        samurai.getCommandManager().getCommandCompletions().registerCompletion("loottables", new LootTableCompletion());
        samurai.getCommandManager().getCommandCompletions().registerCompletion("itemIds", new LootTableItemsCompletion());
        samurai.getCommandManager().getCommandCompletions().registerCompletion("commands", new LootTableCommandCompletion());
        samurai.getCommandManager().getCommandContexts().registerContext(LootTable.class, new LootTableContext());
        samurai.getCommandManager().getCommandContexts().registerContext(LootTableItem.class, new LootTableItemContext());
        samurai.getCommandManager().registerCommand(new LootTableCommand());

        editors.add(new ChanceEdit());
        editors.add(new CommandAddEdit());
        editors.add(new CommandListEdit());
        editors.add(new ItemEdit());
        editors.add(new DeleteEdit());
        editors.add(new SlotEdit());
        editors.add(new ToggleGiveItemEdit());
    }

    @Override
    public void unload(Samurai plugin) {

    }

    public void registerLootTable(LootTable lootTable) {
        this.lootTables.add(lootTable);
    }

    public void unregisterLootTable(LootTable lootTable) {
        this.lootTables.remove(lootTable);
    }

}
