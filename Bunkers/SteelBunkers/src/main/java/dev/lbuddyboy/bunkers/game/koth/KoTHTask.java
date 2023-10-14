package dev.lbuddyboy.bunkers.game.koth;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.GameState;
import dev.lbuddyboy.bunkers.util.Cuboid;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.profile.Profile;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class KoTHTask extends BukkitRunnable {

    private KoTHHandler koTHHandler;

    @Override
    public void run() {
        if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) return;
        if (koTHHandler.isFinished()) {
            cancel();
            Bukkit.broadcastMessage(CC.translate("&g" + koTHHandler.getCurrentCapper().getName() + " &fhas just won the &gKing of the Hill&f. The game is now ending."));
            Bukkit.broadcastMessage(CC.translate("&g" + koTHHandler.getCurrentCapper().getName() + " &fhas just won the &gKing of the Hill&f. The game is now ending."));
            Bukkit.broadcastMessage(CC.translate("&g" + koTHHandler.getCurrentCapper().getName() + " &fhas just won the &gKing of the Hill&f. The game is now ending."));
            Bunkers.getInstance().getGameHandler().setWinner(Bunkers.getInstance().getTeamHandler().getTeam(koTHHandler.getCurrentCapper()));
            Bunkers.getInstance().getGameHandler().end(false);

            Profile profile = BunkersCom.getInstance().getProfileHandler().getByUUID(koTHHandler.getCurrentCapper().getUniqueId());
            profile.setKothCaptures(profile.getKothCaptures() + 1);
            profile.save();

            return;
        }
        if (Bunkers.getInstance().getGameHandler().canKoTHActivate()) return;
        Cuboid cuboid = Bunkers.getInstance().getGameHandler().getGameSettings().getKothCuboid();
        if (cuboid == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("spectator") || player.hasMetadata("modmode")) continue;
            if (Bunkers.getInstance().getTeamHandler().getTeam(player) == null) continue;
            if (!cuboid.contains(player)) {
                if (koTHHandler.getCurrentCapper() != null && koTHHandler.getCurrentCapper().getName().equals(player.getName())) {
                    koTHHandler.setCurrentCapper(null);
                    koTHHandler.setRemaining(koTHHandler.getDefSecs());
                }
                continue;
            }
            if (koTHHandler.getCurrentCapper() != null && koTHHandler.getCurrentCapper() != player) continue;

            koTHHandler.setRemaining(koTHHandler.getRemaining() - 1);
            koTHHandler.setCurrentCapper(player);
            break;
        }
    }

}
