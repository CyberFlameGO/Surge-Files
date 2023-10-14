package dev.lbuddyboy.samurai;

import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum MessageConfiguration {

    SOTW_AUTO_MESSAGES("sotw-auto-messages", Arrays.asList(
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: THERE IS CURRENTLY A 65% OFF SALE! &cPURCHASE MYSTERY BOXES THEY ARE CURRENTLY 5X'ED! https://store.steelpvp.com",
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: THERE IS CURRENTLY A 65% OFF SALE! &cPURCHASE OUR NEW BACK TO SCHOOL MYSTERY BOXES! NEXT TO BUY THEM GETS 5X THE PURCHASED AMOUNT! https://store.steelpvp.com",
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: THERE IS CURRENTLY A 65% OFF SALE! &cPURCHASE LOOT BOXES NEXT TO BUY THEM GETS 5X THE PURCHASED AMOUNT! https://store.steelpvp.com",
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: THERE IS CURRENTLY A 65% OFF SALE! &cPURCHASE AIRDROPS THEY ARE CURRENTLY DOUBLED! https://store.steelpvp.com",
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: THERE IS CURRENTLY A 65% OFF SALE! &cPURCHASE GAMBLE KEYS (WIN RANKS, KITS, & MYSTERY BOXES)! https://store.steelpvp.com",
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: THERE IS CURRENTLY A 65% OFF SALE! &cPURCHASE ANY OF OUR KEYS OR ABILITY PACKAGES (THEY ARE TRIPLED)! https://store.steelpvp.com",
            "&7[&4&lOWNER&7] &4LBuddyBoy&7: CHECK /schedule TO SEE WHENEVER THERE ARE KEY ALLS!"
    )),
    SOTW_AUTO_MESSAGES_DELAY("sotw-auto-messages-times-spam", 3),
    EFFECT_STEALER_ATTACKER("ability-messages.effect-stealer.attacker", Arrays.asList(
            "&6» &fSuccessfully received %target%'s &feffects!",
            "&6» &fYou have received all of their effects for &615 seconds&f!"
    )),
    ANTI_BARD_ATTACKER("ability-messages.anti-bard.attacker", Arrays.asList(
            "&6» &fSuccessfully used a %ability-name%&f!",
            "&6» &f%target%'s bard effects will no longer apply!"
    )),
    ANTI_BARD_TARGET("ability-messages.anti-bard.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with a %ability-name%&f!",
            "&6» &fBard effects will no longer apply!"
    )),
    RAGE_BALL_ATTACKER("ability-messages.rage-ball.attacker", Arrays.asList(
            "&6» &fSuccessfully used a %ability-name%&f!",
            "&6» &fYou now have strength 2 & resistance 3!"
    )),
    RAGE_BALL_TARGET("ability-messages.rage-ball.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with a %ability-name%&f!",
            "&6» &fYou now have wither effects!"
    )),
    TIME_WARP_CLICKER("ability-messages.timewarp-two.clicker", Arrays.asList(
            "&6» &fSuccessfully used a %ability-name%&f!",
            "&6» &fYou are being teleported to where your last enderpearl has landed!"
    )),
    TIME_WARP_CLICKER_TPED("ability-messages.timewarp-two.teleported", Arrays.asList(
            "&6» &fSuccessfully used a %ability-name%&f!",
            "&6» &fYou have been teleported with your time warp!"
    )),
    ROCKET_CLICKER("ability-messages.rocket.clicker", Arrays.asList(
            "&6» &fSuccessfully used a %ability-name%&f!",
            "&6» &fYou are now launched!"
    )),
    SUPPORT_GOATS_CLICKER("ability-messages.support-goats.clicker", Arrays.asList(
            "&6» &fSuccessfully hit &6%target%&f with an %ability-name%&f!",
            "&6» &fYou now have 3 goats giving you bard effects!"
    )),
    SWITCHER_ATTACKER("ability-messages.switcher.attacker", Arrays.asList(
            "&6» &fSuccessfully hit &6%target%&f with an %ability-name%&f!",
            "&6» &fYou have swapped locations with them!"
    )),
    SWITCHER_TARGET("ability-messages.switcher.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with a %ability-name%&f!",
            "&6» &fYou both have swapped locations!"
    )),
    LEVITATOR_ATTACKER("ability-messages.levitator.attacker", Arrays.asList(
            "&6» &fSuccessfully hit &6%target%&f with a %ability-name%&f!",
            "&6» &fThey are now going to the moon!"
    )),
    LEVITATOR_TARGET("ability-messages.levitator.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with a %ability-name%&f!",
            "&6» &fYou will have levitation for 6 seconds!"
    )),
    ANTI_BUILD_STICK_CANT_BUILD("ability-messages.anti-build-stick.cant-build", Arrays.asList(
            "&6» &cYou cannot build for another %remaining%!"
    )),
    ANTI_BUILD_STICK_ATTACKER("ability-messages.anti-build-stick.attacker", Arrays.asList(
            "&6» &fSuccessfully hit &6%target%&f with an %ability-name%&f!",
            "&6» &fThat player can no longer interact with blocks!"
    )),
    ANTI_BUILD_STICK_TARGET("ability-messages.anti-build-stick.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with an %ability-name%&f!",
            "&6» &fYou may not interact with blocks for 15 seconds!"
    )),
    FOCUS_MODE_ATTACKER("ability-messages.focu-mode.attacker", Arrays.asList(
            "&6» &fSuccessfully hit &6%target%&f with an %ability-name%&f!",
            "&6» &fYou will now receive a 20% damage buff!"
    )),
    FOCUS_MODE_TARGET("ability-messages.focus-mode.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with an %ability-name%&f!",
            "&6» &fWatch your hearts!"
    )),
    EXOTIC_BONE_CANT_BUILD("ability-messages.exotic-bone.cant-build", Arrays.asList(
            "&6» &cYou cannot build for another %remaining%!"
    )),
    EXOTIC_BONE_ATTACKER("ability-messages.exotic-bone.attacker", Arrays.asList(
            "&6» &fSuccessfully hit &6%target%&f with an %ability-name%&f!",
            "&6» &fThat player can no longer interact with blocks!"
    )),
    EXOTIC_BONE_TARGET("ability-messages.exotic-bone.target", Arrays.asList(
            "&6» &6%attacker% &fhas hit you with an %ability-name%&f!",
            "&6» You may not interact with blocks for 15 seconds!"
    )),
    CRAWLER_CLICKER("ability-messages.crawler.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!"
    )),
    KIT_DISABLER_CLICKER("ability-messages.kit-disabler.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!",
            "&6» &fIt affect %amount% players!"
    )),
    HULK_SMASH_CLICKER("ability-messages.guardian-angel.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!",
            "&6» &fAny critical landed from your air smashes will be!",
            "&6» &fmultiplied by 2.50x!"
    )),
    HULK_SMASH_EXPIRED("ability-messages.hulk-smash.expired", Arrays.asList(
            "&6» &cYour %ability-name%&c ability has expired!"
    )),
    LEVITATOR_TWO_CLICKER("ability-messages.levitator-two.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!",
            "&6» &fYour hits will now have a 50% chance to levitate a player!"
    )),
    LEVITATOR_TWO_PROCED("ability-messages.levitator-two.proced", Arrays.asList(
            "&6» &fYour levitator ability has just proced!"
    )),
    RAGE_MODE_CLICKER("ability-messages.rage-mode.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!",
            "&6» &fEvery hit for 10 seconds will be a second of strength 2!"
    )),
    RAGE_MODE_PROCED("ability-messages.rage-mode.proced", Arrays.asList(
            "&6» &fYour rage mode's strength seconds will now be %hits%!"
    )),
    GUARDIAN_ANGEL_CLICKER("ability-messages.guardian-angel.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!",
            "&6» &fYou are now full health!"
    )),
    INVISIBILITY_CLICKER("ability-messages.invisibility.clicker", Arrays.asList(
            "&6» &fYou have activated the %ability-name%&f ability!",
            "&6» &fYou are now fully invisible!"
    )),
    NINJA_STAR_ACTIVATED_CLICKER("ability-messages.ninja-star.clicker", Arrays.asList(
            "&6» &fYou have activated %ability-name%!",
            "&6» &fYou will be teleported to &e%target%&f in &e3 seconds&f."
    )),
    NINJA_STAR_ACTIVATED_TARGET("ability-messages.ninja-star.target", Arrays.asList(
            "&6» &fA %ability-name% &fhas just been used on you!",
            "&6» &e%clicker%&f will be teleported to you in &e3 seconds&f."
    ));

    private final String path;
    private final Object value;

    public String getString(Object... objects) {
        if (Samurai.getInstance().getMessageConfig().contains(this.path))
            return CC.translate(Samurai.getInstance().getMessageConfig().getString(this.path), objects);

        loadDefault();

        return CC.translate(String.valueOf(this.value), objects);
    }

    public boolean getBoolean() {
        if (Samurai.getInstance().getMessageConfig().contains(this.path))
            return Samurai.getInstance().getMessageConfig().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public void sendListMessage(Player player, Object... objects) {
        List<Object> os = new ArrayList<>(Arrays.asList(objects));
        os.addAll(Arrays.asList(PLAYER_PLACEHOLDERS(player)));

        for (String s : getStringList(os.toArray(new Object[0]))) {
            player.sendMessage(CC.translate(s));
        }
    }

    public ChatColor getChatColor() {
        return ChatColor.valueOf(getString());
    }

    public Location getLocation() {
        return LocationSerializer.deserializeString(getString());
    }

    public int getInt() {
        if (Samurai.getInstance().getMessageConfig().contains(this.path))
            return Samurai.getInstance().getMessageConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (Samurai.getInstance().getMessageConfig().contains(this.path))
            return CC.translate(Samurai.getInstance().getMessageConfig().getStringList(this.path), objects);

        loadDefault();

        return CC.translate((List<String>) this.value, objects);
    }

    public void update(Object value) {
        Samurai.getInstance().getMessageConfig().set(this.path, value);
        Samurai.getInstance().getMessageConfig().save();
    }

    public void loadDefault() {
        if (Samurai.getInstance().getMessageConfig().contains(this.path)) return;

        Samurai.getInstance().getMessageConfig().set(this.path, this.value);
        Samurai.getInstance().getMessageConfig().save();
    }

    public static Object[] PLAYER_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName()
        };
    }

}
