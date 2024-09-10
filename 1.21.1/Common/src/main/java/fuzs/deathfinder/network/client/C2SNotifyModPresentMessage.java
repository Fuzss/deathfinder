package fuzs.deathfinder.network.client;

import fuzs.deathfinder.init.ModRegistry;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class C2SNotifyModPresentMessage implements WritableMessage<C2SNotifyModPresentMessage> {

    public C2SNotifyModPresentMessage(FriendlyByteBuf friendlyByteBuf) {
        // NO-OP
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        // NO-OP
    }

    @Override
    public MessageHandler<C2SNotifyModPresentMessage> makeHandler() {
        return new MessageHandler<>() {
            @Override
            public void handle(C2SNotifyModPresentMessage message, Player player, Object instance) {
                ModRegistry.VANILLA_CLIENT_CAPABILITY.get((ServerPlayer) player).setVanillaClient();
            }
        };
    }
}
