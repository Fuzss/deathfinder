package fuzs.deathfinder.handler;

import fuzs.deathfinder.util.DeathMessageBuilder;
import net.minecraft.Util;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

import java.util.List;
import java.util.stream.Collectors;

public class MessageSender {

    private final MinecraftServer server;
    private final PlayerList playerList;

    public MessageSender(MinecraftServer server) {

        this.server = server;
        this.playerList = server.getPlayerList();
    }

    public void sendMessage(DeathMessageBuilder message) {

        this.server.sendMessage(message.getMessage(), Util.NIL_UUID);
        this.sendMessage(message, this.playerList.getPlayers());
    }

    private void sendMessage(DeathMessageBuilder message, List<ServerPlayer> players) {

        players.forEach(player -> this.sendMessage(message, player));
    }

    private void sendMessage(DeathMessageBuilder message, ServerPlayer player) {

        player.connection.send(new ClientboundChatPacket(message.getMessage(player), ChatType.SYSTEM, Util.NIL_UUID));
    }

    public void sendMessageToAllTeamMembers(final Player player, DeathMessageBuilder message) {

        Team team = player.getTeam();
        if (team != null) {

            List<ServerPlayer> teamList = team.getPlayers().stream().map(this.playerList::getPlayerByUsername)
                    .filter(players -> players != null && players != player).collect(Collectors.toList());
            this.sendMessage(message, teamList);
        }
    }

    public void sendMessageToTeamOrAllPlayers(final Player player, DeathMessageBuilder message) {

        Team team = player.getTeam();
        if (team == null) {

            this.sendMessage(message);
        } else {

            List<ServerPlayer> noTeamList = this.playerList.getPlayers().stream()
                    .filter(players -> player.getTeam() != team).collect(Collectors.toList());
            this.sendMessage(message, noTeamList);
        }
    }

}
