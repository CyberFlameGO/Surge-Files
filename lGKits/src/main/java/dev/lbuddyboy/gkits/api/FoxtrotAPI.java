package dev.lbuddyboy.gkits.api;

import dev.lbuddyboy.gkits.API;
import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.entity.Player;

public class FoxtrotAPI extends API {

    @Override
    public boolean attemptUse(Player player, GKit kit) {
/*        if (!SpawnTagHandler.isTagged(player)) return true;

        player.sendMessage(CC.translate("&cYou can't use kits in combat."));*/

        return true;
    }

    @Override
    public boolean attemptHit(Player attacker, Player victim) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(attacker);

        if (team != null && team.isMember(victim.getUniqueId())) return false;
        if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())) return false;
        if (DTRBitmask.SAFE_ZONE.appliesAt(attacker.getLocation())) return false;

        return true;
    }
}
