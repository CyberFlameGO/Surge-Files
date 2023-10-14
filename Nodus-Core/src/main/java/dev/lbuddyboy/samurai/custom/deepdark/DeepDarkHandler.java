package dev.lbuddyboy.samurai.custom.deepdark;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkAbility;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkEntity;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkStage;
import dev.lbuddyboy.samurai.custom.deepdark.entity.impl.LevitateAbility;
import dev.lbuddyboy.samurai.custom.deepdark.entity.impl.LightningAbility;
import dev.lbuddyboy.samurai.custom.deepdark.entity.impl.ShockwaveAbility;
import dev.lbuddyboy.samurai.custom.deepdark.listener.DeepDarkListener;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class DeepDarkHandler {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private Config config;
    private LootTable lootTable;
    private ItemStack spawnItem;
    private double health;
    private String bossBarTitle;
    private BarColor bossBarColor;
    @Setter
    private DarkEntity darkEntity;
    private Map<String, DarkAbility> darkAbilities;
    private long focusCooldown;

    public DeepDarkHandler() {
        this.darkAbilities = new HashMap<>();
        this.darkAbilities.put("lightning", new LightningAbility());
        this.darkAbilities.put("shockwave", new ShockwaveAbility());
        this.darkAbilities.put("levitate", new LevitateAbility());

        reload();

        LootTableHandler.getLootTables().add(this.lootTable);
        Bukkit.getPluginManager().registerEvents(new DeepDarkListener(), Samurai.getInstance());

        Bukkit.getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
            DarkEntity entity = this.darkEntity;

            if (entity == null) {
                return;
            }

            Warden warden = entity.getWarden();
            DarkStage stage = entity.getStage();

            if (warden.isDead()) {
                entity.getBossBar().removeAll();
                darkEntity = null;
                return;
            }

            for (DarkAbility ability : darkAbilities.values()) {
                if (random.nextInt(100) > ability.getChance()) continue;

                ability.activate(warden.getLocation());
                break;
            }

            for (int i = 0; i < 3; i++) {
                warden.getWorld().spawnParticle(Particle.FALLING_LAVA, warden.getLocation().clone().add(-2, i, -2), 4);
                warden.getWorld().spawnParticle(Particle.FALLING_LAVA, warden.getLocation().clone().add(2, i, -2), 4);
                warden.getWorld().spawnParticle(Particle.FALLING_LAVA, warden.getLocation().clone().add(2, i, 2), 4);
                warden.getWorld().spawnParticle(Particle.FALLING_LAVA, warden.getLocation().clone().add(-2, i, 2), 4);
            }

            List<Player> nearby = warden.getNearbyEntities(20, 20, 20).stream().filter(e -> e instanceof Player).map(e -> ((Player) e)).toList();

            for (Player player : nearby) {
                if (entity.getBossBar() == null) {
                    entity.setBossBar(Bukkit.createBossBar(CC.translate(this.bossBarTitle, "%warden-health%", ((int) warden.getHealth())),
                            BarColor.BLUE, BarStyle.SEGMENTED_6)
                    );
                    entity.getBossBar().setProgress(warden.getHealth() / warden.getMaxHealth());
                } else {
                    entity.getBossBar().setTitle(CC.translate(this.bossBarTitle,
                            "%warden-health%", ((int) warden.getHealth())
                    ));
                    entity.getBossBar().setProgress(warden.getHealth() / warden.getMaxHealth());
                }
                if (!entity.getBossBar().getPlayers().contains(player)) entity.getBossBar().addPlayer(player);
            }

            if (nearby.size() > 0) {
                if (focusCooldown - System.currentTimeMillis() < 0) {

                    Player player = nearby.get(ThreadLocalRandom.current().nextInt(0, nearby.size()));

                    warden.setTarget(player);
                    warden.increaseAnger(player, warden.getAnger(player) + 1);
                    focusCooldown = System.currentTimeMillis() + 10_000L;
                }
            }

            Team team = Samurai.getInstance().getTeamHandler().getTeam("DeepDark");
            if (team != null && team.getClaims().size() > 0 && team.getHQ() != null) {
                if (!team.getClaims().get(0).contains(warden.getLocation())) {
                    warden.teleport(team.getHQ());
                }
            }

            for (Entity e : warden.getNearbyEntities(20, 20, 20)) {
                if (!(e instanceof Player player)) continue;

                warden.increaseAnger(player, warden.getAnger(player) + 1);
            }

            if (stage == DarkStage.SPAWNING) {
                if (warden.getLocation().clone().getBlock().getType().isSolid()) {
                    warden.teleport(warden.getLocation().clone().add(0, 2, 0));
                    entity.setStage(DarkStage.SPAWNED);
                }
            }
        }, 20, 20);
    }

    public void reload() {
        this.config = new Config(Samurai.getInstance(), "deep-dark");
        this.lootTable = new LootTable(this.config);
        this.spawnItem = ItemUtils.itemStackFromConfigSect("spawn-item", this.config);
        this.health = this.config.getInt("boss-settings.health", 500);
        this.bossBarTitle = this.config.getString("boss-settings.bossbar.title", "&3&lDEEP DARK &7» &bWarden Boss &c❤%warden-health%");
        this.bossBarColor = BarColor.valueOf(this.config.getString("boss-settings.bossbar.color", "BLUE"));
    }

    public void spawnEntity(Player player, Location location) {
        Warden warden = player.getWorld().spawn(location.subtract(0, 3, 0), Warden.class);
        DarkEntity darkEntity = new DarkEntity(player.getUniqueId(), warden, location);

        warden.setHealth(this.health);
        warden.setMetadata("DARK_ENTITY", new FixedMetadataValue(Samurai.getInstance(), true));

        this.darkEntity = darkEntity;
    }

    public void spawnEntity(Location location) {
        Warden warden = location.getWorld().spawn(location.subtract(0, 3, 0), Warden.class);
        DarkEntity darkEntity = new DarkEntity(null, warden, location);

        warden.setHealth(this.health);
        warden.setMetadata("DARK_ENTITY", new FixedMetadataValue(Samurai.getInstance(), true));

        this.darkEntity = darkEntity;
    }

}
