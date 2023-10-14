package dev.lbuddyboy.samurai.chat.chatgames.type;

import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.chat.chatgames.ChatGame;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:37 PM
 * HCTeams / rip.orbit.hcteams.chatgames.type
 */
public class ChatQuestion extends ChatGame {

	private Question pickedQuestion = null;
	private double tickedTime;
	private int current = 0;
	private final List<Question> questions = new ArrayList<>();

	public ChatQuestion(File file, String name) {
		super(file, name);
		this.config = new Config(Samurai.getInstance(), name, file);

		for (String key : this.config.getConfigurationSection("questions").getKeys(false)) {
			this.questions.add(new Question(this.config.getString("questions." + key + ".question"), this.config.getStringList("questions." + key + ".answers")));
		}
	}

	@Override
	public String name() {
		return "Chat Question";
	}

	@Override
	public void start() {
		this.started = true;
		this.tickedTime = 0;

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!started) {
					cancel();
					return;
				}
				tickedTime = tickedTime + 0.1;
			}
		}.runTaskTimerAsynchronously(Samurai.getInstance(), 5, 5);

		Question picked = questions.get(ThreadLocalRandom.current().nextInt(questions.size()));

		this.pickedQuestion = picked;

		List<String> format = CC.translate(this.config.getStringList("start-message"),
				"%question%", picked.getQuestion()
		);

		format.forEach(s -> {
			Bukkit.broadcastMessage(CC.translate(s));
		});

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!started)
					return;
				end();
			}
		}.runTaskLater(Samurai.getInstance(), 20 * 15);

	}

	@Override
	public void end() {

		this.started = false;

		Bukkit.broadcastMessage(CC.translate("&cNobody answered the question in time."));

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.started) {
			if (this.pickedQuestion != null) {
				for (String answer : this.pickedQuestion.getAnswers()) {
					if (answer.toLowerCase().equals(event.getMessage().toLowerCase())) {
						this.started = false;
						event.setCancelled(true);

						List<String> winMessage = CC.translate(this.config.getStringList("solved-message"),
								"%question%", this.pickedQuestion.question,
								"%winner%", event.getPlayer().getDisplayName(),
								"%answer%", event.getMessage(),
								"%solve-time%", Team.DTR_FORMAT2.format(tickedTime)
						);

						winMessage.forEach(Bukkit::broadcastMessage);

						Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
							this.config.getStringList("win-commands").forEach(s -> {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", event.getPlayer().getName()));
							});
						});

					}
				}
			}
		}
	}

	 @AllArgsConstructor
	@Data
	public static class Question {
		private final String question;
		private final List<String> answers;
	}

}
