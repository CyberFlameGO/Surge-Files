package dev.lbuddyboy.bot.thread;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;

public class BotThread extends Thread {

    @Override
    public void run() {
        while (Bot.isEnabled()) {

            Bot.getInstance().getHandlers().forEach(IHandler::save);
            System.out.println("Saving all concurrent data.");

            try {
                Thread.sleep(30_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
