package dev.lbuddyboy.samurai.map.game;

import dev.lbuddyboy.samurai.util.InventoryUtils;
import lombok.experimental.UtilityClass;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@UtilityClass
public class GameUtils {

    public static void resetPlayer(Player player) {
        InventoryUtils.resetInventoryNow(player);

        player.setGameMode(GameMode.SURVIVAL);
        if (Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().getState() != GameState.RUNNING) {
            player.getInventory().setItem(3, GameItems.VOTE_FOR_ARENA);
            player.getInventory().setItem(5, GameItems.LEAVE_EVENT);
        } else {
            player.getInventory().setItem(4, GameItems.LEAVE_EVENT);
        }
        player.updateInventory();
    }

}
