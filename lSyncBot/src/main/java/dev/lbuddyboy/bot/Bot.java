package dev.lbuddyboy.bot;

import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.command.CommandHandler;
import dev.lbuddyboy.bot.config.Config;
import dev.lbuddyboy.bot.config.ConfigHandler;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.invite.InviteHandler;
import dev.lbuddyboy.bot.redis.RedisHandler;
import dev.lbuddyboy.bot.sync.SyncHandler;
import dev.lbuddyboy.bot.thread.BotThread;
import dev.lbuddyboy.bot.ticket.TicketHandler;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Data
public class Bot {

    @Getter
    public static Bot instance;
    @Getter public static boolean enabled;

    private final String token, prefix;
    private JDABuilder builder;
    private JDA jda;
    private Guild guild;

    private final List<IHandler> handlers;
    private Config config;
    private File dataFolder;

    private BotHandler botHandler;
    private ConfigHandler configHandler;
    private CommandHandler commandHandler;
    private RedisHandler redisHandler;
    private SyncHandler syncHandler;
    private InviteHandler inviteHandler;
    private TicketHandler ticketHandler;

    public Bot() {
        instance = this;
        this.handlers = Arrays.asList(
                this.configHandler = new ConfigHandler(),
                this.botHandler = new BotHandler(),
                this.commandHandler = new CommandHandler(),
                this.redisHandler = new RedisHandler(),
                this.syncHandler = new SyncHandler(),
                this.inviteHandler = new InviteHandler(),
                this.ticketHandler = new TicketHandler()
        );

        this.prefix = BotConfiguration.BOT_PREFIX.getString();
        this.token = BotConfiguration.BOT_TOKEN.getString();

        this.handlers.forEach(handler -> handler.load(this));
        this.loadShutdownLogic();

        enabled = true;
        new BotThread().start();
    }

    private void loadShutdownLogic() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.handlers.forEach(handler -> handler.unload(this));
            jda.shutdown();
        }));
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
        new Timer() {
        }.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(instance.jda.getGuilds().size());
                for (Guild guild : instance.jda.getGuilds()) {
                    if (guild.getIdLong() == BotConfiguration.BOT_GUILD.getLong()) Bot.getInstance().setGuild(guild);
                    System.out.println(guild.getId() + ":" + guild.getName());
                }

                for (Command command : Bot.getInstance().getCommandHandler().getCommands()) {
                    Bot.getInstance().getGuild().upsertCommand(Commands.slash(command.getCmd(), command.getDescription())
                                    .setDefaultPermissions(command.getDefaultMemberPermissions())
                            .setGuildOnly(true).addOptions(command.getOptionData())).queue();

                    System.out.println("[Command Handler] Registered '" + command.getCmd() + "' command");
                }

                System.out.println("[Command Handler] Registered " + Bot.getInstance().getCommandHandler().getCommands().size() + " commands in total!");
            }
        }, 1000);
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = this.getClass().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.dataFolder.getAbsolutePath());
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                System.out.println("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            System.out.println("Could not save " + outFile.getName() + " to " + outFile);
        }
    }
}