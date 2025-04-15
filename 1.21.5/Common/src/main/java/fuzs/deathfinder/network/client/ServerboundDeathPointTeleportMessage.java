package fuzs.deathfinder.network.client;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.attachment.DeathTracker;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;

public record ServerboundDeathPointTeleportMessage(TeleportClickEvent clickEvent) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundDeathPointTeleportMessage> STREAM_CODEC = StreamCodec.composite(
            TeleportClickEvent.STREAM_CODEC,
            ServerboundDeathPointTeleportMessage::clickEvent,
            ServerboundDeathPointTeleportMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                this.tryTeleportToDeath(context.player(), ServerboundDeathPointTeleportMessage.this.clickEvent)
                        .ifRight((Unit unit) -> {
                            context.server()
                                    .getCommands()
                                    .performPrefixedCommand(context.player()
                                                    .createCommandSourceStack()
                                                    .withMaximumPermission(2),
                                            ServerboundDeathPointTeleportMessage.this.clickEvent.getValue());
                        })
                        .ifLeft((TeleportToDeathProblem problem) -> {
                            context.player().displayClientMessage(problem.getComponent(), false);
                        });
            }

            private Either<TeleportToDeathProblem, Unit> tryTeleportToDeath(Player player, TeleportClickEvent event) {
                ServerConfig.TeleportRestriction teleportRestriction = DeathFinder.CONFIG.get(ServerConfig.class).components.allowTeleporting;
                if (teleportRestriction != ServerConfig.TeleportRestriction.NO_ONE) {
                    if (player.hasPermissions(2)) {
                        return Either.right(Unit.INSTANCE);
                    } else if (teleportRestriction == ServerConfig.TeleportRestriction.EVERYONE) {
                        DeathTracker deathTracker = ModRegistry.DEATH_TRACKER_ATTACHMENT_TYPE.get(player);
                        Either<TeleportToDeathProblem, Unit> problem = event.acceptsTracker(player, deathTracker);
                        problem.ifRight($ -> ModRegistry.DEATH_TRACKER_ATTACHMENT_TYPE.set(player, null));
                        return problem;
                    } else {
                        return Either.left(TeleportToDeathProblem.MISSING_PERMISSIONS);
                    }
                }

                return Either.left(TeleportToDeathProblem.OTHER_PROBLEM);
            }
        };
    }
}
