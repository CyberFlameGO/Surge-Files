package dev.aurapvp.samurai.faction.claim;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.faction.command.FactionCommand;
import dev.aurapvp.samurai.faction.editor.impl.ClaimForEditor;
import dev.aurapvp.samurai.faction.listener.FactionClaimListener;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cuboid;
import io.netty.util.internal.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ClaimHandler {

    public static final Material[] PILLAR_BLOCKS = new Material[]{
            Material.GOLD_BLOCK,
            Material.EMERALD_BLOCK,
            Material.BEACON,
            Material.BEDROCK,
            Material.WHITE_WOOL,
            Material.DIAMOND_BLOCK,
            Material.OAK_LOG,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.MANGROVE_LOG,
            Material.SPRUCE_LOG,
            Material.REDSTONE_BLOCK
    };
    private final Map<Player, List<Location>> blockChanges = new HashMap<>();
    private final Table<String, Long, Claim> claims = HashBasedTable.create();

    public void updateClaim(Claim claim, boolean remove) {
        int minX = Math.min(claim.getCuboid().getLowerX(), claim.getCuboid().getUpperX());
        int minZ = Math.min(claim.getCuboid().getLowerZ(), claim.getCuboid().getUpperZ());

        int maxX = Math.max(claim.getCuboid().getLowerX(), claim.getCuboid().getUpperX());
        int maxZ = Math.max(claim.getCuboid().getLowerZ(), claim.getCuboid().getUpperZ());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (remove) {
                    this.claims.remove(claim.getCuboid().getWorld().getName(), Claim.toLong(x, z));
                } else {
                    this.claims.put(claim.getCuboid().getWorld().getName(), Claim.toLong(x, z), claim);
                }
            }
        }
    }

    public List<Claim> getClaims(Location origin, int radius) {
        Location nw = new Location(origin.getWorld(), origin.getBlockX() - radius, origin.getBlockY() - radius, origin.getBlockZ() - radius);
        Location se = new Location(origin.getWorld(), origin.getBlockX() + radius, origin.getBlockY() + radius, origin.getBlockZ() + radius);

        return getClaims(nw, se);
    }

    public List<Claim> getClaims(Location pos1, Location pos2) {
        List<Claim> claims = new ArrayList<>();
        int x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = x1; x < x2; x++) {
            for (int z = z1; z < z2; z++) {
                Long hashed = Claim.toLong(x, z);

                Claim claim = this.claims.get(pos1.getWorld().getName(), hashed);

                if (claim != null) {
                    claims.add(claim);
                    System.out.println("Found claim: " + claim.getFaction());
                }
            }
        }

        return claims;
    }

    public void unMapClaims(Player player) {
        List<Location> locations = blockChanges.getOrDefault(player, new ArrayList<>());

        if (locations != null && !locations.isEmpty()) {
            for (Location location : locations) {
                player.sendBlockChange(location, Material.AIR, (byte) 0);
            }
            blockChanges.remove(player);
            player.sendMessage(CC.translate("&cRemoving all previous claim pillars..."));
        }
    }

    public Map<Claim, List<Material>> mapClaims(Player player) {
        Map<Claim, List<Material>> factions = new HashMap<>();
        List<Location> locations = blockChanges.getOrDefault(player, new ArrayList<>());
        Location location = player.getLocation().clone();
        List<Claim> claims = new ArrayList<>();
        final int[] i = {0};

        int minimumX = location.getBlockX() - FactionConfiguration.FACTION_MAP_RADIUS.getInt();
        int minimumZ = location.getBlockZ() - FactionConfiguration.FACTION_MAP_RADIUS.getInt();
        int maximumX = location.getBlockX() + FactionConfiguration.FACTION_MAP_RADIUS.getInt();
        int maximumZ = location.getBlockZ() + FactionConfiguration.FACTION_MAP_RADIUS.getInt();

        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                Claim claim = Samurai.getInstance().getFactionHandler().getClaimByLocation(new Location(location.getWorld(), x, player.getLocation().getY(), z));

                if (claim == null) continue;
                if (claims.contains(claim)) continue;

                claims.add(claim);
            }
        }

        claims.forEach(claim -> {
            Material material = PILLAR_BLOCKS[i[0]++];
            List<Location> corners = claim.getCuboid().getFourCorners(player.getLocation().getY());
            List<Material> materials = factions.getOrDefault(claim, new ArrayList<>());

            corners.forEach(l -> locations.addAll(createPillar(player, l.clone(), material)));
            materials.add(material);
            factions.put(claim, materials);
        });

        if (!factions.isEmpty()) blockChanges.put(player, locations);

        return factions;
    }

    private List<Location> createPillar(Player player, Location origin, Material material) {
        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (origin.add(0, 1, 0).getBlock().getType() != Material.AIR) continue;
            if (i % 3 == 0) {
                player.sendBlockChange(origin, material, (byte) 0);
                locations.add(origin.clone());
                continue;
            }

            player.sendBlockChange(origin, Material.GLASS, (byte) 0);
            locations.add(origin.clone());
        }

        return locations;
    }

    public ClaimPurchaseInfo purchaseClaim(Player player, Faction faction) {
        Location pos1 = FactionClaimListener.getPos1().get(player);
        Location pos2 = FactionClaimListener.getPos2().get(player);

        if (pos1 == null) {
            player.sendMessage(CC.translate("&cYou need to set a first position &7(left click a block)&f with the claim wand!"));
            return null;
        }

        if (pos2 == null) {
            player.sendMessage(CC.translate("&cYou need to set a second position &7(right click a block)&c with the claim wand!"));
            return null;
        }

        Cuboid cuboid = new Cuboid(pos1, pos2);
        double money = (cuboid.getSizeX() + cuboid.getSizeZ()) * FactionConfiguration.LAND_CLAIMED_PRICE_PER_BLOCK.getDouble();
        String moneyStr = Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(money);

        if (!player.hasMetadata(ClaimForEditor.FORCE_CLAIM_METADATA)) {
            if (!isAvailableToClaim(cuboid)) {
                player.sendMessage(CC.translate("&cThis claim selection contains unclaimable land."));
                return null;
            }

            if (cuboid.getSizeX() > FactionConfiguration.LAND_CLAIMED_MAX_CLAIM_SIZE.getInt()) {
                player.sendMessage(CC.translate("&cYou cannot claim this land, as it is too large. It needs to be less than a " + FactionConfiguration.LAND_CLAIMED_MAX_CLAIM_SIZE.getInt() + "x" + FactionConfiguration.LAND_CLAIMED_MAX_CLAIM_SIZE.getInt() + "."));
                return null;
            }

            if (cuboid.getSizeZ() > FactionConfiguration.LAND_CLAIMED_MAX_CLAIM_SIZE.getInt()) {
                player.sendMessage(CC.translate("&cYou cannot claim this land, as it is too large. It needs to be less than a " + FactionConfiguration.LAND_CLAIMED_MAX_CLAIM_SIZE.getInt() + "x" + FactionConfiguration.LAND_CLAIMED_MAX_CLAIM_SIZE.getInt() + "."));
                return null;
            }

            if (faction.getClaims().size() >= FactionConfiguration.MAX_CLAIMS.getInt() && !faction.isSystemFaction()) {
                player.sendMessage(CC.translate("&cYou have too many existing claims."));
                return null;
            }

            if (!faction.getClaims().isEmpty() && getClaimsTouching(faction, cuboid).isEmpty()) {
                player.sendMessage(CC.translate("&cYou have existing claims. If you wish to expand your claim you will need to make them touch."));
                return null;
            }

            if (cuboid.getSizeZ() < 5 || cuboid.getSizeX() < 5) {
                player.sendMessage(CC.translate("&cYou need to make the claim atleast a 5x5 claim."));
                return null;
            }

            if (faction.getBalance() < money) {
                player.sendMessage(CC.translate("&cYou do not have enough money for this claim. You need $" + moneyStr));
                return null;
            }

            faction.setBalance(faction.getBalance() - money);
        } else {
            player.getInventory().remove(Samurai.getInstance().getFactionHandler().getOPClaimWand(player));
        }

        Claim claim = new Claim(UUID.randomUUID(), faction.getUniqueId());

        claim.setCuboid(cuboid);
        faction.getClaims().add(claim);
        updateClaim(claim, false);
        faction.triggerUpdate();

        if (isMapped(player)) {
            FactionCommand.map(player);
        }
        FactionCommand.map(player);

        player.getInventory().remove(Samurai.getInstance().getFactionHandler().getClaimWand());

        removePillar(player, pos1);
        removePillar(player, pos2);

        return new ClaimPurchaseInfo(money, "");
    }

    public ClaimPurchaseInfo purchaseClaim(Player player) {
        return purchaseClaim(player, Samurai.getInstance().getFactionHandler().getFactionByPlayer(player));
    }

    private boolean isAvailableToClaim(Cuboid cuboid) {
        for (Faction faction : Samurai.getInstance().getFactionHandler().getFactions().values()) {
            for (Claim claim : faction.getClaims()) {
                if (claim.getCuboid().contains(cuboid.getLowerNE()) || claim.getCuboid().contains(cuboid.getUpperSW())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void createPillar(Player player, Location origin) {
        for (int i = 0; i < 10; i++) {
            if (origin.add(0, 1, 0).getBlock().getType() != Material.AIR) continue;

            player.sendBlockChange(origin, Material.GLASS, (byte) 5);
        }
    }

    public void removePillar(Player player, Location origin) {
        for (int i = 0; i < 10; i++) {
            if (origin.add(0, 1, 0).getBlock().getType() != Material.AIR) continue;

            player.sendBlockChange(origin, Material.AIR, (byte) 0);
        }
    }

    public void clearAllPillars() {
        Bukkit.getOnlinePlayers().forEach(this::unMapClaims);
    }

    public List<Claim> getClaimsTouching(Faction faction, Cuboid cuboid) {
        return faction.getClaims().stream().filter(claim -> isTouching(claim, cuboid)).collect(Collectors.toList());
    }

    public boolean isTouching(Claim claim, Cuboid cuboid) {
        for (Block block : cuboid.outset(Cuboid.CuboidDirection.Horizontal, 3)) {
            if (claim.getCuboid().contains(block)) {
                Claim claimAt = Samurai.getInstance().getFactionHandler().getClaimByLocation(block.getLocation());
                if (claimAt != null) return true;
            }
        }
        return false;
    }

    public boolean isMapped(Player player) {
        return this.blockChanges.containsKey(player);
    }

    @AllArgsConstructor
    @Getter
    public class ClaimPurchaseInfo {

        private double price;
        private String size;

    }

}
