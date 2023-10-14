package dev.lbuddyboy.samurai.settings;

import dev.lbuddyboy.samurai.commands.ToggleChatCommand;
import dev.lbuddyboy.samurai.settings.menu.button.SettingButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum Setting {

    PUBLIC_CHAT(
            ChatColor.LIGHT_PURPLE + "Public Chat",
            ChatColor.GRAY + "Do you want to see public chat messages?",
            Material.LEGACY_SIGN,
            ChatColor.GREEN + "Show public chat",
            ChatColor.RED + "Hide public chat",
            true
    ) {

        @Override
        public void toggle(Player player) {
            ToggleChatCommand.toggleChat(player);
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId());
        }

    },
    POWER(
            ChatColor.LIGHT_PURPLE + "Power Cooldown",
            ChatColor.GRAY + "Do you want to be able to use your special power cooldown on the sidebar?",
            Material.BELL,
            ChatColor.GREEN + "Enable power cooldown",
            ChatColor.RED + "Disable power cooldown",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getPowerMap().isToggled(player.getUniqueId());

            Samurai.getInstance().getPowerMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to view your pet cooldown on the sidebar.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getPowerMap().isToggled(player.getUniqueId());
        }

    },
    REDEEM(
            ChatColor.LIGHT_PURPLE + "Redeem Broadcasts in Chat",
            ChatColor.GRAY + "Do you want to see /redeem broadcasts in chat?",
            Material.PINK_BANNER,
            ChatColor.GREEN + "Show /redeem broadcasts in chat",
            ChatColor.RED + "Hide /redeem broadcasts in chat",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getRedeemBCToggleMap().isToggled(player.getUniqueId());

            Samurai.getInstance().getRedeemBCToggleMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see /redeem broadcasts in chat.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getRedeemBCToggleMap().isToggled(player.getUniqueId());
        }

    },
    CLAIM_ENTER(
            ChatColor.LIGHT_PURPLE + "Claim Enter/Leave Messages in Chat",
            ChatColor.GRAY + "Do you want to see claim enter/leave messages in chat?",
            Material.WOODEN_HOE,
            ChatColor.GREEN + "Show claim enter/leave messages in chat",
            ChatColor.RED + "Hide claim enter/leave messages in chat",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(player.getUniqueId());

            Samurai.getInstance().getToggleClaimMessageMap().setClaimMessagesEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see claim enter/leave messages in chat.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(player.getUniqueId());
        }

    },
    BATTLE_PASS_TITLE(
            ChatColor.LIGHT_PURPLE + "Battle Pass Challenge Complete on Your Screen",
            ChatColor.GRAY + "Do you want to see challenge completions on your screen?",
            Material.NAME_TAG,
            ChatColor.GREEN + "Show challenge completions on your screen",
            ChatColor.RED + "Hide challenge completions on your screen",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getBattlePassTitleMap().isToggled(player.getUniqueId());

            Samurai.getInstance().getBattlePassTitleMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see challenge completions on your screen.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getBattlePassTitleMap().isToggled(player.getUniqueId());
        }

    },
    SUPPLYCRATE_SB(
            ChatColor.LIGHT_PURPLE + "SupplyCrate Info on Sidebar",
            ChatColor.GRAY + "Do you want to see active supply crate info on the sidebar?",
            Material.CHEST,
            ChatColor.GREEN + "Show active supply crate info on the sidebar",
            ChatColor.RED + "Hide active supply crate info on the sidebar",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getSupplyCrateSBMap().isToggled(player.getUniqueId());

            Samurai.getInstance().getSupplyCrateSBMap().setToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see active supply crate info on the sidebar.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getSupplyCrateSBMap().isToggled(player.getUniqueId());
        }

    },

    DISPLAY_CLAIM_ON_SCOREBOARD(
            ChatColor.LIGHT_PURPLE + "Display Claim On Scoreboard",
            ChatColor.GRAY + "Do you want to see the claim you're in on your scoreboard?",
            Material.COMPASS,
            ChatColor.GREEN + "Show claim",
            ChatColor.RED + "Hide claim",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getToggleClaimDisplayMap().isClaimDisplayEnabled(player.getUniqueId());

            Samurai.getInstance().getToggleClaimDisplayMap().setClaimDisplayEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "seeing" : ChatColor.RED + "hiding") + ChatColor.YELLOW + " the claim scoreboard display.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getToggleClaimDisplayMap().isClaimDisplayEnabled(player.getUniqueId());
        }

    },
    DISPLAY_FOCUS_ON_SCOREBOARD(
            ChatColor.LIGHT_PURPLE + "Display Focus On Scoreboard",
            ChatColor.GRAY + "Do you want to see information about the focused faction?",
            Material.CLOCK,
            ChatColor.GREEN + "Show information",
            ChatColor.RED + "Hide information",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getToggleFocusDisplayMap().isFocusDisplayEnabled(player.getUniqueId());

            Samurai.getInstance().getToggleFocusDisplayMap().setFocusDisplayEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "seeing" : ChatColor.RED + "hiding") + ChatColor.YELLOW + " focus information.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getToggleFocusDisplayMap().isFocusDisplayEnabled(player.getUniqueId());
        }

    },
    FOUND_DIAMONDS(
            ChatColor.LIGHT_PURPLE + "Found Diamonds",
            ChatColor.GRAY + "Do you want to see found-diamonds messages?",
            Material.DIAMOND_ORE,
            ChatColor.GREEN + "Show messages",
            ChatColor.RED + "Hide messages",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId());

            Samurai.getInstance().getToggleFoundDiamondsMap().setFoundDiamondToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see found diamond messages.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId());
        }

    },

    DEATH_MESSAGES(
            ChatColor.LIGHT_PURPLE + "Death Messages",
            ChatColor.GRAY + "Do you want to see death messages?",
            Material.PLAYER_HEAD,
            ChatColor.GREEN + "Show messages",
            ChatColor.RED + "Hide messages",
            true
    ) {
        @Override
        public void toggle(Player player) {
            boolean value = !Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());

            Samurai.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see death messages.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());
        }
    };

    @Getter private String name;
    @Getter private String description;
    @Getter private Material icon;
    @Getter private String enabledText;
    @Getter private String disabledText;
    private boolean defaultValue;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return (defaultValue);
    }

    public SettingButton toButton() {
        return new SettingButton(this);
    }

    public abstract void toggle(Player player);

    public abstract boolean isEnabled(Player player);

}
