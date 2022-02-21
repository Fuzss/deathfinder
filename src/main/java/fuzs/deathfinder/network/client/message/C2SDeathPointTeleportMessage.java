package fuzs.deathfinder.network.client.message;

import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.registry.ModRegistry;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class C2SDeathPointTeleportMessage implements Message {
    private TeleportClickEvent event;

    public C2SDeathPointTeleportMessage() {
    }

    public C2SDeathPointTeleportMessage(TeleportClickEvent event) {
        this.event = event;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.event.serialize(buf);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.event = TeleportClickEvent.deserialize(buf);
    }

    @Override
    public DeathPointTeleportHandler makeHandler() {
        return new DeathPointTeleportHandler();
    }

    private static class DeathPointTeleportHandler extends PacketHandler<C2SDeathPointTeleportMessage> {
        @Override
        public void handle(C2SDeathPointTeleportMessage packet, Player player, Object gameInstance) {
            if (player.getCapability(ModRegistry.PLAYER_DEATH_TRACKER_CAPABILITY).map(tracker -> {
                if (packet.event.acceptsTracker(tracker)) {
                    tracker.invalidate();
                    return true;
                }
                return false;
            }).orElse(false)) {
                ((ServerPlayer) player).server.getCommands().performCommand(player.createCommandSourceStack().withMaximumPermission(2), packet.event.getValue());
            } else {
                player.sendMessage(new TranslatableComponent("death.message.teleport.failure"), Util.NIL_UUID);
            }
        }
    }
}
