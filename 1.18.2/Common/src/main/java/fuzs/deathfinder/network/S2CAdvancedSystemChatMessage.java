package fuzs.deathfinder.network;

import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class S2CAdvancedSystemChatMessage implements MessageV2<S2CAdvancedSystemChatMessage> {
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
    public MessageHandler<S2CAdvancedSystemChatMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CAdvancedSystemChatMessage message, Player player, Object gameInstance) {
                ((Minecraft) gameInstance).gui.handleChat(ChatType.SYSTEM, message.message, Util.NIL_UUID);
            }
        };
    }
}
