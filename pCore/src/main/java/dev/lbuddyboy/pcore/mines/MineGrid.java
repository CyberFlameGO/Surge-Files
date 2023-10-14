package dev.lbuddyboy.pcore.mines;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.registry.WorldData;
import dev.lbuddyboy.pcore.mines.generator.EmptyWorldGenerator;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.Cuboid;
import dev.lbuddyboy.pcore.util.LocationUtils;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Data
public class MineGrid {

    private Config config;
    private World world;
    private com.sk89q.worldedit.world.World worldEditWorld;
    private int LAST_X = 1000;
    private int GRID_SPACING;
    private File defaultSchematicFile;
    private Location startingLocation, spawnPoint, boxMinimumPoint, boxMaximumPoint, pitMinimumPoint, pitMaximumPoint;
    private EditSession editSession;
    private Clipboard clipboard;

    public MineGrid(Config config) {
        this.config = config;
        this.world = Bukkit.createWorld(
                new WorldCreator(config.getString("grid.world"))
                        .type(WorldType.FLAT)
                        .generator(new EmptyWorldGenerator()));
        this.LAST_X = config.getInt("grid.last_x");
        this.GRID_SPACING = config.getInt("grid.spacing");
        this.defaultSchematicFile = new File(
                new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics"),
                this.config.getString("private-mine-settings.schematic-name") + ".schematic");
        this.startingLocation = new Location(this.world, 1000, 80, 0);
        this.spawnPoint = LocationUtils.getLocation(LocationUtils.locationFromConfigSect("private-mine-settings.spawn-location", config), world);
        this.boxMinimumPoint = LocationUtils.getLocation(LocationUtils.locationFromConfigSect("private-mine-settings.box-min", config), world);
        this.boxMaximumPoint = LocationUtils.getLocation(LocationUtils.locationFromConfigSect("private-mine-settings.box-max", config), world);
        this.pitMinimumPoint = LocationUtils.getLocation(LocationUtils.locationFromConfigSect("private-mine-settings.pit-min", config), world);
        this.pitMaximumPoint = LocationUtils.getLocation(LocationUtils.locationFromConfigSect("private-mine-settings.pit-max", config), world);
        this.editSession = new EditSessionBuilder(new BukkitWorld(this.world))
                .limitUnlimited()
                .allowedRegionsEverywhere()
                .fastmode(true).build();
        this.worldEditWorld = FaweAPI.getWorld(this.world.getName());
        try {
            this.clipboard = ClipboardFormat.SCHEMATIC.getReader(Files.newInputStream(this.defaultSchematicFile.toPath())).read(this.worldEditWorld.getWorldData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        this.config.set("grid.last_x", this.LAST_X);
        this.config.set("grid.spacing", this.GRID_SPACING);
        this.config.save();
    }

    public void createMine(PrivateMine mine) {
        Location spawnLocation = getNextLocation();
        spawnLocation.setY(this.spawnPoint.getY());
        spawnLocation.setYaw(this.spawnPoint.getYaw());
        spawnLocation.setPitch(this.spawnPoint.getPitch());

        AffineTransform transform = new AffineTransform();

        ForwardExtentCopy copy = new ForwardExtentCopy(clipboard, clipboard.getRegion(), clipboard.getOrigin(), this.editSession, BukkitUtil.toVector(spawnLocation));
        if (!transform.isIdentity()) copy.setTransform(transform);
        copy.setSourceMask(new ExistingBlockMask(clipboard));
        try {
            Operations.completeLegacy(copy);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
        editSession.flushQueue();

        Location boxMin = this.boxMinimumPoint.clone().add(spawnLocation.getX(), 0, spawnLocation.getZ()), boxMax = this.boxMaximumPoint.clone().add(spawnLocation.getX(), 0, spawnLocation.getZ());
        Location pitMin = this.pitMinimumPoint.clone().add(spawnLocation.getX(), 0, spawnLocation.getZ()), pitMax = this.pitMaximumPoint.clone().add(spawnLocation.getX(), 0, spawnLocation.getZ());
        Location boundMin = spawnLocation.clone().subtract(clipboard.getDimensions().getX(), 0, clipboard.getDimensions().getZ()), boundMax = spawnLocation.clone().add(clipboard.getDimensions().getX(), 256, clipboard.getDimensions().getZ());
        Cuboid minePit = new Cuboid(pitMin, pitMax), minePitBox = new Cuboid(boxMin, boxMax);

        boundMin.setY(0);
        boundMax.setY(256);

        mine.setSpawnLocation(spawnLocation);
        mine.setBounds(new Cuboid(boundMin, boundMax));
        mine.setMinePitBox(minePitBox);
        mine.setMinePit(minePit);

        LAST_X = LAST_X + GRID_SPACING;
        save();
    }

    public void delete(PrivateMine mine) {
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(this.worldEditWorld, -1);
        try {
            editSession.setBlocks(new CuboidRegion(BukkitUtil.toVector(mine.getBounds().getLowerNE().toVector()), BukkitUtil.toVector(mine.getBounds().getUpperSW().toVector())), new BaseBlock(BlockID.AIR));
        } catch (MaxChangedBlocksException e) {
            // As of the blocks are unlimited this should not be called
        }
    }

    public Location getNextLocation() {
        return this.startingLocation.clone().add(LAST_X, 0, 0);
    }

}
