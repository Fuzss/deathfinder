package fuzs.deathfinder.network;

import fuzs.deathfinder.network.chat.CustomComponentSerializer;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class S2CAdvancedSystemChatMessage implements WritableMessage<S2CAdvancedSystemChatMessage> {
    private final Component message;
    private final boolean overlay;

    public S2CAdvancedSystemChatMessage(Component component, boolean overlay) {
        this.message = component;
        this.overlay = overlay;
    }

    public S2CAdvancedSystemChatMessage(FriendlyByteBuf buf) {
        this.message = CustomComponentSerializer.readComponent(buf);
        this.overlay = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        CustomComponentSerializer.writeComponent(buf, this.message);
        buf.writeBoolean(this.overlay);
    }

    @Override
    public MessageHandler<S2CAdvancedSystemChatMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CAdvancedSystemChatMessage message, Player player, Object gameInstance) {
                ((Minecraft) gameInstance).getChatListener().handleSystemMessage(message.message, message.overlay);
            }
        };
    }
}
