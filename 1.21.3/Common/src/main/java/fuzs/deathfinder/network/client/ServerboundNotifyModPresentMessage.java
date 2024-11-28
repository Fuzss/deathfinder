package fuzs.deathfinder.network.client;

import fuzs.deathfinder.init.ModRegistry;
import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record ServerboundNotifyModPresentMessage() implements ServerboundMessage<ServerboundNotifyModPresentMessage> {

    @Override
    public ServerMessageListener<ServerboundNotifyModPresentMessage> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(ServerboundNotifyModPresentMessage message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                ModRegistry.VANILLA_CLIENT_CAPABILITY.get(player).setModAvailableForClient();
            }
        };
    }
}
