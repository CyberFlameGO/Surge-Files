package dev.aurapvp.samurai.battlepass.challenge;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.battlepass.challenge.category.ChallengeCategory;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class ChallengeListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        for (Challenge challenge : Samurai.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.KILL_ENTITY) {
                if (!event.getEntity().getType().name().equals(challenge.getValue().toString().toUpperCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player killer = event.getEntity().getKiller();
                if (killer == null) return;
                BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(killer.getUniqueId());
                if (pass.isComplete(challenge)) continue;

                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    killer.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        for (Challenge challenge : Samurai.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.MINE_BLOCK) {
                if (!event.getBlock().getType().name().equals(challenge.getValue().toString().toUpperCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        for (Challenge challenge : Samurai.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.PLACE_BLOCK) {
                if (!event.getBlock().getType().name().equals(challenge.getValue().toString().toUpperCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }

/*    @EventHandler
    public void onPetUse(PetActivateEvent event) {
        for (Challenge challenge : Samurai.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.PLACE_BLOCK) {
                if (!event.getPet().getName().equalsIgnoreCase(challenge.getValue().toString())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onCoinFlip(CoinFlipStartEvent event) {
        for (Challenge challenge : Samurai.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.PLACE_COIN_FLIPS) {
                ChallengeCategory category = challenge.getCategory();
                UUID sender = event.getSender();
                BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(sender);
                Player player = Bukkit.getPlayer(sender);

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    if (player != null) {
                        player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } else {
                        Samurai.getInstance().getBattlePassHandler().saveBattlePass(sender, true);
                        Samurai.getInstance().getBattlePassHandler().getBattlePasses().remove(sender);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCoinFlipEnd(CoinFlipEndEvent event) {
        for (Challenge challenge : Samurai.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.WIN_COINFLIPS) {
                ChallengeCategory category = challenge.getCategory();
                UUID winner = event.getWinner() == event.getSender() ? event.getSender() : event.getChallenger();
                BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(winner);
                Player player = Bukkit.getPlayer(winner);

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    if (player != null) {
                        player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } else {
                        Samurai.getInstance().getBattlePassHandler().saveBattlePass(winner, true);
                        Samurai.getInstance().getBattlePassHandler().getBattlePasses().remove(winner);
                    }
                }
            }
        }
    }*/

}
