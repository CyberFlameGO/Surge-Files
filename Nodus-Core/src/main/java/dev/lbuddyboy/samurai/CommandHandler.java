package dev.lbuddyboy.samurai;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.samurai.chat.chatgames.command.ChatGameCommand;
import dev.lbuddyboy.samurai.commands.*;
import dev.lbuddyboy.samurai.commands.donator.*;
import dev.lbuddyboy.samurai.commands.staff.*;
import dev.lbuddyboy.samurai.commands.staff.donator.DonatorTimerCommand;
import dev.lbuddyboy.samurai.commands.tutorial.TutorialCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.AbilityItemHandler;
import dev.lbuddyboy.samurai.custom.ability.command.PartnerPackageCommand;
import dev.lbuddyboy.samurai.custom.ability.command.param.OffHandType;
import dev.lbuddyboy.samurai.custom.ability.command.param.PartnerPackageType;
import dev.lbuddyboy.samurai.custom.ability.offhand.OffHand;
import dev.lbuddyboy.samurai.custom.airdrop.command.AirDropCommand;
import dev.lbuddyboy.samurai.custom.battlepass.command.BattlePassCommand;
import dev.lbuddyboy.samurai.custom.deepdark.command.DeepDarkCommand;
import dev.lbuddyboy.samurai.custom.feature.command.FeaturesCommand;
import dev.lbuddyboy.samurai.custom.power.command.PowerCommand;
import dev.lbuddyboy.samurai.custom.reclaim.command.ReclaimCommand;
import dev.lbuddyboy.samurai.custom.redeem.command.PartnerAddCommand;
import dev.lbuddyboy.samurai.custom.redeem.command.RedeemCommand;
import dev.lbuddyboy.samurai.custom.redeem.object.Partner;
import dev.lbuddyboy.samurai.custom.schedule.ScheduledTime;
import dev.lbuddyboy.samurai.custom.schedule.command.ScheduleCommand;
import dev.lbuddyboy.samurai.custom.schedule.command.completions.ScheduleCompletion;
import dev.lbuddyboy.samurai.custom.schedule.command.contexts.ScheduleTimeContext;
import dev.lbuddyboy.samurai.custom.shop.command.SellShopCommand;
import dev.lbuddyboy.samurai.custom.shop.command.ShopCommand;
import dev.lbuddyboy.samurai.custom.supplydrops.command.SupplyCrateCommand;
import dev.lbuddyboy.samurai.custom.vaults.command.VaultCommand;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.citadel.commands.CitadelCommand;
import dev.lbuddyboy.samurai.events.conquest.commands.conquest.ConquestCommand;
import dev.lbuddyboy.samurai.events.conquest.commands.conquestadmin.ConquestAdminCommand;
import dev.lbuddyboy.samurai.events.dtc.commands.DTCCreateCommand;
import dev.lbuddyboy.samurai.events.koth.commands.koth.KOTHCommand;
import dev.lbuddyboy.samurai.events.koth.commands.kothschedule.KOTHScheduleCommand;
import dev.lbuddyboy.samurai.events.region.loothill.command.LootHillCommand;
import dev.lbuddyboy.samurai.events.region.cavern.commands.CavernCommand;
import dev.lbuddyboy.samurai.events.region.glowmtn.commands.GlowCommand;
import dev.lbuddyboy.samurai.listener.DomainListener;
import dev.lbuddyboy.samurai.map.bounty.BountyCommand;
import dev.lbuddyboy.samurai.map.crates.commands.CrateCommand;
import dev.lbuddyboy.samurai.map.duel.arena.ArenaCommand;
import dev.lbuddyboy.samurai.map.duel.arena.DuelArena;
import dev.lbuddyboy.samurai.map.duel.command.AcceptCommand;
import dev.lbuddyboy.samurai.map.duel.command.DuelCommand;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.map.game.arena.ArenaCommands;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.map.game.command.*;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.map.kits.command.KitAdminCommand;
import dev.lbuddyboy.samurai.map.offline.command.DeathsClaimCommand;
import dev.lbuddyboy.samurai.map.shards.command.ShardsCommand;
import dev.lbuddyboy.samurai.map.stats.command.KillstreaksCommand;
import dev.lbuddyboy.samurai.map.stats.command.LeaderboardCommand;
import dev.lbuddyboy.samurai.map.stats.command.StatManagerCommand;
import dev.lbuddyboy.samurai.map.stats.command.StatsCommand;
import dev.lbuddyboy.samurai.server.commands.LootTablesCommand;
import dev.lbuddyboy.samurai.server.pearl.command.ClearTimerCommand;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.server.timer.ServerTimer;
import dev.lbuddyboy.samurai.server.timer.command.completions.PlayerTimerCompletion;
import dev.lbuddyboy.samurai.server.timer.command.completions.ServerTimerCompletion;
import dev.lbuddyboy.samurai.server.timer.command.contexts.PlayerTimerContext;
import dev.lbuddyboy.samurai.server.timer.command.contexts.ServerTimerContext;
import dev.lbuddyboy.samurai.settings.commands.SettingsCommand;
import dev.lbuddyboy.samurai.team.ACFTeamType;
import dev.lbuddyboy.samurai.team.ACFUUIDType;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.boosters.TeamBoosterType;
import dev.lbuddyboy.samurai.team.claims.Subclaim;
import dev.lbuddyboy.samurai.team.claims.SubclaimProvider;
import dev.lbuddyboy.samurai.team.commands.*;
import dev.lbuddyboy.samurai.team.commands.pvp.PvPCommand;
import dev.lbuddyboy.samurai.team.commands.staff.*;
import dev.lbuddyboy.samurai.team.commands.staff.chatspy.TeamChatSpyCommand;
import dev.lbuddyboy.samurai.team.dtr.ACFDTRBitmaskType;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandHandler {

    public CommandHandler(PaperCommandManager paperCommandManager) {
        paperCommandManager.getCommandCompletions().registerCompletion("boosters", new TeamBoosterType.Completion());
        paperCommandManager.getCommandContexts().registerContext(TeamBoosterType.class, new TeamBoosterType.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("kits", new DefaultKit.Completion());
        paperCommandManager.getCommandContexts().registerContext(DefaultKit.class, new DefaultKit.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("kits", new DefaultKit.Completion());
        paperCommandManager.getCommandContexts().registerContext(DefaultKit.class, new DefaultKit.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("duelArenas", new DuelArena.Completion());
        paperCommandManager.getCommandContexts().registerContext(DuelArena.class, new DuelArena.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("gameArenas", new GameArena.Completion());
        paperCommandManager.getCommandContexts().registerContext(GameArena.class, new GameArena.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("gameTypes", new GameType.Completion());
        paperCommandManager.getCommandContexts().registerContext(GameType.class, new GameType.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("scheduledTimes", new ScheduleCompletion());
        paperCommandManager.getCommandContexts().registerContext(ScheduledTime.class, new ScheduleTimeContext());

        paperCommandManager.getCommandCompletions().registerCompletion("objectives", new LeaderboardCommand.StatsObjective.Completion());
        paperCommandManager.getCommandContexts().registerContext(LeaderboardCommand.StatsObjective.class, new LeaderboardCommand.StatsObjective.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("partners", new Partner.Completion());
        paperCommandManager.getCommandCompletions().registerCompletion("events", new Event.Completion());
        paperCommandManager.getCommandContexts().registerContext(Event.class, new Event.Type());

        paperCommandManager.getCommandCompletions().registerCompletion("playertimers", new PlayerTimerCompletion());
        paperCommandManager.getCommandCompletions().registerCompletion("servertimers", new ServerTimerCompletion());
        paperCommandManager.getCommandCompletions().registerCompletion("economyTypes", context -> Arrays.asList("shards", "money"));

        paperCommandManager.getCommandContexts().registerContext(ServerTimer.class, new ServerTimerContext());
        paperCommandManager.getCommandContexts().registerContext(PlayerTimer.class, new PlayerTimerContext());

        Samurai.getInstance().getPaperCommandManager().getCommandContexts().registerContext(AbilityItem.class, new PartnerPackageType());
        Samurai.getInstance().getPaperCommandManager().getCommandCompletions().registerCompletion("abilityitems", (c) -> {
            AbilityItemHandler handler = Samurai.getInstance().getAbilityItemHandler();
            return handler.getAbilityItems()
                    .stream()
                    .map(AbilityItem::getName)
                    .map(str -> str.replace("'", "").replace(" ", "_"))
                    .map(String::toLowerCase)
                    .map(org.bukkit.ChatColor::stripColor)
                    .filter(name -> StringUtils.startsWithIgnoreCase(name, c.getInput()))
                    .collect(Collectors.toList());
        });

        Samurai.getInstance().getPaperCommandManager().getCommandContexts().registerContext(OffHand.class, new OffHandType());
        Samurai.getInstance().getPaperCommandManager().getCommandCompletions().registerCompletion("offhanditems", (c) -> {
            AbilityItemHandler handler = Samurai.getInstance().getAbilityItemHandler();
            return handler.getOffHandItems()
                    .stream()
                    .map(AbilityItem::getName)
                    .map(str -> str.replace("'", "").replace(" ", "_"))
                    .map(String::toLowerCase)
                    .map(org.bukkit.ChatColor::stripColor)
                    .filter(name -> StringUtils.startsWithIgnoreCase(name, c.getInput()))
                    .collect(Collectors.toList());
        });

        paperCommandManager.getCommandCompletions().registerCompletion("uuid", c -> {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasMetadata("invisible") || player.hasMetadata("modmode")) continue;
                players.add(player.getName());
            }

            for (SpoofHandler.SpoofPlayer player : Flash.getInstance().getSpoofHandler().getSpoofPlayers().values()) {
                players.add(player.getName());
            }

            if (players.isEmpty()) {
                players.add("No ones online :(");
            }

            return players;
        });

        paperCommandManager.getCommandContexts().registerContext(Team.class, new ACFTeamType());
        paperCommandManager.getCommandContexts().registerContext(DTRBitmask.class, new ACFDTRBitmaskType());
        paperCommandManager.getCommandContexts().registerContext(Subclaim.class, new SubclaimProvider());
        paperCommandManager.getCommandContexts().registerContext(UUID.class, new ACFUUIDType());

        paperCommandManager.getCommandCompletions().registerCompletion("bitmasks", c -> {
            List<String> bitmasks = new ArrayList<>();

            for (DTRBitmask bitmask : DTRBitmask.values()) {
                if (StringUtils.startsWithIgnoreCase(bitmask.name(), c.getInput())) {
                    bitmasks.add(bitmask.getName());
                }
            }

            return bitmasks;
        });

        paperCommandManager.getCommandCompletions().registerCompletion("team", c -> {
            List<String> teams = new ArrayList<>();
            for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
                if (team.hasDTRBitmask(DTRBitmask.ROAD))
                    continue;
                teams.add(team.getName());
            }

            if (teams.isEmpty()) {
                teams.add("No teams available");
            }

            return teams;
        });

        paperCommandManager.registerCommand(new RTPCommand());
        paperCommandManager.registerCommand(new DonatorTimerCommand());
        paperCommandManager.registerCommand(new CopyToCommand());
        paperCommandManager.registerCommand(new CopyFromCommand());
        paperCommandManager.registerCommand(new DeepDarkCommand());
        paperCommandManager.registerCommand(new SandBiomeCommand());
        paperCommandManager.registerCommand(new LastOnlineCommand());
        paperCommandManager.registerCommand(new DeathClaimRemoveCommand());
        paperCommandManager.registerCommand(new DeathClaimAddCommand());
        paperCommandManager.registerCommand(new DeathsClaimCommand());
        paperCommandManager.registerCommand(new FresCommand());
        paperCommandManager.registerCommand(new SellShopCommand());
        paperCommandManager.registerCommand(new DeathRefundCommand());
        paperCommandManager.registerCommand(new EndInfoCommand());
        paperCommandManager.registerCommand(new KOTHScheduleCommand());
        paperCommandManager.registerCommand(new KOTHRewardKeyCommand());
        paperCommandManager.registerCommand(new SettingsCommand());
        paperCommandManager.registerCommand(new ShardsCommand());
        paperCommandManager.registerCommand(new RebootCommand());
        paperCommandManager.registerCommand(new SetHQCommand());
        paperCommandManager.registerCommand(new BattlePassCommand());
        paperCommandManager.registerCommand(new ClearTimerCommand());
        paperCommandManager.registerCommand(new ScheduleCommand());
        paperCommandManager.registerCommand(new SupplyCrateCommand());
        paperCommandManager.registerCommand(new CondenseCommand());
        paperCommandManager.registerCommand(new CraftCommand());
        paperCommandManager.registerCommand(new FixAllCommand());
        paperCommandManager.registerCommand(new FixCommand());
        paperCommandManager.registerCommand(new InvisibilityCommand());
        paperCommandManager.registerCommand(new LiveStreamCommand());
        paperCommandManager.registerCommand(new TutorialCommand());
        paperCommandManager.registerCommand(new EditConfigurationCommand());
        paperCommandManager.registerCommand(new SpeedCommand());
        paperCommandManager.registerCommand(new ArenaCommand());
        paperCommandManager.registerCommand(new ChatGameCommand());
        paperCommandManager.registerCommand(new BalanceCommand());
        paperCommandManager.registerCommand(new BottleCommand());
        paperCommandManager.registerCommand(new BuildCommand());
        paperCommandManager.registerCommand(new CrowbarCommand());
        paperCommandManager.registerCommand(new FDToggleCommand());
        paperCommandManager.registerCommand(new GoppleCommand());
        paperCommandManager.registerCommand(new HelpCommand());
        paperCommandManager.registerCommand(new KitCommand());
        paperCommandManager.registerCommand(new LFFCommand());
        paperCommandManager.registerCommand(new LivesCommand());
        paperCommandManager.registerCommand(new LocationCommand());
        paperCommandManager.registerCommand(new LogoutCommand());
        paperCommandManager.registerCommand(new OresCommand());
        paperCommandManager.registerCommand(new PayCommand());
        paperCommandManager.registerCommand(new PlaytimeCommand());
        paperCommandManager.registerCommand(new RegenCommand());
        paperCommandManager.registerCommand(new ReviveCommand());
        paperCommandManager.registerCommand(new TellLocationCommand());
        paperCommandManager.registerCommand(new ToggleChatCommand());
        paperCommandManager.registerCommand(new ToggleClaimMessagesCommand());
        paperCommandManager.registerCommand(new ToggleClaimsCommand());
        paperCommandManager.registerCommand(new ToggleCobbleCommand());
        paperCommandManager.registerCommand(new ToggleDeathMessagesCommand());
        paperCommandManager.registerCommand(new AddBalCommand());
        paperCommandManager.registerCommand(new BitmaskCommand());
        paperCommandManager.registerCommand(new ClearItemsCommand());
        paperCommandManager.registerCommand(new DeathsCommand());
        paperCommandManager.registerCommand(new EOTWCommand());
        paperCommandManager.registerCommand(new FixLoadingBugCommand());
        paperCommandManager.registerCommand(new SamuraiCommand());
        paperCommandManager.registerCommand(new GoppleResetCommand());
        paperCommandManager.registerCommand(new KillsCommand());
        paperCommandManager.registerCommand(new OnlineStaffCommand());
        paperCommandManager.registerCommand(new RecachePlayerTeamsCommand());
        paperCommandManager.registerCommand(new SOTWCommand());
        paperCommandManager.registerCommand(new SetBalCommand());
        paperCommandManager.registerCommand(new SpawnCommand());
        paperCommandManager.registerCommand(new TagMeCommand());
        paperCommandManager.registerCommand(new TeamManageCommand());
        paperCommandManager.registerCommand(new WipeDeathbansCommand());
        paperCommandManager.registerCommand(new PartnerPackageCommand());
        paperCommandManager.registerCommand(new AirDropCommand());
        paperCommandManager.registerCommand(new FeaturesCommand());
        paperCommandManager.registerCommand(new PowerCommand());
        paperCommandManager.registerCommand(new ReclaimCommand());
        paperCommandManager.registerCommand(new PartnerAddCommand());
        paperCommandManager.registerCommand(new RedeemCommand());
        paperCommandManager.registerCommand(new ShopCommand());
        paperCommandManager.registerCommand(new VaultCommand());
        paperCommandManager.registerCommand(new CitadelCommand());
        paperCommandManager.registerCommand(new ConquestCommand());
        paperCommandManager.registerCommand(new ConquestAdminCommand());
        paperCommandManager.registerCommand(new DTCCreateCommand());
        paperCommandManager.registerCommand(new KOTHCommand());
        paperCommandManager.registerCommand(new LootHillCommand());
        paperCommandManager.registerCommand(new CavernCommand());
        paperCommandManager.registerCommand(new GlowCommand());
        paperCommandManager.registerCommand(new BountyCommand());
        paperCommandManager.registerCommand(new CrateCommand());
        paperCommandManager.registerCommand(new AcceptCommand());
        paperCommandManager.registerCommand(new DuelCommand());
        paperCommandManager.registerCommand(new ArenaCommands());
        paperCommandManager.registerCommand(new GameCommand());
        paperCommandManager.registerCommand(new HostCommand());
        paperCommandManager.registerCommand(new JoinCommand());
        paperCommandManager.registerCommand(new LeaveCommand());
        paperCommandManager.registerCommand(new SpectateCommand());
        paperCommandManager.registerCommand(new KitAdminCommand());
//        paperCommandManager.registerCommand(new OfflineInvseeCommand());
        paperCommandManager.registerCommand(new DomainListener());
        paperCommandManager.registerCommand(new KillstreaksCommand());
        paperCommandManager.registerCommand(new LeaderboardCommand());
        paperCommandManager.registerCommand(new StatManagerCommand());
        paperCommandManager.registerCommand(new StatsCommand());
        paperCommandManager.registerCommand(new LootTablesCommand());
        paperCommandManager.registerCommand(new ClearItemsCommand());
        paperCommandManager.registerCommand(new ForceDisbandAllCommand());
        paperCommandManager.registerCommand(new ForceDisbandCommand());
        paperCommandManager.registerCommand(new ForceJoinCommand());
        paperCommandManager.registerCommand(new ForceKickCommand());
        paperCommandManager.registerCommand(new ForceLeaderCommand());
        paperCommandManager.registerCommand(new ForceLeaveCommand());
        paperCommandManager.registerCommand(new ForceSetHQCommand());
        paperCommandManager.registerCommand(new FreezeRostersCommand());
        paperCommandManager.registerCommand(new HQCommand());
        paperCommandManager.registerCommand(new ImportTeamDataCommand());
        paperCommandManager.registerCommand(new PowerFactionCommand());
        paperCommandManager.registerCommand(new ResetForceInvitesCommand());
        paperCommandManager.registerCommand(new SetTeamBalanceCommand());
        paperCommandManager.registerCommand(new StartDTRRegenCommand());
        paperCommandManager.registerCommand(new TeamDataCommands());
        paperCommandManager.registerCommand(new PvPCommand());
        paperCommandManager.registerCommand(new TeamChatCommand());
        paperCommandManager.registerCommand(new TeamChatOfficerCommand());
        paperCommandManager.registerCommand(new TeamChatPublicCommand());
        paperCommandManager.registerCommand(new TeamCommands());
        paperCommandManager.registerCommand(new TeamChatSpyCommand());

    }

}
