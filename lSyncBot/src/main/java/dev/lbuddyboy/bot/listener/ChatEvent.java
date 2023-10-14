package dev.lbuddyboy.bot.listener;

import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChatEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        for (String word : BotConfiguration.ILLEGAL_WORDS.getStringList()) {
            if (!(event.getMessage().getContentRaw().toUpperCase().contains(word.toUpperCase()))) {
                continue;
            }

            event.getMessage().delete().queue(m -> {
                event.getChannel().sendMessage("You said a word that isn't allowed.").queue();
            });
            break;
        }

    }
}
