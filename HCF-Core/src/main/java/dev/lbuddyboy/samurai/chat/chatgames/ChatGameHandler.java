package dev.lbuddyboy.samurai.chat.chatgames;

import dev.lbuddyboy.samurai.chat.chatgames.type.ChatMath;
import dev.lbuddyboy.samurai.chat.chatgames.type.ChatQuestion;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:35 PM
 * HCTeams / rip.orbit.hcteams.chatgames
 */

@Getter
public class ChatGameHandler {

	private final List<ChatGame> chatGames;
	private int current = 0;
	private File directory;

	public ChatGameHandler() {
		chatGames = new ArrayList<>();
		this.loadDirectories();

		chatGames.add(new ChatQuestion(this.directory, "question"));
		chatGames.add(new ChatMath(this.directory, "math"));

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					ChatGame game = chatGames.get(current++);

					game.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (current >= chatGames.size()) current = 0;
			}
		}.runTaskTimerAsynchronously(Samurai.getInstance(), 20 * 60 * 5, 20 * 60 * 5);
	}

	private void loadDirectories() {
		this.directory = new File(Samurai.getInstance().getDataFolder(), "chat-games");
		if (!directory.exists()) directory.mkdirs();
	}

}
