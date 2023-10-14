package dev.lbuddyboy.vouchers.object;

import dev.lbuddyboy.vouchers.lVouchers;
import dev.lbuddyboy.vouchers.util.CC;
import dev.lbuddyboy.vouchers.util.Config;
import dev.lbuddyboy.vouchers.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:06 PM
 * GKits / me.lbuddyboy.gkits.object
 */

@AllArgsConstructor
@Data
public class Voucher {

    private Config config;

    private String name, displayName;
    private ItemStack displayItem;
    private List<String> commands;

    public Voucher(String name) {
        this.name = name;
    }

    public Voucher(Config config) {
        this.config = config;
        this.name = config.getFileName().replaceAll(".yml", "");

        reload();
    }

    public void reload() {
        this.config = new Config(lVouchers.getInstance(), this.name, lVouchers.getInstance().getVouchersFolder());
        this.displayName = CC.translate(config.getString("displayName"));
        this.commands = config.getStringList("commands");
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", this.config);
    }

    public void save() {
        this.config = new Config(lVouchers.getInstance(), this.name, lVouchers.getInstance().getVouchersFolder());

        this.config.set("displayName", this.displayName);
        this.config.set("commands", this.commands);
        ItemUtils.itemStackToConfigSect(this.displayItem, -1, "display-item", this.config);

        this.config.save();
    }

    public void delete() {
        if (this.config.getFile().exists()) this.config.getFile().delete();

        lVouchers.getInstance().getVoucher().remove(this.name);
    }
}
