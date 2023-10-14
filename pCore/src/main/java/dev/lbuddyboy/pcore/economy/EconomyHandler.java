package dev.lbuddyboy.pcore.economy;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.command.EconomyCommand;
import dev.lbuddyboy.pcore.economy.command.PayCommand;
import dev.lbuddyboy.pcore.economy.impl.VaultEconomy;
import dev.lbuddyboy.pcore.economy.impl.CustomEconomy;
import dev.lbuddyboy.pcore.economy.listener.EconomyListener;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EconomyHandler implements IModule {

    private IEconomy economy;

    @Override
    public void load(pCore plugin) {

        this.loadEconomy();
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(pCore plugin) {

    }

    @Override
    public void reload() {
        this.loadEconomy();
    }

    private void loadEconomy() {
        if (pCore.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) this.economy = new VaultEconomy();
        else this.economy = new CustomEconomy();
    }

    private void loadCommands() {
//        pCore.getInstance().getCommandManager().registerCommand(new CoinsCommand());
        pCore.getInstance().getCommandManager().registerCommand(new EconomyCommand());
        pCore.getInstance().getCommandManager().registerCommand(new PayCommand());
    }

    private void loadListeners() {
        pCore.getInstance().getServer().getPluginManager().registerEvents(new EconomyListener(), pCore.getInstance());
    }

    public BankAccount getBankAccount(UUID uuid) {
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);
        if (user == null) return null;

        return user.getBankAccount();
    }

}
