package dev.lbuddyboy.samurai.nametag.packet;


import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.nametag.NametagInfo;
import net.minecraft.EnumChatFormat;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ScoreboardTeamPacketMod {

	protected static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	private Object packet;
	private Object packetParams = createPacketParams();

	static Field PARAMS; // k
	static Field TEAM_NAME; // i
	static Field MEMBERS; // j
	static Field PARAM_INT; // h

	static Field DISPLAY_NAME; // a
	static Field PREFIX; // b
	static Field SUFFIX; // c
	static Field VISIBILITY; // d
	static Field PUSH; // e
	static Field TEAM_COLOR; // f
	static Field PACK_OPTION; // g
	// 1.17+

	static Class<?> packetClass, packetParamsClass;

	private static Method getHandle;
	private static Method sendPacket;
	private static Field playerConnection;

	private static Object UNSAFE;
	private static Method ALLOCATE_INSTANCE;

	public ScoreboardTeamPacketMod(NametagInfo info, Collection<?> players, int paramInt) {
		packet = createPacket();

		setupDefaults(info.getName(), paramInt);

		try {
			if (paramInt == 0 || paramInt == 2) {
				TEAM_NAME.set(this.packet, info.getName());
				PARAM_INT.set(this.packet, paramInt);

				PUSH.set(this.packetParams, ScoreboardTeamBase.EnumTeamPush.b.e);
				TEAM_COLOR.set(this.packetParams, info.getColor());
				DISPLAY_NAME.set(this.packetParams, Array.get(CraftChatMessage.fromString(info.getName()), 0));
				SUFFIX.set(this.packetParams, Array.get(CraftChatMessage.fromString(info.getSuffix()), 0));

				if (FrozenNametagHandler.getINVISIBLE() != null && Objects.equals(info.getPrefix(), FrozenNametagHandler.getINVISIBLE().getPrefix())) {
					VISIBILITY.set(this.packetParams, "hideForOtherTeams");
					PREFIX.set(this.packetParams, Array.get(CraftChatMessage.fromString(""), 0));
					PACK_OPTION.set(packetParams, 2);
				} else {
					VISIBILITY.set(this.packetParams, "always");
					PREFIX.set(this.packetParams, Array.get(CraftChatMessage.fromString(info.getPrefix()), 0));
					PACK_OPTION.set(packetParams, 0);
				}
			}

		} catch (Exception var7) {
			var7.printStackTrace();
		}

		if (paramInt == 0) {
			try {
				((Collection) MEMBERS.get(packet)).addAll(players);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	public ScoreboardTeamPacketMod(String name, Collection<?> players, int paramInt) {
		packet = createPacket();

		setupDefaults(name, paramInt);
		setupMembers(players);
	}

	@SuppressWarnings("unchecked")
	private void setupMembers(Collection<?> players) {
		try {
			players = players == null || players.isEmpty() ? new ArrayList<>() : players;
			((Collection) MEMBERS.get(packet)).addAll(players);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setupDefaults(String name, int param) {
		try {
			TEAM_NAME.set(packet, name);
			PARAM_INT.set(packet, param);

			MEMBERS.set(packet, new ArrayList<>());
			PUSH.set(packetParams, "");
			VISIBILITY.set(packetParams, "");
			TEAM_COLOR.set(packetParams, EnumChatFormat.b("RESET"));
			PUSH.set(packetParams, "never");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void constructPacket() {
		try {
			PARAMS.set(packet, Optional.ofNullable(packetParams));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendToPlayer(Player bukkitPlayer) {
		constructPacket();
		sendPacket(bukkitPlayer, packet);
	}

	static {
		try {
			Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
			Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			UNSAFE = theUnsafeField.get(null);
			ALLOCATE_INSTANCE = UNSAFE.getClass().getMethod("allocateInstance", Class.class);

			Class<?> typeCraftPlayer = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
			getHandle = typeCraftPlayer.getMethod("getHandle");

			packetClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam");
			packetParamsClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
			Class<?> typeNMSPlayer = Class.forName("net.minecraft.server.level.EntityPlayer");
			Class<?> typePlayerConnection = Class.forName("net.minecraft.server.network.PlayerConnection");
			playerConnection = typeNMSPlayer.getField("b");
			Class<?>[] sendPacketParameters = new Class[]{Class.forName("net.minecraft.network.protocol.Packet")};
			sendPacket = Arrays.stream(typePlayerConnection.getMethods())
					.filter(method -> Arrays.equals(method.getParameterTypes(), sendPacketParameters))
					.findFirst().orElseThrow(NoSuchMethodException::new);

			PARAM_INT = getNMS("h");
			TEAM_NAME = getNMS("i");
			MEMBERS = getNMS("j");
			PARAMS = getNMS("k");

			DISPLAY_NAME = getParamNMS("a");
			PREFIX = getParamNMS("b");
			SUFFIX = getParamNMS("c");
			VISIBILITY = getParamNMS("d");
			PUSH = getParamNMS("e");
			TEAM_COLOR = getParamNMS("f");
			PACK_OPTION = getParamNMS("g");

		} catch (Exception var1) {
			var1.printStackTrace();
		}
	}

	private static Field getNMS(String path) throws Exception {
		Field field = packetClass.getDeclaredField(path);
		field.setAccessible(true);
		return field;
	}

	// 1.17+
	private static Field getParamNMS(String path) throws Exception {
		Field field = packetParamsClass.getDeclaredField(path);
		field.setAccessible(true);
		return field;
	}

	static Object createPacket() {
		try {
			return ALLOCATE_INSTANCE.invoke(UNSAFE, packetClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static Object createPacketParams() {

		try {
			return ALLOCATE_INSTANCE.invoke(UNSAFE, packetParamsClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static void sendPacket(Player player, Object packet) {
		try {
			Object nmsPlayer = getHandle.invoke(player);
			Object connection = playerConnection.get(nmsPlayer);
			sendPacket.invoke(connection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Player player) {
		constructPacket();
		sendPacket(player, packet);
	}

}

