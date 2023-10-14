package dev.lbuddyboy.pcore.essential.plot;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import dev.lbuddyboy.pcore.essential.plot.command.PlotCommand;
import dev.lbuddyboy.pcore.essential.plot.listener.PlotListener;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.LocationUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

@Getter
public class PlotHandler implements IModule {

    private Config config;
    private File defaultSchematicFile;
    private PlotGrid grid;

    @Override
    public void load(pCore plugin) {
        reload();
        this.loadListeners();
        this.loadCommands();
    }

    @Override
    public void unload(pCore plugin) {

    }

    @Override
    public void reload() {
        this.loadConfig();
        this.grid = new PlotGrid(this.config);
    }

    private void loadConfig() {
        this.config = new Config(pCore.getInstance(), "plots");
        this.defaultSchematicFile = new File(
                new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics"),
                this.config.getString("private-mine-settings.schematic-name") + ".schematic");
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PlotListener(), pCore.getInstance());
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().registerCommand(new PlotCommand());
    }

    public PrivatePlot fetchCache(UUID uuid) {
        return pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid).getPlot();
    }

    public void updateCache(UUID uuid, PrivatePlot plot) {
        MineUser owner = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);
        if (owner == null) return;

        owner.setPlot(plot);
        owner.save();
    }

    public PrivatePlot fetchPrivateMineAt(Location location) {
        for (MineUser user : pCore.getInstance().getMineUserHandler().getUsers().values()) {
            if (user.getPlot() == null) continue;
            if (!user.getPlot().getBounds().contains(location))  {
                System.out.println("not in bounds");
                System.out.println("bound 1: " + LocationUtils.toString(user.getPlot().getBounds().getLowerNE()));
                System.out.println("bound 2: " + LocationUtils.toString(user.getPlot().getBounds().getUpperSW()));
                continue;
            }

            return user.getPlot();
        }

        return null;
    }

    public PrivatePlot createPlot(UUID sender) {
        PrivatePlot mine = new PrivatePlot(sender);

        grid.createMine(mine);

        return mine;
    }

    public void deletePlot(UUID sender, PrivatePlot mine) {
        this.grid.delete(mine);

        MineUser owner = pCore.getInstance().getMineUserHandler().tryMineUserAsync(mine.getOwner());
        if (owner == null) return;

        owner.setPrivateMine(null);
        owner.save();
    }

}
