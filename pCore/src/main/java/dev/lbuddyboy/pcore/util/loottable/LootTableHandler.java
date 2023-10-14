package dev.lbuddyboy.pcore.util.loottable;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.pcore.util.loottable.command.completions.LootTableCommandCompletion;
import dev.lbuddyboy.pcore.util.loottable.command.completions.LootTableCompletion;
import dev.lbuddyboy.pcore.util.loottable.command.completions.LootTableItemsCompletion;
import dev.lbuddyboy.pcore.util.loottable.command.contexts.LootTableContext;
import dev.lbuddyboy.pcore.util.loottable.command.contexts.LootTableItemContext;
import dev.lbuddyboy.pcore.util.loottable.editor.ItemEditor;
import dev.lbuddyboy.pcore.util.loottable.editor.impl.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LootTableHandler implements IModule {

    private List<LootTable> lootTables = new ArrayList<>();
    private List<ItemEditor> editors = new ArrayList<>();

    @Override
    public void load(pCore pCore) {
        pCore.getCommandManager().getCommandCompletions().registerCompletion("loottables", new LootTableCompletion());
        pCore.getCommandManager().getCommandCompletions().registerCompletion("itemIds", new LootTableItemsCompletion());
        pCore.getCommandManager().getCommandCompletions().registerCompletion("commands", new LootTableCommandCompletion());
        pCore.getCommandManager().getCommandContexts().registerContext(LootTable.class, new LootTableContext());
        pCore.getCommandManager().getCommandContexts().registerContext(LootTableItem.class, new LootTableItemContext());
        pCore.getCommandManager().registerCommand(new LootTableCommand());

        editors.add(new ChanceEdit());
        editors.add(new CommandAddEdit());
        editors.add(new CommandListEdit());
        editors.add(new ItemEdit());
        editors.add(new DeleteEdit());
        editors.add(new SlotEdit());
        editors.add(new ToggleGiveItemEdit());
    }

    @Override
    public void unload(pCore plugin) {

    }

    public void registerLootTable(LootTable lootTable) {
        this.lootTables.add(lootTable);
    }

    public void unregisterLootTable(LootTable lootTable) {
        this.lootTables.remove(lootTable);
    }

}
