package fuzs.deathfinder.mixin;

import fuzs.deathfinder.network.chat.CustomTeleportClickEvent;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
abstract class ServerCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected MinecraftServer server;

    @Inject(method = "handleCustomClickAction", at = @At("TAIL"))
    public void handleCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo callback) {
        // custom click actions can also be sent during the configuration phase, but we don't care about that
        if (ServerGamePacketListenerImpl.class.isInstance(this)) {
            ServerPlayer serverPlayer = ServerGamePacketListenerImpl.class.cast(this).player;
            CustomTeleportClickEvent.handleCustomClickAction(this.server, serverPlayer, packet.id(), packet.payload());
        }
    }
}
