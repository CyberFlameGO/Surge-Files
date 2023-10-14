package dev.lbuddyboy.samurai.custom.teamwar.model;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import lombok.Data;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class WarTeam {

    private final ObjectId teamUUID;
    private final Map<UUID, WarPlayer> members;
    private boolean alive;
    private int kills, wins;

    public WarTeam(ObjectId teamUUID) {
        this.teamUUID = teamUUID;
        this.members = new HashMap<>();

        Team team = getTeam();
        for (UUID member : team.getMembers()) {
            members.put(member, new WarPlayer(member, WarKit.DIAMOND, true));
        }
    }

    public List<Map.Entry<UUID, WarPlayer>> getAliveMembers() {
        return this.members.entrySet().stream().filter(entry -> entry.getValue().isAlive()).collect(Collectors.toList());
    }

    public List<Map.Entry<UUID, WarPlayer>> getDeadMembers() {
        return this.members.entrySet().stream().filter(entry -> !entry.getValue().isAlive()).collect(Collectors.toList());
    }

    public Team getTeam() {
        return Samurai.getInstance().getTeamHandler().getTeam(this.teamUUID);
    }

    public int getMemberSize() {
        return this.members.size();
    }

    public void death(Player player, Player killer) {
        if (killer != null) {
            WarTeam killerTeam = Samurai.getInstance().getTeamWarHandler().getWarTeam(killer.getUniqueId());

            if (killerTeam != null) {
                killerTeam.setKills(killerTeam.getKills() + 1);
            }
        }

        Samurai.getInstance().getTeamWarHandler().handleSpectate(player);
        getMembers().get(player.getUniqueId()).setAlive(false);
        checkStatus();
    }

    public void checkStatus() {
        if (!this.getAliveMembers().isEmpty()) return;

        this.alive = false;
        Samurai.getInstance().getTeamWarHandler().handleNext(this);
    }

}
