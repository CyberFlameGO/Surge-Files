package dev.lbuddyboy.samurai.util.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

@Getter
@Setter
public class StackedEntity {

    private LivingEntity entity;
    private int count;
    
    public StackedEntity(LivingEntity entity, final int count) {
        this.count = count;
        this.entity = entity;
    }
    
    public static StackedEntity getFromEntity(final LivingEntity entity) {
        if (entity.getCustomName() != null && ChatColor.stripColor(entity.getCustomName()).contains("Mobs")) {
            return new StackedEntity(entity, Integer.parseInt(ChatColor.stripColor(entity.getCustomName().replace("x Mobs", "").replace(" ", ""))));
        }
        return null;
    }
    
    public void updateDisplay() {
        this.entity.setCustomName(ChatColor.GOLD.toString() + ChatColor.BOLD + this.count + "x Mobs");
        this.entity.setCustomNameVisible(true);
    }
    
    public void addEntity() {
        ++this.count;
    }
	
}
