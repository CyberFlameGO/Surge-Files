package dev.lbuddyboy.bot.config;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import lombok.Getter;

import java.io.File;

@Getter
public class ConfigHandler implements IHandler {

    public ConfigHandler() {
        Bot.getInstance().setDataFolder(new File(System.getProperty("user.dir")));
        Bot.getInstance().setConfig(new Config(Bot.getInstance(), "config"));
    }

    @Override
    public void load(Bot instance) {
        for (BotConfiguration language : BotConfiguration.values()) language.loadDefault();
        for (MessageConfiguration language : MessageConfiguration.values()) language.loadDefault();
    }

    @Override
    public void unload(Bot instance) {

    }

}
