package dev.lbuddyboy.pcore.mines;

import com.boydti.fawe.object.visitor.FastIterator;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.user.model.RankInfo;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cuboid;
import dev.lbuddyboy.pcore.util.LocationUtils;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PrivateMine {

    private UUID owner;
    private int blocksLeft, totalBlocks;
    private boolean open;
    private List<UUID> allowedUUIDs;
    private double tax;
    private long tokensTaxed;
    private Location spawnLocation;
    /* pit = inside layer | box = the box around that is bedrock */
    private Cuboid bounds, minePit, minePitBox;
    private RankInfo rankInfo = new RankInfo();

    private transient long lastResetMillis;

    public PrivateMine(UUID owner) {
        this.owner = owner;
        this.rankInfo = new RankInfo();
    }

    public MineRank getMineRank() {
        long level = getMineRankLevel() > pCore.getInstance().getPrivateMineHandler().getRanks().size() ? getMineRankLevel() - 1 : getMineRankLevel();
        return pCore.getInstance().getPrivateMineHandler().getRanks().get(String.valueOf(level));
    }

    public long getMineRankLevel() {
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(this.owner);
        
        if (user == null) return 1;

        long level = this.rankInfo.getRank() / pCore.getInstance().getPrivateMineHandler().getLevelIncrement();
        return level + 1;
    }

    public void increaseProgress(UUID player, double multiplier) {
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(player);

        if (multiplier <= 0) {
            this.rankInfo.increaseProgress(CustomEnchant.RANDOM.nextDouble(1, 1.53));
        } else {
            this.rankInfo.increaseProgress(CustomEnchant.RANDOM.nextDouble(1, 1.53 * multiplier));
        }
        if (this.rankInfo.getProgress() >= this.rankInfo.getNeededProgress()) {
            this.rankInfo.setRank(this, this.rankInfo.getRank() + 1);
            this.rankInfo.setProgress(0);
            reset();
        }

        user.flagUpdate();
    }

    public List<Player> getPlayers() {
        return this.bounds.getWorld().getPlayers().stream().filter(player -> this.bounds.contains(player.getLocation())).collect(Collectors.toList());
    }

    public void reset() {
        this.lastResetMillis = System.currentTimeMillis();
        this.blocksLeft = 0;
        this.totalBlocks = 0;

        for (Player p : this.bounds.getWorld().getPlayers()) {
            if (!this.minePitBox.contains(p.getLocation())) continue;

            p.teleport(getMinePit().getCenter().clone().add(0, 15, 0));
            p.sendTitle(CC.translate("&a&lMINE RESET"), CC.translate("&aThis private mine has been reset."));
        }

        for (Block block : this.minePitBox) {
            if (block.getType() != Material.AIR) continue;

            for (Map.Entry<MaterialData, Double> entry : new ArrayList<>(getMineRank().getMaterials().entrySet())) {
                MaterialData data = entry.getKey();
                double chance = entry.getValue();
                double random = CustomEnchant.RANDOM.nextDouble(100);

                if (random <= chance) {
                    block.setTypeIdAndData(data.getItemType().getId(), data.getData(), false);
                } else {
                    data = getMineRank().getRandomMaterial();
                    block.setTypeIdAndData(data.getItemType().getId(), data.getData(), false);
                }
            }
        }

        this.blocksLeft = this.minePit.getBlocks().size();
        this.totalBlocks = this.blocksLeft;
        setupBox();
    }

    public void setupBox() {
        for (Block wall : this.minePit.clone().expand(Cuboid.CuboidDirection.Down, 2).getWalls()) {
            wall.setType(Material.BEDROCK);
        }
        for (Block wall : getMinePitRoof()) {
            wall.setType(Material.AIR);
        }
        for (Block wall : getMinePitRoof().getWalls()) {
            wall.setType(Material.BEDROCK);
        }
        for (Block wall : getMinePitFloor()) {
            wall.setType(Material.BEDROCK);
        }

    }

    public long getNextResetMillis() {
        return (this.lastResetMillis + (60_000 * 2)) - System.currentTimeMillis();
    }

    public boolean isResettable() {
        return getNextResetMillis() <= 0;
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("owner", this.owner.toString());
        object.addProperty("blocksLeft", this.blocksLeft);
        object.addProperty("totalBlocks", this.totalBlocks);
        object.addProperty("open", this.open);
        object.addProperty("allowedUUIDs", GSONUtils.getGSON().toJson(this.allowedUUIDs, GSONUtils.UUID));
        object.addProperty("spawnLocation", LocationUtils.serializeString(this.spawnLocation));
        object.addProperty("bounds", LocationUtils.serializeStringCuboid(this.bounds));
        object.addProperty("minePitBox", LocationUtils.serializeStringCuboid(this.minePitBox));
        object.addProperty("minePit", LocationUtils.serializeStringCuboid(this.minePit));
        object.addProperty("rankInfo", GSONUtils.getGSON().toJson(this.rankInfo, GSONUtils.RANK_INFO));
        object.addProperty("tax", this.tax);
        object.addProperty("tokensTaxed", this.tokensTaxed);

        return object;
    }

    public static PrivateMine deserialize(JsonObject object) {
        PrivateMine mine = new PrivateMine();

        mine.setOwner(UUID.fromString(object.get("owner").getAsString()));
        mine.setBlocksLeft(object.get("blocksLeft").getAsInt());
        mine.setTotalBlocks(object.get("totalBlocks").getAsInt());
        mine.setOpen(object.get("open").getAsBoolean());
        mine.setAllowedUUIDs(GSONUtils.getGSON().fromJson(object.get("allowedUUIDs").getAsString(), GSONUtils.UUID));
        mine.setSpawnLocation(LocationUtils.deserializeString(object.get("spawnLocation").getAsString()));
        mine.setBounds(LocationUtils.deserializeStringCuboid(object.get("bounds").getAsString()));
        mine.setMinePitBox(LocationUtils.deserializeStringCuboid(object.get("minePitBox").getAsString()));
        mine.setMinePit(LocationUtils.deserializeStringCuboid(object.get("minePit").getAsString()));
        if (object.has("rankInfo")) mine.setRankInfo(GSONUtils.getGSON().fromJson(object.get("rankInfo").getAsString(), GSONUtils.RANK_INFO));
        if (object.has("tax")) mine.setTax(object.get("tax").getAsDouble());
        if (object.has("tokensTaxed")) mine.setTokensTaxed(object.get("tokensTaxed").getAsLong());

        return mine;
    }

/*    public Cuboid getMinePitRoof() {
        return this.minePit
                .expand(Cuboid.CuboidDirection.North, 1)
                .expand(Cuboid.CuboidDirection.East, 1)
                .expand(Cuboid.CuboidDirection.West, 1)
                .expand(Cuboid.CuboidDirection.South, 1);
    }*/

    public Cuboid getMinePitRoof() {
        Cuboid floor = this.minePit.clone();

        floor.setY1(floor.getY2());

        return floor;
    }

    public Cuboid getMinePitFloor() {
        Cuboid floor = this.minePit.clone();

        floor.setY2(floor.getY1() - 2);
        floor.setY1(floor.getY2());

        return floor
                .expand(Cuboid.CuboidDirection.North, (int) getMineRankLevel())
                .expand(Cuboid.CuboidDirection.East, (int) getMineRankLevel())
                .expand(Cuboid.CuboidDirection.West, (int) getMineRankLevel())
                .expand(Cuboid.CuboidDirection.South, (int) getMineRankLevel());
    }

    public double getTaxed(int amount) {
        return amount * this.tax;
    }

}
