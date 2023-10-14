package dev.drawethree.xprison.autosell.command;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import me.lucko.helper.Commands;

public class SellAllCommand {

    private static final String COMMAND_NAME = "sellall";
    private final XPrisonAutoSell plugin;

    public SellAllCommand(XPrisonAutoSell plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Commands.create()
                .assertPlayer()
                .handler(c -> {
                    this.plugin.getManager().sellAll(c.sender());

                }).registerAndBind(this.plugin.getCore(), COMMAND_NAME);
    }

}
