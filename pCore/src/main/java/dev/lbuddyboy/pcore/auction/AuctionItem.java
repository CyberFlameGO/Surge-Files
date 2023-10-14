package dev.lbuddyboy.pcore.auction;

import dev.lbuddyboy.pcore.economy.EconomyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
@Data
public class AuctionItem {

    private UUID id, sender;
    private ItemStack item;
    private long sentAt, duration;
    private double price;
    private String economy;
    private boolean cancelled;

    public EconomyType getEconomyType() {
        return EconomyType.valueOf(this.economy);
    }

    public long getExpiry() {
        return (this.sentAt + duration) - System.currentTimeMillis();
    }

    public boolean isExpired() {
        return getExpiry() <= 0;
    }

    public boolean isBin() {
        return this.cancelled || isExpired();
    }

    public boolean matches(String search) {
        if (isBin()) return false;
        ItemStack stack = this.item;

        if (stack.hasItemMeta()) {
            return stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(search) ||
                    stack.getItemMeta().hasLore() && stack.getItemMeta().getLore().stream().map(String::toUpperCase).anyMatch(s -> s.contains(search.toUpperCase()));
        }

        return stack.getType().name().contains(search.toUpperCase());
    }

}
