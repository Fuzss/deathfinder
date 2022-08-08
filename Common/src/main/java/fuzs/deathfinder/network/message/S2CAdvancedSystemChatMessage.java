package fuzs.deathfinder.network.message;

import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class S2CAdvancedSystemChatMessage implements Message<S2CAdvancedSystemChatMessage> {
    private Component message;
    private boolean overlay;

    public S2CAdvancedSystemChatMessage() {

    }

    public S2CAdvancedSystemChatMessage(Component component, boolean overlay) {
        this.message = component;
        this.overlay = overlay;
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        // this line is changed to use our own gson encoder
        buf.writeUtf(AdvancedClickEvent.GSON.toJson(this.message), 262144);
        buf.writeBoolean(this.overlay);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        // this line is changed to use our own gson decoder
        this.message = GsonHelper.fromJson(AdvancedClickEvent.GSON, buf.readUtf(262144), MutableComponent.class, false);
        this.overlay = buf.readBoolean();
    }

    @Override
    public PacketHandler<S2CAdvancedSystemChatMessage> makeHandler() {
        return new AdvancedChatHandler();
    }

    private static class AdvancedChatHandler extends PacketHandler<S2CAdvancedSystemChatMessage> {

        @Override
        public void handle(S2CAdvancedSystemChatMessage packet, Player player, Object gameInstance) {
            ((Minecraft) gameInstance).getChatListener().handleSystemMessage(packet.message, packet.overlay);
        }
    }
}
