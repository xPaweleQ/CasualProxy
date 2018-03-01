package xyz.yooniks.cproxy.command.commands;

import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.data.SubProtocol;
import org.spacehq.mc.protocol.data.game.Position;
import org.spacehq.mc.protocol.data.game.values.ClientRequest;
import org.spacehq.mc.protocol.data.game.values.HandshakeIntent;
import org.spacehq.mc.protocol.data.game.values.MessageType;
import org.spacehq.mc.protocol.data.game.values.entity.MetadataType;
import org.spacehq.mc.protocol.data.game.values.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.values.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.values.world.WorldType;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoHandler;
import org.spacehq.mc.protocol.data.status.handler.ServerPingTimeHandler;
import org.spacehq.mc.protocol.packet.handshake.client.HandshakePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientRequestPacket;
import org.spacehq.mc.protocol.packet.ingame.server.*;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerUpdateHealthPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import org.spacehq.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import org.spacehq.mc.protocol.packet.login.server.LoginDisconnectPacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.tcp.TcpSessionFactory;
import xyz.yooniks.cproxy.command.Command;
import xyz.yooniks.cproxy.Group;
import xyz.yooniks.cproxy.managers.PlayerManager;
import xyz.yooniks.cproxy.objects.Bot;
import xyz.yooniks.cproxy.objects.Player;
import xyz.yooniks.cproxy.resolvers.SRVResolver;
import xyz.yooniks.cproxy.utils.ChatUtilities;

import java.net.Proxy;

public class TestObejscieCommand extends Command {

    public TestObejscieCommand() {
        super("obejscie", "Dolacz na serwer z blazingpackiem!", ",obejscie", Group.GRACZ, "testobejscie");
    }

    @Override
    public void onCommand(Player p, Command command, String[] args) {
        final String host = "mc4u.pl";
        final Integer port = 25565;
        p.sendMessage("&8>> &7Proba laczenia do: &amc4u.pl&7 z obejscia..");
        connect(p, p.getSession(), host, "tojestTestx9", true, Proxy.NO_PROXY, "mc4u.pl:25565", false);
    }

