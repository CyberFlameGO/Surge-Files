package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.Formats;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class OreChallenge extends Challenge {

    private OreType oreType;
    private int amount;

    public OreChallenge(String id, String name, int experience, boolean daily, OreType oreType, int amount) {
        super(id, name, experience, daily);

        this.oreType = oreType;
        this.amount = amount;
    }

    @Override
    public Type getAbstractType() {
        return OreChallenge.class;
    }

    @Override
    public String getText() {
        return "Mine " + amount + " " + oreType.plural;
    }

    @Override
    public boolean hasStarted(Player player) {
        return oreType.getCount(player) > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return oreType.getCount(player) >= amount;
    }

    @Override
    public String getProgressText(Player player) {
        int count = oreType.getCount(player);
        int remaining = amount - count;
        return CC.GRAY + "You need to mine " + Formats.formatNumber(remaining) + " more " + (remaining == 1 ? oreType.rendered : oreType.plural) + CC.GRAY + ".";
    }

    @AllArgsConstructor
    public enum OreType {
        COAL(CC.DARK_GRAY + "coal", CC.DARK_GRAY + "coal"),
        IRON(CC.GRAY + "iron", CC.GRAY + "iron"),
        GOLD(CC.GOLD + "gold", CC.GOLD + "gold"),
        EMERALD(CC.GREEN + "emerald", CC.GREEN + "emerald"),
        DIAMOND(CC.AQUA + "diamond", CC.AQUA + "diamond"),
        REDSTONE(CC.RED + "redstone", CC.RED + "redstone"),
        LAPIS(CC.BLUE + "lapis", CC.BLUE + "lapis");

        private String rendered;
        private String plural;

        public int getCount(Player player) {
            switch (this) {
                case COAL: return Samurai.getInstance().getCoalMinedMap().getMined(player.getUniqueId());
                case IRON: return Samurai.getInstance().getIronMinedMap().getMined(player.getUniqueId());
                case GOLD: return Samurai.getInstance().getGoldMinedMap().getMined(player.getUniqueId());
                case EMERALD: return Samurai.getInstance().getEmeraldMinedMap().getMined(player.getUniqueId());
                case DIAMOND: return Samurai.getInstance().getDiamondMinedMap().getMined(player.getUniqueId());
                case REDSTONE: return Samurai.getInstance().getRedstoneMinedMap().getMined(player.getUniqueId());
                case LAPIS: return Samurai.getInstance().getLapisMinedMap().getMined(player.getUniqueId());
            }
            return 0;
        }
    }

}
