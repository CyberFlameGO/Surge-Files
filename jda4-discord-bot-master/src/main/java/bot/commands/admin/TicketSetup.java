package bot.commands.admin;

import bot.Constants;
import bot.command.CommandCategory;
import bot.command.CommandContext;
import bot.command.ICommand;
import bot.database.DataSource;
import bot.database.objects.Ticket;
import bot.utils.BotUtils;
import bot.utils.TicketUtils;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TicketSetup extends ICommand {

    private final static List<String> CANCEL_WORDS = Arrays.asList("cancel", "-cancel");
    private final static String CANCEL = "\n\n`Ticket creation has been cancelled.`";
    private final static String CHANNEL = "Please `mention the channel` in which the reaction message must be sent";
    private final static String TITLE = "Please enter the `title` of the ticket\n" +
            "Example: ```Discord Support Ticket```";
    private final static String ROLE = "What roles should have access to view the newly created tickets? \n\n" +
            "`Please type the name of a existing role in this server.`\n\nAlternatively you can type `none`";
    private final HashMap<String, OffsetDateTime> current;
    private final EventWaiter waiter;

    public TicketSetup(EventWaiter waiter) {
        this.name = "ticket";
        this.usage = "`{p}{i} setup` : start interactive ticket setup\n" +
                "`{p}{i} remconfig` : remove the existing ticket configuration\n" +
                "`{p}{i} log <#channel>` : setup log channel for new tickets\n" +
                "`{p}{i} add <@member>` : add a member to a ticket (staff/players)\n" +
                "`{p}{i} generalrole <@role>` : sets the general support role\n" +
                "`{p}{i} kitmaprole <@role>` : sets the kitmap support role\n" +
                "`{p}{i} bunkersrole <@role>` : sets the bunkers support role\n" +
                "`{p}{i} hcfrole <@role>` : sets the hcf support role\n" +
                "`{p}{i} practicerole <@role>` : sets the practice support role\n" +
                "`{p}{i} claim` : make a ticket only viewable to you and admins+\n" +
                "`{p}{i} adminonly <ON|OFF>` : toggle if admins can only close a ticket\n" +
                "`{p}{i} close` : close a current ticket\n" +
                "`{p}{i} closeall` : close all open tickets\n";
        this.help = "setup ticketing system in your discord server";
        this.multilineHelp = true;
        this.minArgsCount = 1;
        this.userPermissions = new Permission[]{};
        this.current = new HashMap<>();
        this.waiter = waiter;
        this.category = CommandCategory.ADMINISTRATION;
    }

    @Override
    public void handle(@NotNull CommandContext ctx) {
        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());
        final String toDo = ctx.getArgs().get(0);

        if (config == null && (toDo.equalsIgnoreCase("limit") || toDo.equalsIgnoreCase("log")
                || toDo.equalsIgnoreCase("adminonly"))) {
            ctx.reply("You need to configure a ticket before using this command\n" +
                    "Type `" + ctx.getPrefix() + this.name + " setup` to create one");
            return;
        }

        if (ctx.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            switch (toDo.toLowerCase()) {

                case "setup":
                    setup(ctx, config);
                    break;

                case "remconfig":
                case "removeconfig":
                    reset(ctx, config);
                    break;

                case "limit":
                    setLimit(ctx);
                    break;

                case "messageid":
                    setMessageId(ctx);
                    break;

                case "channel":
                    setChannelId(ctx);
                    break;

                case "hcfrole":
                    setHCFRole(ctx);
                    break;

                case "kitmaprole":
                    setKitMapRole(ctx);
                    break;

                case "generalrole":
                    setGeneralRole(ctx);
                    break;

                case "practicerole":
                    setPracticeRole(ctx);
                    break;

                case "bunkersrole":
                    setBunkersRole(ctx);
                    break;

                case "log":
                    setLogChannel(ctx);
                    break;

                case "info":
                    sendInfo(ctx);
                    break;

                case "adminonly":
                    adminOnly(ctx);
                    break;

                case "rename":
                    rename(ctx);
                    break;

                case "claim":
                    claim(ctx);
                    break;

                case "add":
                    addMember(ctx);
                    break;

                case "remove":
                    removeMember(ctx);
                    break;

                case "elevate":
                case "raise":
                    raise(ctx);
                    break;

                case "close":
                    close(ctx);
                    break;

                case "closeall":
                    closeAll(ctx);
                    break;

                default:
                    ctx.reply("Invalid input");
                    break;
            }
        } else {
            if (!TicketUtils.isTicketChannel(ctx.getChannel())) return;
            String type = Objects.requireNonNull(ctx.getChannel().getTopic()).split("\\|")[2];

            if (config == null) return;

            String roleId = null;

            if (type.equalsIgnoreCase("general")) {
                if (config.generalSupportId != null) {
                    roleId = config.generalSupportId;
                }
            }

            if (type.equalsIgnoreCase("kitmap")) {
                if (config.kitmapSupportId != null) {
                    roleId = config.kitmapSupportId;
                }
            }

            if (type.equalsIgnoreCase("practice")) {
                if (config.practiceSupportId != null) {
                    roleId = config.practiceSupportId;
                }
            }

            if (type.equalsIgnoreCase("hcf")) {
                if (config.hcfSupportId != null) {
                    roleId = config.hcfSupportId;
                }
            }

            if (type.equalsIgnoreCase("bunkers")) {
                if (config.bunkersSupportId != null) {
                    roleId = config.bunkersSupportId;
                }
            }

            if (roleId == null) {
                return;
            }

            Role role = ctx.getGuild().getRoleById(roleId);
            if (role == null) return;
            if (!ctx.getMember().getRoles().contains(role)) return;

            switch (toDo.toLowerCase()) {
                case "rename":
                    rename(ctx);
                    break;

                case "claim":
                    claim(ctx);
                    break;

                case "add":
                    addMember(ctx);
                    break;

                case "elevate":
                case "raise":
                    raise(ctx);
                    break;

                case "remove":
                    removeMember(ctx);
                    break;

                default:
                    ctx.reply("Invalid input");
                    break;
            }
        }
    }

    private void setLogChannel(CommandContext ctx) {
        final List<TextChannel> menChannels = ctx.getMessage().getMentionedChannels();

        if (menChannels.isEmpty()) {
            ctx.reply("Please mention a channel name where ticket logs must be sent");
            return;
        }

        TextChannel targetChannel = menChannels.get(0);
        DataSource.INS.setTicketLogChannel(ctx.getGuild().getId(), targetChannel.getId());
        ctx.reply("Configuration saved! Newly created ticket logs will be sent to " + targetChannel.getAsMention());

    }

    private void sendInfo(CommandContext ctx) {
        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        ctx.reply(
                "" + "Practice Role: " + config.practiceSupportId + "\n" +
                        "" + "General Role: " + config.generalSupportId + "\n" +
                        "" + "HCF Role: " + config.hcfSupportId + "\n" +
                        "" + "Kitmap Role: " + config.kitmapSupportId + "\n" +
                        "" + "Bunkers Role: " + config.bunkersSupportId + "\n"
        );
    }

    private void rename(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();

        if (TicketUtils.isTicketChannel(channel)) {
            channel.getManager().setName("ticket-" + ctx.getArgsJoinedAfter(0)).queue();

            ctx.reply("Ticket renamed to " + channel.getName() + " by " + ctx.getMember().getAsMention() + "!");
            ctx.getMessage().delete().queue();
        } else
            ctx.reply("You can only use this command in a ticket channel");
    }

    private void addMember(CommandContext ctx) {
        final List<Member> mentionedMembers = ctx.getMessage().getMentionedMembers();

        if (mentionedMembers.isEmpty()) {
            ctx.reply("Please mention a member name.");
            return;
        }

        TextChannel channel = ctx.getChannel();

        if (TicketUtils.isTicketChannel(channel)) {
            for (Member member : mentionedMembers) {
                channel.getManager().putPermissionOverride(member,
                                Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES),
                                Collections.emptyList())
                        .queue();

                ctx.reply(member.getAsMention() + " added to the ticket by " + ctx.getMember().getAsMention() + "!");
            }

            ctx.getMessage().delete().queue();
        } else
            ctx.reply("You can only use this command in a ticket channel");
    }

    private void removeMember(CommandContext ctx) {
        final List<Member> mentionedMembers = ctx.getMessage().getMentionedMembers();

        if (mentionedMembers.isEmpty()) {
            ctx.reply("Please mention a member name.");
            return;
        }

        TextChannel channel = ctx.getChannel();

        if (TicketUtils.isTicketChannel(channel)) {
            for (Member member : mentionedMembers) {
                channel.getManager()
                        .removePermissionOverride(member)
                        .queue();
            }
            ctx.getMessage().delete().queue();
        } else
            ctx.reply("You can only use this command in a ticket channel");
    }

    private void setPracticeRole(CommandContext ctx) {
        final List<Role> roles = ctx.getMessage().getMentionedRoles();

        if (roles.isEmpty()) {
            ctx.reply("Please mention a role.");
            return;
        }

        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.practiceSupportId = roles.get(0).getId();

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    private void setHCFRole(CommandContext ctx) {
        final List<Role> roles = ctx.getMessage().getMentionedRoles();

        if (roles.isEmpty()) {
            ctx.reply("Please mention a role.");
            return;
        }

        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.hcfSupportId = roles.get(0).getId();

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    private void setMessageId(CommandContext ctx) {
        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.messageId = ctx.getArgs().get(1);

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    private void setChannelId(CommandContext ctx) {
        final List<TextChannel> channels = ctx.getMessage().getMentionedChannels();

        if (channels.isEmpty()) {
            ctx.reply("Please mention a channel.");
            return;
        }

        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.channelId = channels.get(0).getId();

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    private void setBunkersRole(CommandContext ctx) {
        final List<Role> roles = ctx.getMessage().getMentionedRoles();

        if (roles.isEmpty()) {
            ctx.reply("Please mention a role.");
            return;
        }

        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.bunkersSupportId = roles.get(0).getId();

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    private void setGeneralRole(CommandContext ctx) {
        final List<Role> roles = ctx.getMessage().getMentionedRoles();

        if (roles.isEmpty()) {
            ctx.reply("Please mention a role.");
            return;
        }

        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.generalSupportId = roles.get(0).getId();

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    private void setKitMapRole(CommandContext ctx) {
        final List<Role> roles = ctx.getMessage().getMentionedRoles();

        if (roles.isEmpty()) {
            ctx.reply("Please mention a role.");
            return;
        }

        Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());

        if (config == null) {
            return;
        }

        config.kitmapSupportId = roles.get(0).getId();

        DataSource.INS.updateTicketConfig(ctx.getGuildId(), config.channelId, config.messageId, config.title, config.generalSupportId, config.kitmapSupportId, config.practiceSupportId, config.bunkersSupportId, config.hcfSupportId);
    }

    public static void claim(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();

        if (TicketUtils.isTicketChannel(channel)) {
            Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());
            if (config == null) return;

            if (config.practiceSupportId != null) {
                Role role = ctx.getGuild().getRoleById(config.practiceSupportId);

                if (role == null) return;

                ctx.getChannel().upsertPermissionOverride(role)
                        .setAllow(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL)
                        .setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES).queue();
            }

            ctx.getChannel().upsertPermissionOverride(ctx.getMember())
                    .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES, Permission.VIEW_CHANNEL)
                    .setDeny(Collections.emptyList())
                    .queue();

            ctx.reply("Ticket claimed by " + ctx.getMember().getAsMention() + "!");

        } else
            ctx.reply("You can only use this command in a ticket channel");
    }

    private void raise(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();

        if (TicketUtils.isTicketChannel(channel)) {
            Ticket config = DataSource.INS.getTicketConfig(ctx.getGuildId());
            if (config == null) return;

            if (config.generalSupportId != null) {
                Role role = ctx.getGuild().getRoleById(config.generalSupportId);

                if (role != null) {
                    ctx.getChannel().upsertPermissionOverride(role)
                            .setAllow(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL)
                            .setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES).queue();
                }
            }

            Role role = ctx.getGuild().getRolesByName("_", true).get(0);
            if (role != null) {
                ctx.getChannel().upsertPermissionOverride(role)
                        .setAllow(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL).queue();
            }

            ctx.reply("Ticket elevated to admins only by " + ctx.getMember().getAsMention() + "!");

        } else
            ctx.reply("You can only use this command in a ticket channel");
    }

    private void reset(CommandContext ctx, Ticket config) {
        if (config == null) {
            ctx.reply("You do not have a ticket configured on this server");
            return;
        }

        DataSource.INS.deleteTicketConfig(ctx.getGuild().getId());
        ctx.reply("Ticket configuration has been removed!\n" +
                "You can anytime configure it again using `" + ctx.getPrefix() + this.name + " setup`");

    }

    private void setLimit(CommandContext ctx) {
        final List<String> args = ctx.getArgs();
        if (args.size() > 1) {
            try {
                int limit = Integer.parseInt(args.get(1));
                if (limit > 0 && limit <= 50) {
                    DataSource.INS.setTicketLimit(ctx.getGuild().getId(), limit);
                    ctx.reply("Ticket Configuration Updated!");
                }
            } catch (Exception e) {
                ctx.reply("Please enter an integer input");
            }
        } else {
            ctx.reply("Please enter limit! \n`" + ctx.getPrefix() + "ticket limit <number>`");
        }
    }

    private void adminOnly(CommandContext ctx) {
        if (ctx.getArgs().size() < 2) {
            ctx.reply("Incorrect arguments passed!");
            return;
        }
        String state = ctx.getArgs().get(1);

        boolean adminOnly;
        if (state.equalsIgnoreCase("ON"))
            adminOnly = true;
        else if (state.equalsIgnoreCase("OFF"))
            adminOnly = false;
        else {
            ctx.reply("Incorrect usage! Please try again");
            return;
        }

        DataSource.INS.setTicketClose(ctx.getGuildId(), adminOnly);
        ctx.reply("Ticket configuration Updated!");

    }

    private void close(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        final GuildMessageReceivedEvent event = ctx.getEvent();

        if (TicketUtils.isTicketChannel(channel)) {
            TicketUtils.closeTicket("Closed by a moderator", event.getGuild(), event.getAuthor(), channel);
        } else
            ctx.reply("You can only use this command in a ticket channel");

    }

    private void closeAll(CommandContext ctx) {
        int closedTickets = TicketUtils.closeAllTickets(ctx.getGuild());
        if (closedTickets > 0)
            ctx.reply("Successfully closed `" + closedTickets + "` tickets");
        else
            ctx.reply("No Open tickets found");
    }

    private void setup(CommandContext ctx, Ticket config) {
        final Guild guild = ctx.getGuild();

        if (config != null) {
            final TextChannel tcById = guild.getTextChannelById(config.channelId);
            if (tcById != null) {
                ctx.reply("You already have a ticket configured in" + tcById.getAsMention() + "\n" +
                        "Type `" + ctx.getPrefix() + this.name + " rem` to delete previous configuration");
                return;
            }
            DataSource.INS.deleteTicketConfig(guild.getId());
        }

        if (current.containsKey(guild.getId())) {
            if (OffsetDateTime.now().minusMinutes(15).compareTo(current.get(guild.getId())) > 0) {
                current.remove(guild.getId());
            } else {
                ctx.reply("A setup is already in progress");
                return;
            }
        }

        current.put(guild.getId(), OffsetDateTime.now());
        waitForChannel(ctx);
        BotUtils.sendSuccess(ctx.getMessage());

    }

    private void waitForChannel(CommandContext ctx) {
        BotUtils.sendEmbed(ctx.getChannel(), getEmbed("Ticket Channel", CHANNEL));
        wait(ctx, e -> {
            List<TextChannel> mentionedChannels = e.getMessage().getMentionedChannels();
            if (!mentionedChannels.isEmpty()) {
                final TextChannel targetChannel = mentionedChannels.get(0);

                if (!ctx.getSelfMember().hasPermission(targetChannel, TicketUtils.PERMS)) {
                    ctx.reply("Erm, I need the following permissions to initiate a ticket in "
                            + targetChannel.getAsMention() + "```" + parsePerms(TicketUtils.PERMS) + "```");
                    current.remove(ctx.getGuild().getId());
                    return;
                }

                BotUtils.sendSuccess(e.getMessage());
                waitForTitle(ctx, mentionedChannels.get(0));

            } else {
                ctx.reply("Oops! Invalid input. You did not mention a channel " + CANCEL);
                current.remove(ctx.getGuild().getId());
            }

        });
    }

    private void waitForTitle(CommandContext ctx, TextChannel channel) {
        BotUtils.sendEmbed(ctx.getChannel(), getEmbed("Ticket Message", TITLE));
        wait(ctx, e -> {
            String title = e.getMessage().getContentRaw();
            BotUtils.sendSuccess(e.getMessage());
            waitForRoles(ctx, channel, title);
        });
    }

    private void waitForRoles(CommandContext ctx, TextChannel channel, String title) {
        BotUtils.sendEmbed(ctx.getChannel(), getEmbed("Support Role", ROLE));

        wait(ctx, e -> {
            String roleId = null;
            if (!e.getMessage().getContentRaw().equalsIgnoreCase("none")) {
                String query = e.getMessage().getContentRaw().replace(" ", "_");
                List<Role> list = FinderUtil.findRoles(query, ctx.getGuild());

                if (list.isEmpty()) {
                    ctx.replyWithError("Uh oh, I couldn't find any roles called '" + query + "'! Try again");
                    waitForRoles(ctx, channel, title);
                    return;
                }

                if (list.size() > 1) {
                    ctx.replyWithError("Oh... there are multiple roles with that name. Please be more specific!");
                    waitForRoles(ctx, channel, title);
                    return;
                }

                Role role = list.get(0);
                roleId = role.getId();
                ctx.replyWithSuccess("Alright! `" + role.getName() + "` can now view the newly created tickets");

            }
            BotUtils.sendSuccess(e.getMessage());
            if (saveConfig(ctx.getGuild().getId(), channel, title, roleId))
                ctx.replyWithSuccess("Ticket system is successfully configured in " + channel.getAsMention() + "!");
            else
                ctx.replyError("Uh oh. Something went wrong and I wasn't able to create a ticket message." + CANCEL);

        });
    }

    private boolean saveConfig(String guildId, TextChannel channel, String title, String roleId) {
        try {
            EmbedBuilder eb = EmbedUtils.getDefaultEmbed()
                    .setAuthor(title)
                    .setDescription(
                            "To create a ticket click a button that relates to your reason for opening a ticket!\n" +
                                    "\n Whenever you are creating your ticket please have proof and your IGN if applicable to the situation!"
                    )
                    .setFooter("You can only have 1 open ticket at a time!");

            Message message = channel.sendMessage(eb.build())
                    .setActionRow(
                            Button.primary("create-general", "Create a Support Ticket!")
                    )
                    .submit().get();

            DataSource.INS.addTicketConfig(guildId, channel.getId(), message.getId(), title, "", "", "", "", roleId);
            current.remove(guildId);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage());
        }
        current.remove(guildId);
        return false;

    }

    private MessageEmbed getEmbed(String title, String message) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();
        return embed.setAuthor(title)
                .setDescription(message)
                .setFooter("Type cancel to cancel setup")
                .build();
    }

    private void wait(CommandContext ctx, Consumer<GuildMessageReceivedEvent> action) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()), e -> {
                    if (CANCEL_WORDS.contains(e.getMessage().getContentRaw().toLowerCase())) {
                        ctx.reply("Alright, I guess we're not setting up ticket tool after all..." + CANCEL);
                        current.remove(event.getGuild().getId());
                        return;
                    }
                    action.accept(e);
                }, 2, TimeUnit.MINUTES, new Timeout(ctx));
    }

    private class Timeout implements Runnable {

        private final CommandContext ctx;
        private boolean ran = false;

        private Timeout(CommandContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (ran)
                return;
            ran = true;
            EmbedBuilder embed = EmbedUtils.getDefaultEmbed()
                    .setAuthor("Setup Cancelled")
                    .setDescription(("Uh oh! You took longer than 2 minutes to respond, " + ctx.getAuthor().getAsMention() + "!"
                            + CANCEL));
            ctx.reply(embed.build());
            current.remove(ctx.getGuild().getId());
        }

    }

}
