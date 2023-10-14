package dev.lbuddyboy.samurai.util.discord;

import dev.lbuddyboy.samurai.custom.supplydrops.SupplyCrate;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.DiscordWebhook;
import org.bukkit.Bukkit;

import java.awt.*;
import java.io.IOException;

public class DiscordLogger {

	public static void logEventStart(dev.lbuddyboy.samurai.events.Event event) throws IOException {
		if (Bukkit.hasWhitelist()) return;

		Webhook webhook = new Webhook(Samurai.getInstance().getServerHandler().getEventWebhook());
		webhook.addEmbed(new Webhook.EmbedObject()
				.setAuthor((event.getName().equals("Citadel") ? "Citadel" : event.getName() + " KoTH"), null, null)
				.setColor(Color.BLUE)
				.addField("Server", FoxtrotConfiguration.SERVER_NAME.getString(), false)
				.addField("Type", event.getType().name(), false)
				.addField("Information", "Has just started and is now capable.", false)

		);
		webhook.execute();
	}

	public static void logRetard(String message) {
		try {
			DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1134250175792152758/xOv4tuxdI3xSMAtM2x4FmECst8DP8TSkElTjJxNROqAlDX7-_OINhc8Pn5DWl8m_cg6G");
			webhook.setContent("\n@everyone | URGENT BUG FIX \nMessage from Buddy: " + message + "\n Server: " + FoxtrotConfiguration.SERVER_NAME.getString());
			webhook.setUsername("Retard Alert");
			webhook.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logEventCaptured(Event event, String team) throws IOException {

		Webhook webhook = new Webhook(Samurai.getInstance().getServerHandler().getEventWebhook());
		webhook.addEmbed(new Webhook.EmbedObject()
				.setAuthor((event.getName().equals("Citadel") ? "Citadel" : event.getName() + " KoTH"), null, null)
				.setDescription((event.getName().equals("Citadel") ? "Citadel" : event.getName().equals("Vault") ? "Vault Post" : event.getName() + " KoTH") + " has just been captured by " + team + ".")
				.setColor(Color.BLUE)
				.addField("Server", FoxtrotConfiguration.SERVER_NAME.getString(), false)
				.addField("Type", (event.getType().name().equals("Vault") ? "Vault Post" : event.getType().name()), false)

		);
		webhook.execute();
	}

	public static void logSchedule(String name, String time) throws IOException {
		if (Bukkit.hasWhitelist()) return;

		Webhook webhook = new Webhook(Samurai.getInstance().getServerHandler().getScheduleWebhook());
		webhook.addEmbed(new Webhook.EmbedObject()
				.setAuthor("[Mine Surge Scheduler]", null, null)
				.setDescription("A " + name + " will commence in " + time)
				.setColor(Color.BLUE)
						.addField("Server", FoxtrotConfiguration.SERVER_NAME.getString(), false)
				.addField("Commences In", time, false)

		);
		webhook.execute();
	}

	public static void logSupplyCrate(SupplyCrate crate) throws IOException {
		if (Bukkit.hasWhitelist()) return;

//		Webhook webhook = new Webhook(Foxtrot.getInstance().getServerHandler().getEventWebhook());
//		webhook.addEmbed(new Webhook.EmbedObject()
//				.setAuthor("Supply Crate", null, null)
//				.setDescription("A supply crate has just spawned.")
//				.addField("Server", Configuration.SERVER_NAME.getString(), false)
//				.setColor(Color.BLUE)
//				.addField("X", "" + crate.getEndLocation().getBlockX(), false)
//				.addField("Y", "" + crate.getEndLocation().getBlockY(), false)
//				.addField("Z", "" + crate.getEndLocation().getBlockZ(), false)
//
//		);
//		webhook.execute();
	}

	public static void logSpecialEvent(String specialEvent, String time, String description) throws IOException {
		if (Bukkit.hasWhitelist()) return;

		Webhook webhook = new Webhook(Samurai.getInstance().getServerHandler().getEventWebhook());
		webhook.addEmbed(new Webhook.EmbedObject()
				.setAuthor(specialEvent, null, null)
				.setDescription("A " + specialEvent + " has just begun!")
				.setColor(Color.BLUE)
				.addField("Server", FoxtrotConfiguration.SERVER_NAME.getString(), false)
				.addField("Time", time, false)
				.addField("Information", description, false)

		);
		webhook.execute();
	}

}