    private void connect(final Player owner, final Session session, String ip, final String nick, final boolean ping, java.net.Proxy proxy, String argIp, final boolean resolver) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String host;
                Integer port;
                if (resolver) {
                    owner.sendMessage("$p &7Proba pozyskania adresu ip uzywajac resolvera..");
                    final String resolvedIp = SRVResolver.srv(ip, owner);
                    //try {
                    host = resolvedIp.split(":")[0];
                    port = Integer.parseInt(resolvedIp.split(":")[1]);
                    owner.sendMessage("$p &7Pobrano ip! &a" + host + ":" + port);
                    //} catch (ArrayIndexOutOfBoundsException e) {
                    //   host = argIp.split(":")[0];
                    //   port = Integer.parseInt(argIp.split(":")[1]);
                    //  owner.sendMessage("$p &7Proba pozyskania adresu ip &cnieudana&7..");
                    //}
                } else {
                    host = argIp.split(":")[0];
                    port = Integer.parseInt(argIp.split(":")[1]);
                }
                //System.out.println("host: "+host+" port: "+port+" argip: "+argIp);
                final GameProfile profile = session.getFlag("profile");
                final Player p = PlayerManager.getPlayer(profile.getName());
                p.setLastPacketMs(0L);
                if (p.getSessionConnect() != null) {
                    p.getSessionConnect().getListeners().forEach(l -> p.getSessionConnect().removeListener(l));
                    p.getSessionConnect().disconnect("Nowa sesja.. Jesli chcesz zeby ci nie rozlaczalo bota uzywaj pierw ,detach, potem dopiero ,connect");
                    p.setSessionConnect(null);
                }
                Client c = new Client(host, port, new MinecraftProtocol(nick), new TcpSessionFactory(proxy));
                final long ms = System.currentTimeMillis();
                c.getSession().setConnectTimeout(p.playerOptions.timeOutConnect);
                if (ping) {
                    p.sendMessage("$p &aPingowanie...");
                    final MinecraftProtocol protocol = new MinecraftProtocol(SubProtocol.STATUS);
                    final Client client = new Client(host, port, protocol, new TcpSessionFactory(proxy));
                    client.getSession().setConnectTimeout(p.playerOptions.timeOutPing);
                    client.getSession().setFlag("server-info-handler", new ServerInfoHandler() {
                        @Override
                        public void handle(final Session session, final ServerStatusInfo info) {
                            p.sendMessage("$p &7Zpingowano. &a[ Silnik serwera: &7" + info.getVersionInfo().getVersionName() + "&a, graczy: &7"
                                    + info.getPlayerInfo().getOnlinePlayers() + "&8/&7" + info.getPlayerInfo().getMaxPlayers() + "&a, motd: &7" +
                                    info.getDescription().getFullText() + " &a]");
                            p.sendMessage("$p &aSerwer odpowiedzial na ping w: &7" + (System.currentTimeMillis() - ms) + "ms");
                            client.getSession().disconnect("zpingowano.");
                        }
                    });
                    client.getSession().setFlag("server-ping-time-handler", new ServerPingTimeHandler() {
                        @Override
                        public void handle(final Session session, final long pingTime) {
                        }
                    });
                    client.getSession().connect();
                }
                p.sendMessage("$p &aLacze z serwerem..");
                c.getSession().addListener(new SessionListener() {
                    @Override
                    public void disconnected(final DisconnectedEvent event) {
                        event.getCause().printStackTrace();
                        final GameProfile profile2 = event.getSession().getFlag("profile");
                        p.sendMessage("$p &cBot &7" + profile2.getName() + " " +
                                "&czostal rozlaczony! &8(&cPowod: &7" + event.getReason() + "&8)");
                        if (p.getSessionConnect() != null) {
                            final GameProfile profilex = p.getSessionConnect().getFlag("profile");
                            if (profile2.getName().equals(profilex.getName())) {
                                session.send(new ServerChatPacket(
                                        ChatUtilities.fixColor("$p &cDisconnected from: &7" +
                                                event.getSession().getHost() + " &8(" + event.getSession().getPort() + ")"),
                                        MessageType.NOTIFICATION));
                                p.setSessionConnect(null);
                                p.setLastPacketMs(0L);
                                p.setConnected(false);
                                p.setLastPacket("&cRozlaczono");
                            } else {
                                session.send(new ServerChatPacket(ChatUtilities.fixColor("&cBot " +
                                        profile2.getName() + " has been just disconnected from: &7" + event.getSession().getHost() +
                                        " &8(" + event.getSession().getPort() + ")"), MessageType.NOTIFICATION));
                            }
                        } else {
                            session.send(new ServerChatPacket(ChatUtilities.fixColor("&cBot "
                                    + profile2.getName() + " has been just disconnected from: &7" +
                                    "" + event.getSession().getHost() + " &8(" + event.getSession().getPort() + ")"),
                                    MessageType.NOTIFICATION));
                        }
                        event.getSession().getPacketProtocol().clearPackets();
                    }

                    @Override
                    public void packetSent(final PacketSentEvent packetSentEvent) {
                    }

                    @Override
                    public void disconnecting(final DisconnectingEvent disconnectingEvent) {
                    }

                    @Override
                    public void connected(final ConnectedEvent connectedEvent) {

                    }

                    @Override
                    public void packetReceived(final PacketReceivedEvent event) {
                        if (event.getPacket() instanceof ServerDisconnectPacket) {
                            final GameProfile profile2 = event.getSession().getFlag("profile");
                            final String reason = ((ServerDisconnectPacket) event.getPacket()).getReason().toString();
                            p.sendMessage("$p &cBot &7" + profile2.getName() + " &czostal rozlaczony!" +
                                    " &8(&cPowod: &7" + reason + "&8)");
                            if (p.getSessionConnect() != null) {
                                final GameProfile profilex = p.getSessionConnect().getFlag("profile");
                                if (profile2.getName().equals(profilex.getName())) {
                                    session.send(new ServerChatPacket(ChatUtilities.fixColor(
                                            "$p &cDisconnected from: &7" + event.getSession().getHost() + "" +
                                                    " &8(" + event.getSession().getPort() + ")"), MessageType.NOTIFICATION));
                                    p.setSessionConnect(null);
                                    p.setLastPacketMs(0L);
                                    p.setConnected(false);
                                    p.setLastPacket("&cRozlaczono");
                                }
                            }
                        }
                        if (event.getPacket() instanceof LoginDisconnectPacket) {
                            final LoginDisconnectPacket packet = event.getPacket();
                            final GameProfile profile3 = event.getSession().getFlag("profile");
                            p.sendMessage("$p &cBot &7" + profile3.getName() + " &czostal rozlaczony podczas laczenia!" +
                                    " &8(&cPowod: &7" + packet.getReason() + "&8)");
                        }
                        final String ll = event.getPacket().toString();
                        if (ll.toLowerCase().contains("server")) {
                            p.setLastPacketMs(System.currentTimeMillis());
                            p.setLastPacket(event.getPacket().getClass().getSimpleName());
                        }
                        final MinecraftProtocol mp = (MinecraftProtocol) session.getPacketProtocol();
                        final MinecraftProtocol mp2 = (MinecraftProtocol) c.getSession().getPacketProtocol();
                        if (mp.getSubProtocol() != SubProtocol.GAME || mp2.getSubProtocol() != SubProtocol.GAME) {
                            if (!session.isConnected() || c.getSession().isConnected()) {
                            }
                            return;
                        }
                        if (event.getPacket() instanceof ServerJoinGamePacket) {
                            session.send(new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 1,
                                    Difficulty.PEACEFUL, 10, WorldType.DEFAULT_1_1, false));
                            session.send(new ServerPlayerPositionRotationPacket(0.0, 90.0, 0.0, 90.0f, 90.0f));
                            session.send(new ServerSpawnPositionPacket(new Position(0, 90, 0)));
                            session.send(new ServerRespawnPacket(0, Difficulty.PEACEFUL, GameMode.SURVIVAL,
                                    WorldType.DEFAULT));
                            c.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                            c.getSession().send(new ClientKeepAlivePacket(1));
                            final GameProfile profileBot = c.getSession().getFlag("profile");
                            p.setSessionConnect(event.getSession());
                            p.setLastPacketMs(0L);
                            p.setConnected(true);
                            p.setLastPacket("&cRozlaczono");
                            if (!event.getSession().getHost().toLowerCase().contains("nssv") &&
                                    !event.getSession().getHost().toLowerCase().contains("proxy") && p.playerOptions.autoLogin) {
                                c.getSession().send(new ClientChatPacket("/register cproxy123 cproxy123"));
                                c.getSession().send(new ClientChatPacket("/login cproxy123"));
                            }
                            ChatUtilities.broadcast("$p &a"
                                    + profile.getName() + "&7 dolaczyl do serwera: &a" +
                                    argIp.split(":")[0] + ":" + argIp.split(":")[1] + " &8(&7Uzywajac nicku: &a"
                                    + profileBot.getName() + "&8)");
                            return;
                        }

                        //autocaptcha
                        if (event.getPacket() instanceof ServerChatPacket) {
                            if (p.playerOptions.chatFromServer) session.send(event.getPacket());

                            if (p.playerOptions.autoCaptcha) {
                                if (event.getSession().getHost().contains("proxy")) return;
                                final ServerChatPacket p3 = event.getPacket();
                                if (p3.getMessage().toString().toLowerCase().contains("captcha:") || p3.getMessage().toString().toLowerCase().contains("kod:")) {
                                    if (event.getSession().getHost().contains("megaxcore")) return;
                                    final String message = p3.getMessage().toString();
                                    final String[] args2 = message.split(":");
                                    if (args2.length < 2 || args2[1] == null) return;
                                    args2[1] = args2[1].replace(" ", "");
                                    event.getSession().send(new ClientChatPacket("/register " + args2[1] + " cproxy123 cproxy123"));
                                    event.getSession().send(new ClientChatPacket("/register cproxy123 cproxy123 " + args2[1]));
                                    p.sendMessage("$p &7Wykryto kod captcha: &a" + args2[1]);
                                } else if (p3.getMessage().toString().toLowerCase().contains("kod") && p3.getMessage().toString().toLowerCase().contains("to")) {
                                    if (event.getSession().getHost().contains("megaxcore")) return;
                                    final String message = p3.getMessage().toString();
                                    final String[] args2 = message.split("to ");
                                    if (args2.length < 2 || args2[1] == null) return;
                                    args2[1] = args2[1].replace(" ", "");
                                    p.sendMessage("$p &7Wykryto kod captcha: &a" + args2[1]);
                                    event.getSession().send(new ClientChatPacket("/register " + args2[1] + " cproxy123 cproxy123"));
                                    event.getSession().send(new ClientChatPacket("/register cproxy123 cproxy123 " + args2[1]));
                                }
                            }
                            return;
                        }
                        //autocaptcha
                        else if (event.getPacket() instanceof ServerSpawnMobPacket && p.playerOptions.autoCaptcha) {
                            final ServerSpawnMobPacket p3 = event.getPacket();
                            for (int i = 0; i < p3.getMetadata().length; ++i) {
                                if (p3.getMetadata()[i].getType() == MetadataType.STRING) {
                                    final String msg2 = p3.getMetadata()[i].getValue().toString();
                                    if (msg2.toLowerCase().contains("captcha:") || msg2.toLowerCase().contains("kod:")) {
                                        final String[] args2 = msg2.split(":");
                                        if (args2.length < 2 || args2[1] == null) {
                                            return;
                                        }
                                        args2[1] = args2[1].replace(" ", "");
                                        args2[1] = args2[1].replace("§c", "");
                                        args2[1] = args2[1].replace("§e", "");
                                        args2[1] = args2[1].replace("§6", "");
                                        args2[1] = args2[1].replace("§a", "");
                                        args2[1] = args2[1].replace("§b", "");
                                        args2[1] = args2[1].replace("§2", "");
                                        event.getSession().send(new ClientChatPacket("/register " + args2[1] + " cproxy123 cproxy123"));
                                        event.getSession().send(new ClientChatPacket("/register cproxy123 cproxy123 " + args2[1]));
                                    }
                                }
                            }
                        } else if (event.getPacket() instanceof ServerPluginMessagePacket) {
                            final ServerPluginMessagePacket p4 = event.getPacket();
                            if (p4.getChannel().equals("MC|Brand")) {
                                p.sendMessage("$p &7Silnik serwera: &a" + new String(p4.getData()));
                            }
                        } else if (event.getPacket() instanceof ServerKeepAlivePacket) {
                            event.getSession().send(new ClientKeepAlivePacket(((ServerKeepAlivePacket) event.getPacket()).getPingId()));
                        }
                        if (!(event.getPacket() instanceof ServerUpdateTimePacket) && !event.getPacket().toString().toLowerCase().contains("dis")) {
                            if (event.getPacket() instanceof ServerChatPacket || event.getPacket() instanceof ServerPlayerListDataPacket || event.getPacket() instanceof ServerTeamPacket || event.getPacket() instanceof ServerEntityStatusPacket || event.getPacket() instanceof ServerUpdateTileEntityPacket) {
                                return;
                            }
                            if (event.getPacket() instanceof ServerPlayerListEntryPacket) {
                                final ServerPlayerListEntryPacket p5 = event.getPacket();
                                if (p5.getEntries().length > 0 && p5.getEntries()[0].getGameMode() == null) {
                                    return;
                                }
                                session.send(event.getPacket());
                            } else {
                                session.send(event.getPacket());
                            }
                        }
                    }
                });
                session.addListener(new SessionListener() {
                    @Override
                    public void packetReceived(final PacketReceivedEvent event) {
                        final MinecraftProtocol mp = (MinecraftProtocol) c.getSession().getPacketProtocol();
                        final MinecraftProtocol mp2 = (MinecraftProtocol) session.getPacketProtocol();
                        if (mp.getSubProtocol() != SubProtocol.GAME || mp2.getSubProtocol() != SubProtocol.GAME) {
                            if (!c.getSession().isConnected() || session.isConnected()) {
                            }
                            return;
                        }

                        if (event.getPacket() instanceof ServerKeepAlivePacket) {
                            event.getSession().send(new ClientKeepAlivePacket(((ServerKeepAlivePacket)
                                    event.getPacket()).getPingId()));
                        } else if (event.getPacket() instanceof ServerUpdateHealthPacket) {
                            if (((ServerUpdateHealthPacket) event.getPacket()).getHealth() <= 0.0f) {
                                c.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                            }
                            c.getSession().send(event.getPacket());
                        } else if (event.getPacket() instanceof ServerJoinGamePacket) {
                            p.setLastPacketMs(0L);
                            p.setLastPacket("&cRozlaczono");
                        } else if (event.getPacket() instanceof ClientChatPacket && !((ClientChatPacket) event.getPacket()).getMessage().startsWith(",") && !((ClientChatPacket) event.getPacket()).getMessage().startsWith("@")) {
                            String msg = ((ClientChatPacket) event.getPacket()).getMessage().toLowerCase();
                            //if (u.userOptions.superChars || u.userOptions.invalidMessage) {
                            if (false) {
                                msg = msg.replace("a", "\uff21");
                                msg = msg.replace("b", "\uff22");
                                msg = msg.replace("c", "\uff23");
                                msg = msg.replace("d", "\uff24");
                                msg = msg.replace("e", "\uff25");
                                msg = msg.replace("f", "\uff26");
                                msg = msg.replace("g", "\uff27");
                                msg = msg.replace("h", "\uff28");
                                msg = msg.replace("i", "\uff29");
                                msg = msg.replace("j", "\uff2a");
                                msg = msg.replace("l", "\uff2c");
                                msg = msg.replace("m", "\uff2d");
                                msg = msg.replace("n", "\uff2e");
                                msg = msg.replace("o", "\uff2f");
                                msg = msg.replace("p", "\uff30");
                                msg = msg.replace("r", "\uff32");
                                msg = msg.replace("s", "\uff33");
                                msg = msg.replace("t", "\uff34");
                                msg = msg.replace("u", "\uff35");
                                msg = msg.replace("w", "\uff37");
                                msg = msg.replace("y", "\uff39");
                                msg = msg.replace("z", "\uff3a");
                                msg = msg.replace("1", "\uff11");
                                msg = msg.replace("2", "\uff12");
                                msg = msg.replace("3", "\uff13");
                                msg = msg.replace("4", "\uff14");
                                msg = msg.replace("5", "\uff15");
                                msg = msg.replace("6", "\uff16");
                                msg = msg.replace("7", "\uff17");
                                msg = msg.replace("8", "\uff18");
                                msg = msg.replace("9", "\uff19");
                                msg = msg.replace("0", "\uff10");
                                msg = msg.replace(".", "\u2080");
                                c.getSession().send(new ClientChatPacket(msg));
                            } else {
                                c.getSession().send(new ClientChatPacket(((ClientChatPacket) event.getPacket()).getMessage()));
                            }
                            return;
                        }
                        if (!event.getPacket().toString().toLowerCase().contains("dis") && !(event.getPacket() instanceof ClientChatPacket)) {
                            c.getSession().send(event.getPacket());
                        }
                    }

                    @Override
                    public void packetSent(final PacketSentEvent event) {
                    }

                    @Override
                    public void connected(final ConnectedEvent event) {
                    }

                    @Override
                    public void disconnecting(final DisconnectingEvent event) {
                    }

                    @Override
                    public void disconnected(final DisconnectedEvent event) {
                        p.addBot(new Bot(p, nick, c.getSession()));
                        event.getSession().getPacketProtocol().clearPackets();
                    }
                });
                c.getSession().connect();
                c.getSession().send(new HandshakePacket(48, host, port, HandshakeIntent.STATUS));
                c.getSession().send(new HandshakePacket(48, host, port, HandshakeIntent.LOGIN));
            }
        }).start();
    }
}
