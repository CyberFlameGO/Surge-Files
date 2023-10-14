package dev.lbuddyboy.jailcorder.listener;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.user.User;
import dev.lbuddyboy.jailcorder.JailCorder;
import dev.lbuddyboy.jailcorder.record.RecordingUser;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class RecordingListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        RecordingUser user = new RecordingUser(event.getUniqueId(), event.getName());

        JailCorder.getInstance().getRecordingUsers().put(event.getUniqueId(), user);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RecordingUser user = JailCorder.getInstance().getRecordingUsers().getOrDefault(event.getPlayer().getUniqueId(), null);
        if (user == null) return;

        user.record();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(JailCorder.getInstance(), () -> {
            User user = Flash.getInstance().getUserHandler().tryUser(player.getUniqueId(), false);
            RecordingUser recordingUser = JailCorder.getInstance().getRecordingUsers().getOrDefault(player.getUniqueId(), null);
            Block block = event.getBlock();

            if (recordingUser != null && user != null) {
                Location lastBrokenLocation = user.getLastBrokenLocation();

                if (lastBrokenLocation != null)
                    System.out.println(lastBrokenLocation.getBlockX() + ", " + lastBrokenLocation.getBlockY() + ", " + lastBrokenLocation.getBlockZ());

                if (lastBrokenLocation != null && lastBrokenLocation != block.getLocation()) {
                    recordingUser.setBlocksBroken(recordingUser.getBlocksBroken() + 1);
                    System.out.println(block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ());
                }
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RecordingUser user = JailCorder.getInstance().getRecordingUsers().get(player.getUniqueId());

        if (user == null) return;

        user.stopRecording();
        user.save(true);
        JailCorder.getInstance().getRecordingUsers().remove(player.getUniqueId());
    }

}
