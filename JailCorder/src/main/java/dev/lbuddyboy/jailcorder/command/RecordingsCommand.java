package dev.lbuddyboy.jailcorder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import dev.lbuddyboy.jailcorder.JailCorder;
import dev.lbuddyboy.jailcorder.record.Recording;
import dev.lbuddyboy.jailcorder.record.RecordingUser;
import dev.lbuddyboy.jailcorder.util.CC;
import dev.lbuddyboy.jailcorder.util.FancyPagedItem;
import dev.lbuddyboy.jailcorder.util.PagedItem;
import dev.lbuddyboy.jailcorder.util.fanciful.FancyMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@CommandAlias("recordings|logs|recordlogs")
@CommandPermission("flash.command.freeze")
public class RecordingsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("player") UUID playerUUID, @Name("page") @Optional Integer page) {
        RecordingUser user = JailCorder.getInstance().getRecordingUsers().getOrDefault(playerUUID, new RecordingUser(playerUUID));
        if (page == null) page = 1;

        if (user == null) {
            sender.sendMessage(CC.translate("&cCould not load a user..."));
            return;
        }

        List<FancyMessage> items = new ArrayList<>();
        for (Recording recording : user.getRecordings()) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));

            items.add(new FancyMessage(CC.translate("&6Replay &7(" + recording.getId() + ") "))
                    .then(CC.translate("&b[Blocks Broken]")).tooltip(CC.translate("&e" + recording.getBlocksBroken()))
                    .then(CC.translate(" &a[Started At]")).tooltip(CC.translate("&e" + sdf.format(recording.getStartedAt())))
                    .then(CC.translate(" &c[Ended At]")).tooltip(CC.translate("&e" + sdf.format(recording.getEndedAt())))
                    .then(CC.translate(" &e[View]")).tooltip(CC.translate("&7Click to view this replay!")).command("/replay play " + recording.getId())
            );
        }

        FancyPagedItem item = new FancyPagedItem(items, new ArrayList<>(), 10);

        item.setHeader(Collections.singletonList("&e===========&6&l " + user.getName() + "'s Jail Recordings &7(" + page + "/" + item.getMaxPages() + ")&e==========="));

        item.send(sender, page);
    }

}
