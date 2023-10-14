package dev.lbuddyboy.lqueue.distributed.thread;

import dev.lbuddyboy.lqueue.distributed.lQueueDistributed;

public class RedisClearThread extends Thread {

    @Override
    public void run() {
        while (lQueueDistributed.getInstance().threads) {
            try {
                System.gc();

                Thread.sleep(60000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
