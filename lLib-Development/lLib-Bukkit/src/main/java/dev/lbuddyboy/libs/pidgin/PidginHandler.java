//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.libs.pidgin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lbuddyboy.libs.pidgin.packet.Packet;
import dev.lbuddyboy.libs.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.libs.pidgin.packet.handler.PacketExceptionHandler;
import dev.lbuddyboy.libs.pidgin.packet.listener.PacketListener;
import dev.lbuddyboy.libs.pidgin.packet.listener.PacketListenerData;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Getter
public class PidginHandler {

    @Getter public static JsonParser PARSER = new JsonParser();
    private final String channel;
    private final JedisPool pool;
    private final PidginPubSub pubSub;
    private final List<PacketListenerData> listeners;
    private final Map<Integer, Class<? extends Packet>> idToType;
    private final Map<Class<? extends Packet>, Integer> typeToId;

    public PidginHandler(String channel, JedisPool jedisPool) {
        this.channel = channel;
        this.listeners = new ArrayList<>();
        this.idToType = new HashMap<>();
        this.typeToId = new HashMap<>();
        this.pool = jedisPool;
        this.pubSub = new PidginPubSub(this, channel);
        ForkJoinPool.commonPool().execute(() -> {
            Jedis jedis = this.pool.getResource();

            try {
                jedis.subscribe(this.pubSub, channel);
            } catch (Throwable var6) {
                if (jedis != null) {
                    try {
                        jedis.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (jedis != null) {
                jedis.close();
            }

        });
    }

    public void sendPacket(Packet packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet packet, PacketExceptionHandler exceptionHandler) {
        try {
            JsonObject object = packet.serialize();
            if (object == null) {
                throw new IllegalStateException("Packet cannot generate null serialized data");
            }

            Jedis jedis = this.pool.getResource();

            try {
                jedis.publish(this.channel, packet.id() + ";" + object);
            } catch (Throwable var8) {
                if (jedis != null) {
                    try {
                        jedis.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            jedis.close();
        } catch (Exception var9) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(var9);
            }
        }

    }

    public Packet buildPacket(int id) {
        if (!this.idToType.containsKey(id)) {
            throw new IllegalStateException("A packet with that ID does not exist");
        } else {
            try {
                return (Packet)((Class<?>)this.idToType.get(id)).newInstance();
            } catch (Exception var3) {
                var3.printStackTrace();
                throw new IllegalStateException("Could not create new instance of packet type");
            }
        }
    }

    public void registerPacket(Class<? extends Packet> clazz) {
        try {
            int id = (Integer)clazz.getDeclaredMethod("id").invoke(clazz.newInstance(), (Object[])null);
            if (this.idToType.containsKey(id) || this.typeToId.containsKey(clazz)) {
                throw new IllegalStateException("A packet with that ID has already been registered");
            }

            this.idToType.put(id, clazz);
            this.typeToId.put(clazz, id);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void registerListener(PacketListener packetListener) {
        Method[] var2 = packetListener.getClass().getDeclaredMethods();

        for (Method method : var2) {
            if (method.getDeclaredAnnotation(IncomingPacketHandler.class) != null) {
                Class<?> packetClass = null;
                if (method.getParameters().length > 0) {
                    if (!Packet.class.isAssignableFrom(method.getParameters()[0].getType())) {
                        continue;
                    }

                    packetClass = method.getParameters()[0].getType();
                }

                if (packetClass != null) {
                    this.listeners.add(new PacketListenerData(packetListener, method, packetClass));
                }
            }
        }

    }
}
