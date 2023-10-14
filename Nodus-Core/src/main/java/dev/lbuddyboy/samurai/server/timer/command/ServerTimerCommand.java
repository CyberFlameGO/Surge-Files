package dev.lbuddyboy.samurai.server.timer.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.server.timer.ScheduledTask;
import dev.lbuddyboy.samurai.server.timer.ServerTimer;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.team.menu.button.RollbackButton;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ConversationBuilder;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.server.timer.menu.ServerTimerMenu;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("servertimer")
@CommandPermission("samurai.command.servertimer")
public class ServerTimerCommand extends BaseCommand {

    @Subcommand("create|start")
    public void create(Player sender, @Name("name") @Single String name, @Name("duration") @Single String duration, @Name("display") String displayName) {
        ServerTimer.ServerTimerBuilder builder = ServerTimer.builder()
                .name(name)
                .duration(TimeUtils.parseTime(duration) * 1000L)
                .displayName(displayName);

        sender.sendMessage(CC.translate("&aPlease type the context this should be displayed as. Type 'cancel' to stop this process."));
        sender.sendMessage(CC.translate("&aExample&7: &f%timer% will end in %time-left%&f!"));

        Conversation conversation = new ConversationBuilder(sender, new ConversationFactory(Flash.getInstance()))
                .closeableStringPrompt("", (conversationContext, value) -> {
                    builder.sentAt(System.currentTimeMillis());
                    builder.context(value);
                    Samurai.getInstance().getTimerHandler().getServerTimers().put(builder.build().getName(), new ServerTimer(
                            builder.build().getName(),
                            builder.build().getDisplayName(),
                            builder.build().getContext(),
                            builder.build().getSentAt(),
                            builder.build().getDuration()
                    ));

                    sender.sendMessage(CC.translate("&aSuccessfully created the " + builder.build().getName() + " timer!"));

                    return Prompt.END_OF_CONVERSATION;
                })
                .echo(false).build();

        sender.beginConversation(conversation);
    }

    @Subcommand("delete|stop|end")
    @CommandCompletion("@servertimers")
    public static void delete(CommandSender sender, @Name("timer") ServerTimer timer) {
        timer.getBossBar().removeAll();
        timer.setBossBar(null);
        Samurai.getInstance().getTimerHandler().getServerTimers().remove(timer.getName());
        sender.sendMessage(CC.translate("&aSuccessfully ended the " + timer.getName() + " server timer!"));
    }

    @Subcommand("starthcfdefaults")
    @CommandCompletion("@servertimers")
    public static void starthcfdefaults(CommandSender sender) {

        ServerTimer airdropTimer = new ServerTimer("Double-Airdrops",
                FoxtrotConfiguration.DOUBLE_AIRDROPS_DISPLAY.getString(),
                FoxtrotConfiguration.DOUBLE_AIRDROPS_CONTEXT.getString(),
                System.currentTimeMillis(),
                JavaUtils.parse("1h")
        );

        ServerTimer coinsTimer = new ServerTimer("Double-Coins",
                FoxtrotConfiguration.DOUBLE_COINS_DISPLAY.getString(),
                FoxtrotConfiguration.DOUBLE_COINS_CONTEXT.getString(),
                System.currentTimeMillis(),
                JavaUtils.parse("2h")
        );

        ServerTimer keysTimer = new ServerTimer("Triple-Keys",
                FoxtrotConfiguration.TRIPLE_KEYS_DISPLAY.getString(),
                FoxtrotConfiguration.TRIPLE_KEYS_CONTEXT.getString(),
                System.currentTimeMillis(),
                JavaUtils.parse("1h30m")
        );

        airdropTimer
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &360 minutes&f.", JavaUtils.parse("60m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &355 minutes&f.", JavaUtils.parse("55m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &350 minutes&f.", JavaUtils.parse("50m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &345 minutes&f.", JavaUtils.parse("45m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &330 minutes&f.", JavaUtils.parse("30m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &315 minutes&f.", JavaUtils.parse("15m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &310 minutes&f.", JavaUtils.parse("10m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &35 minutes&f.", JavaUtils.parse("5m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &31 minute&f.", JavaUtils.parse("1m"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f will be ending in &330 seconds&f.", JavaUtils.parse("30s"))
                .addReminder(airdropTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &3Double Airdrops&f has just &c&lENDED&f.", JavaUtils.parse("2s"));

        airdropTimer
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &360 minutes&f.", JavaUtils.parse("60m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &355 minutes&f.", JavaUtils.parse("55m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &350 minutes&f.", JavaUtils.parse("50m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &345 minutes&f.", JavaUtils.parse("45m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &330 minutes&f.", JavaUtils.parse("30m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &315 minutes&f.", JavaUtils.parse("15m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &310 minutes&f.", JavaUtils.parse("10m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &35 minutes&f.", JavaUtils.parse("5m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &31 minute&f.", JavaUtils.parse("1m"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f will be ending in &330 seconds&f.", JavaUtils.parse("30s"))
                .addReminderTitle(airdropTimer.getDisplayName(), "&3Double Airdrops&f has just &c&lENDED&f.", JavaUtils.parse("2s"));

