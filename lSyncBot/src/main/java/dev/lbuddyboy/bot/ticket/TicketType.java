package dev.lbuddyboy.bot.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@AllArgsConstructor
@Getter
public enum TicketType {

    GENERAL_SUPPORT(Button.success("general-support", "General Support"), DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS)),
    STORE_SUPPORT(Button.success("store-support", "Store Support"), DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS));

    private final Button button;
    private final DefaultMemberPermissions permissions;

}
