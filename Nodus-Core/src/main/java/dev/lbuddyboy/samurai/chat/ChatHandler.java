package dev.lbuddyboy.samurai.chat;

import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.chat.chatgames.ChatGameHandler;
import dev.lbuddyboy.samurai.chat.listeners.ChatListener;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class ChatHandler {

    @Getter private static final AtomicInteger publicMessagesSent = new AtomicInteger();

    private final ChatGameHandler chatGameHandler;

    public ChatHandler() {
        chatGameHandler = new ChatGameHandler();
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), Samurai.getInstance());
    }

}