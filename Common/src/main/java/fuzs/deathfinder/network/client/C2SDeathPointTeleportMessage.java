package fuzs.deathfinder.network.client;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import fuzs.puzzleslib.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;

public class C2SDeathPointTeleportMessage implements Message<C2SDeathPointTeleportMessage> {
    private TeleportClickEvent clickEvent;

    public C2SDeathPointTeleportMessage() {

    }

    public C2SDeathPointTeleportMessage(TeleportClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        MutableComponent component = Component.empty().withStyle(Style.EMPTY.withClickEvent(this.clickEvent));
        buf.writeUtf(AdvancedClickEvent.GSON.toJson(component), 262144);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.clickEvent = ((TeleportClickEvent) GsonHelper.fromJson(AdvancedClickEvent.GSON, buf.readUtf(262144), MutableComponent.class, false).getStyle().getClickEvent());
    }

    @Override
    public MessageHandler<C2SDeathPointTeleportMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SDeathPointTeleportMessage message, Player player, Object gameInstance) {
                this.tryTeleportToDeath(player, message.clickEvent).ifRight(unit -> {
                    ((ServerPlayer) player).server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withMaximumPermission(2), message.clickEvent.getValue());
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
                        return ModRegistry.PLAYER_DEATH_TRACKER_CAPABILITY.maybeGet(player).map(tracker -> {
                            final Either<TeleportToDeathProblem, Unit> either = event.acceptsTracker(player, tracker);
                            either.ifRight(unit -> tracker.invalidate());
                            return either;
                        }).orElse(Either.left(TeleportToDeathProblem.OTHER_PROBLEM));
                    } else {
                        return Either.left(TeleportToDeathProblem.MISSING_PERMISSIONS);
                    }
                }
                return Either.left(TeleportToDeathProblem.OTHER_PROBLEM);
            }
        };
    }
}
