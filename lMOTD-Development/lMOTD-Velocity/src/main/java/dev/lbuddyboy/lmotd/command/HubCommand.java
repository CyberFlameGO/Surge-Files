package dev.lbuddyboy.lmotd.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.lbuddyboy.lmotd.lMOTD;
import dev.lbuddyboy.lmotd.util.CC;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HubCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        List<RegisteredServer> hubs = lMOTD.getInstance().getServer().getAllServers().stream().filter(s -> s.getServerInfo().getName().startsWith("Hub-")).collect(Collectors.toList());
        RegisteredServer hub = hubs.get(ThreadLocalRandom.current().nextInt(0, hubs.size()));

        Player source = (Player) invocation.source();

        source.createConnectionRequest(hub).connect();
        source.sendMessage(CC.translate("&aFinding an available hub... Found " + hub.getServerInfo().getName() + "!"));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return true;
    }

}
