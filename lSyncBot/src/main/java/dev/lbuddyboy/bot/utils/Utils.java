//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package dev.lbuddyboy.bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public Utils() {
    }

    public static boolean canUse(Member m) {
        if (!m.getRoles().isEmpty()) {
            for (Role r : m.getRoles()) {
                if (r.getName().toUpperCase().contains("FOUNDER")) {
                    return true;
                }
                if (r.getName().toUpperCase().contains("OWNER")) {
                    return true;
                }
                if (r.getName().toUpperCase().contains("MANAGER")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canUseOther(Member m) {
        if (m == null) return false;

        if (!m.getRoles().isEmpty()) {
            for (Role r : m.getRoles()) {
                if (r.getName().toUpperCase().contains("SUPPORT TEAM")) {
                    return true;
                }
            }
        }
        return canUse(m);
    }

    public static String getFooterFormat(Member member, Message message) {
        return "Sent by " + member.getUser().getAsTag() + " | " + message.getTimeCreated().getDayOfWeek() + ", " + message.getTimeCreated().getMonth().getValue() + "-" + message.getTimeCreated().getDayOfMonth() + "-" + message.getTimeCreated().getYear();
    }

    public static boolean isNullOrEmpty(String msg) {
        if (msg == null) {
            return true;
        } else {
            return msg.isEmpty();
        }
    }

    public static Role supportRole(Guild guild) {
        for (Role role : guild.getRoles()) {
            if (role.getName().contains("Support")) {
                return role;
            }
        }
        return null;
    }

    public static List<Role> allowedRoles(Guild guild) {
        List<Role> roles = new ArrayList<>();
        for (Role role : guild.getRoles()) {
            if (role.getName().contains("Support") || role.getName().contains("Manager") || role.getName().contains("Founder")) {
                roles.add(role);
            }
        }
        return roles;
    }

    public static MessageEmbed newEmb(String title, String footer, String footerImage, String author, String imageUrl, String iconUrl, String description, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        if (!isNullOrEmpty(title)) {
            eb.setTitle(title);
        }

        if (!isNullOrEmpty(footer)) {
            if (!isNullOrEmpty(footerImage)) {
                eb.setFooter(footer, footerImage);
            } else {
                eb.setFooter(footer);
            }
        }

        if (!isNullOrEmpty(author)) {
            if (!isNullOrEmpty(imageUrl)) {
                if (!isNullOrEmpty(iconUrl)) {
                    eb.setAuthor(author, imageUrl, iconUrl);
                } else {
                    eb.setAuthor(author, imageUrl);
                }
            } else {
                eb.setAuthor(author);
            }
        }

        if (!isNullOrEmpty(description)) {
            eb.setDescription(description);
        }

        eb.setColor(color);
        return eb.build();
    }
}
