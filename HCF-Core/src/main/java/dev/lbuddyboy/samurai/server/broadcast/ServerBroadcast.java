package dev.lbuddyboy.samurai.server.broadcast;

import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;

/**
 * Created by vape on 11/4/2020 at 8:45 PM.
 */
@Getter
public class ServerBroadcast {
    private final Type type;
    private final String[] message;

    public ServerBroadcast(Type type, String[] message) {
        this.type = type;
        this.message = message;

        for (int i = 0; i < message.length; i++) {
            if (i == 0) {
                message[i] = CC.translate("&g&l" + message[i]);
            } else if (i == message.length - 1) {
                message[i] = CC.translate("&g&n" + message[i]);
            } else {
                message[i] = CC.WHITE + message[i];
            }
        }
    }

    public boolean isActive() {
        boolean kits = Samurai.getInstance().getMapHandler().isKitMap();
        return type == Type.BOTH || (type == Type.KITS_ONLY && kits || type == Type.HCF_ONLY && !kits);
    }

    public enum Type {
        HCF_ONLY,
        KITS_ONLY,
        BOTH
    }
}