package fuzs.deathfinder.network.client;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.attachment.DeathTracker;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;

public class C2SDeathPointTeleportMessage implements WritableMessage<C2SDeathPointTeleportMessage> {
    private final TeleportClickEvent clickEvent;

    public C2SDeathPointTeleportMessage(TeleportClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    public C2SDeathPointTeleportMessage(FriendlyByteBuf buf) {
        this.clickEvent = TeleportClickEvent.readTeleportClickEvent(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        TeleportClickEvent.writeTeleportClickEvent(buf, this.clickEvent);
    }

    @Override
    public MessageHandler<C2SDeathPointTeleportMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SDeathPointTeleportMessage message, Player player, Object gameInstance) {
                this.tryTeleportToDeath(player, message.clickEvent).ifRight(unit -> {
                    ((ServerPlayer) player).server.getCommands()
                            .performPrefixedCommand(((ServerPlayer) player).createCommandSourceStack()
                                    .withMaximumPermission(2), message.clickEvent.getValue());
                }).ifLeft(problem -> {
                    player.displayClientMessage(problem.getComponent(), false);
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
