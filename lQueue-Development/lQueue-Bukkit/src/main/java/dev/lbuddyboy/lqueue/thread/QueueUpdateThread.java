package dev.lbuddyboy.lqueue.thread;

import dev.lbuddyboy.lqueue.lQueue;

public class QueueUpdateThread extends Thread {

    @Override
    public void run() {
        while (lQueue.getInstance().isEnabled()) {
            try {
                lQueue.getInstance().updateLocalQueue();
                Thread.sleep(15_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
