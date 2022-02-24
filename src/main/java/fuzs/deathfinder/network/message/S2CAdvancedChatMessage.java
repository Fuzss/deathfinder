package fuzs.deathfinder.network.message;

import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class S2CAdvancedChatMessage implements Message {
    private Component message;
    private ChatType type;
    private UUID sender;

    public S2CAdvancedChatMessage() {
    }

    public S2CAdvancedChatMessage(Component component, ChatType chatType, UUID uUID) {
        this.message = component;
        this.type = chatType;
        this.sender = uUID;
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        // this line is changed to use our own gson encoder
        buf.writeUtf(AdvancedClickEvent.GSON.toJson(this.message), 262144);
        buf.writeByte(this.type.getIndex());
        buf.writeUUID(this.sender);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        // this line is changed to use our own gson decoder
        this.message = GsonHelper.fromJson(AdvancedClickEvent.GSON, buf.readUtf(262144), MutableComponent.class, false);
        this.type = ChatType.getForIndex(buf.readByte());
        this.sender = buf.readUUID();
    }

    @Override
    public AdvancedChatHandler makeHandler() {
        return new AdvancedChatHandler();
    }

    private static class AdvancedChatHandler extends PacketHandler<S2CAdvancedChatMessage> {
        @Override
        public void handle(S2CAdvancedChatMessage packet, Player player, Object gameInstance) {
            ((Minecraft) gameInstance).gui.handleChat(packet.type, packet.message, packet.sender);
        }
    }
}
