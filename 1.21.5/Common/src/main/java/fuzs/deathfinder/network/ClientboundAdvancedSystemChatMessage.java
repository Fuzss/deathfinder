package fuzs.deathfinder.network;

import fuzs.deathfinder.network.chat.CustomComponentSerializer;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundAdvancedSystemChatMessage(Component message,
                                                   boolean overlay) implements ClientboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAdvancedSystemChatMessage> STREAM_CODEC = StreamCodec.composite(
            CustomComponentSerializer.STREAM_CODEC,
            ClientboundAdvancedSystemChatMessage::message,
            ByteBufCodecs.BOOL,
            ClientboundAdvancedSystemChatMessage::overlay,
            ClientboundAdvancedSystemChatMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                context.client()
                        .getChatListener()
                        .handleSystemMessage(ClientboundAdvancedSystemChatMessage.this.message,
                                ClientboundAdvancedSystemChatMessage.this.overlay);
            }
        };
    }
}
