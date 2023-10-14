package dev.lbuddyboy.samurai.custom.schedule;

import dev.lbuddyboy.samurai.Samurai;
import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduledTime {

    private String name, command;
    private long duration, sentAt;
    private List<Long> reminders = new ArrayList<>();

    public ScheduledTime(String name, String command, long duration) {
        this.name = name;
        this.command = command;
        this.duration = duration;
        this.sentAt = System.currentTimeMillis();
        this.reminders = new ArrayList<>();

        for (Long interval : ScheduleHandler.getNotifyIntervals()) {
            if (interval > this.duration) this.reminders.add(interval);
        }
    }

    public ScheduledTime(Document document) {
        this.name = document.getString("name");
        this.command = document.getString("command");
        this.duration = document.getLong("duration");
        this.sentAt = document.getLong("sentAt");

        for (Long interval : ScheduleHandler.getNotifyIntervals()) {
            if (interval > getTimeLeft()) reminders.add(interval);
        }
    }

    public Document save() {
        Document document = new Document();

        document.put("name", this.name);
        document.put("command", this.command);
        document.put("duration", this.duration);
        document.put("sentAt", this.sentAt);

        return document;
    }

    public long getTimeLeft() {
        return (this.duration + this.sentAt) - System.currentTimeMillis();
    }

}
