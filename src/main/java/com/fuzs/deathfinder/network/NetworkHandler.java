package com.fuzs.deathfinder.network;

import com.fuzs.deathfinder.DeathFinder;
import com.fuzs.deathfinder.network.message.MessageDeathCoords;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Modeled after https://github.com/sinkillerj/ProjectE/blob/mc1.14.x/src/main/java/moze_intel/projecte/network/PacketHandler.java
 */
public class NetworkHandler {

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DeathFinder.MODID, "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {

        int discriminator = 0;
        INSTANCE.registerMessage(discriminator++, MessageDeathCoords.class, MessageDeathCoords::writePacketData, MessageDeathCoords::readPacketData, MessageDeathCoords::processPacket);

    }

    public static void sendTo(Object message, ServerPlayerEntity player) {

        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);

    }

    public static void sendToAll(Object message) {

        INSTANCE.send(PacketDistributor.ALL.noArg(), message);

    }

    public static void sendToAllTeamMembers(Object message, ServerPlayerEntity player) {

        Team team = player.getTeam();

        if (team != null) {

            for (String s : team.getMembershipCollection()) {
                ServerPlayerEntity entityplayermp = player.server.getPlayerList().getPlayerByUsername(s);

                if (entityplayermp != null && entityplayermp != player) {
                    sendTo(message, entityplayermp);
                }
            }

        }

    }

    public static void sendToTeamOrAllPlayers(Object message, ServerPlayerEntity player) {

        Team team = player.getTeam();

        if (team == null) {

            sendToAll(message);

        } else {

            for (int i = 0; i < player.server.getPlayerList().getPlayers().size(); ++i) {
                ServerPlayerEntity entityplayermp = player.server.getPlayerList().getPlayers().get(i);

                if (entityplayermp.getTeam() != team) {
                    sendTo(message, entityplayermp);
                }
            }

        }

    }

}