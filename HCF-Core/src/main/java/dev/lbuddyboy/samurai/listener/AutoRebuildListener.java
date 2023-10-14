package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.team.event.TeamRaidableEvent;
import dev.lbuddyboy.samurai.team.event.TeamRegenerateEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoRebuildListener implements Listener {

    @EventHandler
    private void onTeamRaidable(TeamRaidableEvent event) {
        Team team = event.getTeam();

        // Copy the claim list for thread safety reasons
        List<Claim> claims = new ArrayList<>(team.getClaims());
        if (claims.isEmpty()) return;

        Thread thread = new Thread(() -> {
            for (Claim claim : claims) {
                Location min = claim.getMinimumPoint();
                Location max = claim.getMaximumPoint();
                World world = min.getWorld();

                int minX = min.getBlockX();
                int maxX = max.getBlockX();

                int minZ = min.getBlockZ();
                int maxZ = max.getBlockZ();

                System.out.println("Saving claim " + claim.getName());

                File file = new File(Samurai.getInstance().getDataFolder(), "claim-snapshots" + File.separator + claim.getName());

                if (file.getParentFile().mkdirs()) {
                    System.out.println("Created parent directory " + file.getParentFile().getName());
                }

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); DataOutputStream dos = new DataOutputStream(baos)) {
                    dos.writeUTF(world.getName());

                    dos.writeInt(minX);
                    dos.writeInt(maxX);

                    dos.writeInt(minZ);
                    dos.writeInt(maxZ);

                    AtomicInteger y = new AtomicInteger(world.getMinHeight());
                    CountDownLatch latch = new CountDownLatch(1);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            try {
                                for (int x = minX; x < maxX; x++) {
                                    for (int z = minZ; z < maxZ; z++) {
                                        Location loc = new Location(world, x, y.get(), z);
                                        Block block = loc.getBlock();
                                        dos.writeUTF(block.getBlockData().getAsString());
                                    }
                                }

                                if (y.incrementAndGet() == world.getMaxHeight()) {
                                    cancel();
                                    latch.countDown();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                team.sendMessage(ChatColor.RED + "An error occurred while saving claim '" + claim.getName() + "'");
                                cancel();
                                latch.countDown();
                            }
                        }
                    }.runTaskTimer(Samurai.getInstance(), 0, 1);

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(baos.toByteArray());
                    }

                    System.out.println("Saved claim " + claim.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.setName("save-" + team.getName());
        thread.start();
    }

    @EventHandler
    public void onTeamRegenerate(TeamRegenerateEvent event) {
        if (!event.isWasRaidable()) return;

        Team team = event.getTeam();

        // Copy the claim list for thread safety reasons
        List<Claim> claims = new ArrayList<>(team.getClaims());
        if (claims.isEmpty()) return;

        Thread thread = new Thread(() -> {
            team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your claim has begun regenerating.");

            for (Claim claim : claims) {
                File file = new File(Samurai.getInstance().getDataFolder(), "claim-snapshots" + File.separator + claim.getName());

                if (!file.exists()) {
                    team.sendMessage(ChatColor.RED + "No snapshot found for claim: " + claim.getName());
                    return;
                }

                System.out.println("Regenerating claim " + claim.getName());

                // Load the file into memory so we can read from DataInputStream on the main thread without lag caused by disk IO
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buf = new byte[4096];

                    int read;
                    while ((read = fis.read(buf)) != -1) {
                        baos.write(buf, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    team.sendMessage(ChatColor.RED + "An error occurred while regenerating claim '" + claim.getName() + "'");
                    return;
                }

                try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                    World world = Bukkit.getWorld(dis.readUTF());

                    int minX = dis.readInt();
                    int maxX = dis.readInt();

                    int minZ = dis.readInt();
                    int maxZ = dis.readInt();

                    AtomicInteger y = new AtomicInteger(world.getMinHeight());
                    CountDownLatch latch = new CountDownLatch(1);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            try {
                                for (int x = minX; x < maxX; x++) {
                                    for (int z = minZ; z < maxZ; z++) {
                                        BlockData blockData = Bukkit.getServer().createBlockData(dis.readUTF());
                                        Location loc = new Location(world, x, y.get(), z);
                                        Block block = loc.getBlock();

                                        if (block.getType() != blockData.getMaterial() && blockData.getMaterial() != Material.SPAWNER) {
                                            block.setBlockData(blockData);
                                        }
                                    }
                                }

                                if (y.incrementAndGet() == world.getMaxHeight()) {
                                    cancel();
                                    latch.countDown();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                team.sendMessage(ChatColor.RED + "An error occurred while regenerating claim '" + claim.getName() + "'");
                                cancel();
                                latch.countDown();
                            }
                        }
                    }.runTaskTimer(Samurai.getInstance(), 0, 1);

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Regenerated claim " + claim.getName());

                    if (file.delete()) {
                        System.out.println("Deleted " + file.getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your claim has finished regenerating.");
        });

        thread.setName("regenerate-" + team.getName());
        thread.start();
    }
}
