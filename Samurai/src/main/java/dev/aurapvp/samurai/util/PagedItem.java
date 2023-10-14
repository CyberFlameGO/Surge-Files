package dev.aurapvp.samurai.util;

import java.util.List;
import org.bukkit.command.CommandSender;

public class PagedItem {
    private List<String> items;
    private List<String> header;
    private int maxItemsPerPage;

    public int getMaxPages() {
        return this.items.size() / this.maxItemsPerPage + 1;
    }

    public void send(CommandSender sender, int page) {
        if (page > this.getMaxPages()) {
            sender.sendMessage(CC.translate("&cThat page is not within the bounds of " + this.getMaxPages() + "."));
        } else {
            this.header.forEach((s) -> {
                sender.sendMessage(CC.translate(s.replaceAll("%page%", "" + page).replaceAll("%max-pages%", "" + this.getMaxPages())));
            });

            for(int i = page * this.maxItemsPerPage - this.maxItemsPerPage; i < page * this.maxItemsPerPage; ++i) {
                if (this.items.size() > i) {
                    sender.sendMessage(CC.translate((String)this.items.get(i)));
                }
            }

        }
    }

    public PagedItem(final List<String> items, final List<String> header, final int maxItemsPerPage) {
        this.items = items;
        this.header = header;
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public List<String> getItems() {
        return this.items;
    }

    public List<String> getHeader() {
        return this.header;
    }

    public int getMaxItemsPerPage() {
        return this.maxItemsPerPage;
    }
}
