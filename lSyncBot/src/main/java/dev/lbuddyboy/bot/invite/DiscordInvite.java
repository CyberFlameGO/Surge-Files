package dev.lbuddyboy.bot.invite;

import com.mongodb.lang.Nullable;
import dev.lbuddyboy.bot.Bot;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;

@AllArgsConstructor
@Data
public class DiscordInvite {

    private long id;
    private String code;
    private long createdAt;
    private int joined, leaves, bots;
    private transient Invite invite;
    @Nullable private transient User inviter;

    public DiscordInvite(Invite invite) {
        this.invite = invite;
        this.id = invite.getInviter().getIdLong();
        this.code = invite.getCode();
        this.createdAt = invite.getTimeCreated().toInstant().getEpochSecond();
        this.joined = invite.getUses();
        this.leaves = Bot.getInstance().getInviteHandler().loadInviteLeaves(invite);
        this.bots = Bot.getInstance().getInviteHandler().loadInviteBots(invite);
        if (invite.isExpanded()) {
            this.inviter = invite.getInviter();
        }
        else {
            invite.expand().queue(inv -> this.inviter = inv.getInviter());
        }
    }

    public int getSum() {
        return joined - leaves - bots;
    }

}
