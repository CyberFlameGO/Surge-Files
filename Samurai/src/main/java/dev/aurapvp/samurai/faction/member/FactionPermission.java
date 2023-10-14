package dev.aurapvp.samurai.faction.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.UUID;

@AllArgsConstructor
@Getter
public enum FactionPermission {

    LEAVE_FACTION("leave the faction"),
    WITHDRAW_BALANCE("withdraw from the faction bank"),
    DISBAND_FACTION("disband the faction"),
    UNCLAIM_LAND("unclaim land"),
    SET_HOME("set faction home"),
    CLAIM_LAND("claim land"),
    PROMOTE_OFFICERS("promote officers"),
    PROMOTE_MEMBERS("promote members"),
    INVITE_MEMBERS("invite members");

    private final String context;

    @RequiredArgsConstructor
    @Data
    public static class FactionMember {

        private final UUID uuid;
        private final String name;
        private FactionRole role = FactionRole.MEMBER;

        public Player getPlayer() {
            return Bukkit.getPlayer(this.uuid);
        }

        public OfflinePlayer getOfflinePlayer() {
            return Bukkit.getOfflinePlayer(this.uuid);
        }

        public boolean hasPermission(FactionPermission permission) {
            return this.role.getAllPermissions().contains(permission);
        }

        public boolean hasIndividualPermission(FactionPermission permission) {
            return this.role.getPermissions().contains(permission);
        }

        public String getDisplay() {
            return this.role.getAstrix() + this.name;
        }

    }

    public static class RoleComparator implements Comparator<FactionRole> {

        @Override
        public int compare(FactionRole o1, FactionRole o2) {
            return Integer.compare(o1.getWeight(), o2.getWeight());
        }
    }

    public static class FactionMemberComparator implements Comparator<FactionMember> {

        @Override
        public int compare(FactionMember o1, FactionMember o2) {
            return new RoleComparator().compare(o1.getRole(), o2.getRole());
        }
    }

}
