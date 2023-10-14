package dev.lbuddyboy.samurai.util.object;

import lombok.Getter;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Reward {

    private final UUID uuid = UUID.randomUUID();

    private String rewardName;
    private boolean rewardMessage;

    private List<String> commands = new ArrayList<>();
    private List<ItemStack> items = new ArrayList<>();

    public Reward(String rewardName, boolean rewardMessage) {
        this.rewardName = rewardName;
        this.rewardMessage = rewardMessage;
    }

    public Reward addCommand(String command) {
        commands.add(command);
        return this;
    }

    public void execute(Player player) {
        for (String command : commands) {
            String processedCommand = command
                    .replace("{playerName}", player.getName())
                    .replace("{playerUuid}", player.getUniqueId().toString());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            if (isRewardMessage()) {
                player.sendMessage(CC.translate("&aYou have just been rewarded the " + CC.translate(this.rewardName) + "&a!"));
            }
        }
    }

}
