package dev.lbuddyboy.bot.utils.html;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.ticket.Ticket;
import dev.lbuddyboy.bot.utils.TimeUtils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

@Getter
public class HTMLFile {

    private final String name;
    private final LinkedList<String> contents;

    public HTMLFile(String name) {
        this.name = name;
        this.contents = DEFAULT_CONTENTS(name);
    }

    public HTMLFile(String name, LinkedList<String> contents) {
        this.name = name;
        this.contents = contents;
    }

    public void addBody(String line) {
        this.contents.add(line);
    }

    public void addMessage(Ticket.TicketMessage message) {
        Bot.getInstance().getJda().retrieveUserById(message.getSender()).queue(user -> {
            this.contents.add(this.contents.indexOf("<body>") + 1, "<a\n" +
                    "><img\n" +
                    "        src=\"" + user.getAvatarUrl() + "\"\n" +
                    "        width=\"32\"\n" +
                    "        height=\"32\"\n" +
                    "        loading=\"lazy\"\n" +
                    "/> " + user.getName() + "<a id=\"time\"> " + TimeUtils.formatIntoCalendarString(new Date(message.getTimeSent())) + " EST</a></a\n" +
                    ">\n" +
                    "<ol>\n" +
                    "    <a>" + message.getMessage() + "</a>\n" +
                    "</ol>");
        });
    }

    public void addMessage(Message message, int i) {
        this.contents.add((this.contents.indexOf("<body>") + 1) + i, "<a\n" +
                "><img\n" +
                "        src=\"" + message.getAuthor().getAvatarUrl() + "\"\n" +
                "        width=\"32\"\n" +
                "        height=\"32\"\n" +
                "        loading=\"lazy\"\n" +
                "/> " + message.getAuthor().getName() + "<a id=\"time\"> " + message.getTimeCreated() + " EST</a></a\n" +
                ">\n" +
                "<ol>\n" +
                "    <a>" + message.getContentRaw() + "</a>\n" +
                "</ol>");
    }

    public static LinkedList<String> DEFAULT_CONTENTS(String name) {
        return new LinkedList<>(Arrays.asList(
                "<!DOCTYPE html>",
                "<html lang=\"en\">",
                "<head>",
                "<meta charset=\"UTF-8\">",
                "<meta http-equiv=\"X-UA-Compatible content=\"IE=edge\">",
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">",
                "<title>" + name + "</title>",
                "<style>",
                "html {",
                "font-size: 15px;",
                "}",
                "body {",
                "overflow: hidden;",
                "-webkit-user-select: none;",
                "-moz-user-select: none;",
                "-ms-user-select: none;",
                "user-select: none;",
                "background: #35393e;",
                "}",
                "a {",
                "font-size: 15px;",
                "color: hsla(0, 0%, 100%, 0.7);",
                "font-family: \"gg sans\", \"Noto Sans\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;",
                "text-align: center;",
                "}",
                "#time {",
                "font-size: 12px;",
                "color: gray;",
                "font-family: \"gg sans\", \"Noto Sans\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;",
                "text-align: center;",
                "}",
                "img {",
                "border-radius: 50%;",
                "vertical-align: middle;",
                "}",
                "</style>",
                "</head>",
                "<body>",
                "</body>",
                "</html>"
        ));
    }

}
