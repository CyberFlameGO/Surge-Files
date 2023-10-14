package dev.lbuddyboy.samurai.chat.chatgames.type;

import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.chat.chatgames.ChatGame;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 5:25 PM
 * HCTeams / rip.orbit.hcteams.chatgames.type
 */
public class ChatMath extends ChatGame {

    private Equation pickedEquation = null;
    private double tickedTime;
    private final List<Equation> equations = new ArrayList<>();

    public ChatMath(File file, String name) {
        super(file, name);
        for (int i = 0; i < 50; i++) {
            int first = new Random().nextInt(150);
            int second = new Random().nextInt(200);
            if (i <= 15) {
                equations.add(new Equation(first, second, "*"));
            } else if (i <= 30) {
                equations.add(new Equation(first, second, "+"));
            } else if (i <= 40) {
                equations.add(new Equation(first, second, "-"));
            } else if (i <= 48) {
                equations.add(new Equation(first, second, "/"));
            }
        }
    }

    @Override
    public String name() {
        return "Math Game";
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
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 10, 10);

        this.pickedEquation = equations.get((new Random().nextInt(equations.size())) - 1);

        List<String> format = CC.translate(this.config.getStringList("start-message"),
                "%first-number%", this.pickedEquation.firstNumber(),
                "%second-number%", this.pickedEquation.secondNumber(),
                "%equation-type%", this.pickedEquation.equationType()
        );

        format.forEach(Bukkit::broadcastMessage);

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

        Bukkit.broadcastMessage(CC.translate("&cNobody answered the equation in time."));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.started) {
            if (this.pickedEquation != null) {
                try {
                    if (this.pickedEquation.getTotal() == Integer.parseInt(event.getMessage())) {
                        this.started = false;
                        event.setCancelled(true);

                        List<String> winMessage = CC.translate(this.config.getStringList("solved-message"),
                                "%winner%", event.getPlayer().getDisplayName(),
                                "%answer%", this.pickedEquation.getTotal(),
                                "%solve-time%", Team.DTR_FORMAT2.format(tickedTime)
                        );

                        winMessage.forEach(s -> {
                            Bukkit.broadcastMessage(CC.translate(s));
                        });

                        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                            this.config.getStringList("win-commands").forEach(s -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", event.getPlayer().getName()));
                            });
                        });

                    }
                } catch (NumberFormatException ignored) {

                }
            }
        }
    }

    public record Equation(double firstNumber, double secondNumber, String equationType) {

        public double getTotal() {
            if (this.equationType.equalsIgnoreCase("*")) {
                return firstNumber * secondNumber;
            } else if (this.equationType.equalsIgnoreCase("+")) {
                return firstNumber + secondNumber;
            } else if (this.equationType.equalsIgnoreCase("/")) {
                return firstNumber / secondNumber;
            } else if (this.equationType.equalsIgnoreCase("-")) {
                return firstNumber - secondNumber;
            } else {
                return firstNumber * secondNumber;
            }
        }

    }

}
