package dev.lbuddyboy.pcore.mines.thread;

import dev.drawethree.xprison.utils.misc.ProgressBar;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.ActionBarAPI;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PrivateMineThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                tick();
                sleep(300L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        for (MineUser user : pCore.getInstance().getMineUserHandler().getUsers().values()) {
            Player player = Bukkit.getPlayer(user.getUuid());
            if (player == null) continue;
            if (user.getPrivateMine() == null) continue;
            if (pCore.getInstance().getPrivateMineHandler().getGrid().getWorld() != player.getWorld()) continue;

            ActionBarAPI.sendActionBar(player, CC.translate("&5Rankup&7: &8[" + ProgressBar.getProgressBar(
                    15,
                    "|",
                    user.getPrivateMine().getRankInfo().getProgress(),
                    user.getPrivateMine().getRankInfo().getNeededProgress()
            ) + "&8] &8(&a" + (PROGRESS_FORMAT.format((user.getPrivateMine().getRankInfo().getProgress() / user.getPrivateMine().getRankInfo().getNeededProgress()) * 100.0)) + "%&8)"));
        }
    }

    public static final DecimalFormat PROGRESS_FORMAT = new DecimalFormat("0.00");

}
