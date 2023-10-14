package dev.lbuddyboy.pcore.robots.upgrade;

import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.pRobot;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.List;

@Getter
public abstract class RobotUpgrade {

    private final ConfigurationSection section;
    private final int costIncrement, maxLevel;
    private final String displayName;
    private final List<String> description;
    private final CompMaterial material;

    public abstract String getId();
    public abstract void multiply(pRobot robot);

    public RobotUpgrade() {
        this.section = pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("upgrades." + getId());
        this.costIncrement = this.section.getInt("cost-increment");
        this.maxLevel = this.section.getInt("max-level");
        this.displayName = this.section.getString("display-name");
        this.description = this.section.getStringList("description");
        this.material = CompMaterial.fromString(this.section.getString("display-material"));
    }

}
