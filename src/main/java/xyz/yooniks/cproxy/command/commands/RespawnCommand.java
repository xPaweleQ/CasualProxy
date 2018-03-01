package xyz.yooniks.cproxy.command.commands;

import org.spacehq.mc.protocol.data.game.values.ClientRequest;
import org.spacehq.mc.protocol.packet.ingame.client.ClientRequestPacket;
import xyz.yooniks.cproxy.command.Command;
import xyz.yooniks.cproxy.Group;
import xyz.yooniks.cproxy.objects.Player;

public class RespawnCommand extends Command {

    public RespawnCommand() {
        super("respawn", "Wysyla pakiet od respawnu do klienta", ",respawn",
                Group.GRACZ, "respawnchuj");
    }

    @Override
    public void onCommand(Player p, Command command, String[] args) {
        if (p.isConnected()) {
            p.getSessionConnect().send(new ClientRequestPacket(ClientRequest.RESPAWN));
            p.sendMessage("$p &aWyslano zapytanie o respawn!");
        } else {
            p.sendMessage("$p &cNie jestes polaczony z zadnym serwerem!");
        }
    }
}