package fuzs.deathfinder.network;

import fuzs.deathfinder.network.chat.CustomComponentSerializer;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundAdvancedSystemChatMessage(Component message,
                                                   boolean overlay) implements ClientboundMessage<ClientboundAdvancedSystemChatMessage> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAdvancedSystemChatMessage> STREAM_CODEC = StreamCodec.composite(
            CustomComponentSerializer.STREAM_CODEC,
            ClientboundAdvancedSystemChatMessage::message,
            ByteBufCodecs.BOOL,
            ClientboundAdvancedSystemChatMessage::overlay,
            ClientboundAdvancedSystemChatMessage::new);

    @Override
    public ClientMessageListener<ClientboundAdvancedSystemChatMessage> getHandler() {
        return new ClientMessageListener<>() {
            @Override
            public void handle(ClientboundAdvancedSystemChatMessage message, Minecraft minecraft, ClientPacketListener clientPacketListener, LocalPlayer localPlayer, ClientLevel clientLevel) {
                minecraft.getChatListener().handleSystemMessage(message.message, message.overlay);
            }
        };
    }
}
