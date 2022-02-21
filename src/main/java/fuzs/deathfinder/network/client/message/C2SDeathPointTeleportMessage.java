package fuzs.deathfinder.network.client.message;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import fuzs.deathfinder.registry.ModRegistry;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
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
            this.tryTeleportToDeath(player, packet.event).ifRight(unit -> {
                ((ServerPlayer) player).server.getCommands().performCommand(player.createCommandSourceStack().withMaximumPermission(2), packet.event.getValue());
            }).ifLeft(problem -> {
                player.displayClientMessage(problem.getComponent(), false);
            });
        }

        private Either<TeleportToDeathProblem, Unit> tryTeleportToDeath(Player player, TeleportClickEvent event) {
            ServerConfig.TeleportRestriction teleportRestriction = DeathFinder.CONFIG.server().components.allowTeleporting;
            if (teleportRestriction != ServerConfig.TeleportRestriction.NO_ONE) {
                if (player.hasPermissions(2)) {
                    return Either.right(Unit.INSTANCE);
                } else if (teleportRestriction == ServerConfig.TeleportRestriction.EVERYONE) {
                    return player.getCapability(ModRegistry.PLAYER_DEATH_TRACKER_CAPABILITY).map(tracker -> {
                        final Either<TeleportToDeathProblem, Unit> either = event.acceptsTracker(tracker);
                        either.ifRight(unit -> tracker.invalidate());
                        return either;
                    }).orElse(Either.left(TeleportToDeathProblem.OTHER_PROBLEM));
                } else {
                    return Either.left(TeleportToDeathProblem.MISSING_PERMISSIONS);
                }
            }
            return Either.left(TeleportToDeathProblem.OTHER_PROBLEM);
        }
    }
}
