package dev.lbuddyboy.samurai.util.loottable;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.samurai.util.loottable.command.completions.LootTableCommandCompletion;
import dev.lbuddyboy.samurai.util.loottable.command.completions.LootTableCompletion;
import dev.lbuddyboy.samurai.util.loottable.command.completions.LootTableItemsCompletion;
import dev.lbuddyboy.samurai.util.loottable.command.contexts.LootTableContext;
import dev.lbuddyboy.samurai.util.loottable.command.contexts.LootTableItemContext;
import dev.lbuddyboy.samurai.util.loottable.editor.ItemEditor;
import dev.lbuddyboy.samurai.util.loottable.editor.impl.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LootTableHandler {

    @Getter public static List<LootTable> lootTables = new ArrayList<>();
    @Getter public static List<ItemEditor> editors = new ArrayList<>();

    public LootTableHandler(Samurai samurai) {
        samurai.getPaperCommandManager().getCommandCompletions().registerCompletion("loottables", new LootTableCompletion());
        samurai.getPaperCommandManager().getCommandCompletions().registerCompletion("itemIds", new LootTableItemsCompletion());
        samurai.getPaperCommandManager().getCommandCompletions().registerCompletion("commands", new LootTableCommandCompletion());
        samurai.getPaperCommandManager().getCommandContexts().registerContext(LootTable.class, new LootTableContext());
        samurai.getPaperCommandManager().getCommandContexts().registerContext(LootTableItem.class, new LootTableItemContext());
        samurai.getPaperCommandManager().registerCommand(new LootTableCommand());

        editors.add(new ChanceEdit());
        editors.add(new CommandAddEdit());
        editors.add(new CommandListEdit());
        editors.add(new ItemEdit());
        editors.add(new DeleteEdit());
        editors.add(new SlotEdit());
        editors.add(new ToggleGiveItemEdit());
    }

}
