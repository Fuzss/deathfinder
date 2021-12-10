package fuzs.deathfinder.util;

import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

import java.util.stream.Stream;

public class DeathMessageSender {
    private final MinecraftServer server;
    private final PlayerList playerList;

    private DeathMessageSender(MinecraftServer server) {
        this.server = server;
        this.playerList = server.getPlayerList();
    }

    public void sendToAll(DeathMessageBuilder builder) {
        this.sendToAll(builder, true);
    }

    public void sendToAll(DeathMessageBuilder builder, boolean sendToConsole) {
        if (sendToConsole) this.sendToConsole(builder);
        this.sendToAll(builder, this.playerList.getPlayers().stream());
    }

    public void sendMessageToAllTeamMembers(final Player player, DeathMessageBuilder builder) {
        Team team = player.getTeam();
        if (team != null) {
            final Stream<ServerPlayer> teamMembers = team.getPlayers().stream()
                    .map(this.playerList::getPlayerByName)
                    .filter(players -> players != null && players != player);
            this.sendToAll(builder, teamMembers);
        }
    }

    public void sendMessageToTeamOrAllPlayers(final Player player, DeathMessageBuilder message) {
        Team team = player.getTeam();
        if (team == null) {
            this.sendToAll(message);
        } else {
            final Stream<ServerPlayer> notTeamMembers = this.playerList.getPlayers().stream()
                    .filter(players -> player.getTeam() != team);
            this.sendToAll(message, notTeamMembers);
        }
    }

    private void sendToConsole(DeathMessageBuilder builder) {
        this.server.sendMessage(builder.build(null), Util.NIL_UUID);
    }

    private void sendToAll(DeathMessageBuilder builder, Stream<ServerPlayer> players) {
        players.forEach(player -> player.connection.send(new ClientboundChatPacket(builder.build(player), ChatType.SYSTEM, Util.NIL_UUID)));
    }

    public static DeathMessageSender from(MinecraftServer server) {
        return new DeathMessageSender(server);
    }
}
