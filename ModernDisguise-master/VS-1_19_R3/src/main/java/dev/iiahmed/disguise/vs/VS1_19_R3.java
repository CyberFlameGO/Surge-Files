package dev.iiahmed.disguise.vs;

import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.DisguiseUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.WorldDimension;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("all")
public final class VS1_19_R3 extends DisguiseProvider {

    private final Field id;

    {
        try {
            id = ClientboundAddEntityPacket.class.getDeclaredField("c");
            id.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshAsPlayer(@NotNull final Player player) {
        if (!player.isOnline()) {
            return;
        }

        final long seed = player.getWorld().getSeed();
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        Location location = player.getLocation().clone();
        MinecraftServer server = entityPlayer.c;
        IRegistry<WorldDimension> dimensions = server.aY().a().d(Registries.aG);
        WorldDimension worlddimension = (WorldDimension)dimensions.a(entityPlayer.cG().getTypeKey());

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityPlayer.getBukkitEntity().getEntityId());
        ClientboundPlayerInfoUpdatePacket add = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.a, entityPlayer);
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(((WorldDimension)worlddimension).a().e().get(), entityPlayer.P(), seed, entityPlayer.d.b(), entityPlayer.d.c(), false, false, (byte) 0, Optional.of(GlobalPos.a(entityPlayer.H.ab(), entityPlayer.dg())));

        entityPlayer.b.a(destroy);
        entityPlayer.b.a(add);
        entityPlayer.b.a(respawn);

        player.teleport(location);
        player.updateInventory();

        player.setPlayerListName(this.getInfo(player).getName());
        player.setDisplayName(this.getInfo(player).getName());

        (new BukkitRunnable() {
            public void run() {
                Bukkit.getOnlinePlayers().forEach((all) -> {
                    all.hidePlayer(player);
                    all.showPlayer(player);
                });
            }
        }).runTask(plugin);
    }

    @Override
    public void refreshAsEntity(@NotNull final Player refreshed, final boolean remove, final Player... targets) {
        if (!isDisguised(refreshed) || targets.length == 0 || !getInfo(refreshed).hasEntity()) {
            return;
        }
        final ServerPlayer rfep = ((ServerPlayer) refreshed);
        final org.bukkit.entity.EntityType type = getInfo(refreshed).getEntityType();
        final ClientboundAddEntityPacket spawn;
        try {
            final Entity entity = (Entity) DisguiseUtil.createEntity(type, rfep.getLevel());
            spawn = new ClientboundAddEntityPacket(entity);
            id.set(spawn, refreshed.getEntityId());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        final ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(refreshed.getEntityId());
        final ClientboundTeleportEntityPacket tp = new ClientboundTeleportEntityPacket(rfep);
        final ClientboundUpdateAttributesPacket attributes = new ClientboundUpdateAttributesPacket(refreshed.getEntityId(), rfep.getAttributes().getDirtyAttributes());
        for (final Player player : targets) {
            if (player == refreshed) continue;
            final ServerPlayer ep = ((ServerPlayer) player);
            if (remove) {
                ep.connection.send(destroy);
            }
            ep.connection.send(spawn);
            ep.connection.send(tp);
            ep.connection.send(attributes);
        }
    }

}
