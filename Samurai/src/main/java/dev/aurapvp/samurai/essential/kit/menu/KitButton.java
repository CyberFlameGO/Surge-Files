package dev.aurapvp.samurai.essential.kit.menu;

import dev.aurapvp.samurai.essential.kit.Kit;
import dev.aurapvp.samurai.essential.kit.KitCooldown;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.Callback;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.TimeUtils;
import dev.aurapvp.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class KitButton extends Button {

    private Player player;
    private Kit kit;
    private Callback.SingleCallback<Kit> callable;

    @Override
    public int getSlot() {
        return kit.getSlot();
    }

    @Override
    public ItemStack getItem() {
        ItemBuilder builder = new ItemBuilder(this.kit.getData().getItemType());
        Optional<KitCooldown> cooldown = Samurai.getInstance().getKitHandler().getCooldown(player.getUniqueId(), this.kit.getName());

        builder.setName(kit.getDisplayName());
        builder.setDurability(kit.getData().getData());

        if (cooldown.isPresent()) {
            List<String> lore = new ArrayList<>();

            for (String s : this.kit.getDescriptionOnCooldown()) {
                lore.add(s.replaceAll("%cooldown%", TimeUtils.formatIntoHHMMSS((int) (cooldown.get().getExpiry() / 1000))));
            }

            builder.setLore(lore);
        } else if (player.hasPermission("samurai.kit." + kit.getName())) {
            builder.setLore(this.kit.getDescription());
        } else {
            builder.setLore(this.kit.getDescriptionNoPermission());
        }

        if (kit.isGlowing()) {
            builder.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
            builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        }

        return builder.create();
    }

    @Override
    public void action(InventoryClickEvent event) throws Exception {
        if (callable != null) {
            callable.call(this.kit);
            return;
        }

        if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
            player.setMetadata("opening_menu", new FixedMetadataValue(Samurai.getInstance(), true));
            new KitPreviewMenu(this.kit, null).openMenu(player);
            player.removeMetadata("opening_menu", Samurai.getInstance());
            return;
        }
        this.kit.apply(player, player.isOp() || player.hasPermission("samurai.kit.bypass"));
    }

    @Override
    public boolean clickUpdate() {
        return super.clickUpdate();
    }
}
