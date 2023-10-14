package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ChatModeMap extends PersistMap<ChatMode> {

    public ChatModeMap() {
        super("ChatModes", "ChatMode");
    }

    @Override
    public String getRedisValue(ChatMode chatMode) {
        return (chatMode.name());
    }

    @Override
    public ChatMode getJavaObject(String str) {
        return (ChatMode.valueOf(str));
    }

    @Override
    public Object getMongoValue(ChatMode chatMode) {
        return (chatMode.name());
    }

    public ChatMode getChatMode(UUID check) {
        return (contains(check) ? getValue(check) : ChatMode.PUBLIC);
    }

    public void setChatMode(UUID update, ChatMode chatMode) {
        updateValueAsync(update, chatMode);
    }

}