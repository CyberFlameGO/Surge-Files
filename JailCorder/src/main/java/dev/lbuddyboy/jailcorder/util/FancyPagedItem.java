package dev.lbuddyboy.jailcorder.util;

import dev.lbuddyboy.jailcorder.util.fanciful.FancyMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FancyPagedItem {

    private List<FancyMessage> messages;
    private List<String> header;
    private int maxItemsPerPage;

    public int getMaxPages() {
        return (messages.size() / maxItemsPerPage) + 1;
    }

    public void send(CommandSender sender, int page) {

        if (page > getMaxPages()) {
            sender.sendMessage(CC.translate("&cThat page is not within the bounds of " + getMaxPages() + "."));
            return;
        }

        header.forEach(s -> sender.sendMessage(CC.translate(s
                .replaceAll("%page%", "" + page)
                .replaceAll("%max-pages%", "" + getMaxPages())
        )));

        for (int i = (page * maxItemsPerPage) - maxItemsPerPage; i < (page * maxItemsPerPage); i++) {
            if (messages.size() <= i) continue;

            messages.get(i).send(sender);
        }

    }

}
