package dev.lbuddyboy.pcore.util;

import dev.lbuddyboy.pcore.pCore;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class ConversationBuilder {

    public Player receiver;
    public ConversationFactory factory;

    public ConversationBuilder(Player receiver) {
        this.receiver = receiver;
        this.factory = new ConversationFactory(pCore.getInstance());
    }

    public ConversationBuilder stringPrompt(String prompt, PromptCallback<ConversationContext, String> callback) {

        this.factory = this.factory.withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                return callback.call(context, input);
            }
        });

        return this;
    }

    public ConversationBuilder closeableStringPrompt(String prompt, PromptCallback<ConversationContext, String> callback) {

        this.factory = this.factory.withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    receiver.sendMessage(CC.translate("&cProcess cancelled."));
                    return END_OF_CONVERSATION;
                }

                return callback.call(context, input);
            }
        });

        return this;
    }

    public ConversationBuilder echo(boolean echo) {
        this.factory.withLocalEcho(echo);
        return this;
    }

    public StagedConversationBuilder addStage(StagedConversationBuilder builder) {
        builder.conversationFactories.add(this.factory);
        return builder;
    }

    public Conversation build() {
        return factory.buildConversation(this.receiver);
    }

    public interface PromptCallback<T, O> {
        Prompt call(T t, O t2);
    }

}
