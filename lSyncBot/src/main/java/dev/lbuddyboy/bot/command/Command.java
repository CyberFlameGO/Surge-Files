package dev.lbuddyboy.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class Command {

    private final String cmd;
    private final List<String> aliases;
    private final List<OptionData> optionData;
    private final DefaultMemberPermissions defaultMemberPermissions;
    private final String description;
    private final String otherArgs;

    public void send(SlashCommandInteractionEvent event) {

    }

    public String getHelpArgs(String start) {
        return start + " " + this.otherArgs;
    }

}
