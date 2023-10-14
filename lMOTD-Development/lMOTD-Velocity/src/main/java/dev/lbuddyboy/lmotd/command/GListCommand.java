package dev.lbuddyboy.lmotd.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.lbuddyboy.lmotd.lMOTD;
import dev.lbuddyboy.lmotd.util.CC;

public class GListCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        for (RegisteredServer server : lMOTD.getInstance().getServer().getAllServers()) {
            invocation.source().sendMessage(CC.translate("&6" + server.getServerInfo().getName() + "&7: &f" + server.getPlayersConnected().size()));
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("command.test");
    }

}
