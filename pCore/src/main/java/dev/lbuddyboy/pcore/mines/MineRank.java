package dev.lbuddyboy.pcore.mines;

import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class MineRank {

    private String id;
    private MaterialData displayItem;
    private Map<MaterialData, Double> materials;
    private String displayName;
    private int blockData, mineSize;

    public MaterialData getRandomMaterial() {
        return new ArrayList<>(this.materials.keySet()).get(CustomEnchant.RANDOM.nextInt(0, materials.size()));
    }

    public ItemStack getDisplayItem(Player player) {
        String name = pCore.getInstance().getPrivateMineHandler().getConfig().getString("private-mines-menu.buttons.display-item.name");
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());
        List<String> lore = new ArrayList<>();

        for (String s : pCore.getInstance().getPrivateMineHandler().getConfig().getStringList("private-mines-menu.buttons.display-item.lore")) {
            if (s.equalsIgnoreCase("%blocks%")) {
                for (MaterialData data : this.materials.keySet()) {
                    lore.add(CC.GRAY + "- " + CompMaterial.fromItem(data.toItemStack()).name());
                }
            } else {
                lore.add(s);
            }
        }

        return new ItemBuilder(this.displayItem.toItemStack(), 1)
                .setName(name,
                        "%mine-rank-display%", this.displayName,
                        "%mine-rank-status%", cache.getMineRank() == this || cache.getMineRank().getMineSize() > this.mineSize ? "&a&lUNLOCKED" : "&c&lLOCKED"
                )
                .setLore(lore,
                        "%mine-rank-status%", cache.getMineRank() == this || cache.getMineRank().getMineSize() > this.mineSize ? "&a&lUNLOCKED" : "&c&lLOCKED"
                )
                .create();
    }

}
