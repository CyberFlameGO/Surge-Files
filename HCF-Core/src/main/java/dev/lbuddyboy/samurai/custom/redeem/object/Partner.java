package dev.lbuddyboy.samurai.custom.redeem.object;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 7:33 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */

@Data
public class Partner {

	@Getter public static MongoCollection<Document> collection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("partners");

	private String name;
	private int redeemedAmount = 0;

	public Partner(String name) {
		this.name = name;

		load();
	}

	public void load() {
		Document document = collection.find(Filters.eq("name", this.name)).first();

		if (document == null) return;

		this.redeemedAmount = document.getInteger("redeemedAmount");
	}

	public void save() {
		Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			Document document = new Document();

			document.put("name", this.name);
			document.put("redeemedAmount", this.redeemedAmount);

			collection.replaceOne(Filters.eq("name", this.name), document, new ReplaceOptions().upsert(true));
		});
	}

	public void delete() {
		Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			collection.deleteOne(Filters.eq("name", this.name));
		});
	}

	public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

		@Override
		public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
			List<String> completions = new ArrayList<>();
			Player player = context.getPlayer();
			String input = context.getInput();

			for (Partner partner : Samurai.getInstance().getRedeemHandler().getPartners()) {
				if (StringUtils.startsWith(partner.getName(), input)) {
					completions.add(partner.getName());
				}
			}

			return completions;
		}

	}
}
