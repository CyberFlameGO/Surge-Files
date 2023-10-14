package dev.lbuddyboy.lqueue.distributed.config;

import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Getter
public class Config {

    private final File file;

    private String[] hubs;
    private String[] queues;
    private String redisHost;
    private int redisPort, redisDatabase;
    private double delay;
    private String redisPassword, redisChannel;

    public Config() {
        this.file = new File("config.properties");
        this.loadDefaults();

        Properties prop = new Properties();
        InputStream input;

        try {
            input = Files.newInputStream(Paths.get("config.properties"));
            prop.load(input);
            this.hubs = ((String)prop.getOrDefault("hubs", "")).split(",");
            this.queues = ((String)prop.getOrDefault("queues", "")).split(",");
            this.redisHost = (String)prop.getOrDefault("redis-host", "127.0.0.1");
            this.redisPort = Integer.parseInt((String)prop.getOrDefault("redis-port", "6379"));
            this.redisDatabase = Integer.parseInt((String)prop.getOrDefault("redis-database", "0"));
            this.redisPassword = (String)prop.getOrDefault("redis-password", "");
            this.redisChannel = (String)prop.getOrDefault("redis-channel", "lQueue|Global");
            this.delay = Double.parseDouble((String)prop.getOrDefault("send-delay", "0.5"));
            this.delay = (int)(this.getDelay() * 1000.0);

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDefaults() {
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream output = new FileOutputStream(file);
                output.write("hubs=hub-01\n".getBytes());
                output.write("queues=test1,test2,test3\n".getBytes());
                output.write("redis-host=127.0.0.1\n".getBytes());
                output.write("redis-port=6379\n".getBytes());
                output.write("redis-database=0\n".getBytes());
                output.write("redis-password=\n".getBytes());
                output.write("redis-channel=lQueue|Global\n".getBytes());
                output.write("send-delay=0.5\n".getBytes());
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
