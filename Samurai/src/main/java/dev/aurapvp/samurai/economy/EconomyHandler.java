package dev.aurapvp.samurai.economy;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.economy.command.CoinsCommand;
import dev.aurapvp.samurai.economy.command.EconomyCommand;
import dev.aurapvp.samurai.economy.command.PayCommand;
import dev.aurapvp.samurai.economy.impl.VaultEconomy;
import dev.aurapvp.samurai.economy.impl.SkyEconomy;
import dev.aurapvp.samurai.economy.listener.EconomyListener;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EconomyHandler implements IModule {

    private final ConcurrentHashMap<UUID, BankAccount> banks;
    private IEconomy economy;

    public EconomyHandler() {
        this.banks = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return "economy";
    }

    @Override
    public void load(Samurai plugin) {

        this.loadEconomy();
        this.loadCommands();
        this.loadListeners();

        Bukkit.getConsoleSender().sendMessage(CC.translate("[Economy] Loaded " + this.banks.size() + " bank accounts."));
    }

    @Override
    public void unload(Samurai plugin) {
        economy.saveAll();
    }

    @Override
    public void save() {
        economy.saveAll();
    }

    @Override
    public void reload() {
        this.loadEconomy();
    }

    private void loadEconomy() {
        if (Samurai.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) this.economy = new VaultEconomy(this.banks);
        else this.economy = new SkyEconomy(this.banks);
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().registerCommand(new CoinsCommand());
        Samurai.getInstance().getCommandManager().registerCommand(new EconomyCommand());
        Samurai.getInstance().getCommandManager().registerCommand(new PayCommand());
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new EconomyListener(), Samurai.getInstance());
    }

    public BankAccount getBankAccount(UUID uuid) {
        return this.banks.getOrDefault(uuid, null);
    }

}
