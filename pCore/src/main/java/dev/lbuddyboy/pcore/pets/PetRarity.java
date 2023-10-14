package dev.lbuddyboy.pcore.pets;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Config;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class PetRarity {

    private String name, displayName, color;
    private int defaultWalk, menuSlot;
    private ItemStack openItem;

    public PetRarity(String name, Config config) {
        this.name = name;
        this.displayName = config.getString("rarities." + name + ".display-name");
        this.color = config.getString("rarities." + name + ".color");
        this.defaultWalk = config.getInt("rarities." + name + ".default-walk");
        this.menuSlot = config.getInt("rarities." + name + ".menu-slot");
    }

    public List<IPet> getPets() {
        return pCore.getInstance().getPetHandler().getPets().values().stream().filter(pet -> pet.getPetRarity() == this).collect(Collectors.toList());
    }

}
