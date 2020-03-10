package com.fuzs.deathfinder.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ChatType;

import java.util.List;
import java.util.stream.Collectors;

public class MessageSender {

    private final MinecraftServer server;
    private final PlayerList playerList;

    public MessageSender(MinecraftServer server) {

        this.server = server;
        this.playerList = server.getPlayerList();
    }

    public void sendMessage(DeathMessage message) {

        this.server.sendMessage(message.getMessage());
        this.sendMessage(message, this.playerList.getPlayers());
    }

    private void sendMessage(DeathMessage message, List<ServerPlayerEntity> players) {

        players.forEach(player -> this.sendMessage(message, player));
    }

    private void sendMessage(DeathMessage message, ServerPlayerEntity player) {

        player.connection.sendPacket(new SChatPacket(message.getMessage(player), ChatType.SYSTEM));
    }

    public void sendMessageToAllTeamMembers(final PlayerEntity player, DeathMessage message) {

        Team team = player.getTeam();
        if (team != null) {

            List<ServerPlayerEntity> teamList = team.getMembershipCollection().stream().map(this.playerList::getPlayerByUsername)
                    .filter(players -> players != null && players != player).collect(Collectors.toList());
            this.sendMessage(message, teamList);
        }
    }

    public void sendMessageToTeamOrAllPlayers(final PlayerEntity player, DeathMessage message) {

        Team team = player.getTeam();
        if (team == null) {

            this.sendMessage(message);
        } else {

            List<ServerPlayerEntity> noTeamList = this.playerList.getPlayers().stream()
                    .filter(players -> player.getTeam() != team).collect(Collectors.toList());
            this.sendMessage(message, noTeamList);
        }
    }

}