        coinsTimer
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e60 minutes&f.", JavaUtils.parse("60m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e55 minutes&f.", JavaUtils.parse("55m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e50 minutes&f.", JavaUtils.parse("50m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e45 minutes&f.", JavaUtils.parse("45m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e30 minutes&f.", JavaUtils.parse("30m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e15 minutes&f.", JavaUtils.parse("15m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e10 minutes&f.", JavaUtils.parse("10m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e5 minutes&f.", JavaUtils.parse("5m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e1 minute&f.", JavaUtils.parse("1m"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f will be ending in &e30 seconds&f.", JavaUtils.parse("30s"))
                .addReminder(coinsTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &eDouble Coins&f has just &c&lENDED&f.", JavaUtils.parse("2s"));

        coinsTimer
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e60 minutes&f.", JavaUtils.parse("2h"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e60 minutes&f.", JavaUtils.parse("1h45m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e60 minutes&f.", JavaUtils.parse("1h30m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e60 minutes&f.", JavaUtils.parse("1h15m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e60 minutes&f.", JavaUtils.parse("60m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e55 minutes&f.", JavaUtils.parse("55m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e50 minutes&f.", JavaUtils.parse("50m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e45 minutes&f.", JavaUtils.parse("45m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e30 minutes&f.", JavaUtils.parse("30m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e15 minutes&f.", JavaUtils.parse("15m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e10 minutes&f.", JavaUtils.parse("10m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e5 minutes&f.", JavaUtils.parse("5m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e1 minute&f.", JavaUtils.parse("1m"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f will be ending in &e30 seconds&f.", JavaUtils.parse("30s"))
                .addReminderTitle(coinsTimer.getDisplayName(), "&eDouble Coins&f has just &c&lENDED&f.", JavaUtils.parse("2s"));

        keysTimer
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b60 minutes&f.", JavaUtils.parse("60m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b55 minutes&f.", JavaUtils.parse("55m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b50 minutes&f.", JavaUtils.parse("50m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b45 minutes&f.", JavaUtils.parse("45m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b30 minutes&f.", JavaUtils.parse("30m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b15 minutes&f.", JavaUtils.parse("15m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b10 minutes&f.", JavaUtils.parse("10m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b5 minutes&f.", JavaUtils.parse("5m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b1 minute&f.", JavaUtils.parse("1m"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f will be ending in &b30 seconds&f.", JavaUtils.parse("30s"))
                .addReminder(keysTimer.getDisplayName() + " &7" + CC.UNICODE_ARROWS_RIGHT + " &bTriple Keys&f has just &c&lENDED&f.", JavaUtils.parse("2s"));

        keysTimer
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b60 minutes&f.", JavaUtils.parse("60m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b55 minutes&f.", JavaUtils.parse("55m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b50 minutes&f.", JavaUtils.parse("50m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b45 minutes&f.", JavaUtils.parse("45m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b30 minutes&f.", JavaUtils.parse("30m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b15 minutes&f.", JavaUtils.parse("15m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b10 minutes&f.", JavaUtils.parse("10m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b5 minutes&f.", JavaUtils.parse("5m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b1 minute&f.", JavaUtils.parse("1m"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f will be ending in &b30 seconds&f.", JavaUtils.parse("30s"))
                .addReminderTitle(keysTimer.getDisplayName(), "&bTriple Keys&f has just &c&lENDED&f.", JavaUtils.parse("2s"));


        Samurai.getInstance().getTimerHandler().getServerTimers().put("Double-Airdrops", airdropTimer);
        Samurai.getInstance().getTimerHandler().getServerTimers().put("Double-Coins", coinsTimer);
        Samurai.getInstance().getTimerHandler().getServerTimers().put("Triple-Keys", keysTimer);

    }

    @Subcommand("editor")
    public void editor(Player sender) {
        new ServerTimerMenu().openMenu(sender);
    }

}
