package fuzs.deathfinder.util;

import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.ClientboundAdvancedSystemChatMessage;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

import java.util.stream.Stream;

public class DeathMessageSender {
    private final MinecraftServer minecraftServer;
    private final PlayerList playerList;

    public DeathMessageSender(MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
        this.playerList = minecraftServer.getPlayerList();
    }

    public void sendToAll(DeathMessageBuilder builder) {
        this.sendToAll(builder, true);
    }

    public void sendToAll(DeathMessageBuilder builder, boolean sendToConsole) {
        if (sendToConsole) this.sendToConsole(builder);
        this.sendToAll(builder, this.playerList.getPlayers().stream());
    }

    public void sendMessageToAllTeamMembers(Player player, DeathMessageBuilder builder) {
        Team team = player.getTeam();
        if (team != null) {
            final Stream<ServerPlayer> teamMembers = team.getPlayers()
                    .stream()
                    .map(this.playerList::getPlayerByName)
                    .filter((ServerPlayer serverPlayer) -> serverPlayer != null && serverPlayer != player);
            this.sendToAll(builder, teamMembers);
        }
    }

    public void sendMessageToTeamOrAllPlayers(Player player, DeathMessageBuilder message) {
        Team team = player.getTeam();
        if (team == null) {
            this.sendToAll(message);
        } else {
            final Stream<ServerPlayer> notTeamMembers = this.playerList.getPlayers()
                    .stream()
                    .filter((ServerPlayer serverPlayer) -> player.getTeam() != team);
            this.sendToAll(message, notTeamMembers);
        }
    }

    private void sendToConsole(DeathMessageBuilder builder) {
        this.minecraftServer.sendSystemMessage(builder.build(null));
    }

    private void sendToAll(DeathMessageBuilder builder, Stream<ServerPlayer> players) {
        players.forEach((ServerPlayer serverPlayer) -> {
            sendSystemMessage(serverPlayer, builder.build(serverPlayer), false);
        });
    }

    public static void sendSystemMessage(ServerPlayer serverPlayer, Component component, boolean bypassHiddenChat) {
        if (ModRegistry.MESSAGE_SENDER_ATTACHMENT_TYPE.has(serverPlayer)) {
            MessageSender.broadcast(PlayerSet.ofPlayer(serverPlayer),
                    new ClientboundAdvancedSystemChatMessage(component, bypassHiddenChat));
        } else {
            serverPlayer.sendSystemMessage(component, bypassHiddenChat);
        }
    }
}
