package dev.aurapvp.samurai.faction.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public enum FactionRole {

    MEMBER("member", "Member", "+", 100, Arrays.asList(
            FactionPermission.LEAVE_FACTION
    ), Collections.emptyList()),
    OFFICER("officer", "Officer", "*", 500, Arrays.asList(
            FactionPermission.LEAVE_FACTION,
            FactionPermission.CLAIM_LAND,
            FactionPermission.WITHDRAW_BALANCE,
            FactionPermission.SET_HOME,
            FactionPermission.INVITE_MEMBERS
    ), Collections.singletonList(MEMBER)),
    CO_LEADER("co_leader", "Co Leader", "**", 750, Arrays.asList(
            FactionPermission.LEAVE_FACTION,
            FactionPermission.UNCLAIM_LAND,
            FactionPermission.CLAIM_LAND,
            FactionPermission.PROMOTE_MEMBERS
    ), Arrays.asList(MEMBER, OFFICER)),
    LEADER("leader", "Leader", "***", 1000, Arrays.asList(
            FactionPermission.PROMOTE_OFFICERS,
            FactionPermission.DISBAND_FACTION,
            FactionPermission.CLAIM_LAND
    ), Arrays.asList(MEMBER, OFFICER, CO_LEADER));

    private final String name, displayName, astrix;
    private final int weight;
    private final List<FactionPermission> permissions;
    private final List<FactionRole> inheritances;

    public List<FactionPermission> getAllPermissions() {
        return new ArrayList<FactionPermission>(this.permissions){{
            inheritances.forEach(r -> addAll(r.getPermissions()));
        }};
    }

}
