package dev.lbuddyboy.pcore.timer.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ScheduledTask;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.timer.menu.sub.ServerTimerTasksMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.MetadataValue;

public class ServerTimerListener implements Listener {
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!player.hasMetadata("server_timer_creation")) return;

        Tasks.run(() -> {
            MetadataValue metadata = player.getMetadata("server_timer_creation").get(0);
            if (metadata == null) return;

            if (message.equalsIgnoreCase("cancel")) {
                player.removeMetadata("server_timer_creation", pCore.getInstance());
                player.sendMessage(CC.translate("&cProcess cancelled."));
                return;
            }

            ServerTimer.ServerTimerBuilder builder = (ServerTimer.ServerTimerBuilder) metadata.value();
            builder.sentAt(System.currentTimeMillis());
            builder.context(message);
            player.sendMessage(CC.translate("&aSuccessfully created the " + builder.build().getName() + " timer!"));
            player.removeMetadata("server_timer_creation", pCore.getInstance());
            pCore.getInstance().getTimerHandler().getServerTimers().put(builder.build().getName(), new ServerTimer(
                    builder.build().getName(),
                    builder.build().getDisplayName(),
                    builder.build().getContext(),
                    builder.build().getSentAt(),
                    builder.build().getDuration()
            ));
        });

        event.setCancelled(true);
    }

    @EventHandler
    public void onTaskActionEdit(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!player.hasMetadata("server_timer_editor_action")) return;
        if (!player.hasMetadata("server_timer_editor")) return;

        Tasks.run(() -> {
            MetadataValue metadata = player.getMetadata("server_timer_editor_action").get(0);
            MetadataValue server_timer_editor = player.getMetadata("server_timer_editor").get(0);
            if (metadata == null) return;
            if (server_timer_editor == null) return;

            ScheduledTask task = (ScheduledTask) metadata.value();
            ServerTimer timer = (ServerTimer) server_timer_editor.value();

            if (message.equalsIgnoreCase("cancel")) {
                new ServerTimerTasksMenu(timer).openMenu(player);
                player.removeMetadata("server_timer_editor_action", pCore.getInstance());
                player.removeMetadata("server_timer_editor", pCore.getInstance());
                player.sendMessage(CC.translate("&cTask edit process cancelled."));
                return;
            }

            try {
                ScheduledTask.Task taskType = ScheduledTask.Task.valueOf(message.toUpperCase());

                task.setTask(taskType);
                player.removeMetadata("server_timer_editor_action", pCore.getInstance());
                player.removeMetadata("server_timer_editor", pCore.getInstance());
                player.sendMessage(CC.translate("&aSuccessfully edited the task's type to " + message.toUpperCase() + "!"));
                new ServerTimerTasksMenu(timer).openMenu(player);
            } catch (Exception ignored) {
                player.sendMessage(CC.translate("&cPlease try a different task action. (COMMAND or BROADCAST)"));
            }
        });

        event.setCancelled(true);
    }

    @EventHandler
    public void onTaskTimeEdit(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!player.hasMetadata("server_timer_editor_time")) return;
        if (!player.hasMetadata("server_timer_editor")) return;

        Tasks.run(() -> {
            MetadataValue metadata = player.getMetadata("server_timer_editor_time").get(0);
            MetadataValue server_timer_editor = player.getMetadata("server_timer_editor").get(0);
            if (metadata == null) return;
            if (server_timer_editor == null) return;

            ScheduledTask task = (ScheduledTask) metadata.value();
            ServerTimer timer = (ServerTimer) server_timer_editor.value();

            if (message.equalsIgnoreCase("cancel")) {
                new ServerTimerTasksMenu(timer).openMenu(player);
                player.removeMetadata("server_timer_editor_time", pCore.getInstance());
                player.removeMetadata("server_timer_editor", pCore.getInstance());
                player.sendMessage(CC.translate("&cTask edit process cancelled."));
                return;
            }

            try {
                task.setTimeLeft(Long.parseLong(message));
                player.removeMetadata("server_timer_editor_time", pCore.getInstance());
                player.removeMetadata("server_timer_editor", pCore.getInstance());
                player.sendMessage(CC.translate("&aSuccessfully edited the task's execute time to " + message + "s!"));
                new ServerTimerTasksMenu(timer).openMenu(player);
            } catch (Exception ignored) {
                player.sendMessage(CC.translate("&cPlease try a different time. (seconds & has to be a number)"));
            }
        });

        event.setCancelled(true);
    }

    @EventHandler
    public void onTaskValueEdit(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!player.hasMetadata("server_timer_editor_value")) return;
        if (!player.hasMetadata("server_timer_editor")) return;

        Tasks.run(() -> {
            MetadataValue metadata = player.getMetadata("server_timer_editor_value").get(0);
            MetadataValue server_timer_editor = player.getMetadata("server_timer_editor").get(0);
            if (metadata == null) return;
            if (server_timer_editor == null) return;

            ScheduledTask task = (ScheduledTask) metadata.value();
            ServerTimer timer = (ServerTimer) server_timer_editor.value();

            if (message.equalsIgnoreCase("cancel")) {
                new ServerTimerTasksMenu(timer).openMenu(player);
                player.removeMetadata("server_timer_editor_value", pCore.getInstance());
                player.removeMetadata("server_timer_editor", pCore.getInstance());
                player.sendMessage(CC.translate("&cTask edit process cancelled."));
                return;
            }

            try {
                task.setValue(message);
                player.removeMetadata("server_timer_editor_value", pCore.getInstance());
                player.removeMetadata("server_timer_editor", pCore.getInstance());
                player.sendMessage(CC.translate("&aSuccessfully edited the task's execute value to " + message + "s!"));
                new ServerTimerTasksMenu(timer).openMenu(player);
            } catch (Exception ignored) {
                player.sendMessage(CC.translate("&cPlease try a different value."));
            }
        });

        event.setCancelled(true);
    }

}
