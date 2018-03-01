package xyz.yooniks.cproxy.utils;

import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.packet.Packet;

public class PacketUtil {

    public static void sendJoinPayload(Session s) {
        final String name = ((GameProfile) s.getFlag("profile")).getName();
        final byte[] bytes = name.getBytes();
        final Packet p = new ClientPluginMessagePacket("CasualProxy|JOIN", bytes);
        s.send(p);
    }
}
