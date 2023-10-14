package dev.lbuddyboy.vouchers;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.vouchers.api.FoxtrotAPI;
import dev.lbuddyboy.vouchers.api.VoucherAPI;
import dev.lbuddyboy.vouchers.command.VouchersCommand;
import dev.lbuddyboy.vouchers.command.completions.KitCompletion;
import dev.lbuddyboy.vouchers.command.contexts.KitContext;
import dev.lbuddyboy.vouchers.listener.VoucherListener;
import dev.lbuddyboy.vouchers.object.Voucher;
import dev.lbuddyboy.vouchers.util.Config;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:01 PM
 * GKits / me.lbuddyboy.gkits
 */

@Getter
public class lVouchers extends JavaPlugin {

	@Getter
	private static lVouchers instance;

	private Map<String, Voucher> voucher;
	private PaperCommandManager commandManager;

	private API api;
	private File vouchersFolder;
	private Config messageConfig;


	@SneakyThrows
	@Override
	public void onEnable() {
		instance = this;
		this.voucher = new HashMap<>();
		this.saveDefaultConfig();

		this.messageConfig = new Config(this, "messages");
		this.commandManager = new PaperCommandManager(this);

		this.loadCommands();
		this.loadListeners();
		this.loadVouchers();

		if (this.getServer().getPluginManager().getPlugin("Samurai") != null) {
			this.api = new FoxtrotAPI();
		} else {
			this.api = new VoucherAPI();
		}
	}

	private void loadCommands() {
		this.commandManager.getCommandCompletions().registerCompletion("vouchers", new KitCompletion());
		this.commandManager.getCommandContexts().registerContext(Voucher.class, new KitContext());
		this.commandManager.registerCommand(new VouchersCommand());
	}

	private void loadListeners() {
		this.getServer().getPluginManager().registerEvents(new VoucherListener(), this);
	}

	public void loadVouchers() {
		voucher.clear();

		File folder = new File(getDataFolder(), "vouchers");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.vouchersFolder = folder;

		for (String s : this.vouchersFolder.list()) {
			this.voucher.put(s.replaceAll(".yml", ""), new Voucher(new Config(this, s.replaceAll(".yml", ""), this.vouchersFolder)));
		}
	}

}
