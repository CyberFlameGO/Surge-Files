package dev.lbuddyboy.bot.command;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;
import dev.lbuddyboy.bot.command.impl.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/03/2022 / 1:23 AM
 * SteelPvPBot / com.steelpvp.bot.handlers
 */

@Getter
public class CommandHandler implements IHandler {

    private final List<Command> commands;

    public CommandHandler() {
        this.commands = new ArrayList<>();
    }

    @Override
    public void load(Bot instance) {
        this.registerCommands();
    }

    @Override
    public void unload(Bot instance) {

    }

    private void registerCommands() {
        this.commands.add(new HelpCommand());
        this.commands.add(new ClearChatCommand());
        this.commands.add(new CloseCommand());
        this.commands.add(new InvitesCommand());
        this.commands.add(new InviteSyncCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new SendTicketMessageCommand());
        this.commands.add(new SyncCommand());
        this.commands.add(new SyncResetCommand());
        this.commands.add(new TriggerWelcomeCommand());
    }

}
