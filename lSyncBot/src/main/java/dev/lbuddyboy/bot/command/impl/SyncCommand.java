package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.packet.impl.SyncCodeUpdatePacket;
import dev.lbuddyboy.bot.packet.impl.SyncInformationUpdatePacket;
import dev.lbuddyboy.bot.sync.SyncCode;
import dev.lbuddyboy.bot.sync.cache.SyncInformation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class SyncCommand extends Command {

    public SyncCommand() {
        super("sync",
                Collections.emptyList(),
                Collections.singletonList(
                        new OptionData(OptionType.INTEGER, "code", "4 digit code provided ingame by doing /sync", true, false)
                ), DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND), "Sync your discord account with your minecraft account", "");
    }

    @Override
    public void send(SlashCommandInteractionEvent event) {
        if (event.getName().equals("sync")) {
            Member member = event.getMember();

            int code = event.getOption("code").getAsInt();
            SyncCode syncCode = Bot.getInstance().getSyncHandler().getSyncCodeByCode(code);

            if (Bot.getInstance().getSyncHandler().isSynced(event.getMember().getIdLong())) {
                SyncInformation information = Bot.getInstance().getSyncHandler().getSyncInformation(event.getMember().getIdLong());

                event.reply("Your account is already synced to " + information.getPlayerName() + ".").setEphemeral(true).queue();
                return;
            }

            if (syncCode == null) {
                event.reply("That sync code does not exist.").setEphemeral(true).queue();
                return;
            }

            Map<Long, String> ranks = new HashMap<>();
            Map<Long, String> conversion = Bot.getInstance().getSyncHandler().getRankConversion();

            Bot.getInstance().getSyncHandler().getRankConversion().forEach((key, value) -> {
                for (Role role : member.getRoles()) {
                    if (!conversion.containsKey(role.getIdLong())) continue;

                    if (role.getIdLong() == key) {
                        ranks.put(role.getIdLong(), conversion.get(role.getIdLong()));
                    }
                }
            });

            SyncInformation information = new SyncInformation(
                    syncCode.getPlayerUUID(),
                    syncCode.getPlayerName(),
                    member.getIdLong(),
                    new HashMap<>(),
                    ranks
            );

            new SyncInformationUpdatePacket(information, false, member.getRoles().stream().map(Role::getId).collect(Collectors.toList())).send(Bot.getInstance().getRedisHandler().getPidginHandler());
            Bot.getInstance().getSyncHandler().getSyncCodes().remove(syncCode);
            new SyncCodeUpdatePacket(Bot.getInstance().getSyncHandler().getSyncCodes()).send(Bot.getInstance().getRedisHandler().getPidginHandler());
            event.reply("Your account is now synced to " + information.getPlayerName() + ".").setEphemeral(true).queue();
        }
    }

}
