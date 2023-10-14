package dev.lbuddyboy.samurai.server.threads;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.flash.util.StringUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.server.deathban.DeathbanListener;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.commands.TeamCommands;
import dev.lbuddyboy.samurai.team.logs.TeamLog;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import net.coreprotect.CoreProtectAPI;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EcoFixTask extends BukkitRunnable {

    private final List<UUID> alts;

    private List<UUID> used = new ArrayList<>();
    private List<String> usedNames = new ArrayList<>();
    private long lastCreated = -1;
    private long randomDelay = -1;
    private int randomTeamSize = 1;
    private static List<String> RANDOM_NAMES = Arrays.asList(
            "TRT",
            "Firs",
            "pdlw3",
            "burgerboy",
            "buddybozo",
            "sinclair",
            "nineteen",
            "hcf",
            "femboys",
            "factions",
            "joseph",
            "noobs",
            "green",
            "flash",
            "gangnem",
            "mandem",
            "wagone",
            "ohana",
            "friend",
            "lolitselx",
            "frankly",
            "pros",
            "fast",
            "fatmous",
            "fatass",
            "fatties",
            "friendly",
            "lewl",
            "avatar",
            "lame",
            "lambo",
            "Bugger",
            "flamboiant",
            "roads",
            "freaks",
            "freakhoes",
            "goodbros",
            "fold",
            "randoms",
            "staff",
            "Trenything"
    );

    @Override
    public void run() {
        if (lastCreated + randomDelay > System.currentTimeMillis()) return;
        if (usedNames.size() >= RANDOM_NAMES.size()) return;

        int i = -1;
        for (UUID alt : alts) {
            i++;
            if (!Flash.getInstance().getSpoofHandler().getSpoofPlayers().containsKey(FrozenUUIDCache.name(alt))) continue;
            if (used.contains(alt)) continue;

            String randomName = chooseRandom();
            if (randomName == null) return;

            Team createdTeam = new Team(randomName);

            createdTeam.setUniqueId(new ObjectId());
            createdTeam.setOwner(alt);
            createdTeam.setName(randomName);

            if (randomTeamSize > 1) {
                for (int j = i; j < i + randomTeamSize; j++) {
                    UUID uuid = alts.get(j);
                    if (uuid == null) continue;

                    createdTeam.addMember(uuid);
                    Samurai.getInstance().getTeamHandler().setTeam(uuid, createdTeam);
                    used.add(uuid);
                }
            }

            generateRandomLogs(createdTeam);
            createdTeam.setBalance(1000);
            createdTeam.setDTR(createdTeam.getMaxDTR());
            Samurai.getInstance().getTeamHandler().setupTeam(createdTeam);
            used.add(alt);
            usedNames.add(randomName);
            System.out.println("Created a spoof faction with the name " + randomName + " for " + UUIDUtils.name(alt) + " with the team size of " + randomTeamSize + " (" + StringUtils.join(createdTeam.getMembers().stream().map(UUIDUtils::name).collect(Collectors.toList()), ", ") + ")");
            Samurai.getInstance().getPlaytimeMap().setPlaytime(alt, ThreadLocalRandom.current().nextLong(JavaUtils.parse("5m"), JavaUtils.parse("45m")));
            break;
        }

        randomDelay = ThreadLocalRandom.current().nextLong(4000, 10000);
        lastCreated = System.currentTimeMillis();
        randomTeamSize = ThreadLocalRandom.current().nextInt(1, 4);
    }

    public String chooseRandom() {
        if (usedNames.size() >= RANDOM_NAMES.size()) return null;

        String name = RANDOM_NAMES.get(ThreadLocalRandom.current().nextInt(0, RANDOM_NAMES.size()));
        if (usedNames.contains(name)) return chooseRandom();
        return name;
    }

    public void generateRandomLogs(Team team) {

        for (int i = 0; i < ThreadLocalRandom.current().nextInt(7, 15); i++) {
            int random = ThreadLocalRandom.current().nextInt(100);
            if (random <= 10) getRandomDeath(team);
            else if (random <= 20) getRandomInvite(team);
            else if (random <= 30) getRandomLeave(team);
            else if (random <= 40) getRandomDepo(team);
            else if (random <= 50) getRandomWithdraw(team);
            else if (random <= 60) getRandomKick(team);
        }

        team.createLog(team.getOwner(), "CREATED", "/t create " + team, System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));
    }

    public void getRandomDeath(Team team) {
        List<UUID> members = team.getMembers().stream().toList();
        UUID random = null;

        if (members.size() == 1) random = members.get(0);
        else random = members.get(ThreadLocalRandom.current().nextInt(0, members.size()));

        DeathbanListener.insertSpoofedDeath(random);
        team.setDTRCooldown(System.currentTimeMillis() + Samurai.getInstance().getMapHandler().getRegenTimeDeath());
        team.createLog(random, "DEATH (" + team.getDTR() + " -> " + (team.getDTR() - 1) + ")", UUIDUtils.name(random) + " died to fall damage", System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));
        Samurai.getInstance().getMapHandler().getStatsHandler().getStats(random).addDeath();
    }

    public void getRandomDepo(Team team) {
        List<UUID> members = team.getMembers().stream().toList();
        UUID random = null;
        int amount = ThreadLocalRandom.current().nextInt(1000, 10000);

        if (members.size() == 1) random = members.get(0);
        else random = members.get(ThreadLocalRandom.current().nextInt(0, members.size()));

        team.setBalance(team.getBalance() + amount);
        team.createLog(random, "DEPOSITED", "/t deposit " + amount, System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));
    }

    public void getRandomWithdraw(Team team) {
        List<UUID> members = team.getMembers().stream().toList();
        UUID random = null;
        int amount = ThreadLocalRandom.current().nextInt(1, (int) team.getBalance());

        if (members.size() == 1) random = members.get(0);
        else random = members.get(ThreadLocalRandom.current().nextInt(0, members.size()));

        team.setBalance(team.getBalance() - amount);
        team.createLog(random, "WITHDRAW", "/t withdraw " + amount, System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));
    }

    public void getRandomInvite(Team team) {
        List<UUID> members = team.getMembers().stream().toList();

        if (members.size() == 1) {
            getRandomDeath(team);
            return;
        }

        for (UUID member : members) {
            if (member == team.getOwner()) continue;

            boolean good = false;
            for (TeamLog log : team.getLogs()) {
                if (log.getAction().equalsIgnoreCase("INVITE") && log.getAction().contains(UUIDUtils.name(member))) {
                    good = true;
                }
            }

            if (good) continue;

            team.createLog(team.getOwner(), "INVITE", "/t invite " + UUIDUtils.name(member), System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));
            break;
        }
    }

    public void getRandomLeave(Team team) {
        List<UUID> members = team.getMembers().stream().toList();

        if (members.size() == 1) {
            getRandomDeath(team);
            return;
        }

        for (UUID member : members) {
            if (member == team.getOwner()) continue;

            team.createLog(team.getOwner(), "LEFT", "/t leave", System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));

            team.removeMember(member);
            Samurai.getInstance().getTeamHandler().setTeam(member, null);
            team.flagForSave();
            break;
        }
    }

    public void getRandomKick(Team team) {
        List<UUID> members = team.getMembers().stream().toList();

        if (members.size() == 1) {
            getRandomDeath(team);
            return;
        }

        for (UUID member : members) {
            if (member == team.getOwner()) continue;

            team.createLog(team.getOwner(), "KICK", "/t kick " + UUIDUtils.name(member), System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(60 * 1000, 600 * 1000));

            team.removeMember(member);
            Samurai.getInstance().getTeamHandler().setTeam(member, null);
            team.flagForSave();
            break;
        }
    }

}
