package dev.lbuddyboy.samurai.util;

import dev.lbuddyboy.flash.Flash;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StagedConversationBuilder {

    public Player receiver;
    public List<ConversationFactory> conversationFactories = new ArrayList<>();
    public int current = 0;

    public StagedConversationBuilder(Player receiver) {
        this.receiver = receiver;
    }

    public ConversationBuilder newConversation() {
        return new ConversationBuilder(this.receiver, new ConversationFactory(Flash.getInstance()));
    }

    public List<Conversation> build() {
        return this.conversationFactories.stream().map(factory -> factory.buildConversation(this.receiver)).collect(Collectors.toList());
    }

    public void next() {
        this.current++;
    }

    public Conversation getActiveConversation() {
        if (this.current >= this.conversationFactories.size()) return null;

        return build().get(this.current);
    }

}
