package fuzs.deathfinder.network.client.message;

import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
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
            ((ServerPlayer) player).server.getCommands().performCommand(player.createCommandSourceStack().withMaximumPermission(2), packet.event.getValue());
        }
    }
}
