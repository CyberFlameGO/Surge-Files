package dev.lbuddyboy.samurai.custom.battlepass.challenge.menu;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.*;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.object.IntRange;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.BattlePassProgress;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitActiveKOTHChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitEndChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitGlowstoneMountain;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitNetherChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.menu.BattlePassMenu;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class ChallengesMenu extends Menu {

    private int[] SLOTS_PREMIUM = {11, 20, 29, 38, 47, 15, 24, 33, 42, 51};

    private int page = 1;

    private final boolean daily;
    private final BattlePassProgress progress;

    @Override
    public String getTitle(Player player) {
        return (daily ? "Daily" : "Premium") + " Challenges";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Collection<Challenge> challenges;
        if (daily) {
            challenges = Samurai.getInstance().getBattlePassHandler().getDailyChallenges().getChallenges().stream().sorted(CHALLENGE_COMPARATOR).collect(Collectors.toList());
        } else {
            challenges = Samurai.getInstance().getBattlePassHandler().getChallenges().stream().sorted(CHALLENGE_COMPARATOR).collect(Collectors.toList());
        }

        int[] ints;
        if (page == 1) {
            ints = IntStream.rangeClosed(1, 10).toArray();
        } else {
            ints = IntStream.rangeClosed(((page - 1) * 10) + 1, page * 10).toArray();
        }

        int skipped = 0;
        int slotIndex = 0;
        for (Challenge challenge : challenges) {
            if (skipped < ints[0]) {
                skipped++;
                continue;
            }

            if (slotIndex >= 5) {
                buttons.put(SLOTS_PREMIUM[slotIndex], new ChallengeButton(challenge));
                buttons.put(SLOTS_PREMIUM[slotIndex] - 1, new ChallengeStatusButton(challenge));
            } else {
                buttons.put(SLOTS_PREMIUM[slotIndex], new ChallengeButton(challenge));
                buttons.put(SLOTS_PREMIUM[slotIndex] + 1, new ChallengeStatusButton(challenge));
            }

            if (slotIndex >= 9) {
                break;
            } else {
                slotIndex++;
            }
        }

        buttons.put(4, new InfoButton());
        buttons.put(31, new BackButton());

        buttons.put(13, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, ""));
        buttons.put(22, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, ""));
        buttons.put(40, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, ""));
        buttons.put(49, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, ""));

        buttons.put(28, new PreviousPageButton());
        buttons.put(34, new NextPageButton());

        for (int i = 0; i < 54; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        return buttons;
    }

    private int getMaxPages() {
        Collection<Challenge> challenges;
        if (daily) {
            challenges = Samurai.getInstance().getBattlePassHandler().getDailyChallenges().getChallenges();
        } else {
            challenges = Samurai.getInstance().getBattlePassHandler().getChallenges();
        }

        if (challenges.size() == 0) {
            return 1;
        } else {
            return (int) Math.ceil(challenges.size() / (double) 10);
        }
    }

    private class InfoButton extends Button {
        @Override
        public String getName(Player player) {
            return CC.GOLD + CC.BOLD + (daily ? "Daily" : "Premium") + " Challenges";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            if (daily) {
                return Material.GOLD_INGOT;
            } else {
                return Material.DIAMOND;
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            String url = daily ? "b86b9d58bcd1a555f93e7d8659159cfd25b8dd6e9bce1e973822824291862" : "acd70ce4818581ca47adf6b81679fd1646fd687c7127fdaae94fed640155e";

            return CC.getCustomHead(getName(player), 1, url, getDescription(player));
        }
    }

    @AllArgsConstructor
    private class ChallengeButton extends Button {
        private Challenge challenge;

        @Override
        public String getName(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return CC.GREEN + CC.BOLD + challenge.getName();
            } else if (challenge.hasStarted(player)) {
                return CC.YELLOW + CC.BOLD + challenge.getName();
            } else {
                return CC.RED + CC.BOLD + challenge.getName();
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.singletonList(CC.GRAY + challenge.getText());
        }

        @Override
        public Material getMaterial(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return Material.BOOK;
            } else {
                return Material.ENCHANTED_BOOK;
            }
        }
    }

    @AllArgsConstructor
    private class ChallengeStatusButton extends Button {
        private Challenge challenge;

        @Override
        public String getName(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return CC.GREEN + CC.BOLD + "Finished";
            } else if (challenge.hasStarted(player)) {
                return CC.YELLOW + CC.BOLD + "In Progress";
            } else {
                return CC.RED + CC.BOLD + "No Progress";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return Collections.singletonList(CC.GRAY + "You've completed this challenge!");
            } else {
                String progressText = challenge.getProgressText(player);
                if (progressText != null) {
                    return Collections.singletonList(CC.GRAY + progressText);
                } else {
                    return Collections.emptyList();
                }
            }
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.LEGACY_STAINED_GLASS_PANE;
        }

        @Override
        public byte getDamageValue(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return 5;
            } else if (challenge.hasStarted(player)) {
                return 4;
            } else {
                return 14;
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return CC.getCustomHead(getName(player), 1, "b8fff22c6e6546d0d8eb7f9763398407dd2ab80f74fe3d16b10a983ecaf347e", getDescription(player));
            } else if (challenge.hasStarted(player)) {
                return CC.getCustomHead(getName(player), 1, "c641682f43606c5c9ad26bc7ea8a30ee47547c9dfd3c6cda49e1c1a2816cf0ba", getDescription(player));
            } else {
                return CC.getCustomHead(getName(player), 1, "5fde3bfce2d8cb724de8556e5ec21b7f15f584684ab785214add164be7624b", getDescription(player));
            }
        }
    }

    private class BackButton extends Button {
        @Override
        public String getName(Player player) {
            return CC.RED + CC.BOLD + "Go Back";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.ACACIA_SIGN;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (progress != null) {
                new BattlePassMenu(progress).openMenu(player);
            } else {
                BattlePassProgress progress = Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
                new BattlePassMenu(progress).openMenu(player);
            }
        }
    }

    private class PreviousPageButton extends Button {
        @Override
        public String getName(Player player) {
            if (page > 1) {
                return CC.RED + CC.BOLD + "Previous Page";
            } else {
                return CC.GRAY + CC.BOLD + "No Previous Page";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.BARRIER;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return (page > 1 ? CC.getCustomHead(getName(player), 1, "93971124be89ac7dc9c929fe9b6efa7a07ce37ce1da2df691bf8663467477c7") : super.getButtonItem(player));
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick() && page > 1) {
                page -= 1;
                openMenu(player);
            }
        }
    }

    private class NextPageButton extends Button {
        @Override
        public String getName(Player player) {
            if (page < getMaxPages()) {
                return CC.RED + CC.BOLD + "Next Page";
            } else {
                return CC.GRAY + CC.BOLD + "No Next Page";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.BARRIER;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return (page < getMaxPages() ? CC.getCustomHead(getName(player), 1, "2671c4c04337c38a5c7f31a5c751f991e96c03df730cdbee99320655c19d") : super.getButtonItem(player));
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick() && page < getMaxPages()) {
                page += 1;
                openMenu(player);
            }
        }
    }

    private static Comparator<Challenge> CHALLENGE_COMPARATOR = new Comparator<Challenge>() {
        @Override
        public int compare(Challenge o1, Challenge o2) {
            if (o1.getClass() == o2.getClass()) {
                if (o1 instanceof PlayTimeChallenge) {
                    return (int) ((PlayTimeChallenge) o1).getPlayTime() - (int) ((PlayTimeChallenge) o2).getPlayTime();
                }

                if (o1 instanceof OreChallenge) {
                    return ((OreChallenge) o1).getAmount() - ((OreChallenge) o2).getAmount();
                }

                if (o1 instanceof MineBlockChallenge) {
                    return ((MineBlockChallenge) o1).getAmount() - ((MineBlockChallenge) o2).getAmount();
                }

                if (o1 instanceof KillEntityChallenge) {
                    return ((KillEntityChallenge) o1).getKills() - ((KillEntityChallenge) o2).getKills();
                }

                if (o1 instanceof UsePartnerItemChallenge) {
                    return ((UsePartnerItemChallenge) o1).getUses() - ((UsePartnerItemChallenge) o2).getUses();
                }
            }

            return PRIORITIES.get(o1.getClass()) - PRIORITIES.get(o2.getClass());
        }
    };

    private static Map<Class<?>, Integer> PRIORITIES = new HashMap<>();

    static {
        PRIORITIES.put(ValuablesSoldChallenge.class, 0);
        PRIORITIES.put(MineBlockChallenge.class, 1);
        PRIORITIES.put(MineLogChallenge.class, 1);
        PRIORITIES.put(KillEntityChallenge.class, 2);
        PRIORITIES.put(PlayTimeChallenge.class, 4);
        PRIORITIES.put(OreChallenge.class, 5);
        PRIORITIES.put(AttemptCaptureKOTHChallenge.class, 6);
        PRIORITIES.put(VisitActiveKOTHChallenge.class, 7);
        PRIORITIES.put(VisitNetherChallenge.class, 8);
        PRIORITIES.put(VisitEndChallenge.class, 9);
        PRIORITIES.put(VisitGlowstoneMountain.class, 10);
        PRIORITIES.put(ArcherTagsChallenge.class, 11);
        PRIORITIES.put(KillstreakChallenge.class, 12);
        PRIORITIES.put(MakeFactionRaidableChallenge.class, 13);
        PRIORITIES.put(MakeGemShopPurchaseChallenge.class, 14);
        PRIORITIES.put(UsePartnerItemChallenge.class, 15);
        PRIORITIES.put(UsePetCandyChallenge.class, 16);
        PRIORITIES.put(UsePetChallenge.class, 17);
        PRIORITIES.put(LevelUpPetChallenge.class, 18);
    }

}
