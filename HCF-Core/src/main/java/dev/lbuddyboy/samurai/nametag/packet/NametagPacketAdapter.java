package dev.lbuddyboy.samurai.nametag.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.lbuddyboy.samurai.Samurai;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.plugin.Plugin;

public class NametagPacketAdapter extends PacketAdapter {

    public NametagPacketAdapter() {
        super(Samurai.getInstance(), PacketType.Play.Server.SCOREBOARD_TEAM);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer container = event.getPacket();
        String teamName = container.getStrings().read(0);
        byte mode = container.getBytes().read(0);

        if (mode == 0 || mode == 2) {
            byte friendlyFlags = container.getBytes().read(1);
            String[] members = container.getStringArrays().read(0);
            WrappedChatComponent displayName = container.getChatComponents().read(0);
            WrappedChatComponent prefix = container.getChatComponents().read(1);
            WrappedChatComponent suffix = container.getChatComponents().read(2);
            if (mode == 2) {

            }
        }
    }
}
