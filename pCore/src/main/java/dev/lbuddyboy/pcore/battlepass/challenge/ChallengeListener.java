package dev.lbuddyboy.pcore.battlepass.challenge;

import dev.lbuddyboy.pcore.api.event.CoinFlipEndEvent;
import dev.lbuddyboy.pcore.api.event.CoinFlipStartEvent;
import dev.lbuddyboy.pcore.api.event.PetActivateEvent;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.BattlePass;
import dev.lbuddyboy.pcore.battlepass.challenge.category.ChallengeCategory;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class ChallengeListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.KILL_ENTITY) {
                if (!event.getEntity().getType().name().equals(challenge.getValue().toString().toUpperCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player killer = event.getEntity().getKiller();
                if (killer == null) return;
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(killer.getUniqueId());
                if (pass.isComplete(challenge)) continue;

                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    killer.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.MINE_BLOCK) {
                if (!event.getBlock().getType().name().equals(challenge.getValue().toString().toUpperCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.MINE_BLOCK) {
                if (!event.getMessage().toLowerCase().contains(challenge.getValue().toString().toLowerCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.PLACE_BLOCK) {
                if (!event.getBlock().getType().name().equals(challenge.getValue().toString().toUpperCase())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onPetUse(PetActivateEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.PLACE_BLOCK) {
                if (!event.getPet().getName().equalsIgnoreCase(challenge.getValue().toString())) continue;

                ChallengeCategory category = challenge.getCategory();
                Player player = event.getPlayer();
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onCoinFlip(CoinFlipStartEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.PLACE_COIN_FLIPS) {
                ChallengeCategory category = challenge.getCategory();
                UUID sender = event.getSender();
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(sender);
                Player player = Bukkit.getPlayer(sender);

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    if (player != null) {
                        player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    } else {
                        pCore.getInstance().getBattlePassHandler().saveBattlePass(sender, true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCoinFlipEnd(CoinFlipEndEvent event) {
        for (Challenge challenge : pCore.getInstance().getBattlePassHandler().getChallenges()) {
            if (challenge.getChallengeType() == ChallengeTypes.WIN_COINFLIPS) {
                ChallengeCategory category = challenge.getCategory();
                UUID winner = event.getWinner() == event.getSender() ? event.getSender() : event.getChallenger();
                BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(winner);
                Player player = Bukkit.getPlayer(winner);

                if (pass.isComplete(challenge)) continue;
                if (pass.progress(challenge, pass.getProgress(challenge) + 1)) {
                    pass.setExperience(pass.getExperience() + challenge.getXp());
                    if (player != null) {
                        player.sendTitle(CC.translate("&a&l" + category.getDisplayName()), CC.translate("&aYou have just completed the " + challenge.getDisplayName() + " challenge!"));
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    } else {
                        pCore.getInstance().getBattlePassHandler().saveBattlePass(winner, true);
                    }
                }
            }
        }
    }

}
