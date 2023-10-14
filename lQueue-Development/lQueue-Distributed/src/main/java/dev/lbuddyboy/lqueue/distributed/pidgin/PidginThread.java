package dev.lbuddyboy.lqueue.distributed.pidgin;

import lombok.AllArgsConstructor;
import redis.clients.jedis.Jedis;

@AllArgsConstructor
public class PidginThread extends Thread {

    private PidginHandler pidginHandler;

    @Override
    public void run() {
        while (this.pidginHandler.getPool() != null && !this.pidginHandler.getPool().isClosed()) {
            try (Jedis jedis = this.pidginHandler.getPool().getResource()) {
                try {
                    jedis.subscribe(this.pidginHandler.getPubSub(), this.pidginHandler.getChannel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
