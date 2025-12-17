package fuzs.deathfinder.network.chat;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.attachment.DeathTracker;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.init.ModRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record CustomTeleportClickEvent(UUID uuid, GlobalPos position) {
    public static final Identifier RESOURCE_LOCATION = DeathFinder.id("teleport");
    public static final Codec<CustomTeleportClickEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(CustomTeleportClickEvent::uuid),
                    GlobalPos.MAP_CODEC.forGetter(CustomTeleportClickEvent::position))
            .apply(instance, CustomTeleportClickEvent::new));
    public static final StreamCodec<ByteBuf, CustomTeleportClickEvent> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC,
            CustomTeleportClickEvent::uuid,
            GlobalPos.STREAM_CODEC,
            CustomTeleportClickEvent::position,
            CustomTeleportClickEvent::new);

    public String getCommand() {
        BlockPos blockPos = this.position.pos();
        return String.format("/execute in %s run tp @s %s %s %s",
                this.position.dimension().identifier(),
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ());
    }

    public static ClickEvent getClickEvent(UUID uuid, ResourceKey<Level> dimension, BlockPos position) {
        CustomTeleportClickEvent clickEvent = new CustomTeleportClickEvent(uuid, GlobalPos.of(dimension, position));
        Optional<Tag> tag = CODEC.encodeStart(NbtOps.INSTANCE, clickEvent).resultOrPartial(DeathFinder.LOGGER::warn);
        return new ClickEvent.Custom(RESOURCE_LOCATION, tag);
    }

    public static void handleCustomClickAction(MinecraftServer minecraftServer, ServerPlayer serverPlayer, Identifier id, Optional<Tag> payload) {
        if (Objects.equals(id, RESOURCE_LOCATION)) {
            payload.flatMap((Tag tag) -> CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(DeathFinder.LOGGER::warn))
                    .ifPresent((CustomTeleportClickEvent clickEvent) -> {
                        clickEvent.tryTeleportToDeath(serverPlayer).ifRight((Unit unit) -> {
                            // TODO This should call CommandSourceStack::withMaximumPermission, but that is currently broken in vanilla.
                            minecraftServer.getCommands()
                                    .performPrefixedCommand(serverPlayer.createCommandSourceStack()
                                                    .withPermission(LevelBasedPermissionSet.GAMEMASTER),
                                            clickEvent.getCommand());
                        }).ifLeft((TeleportToDeathProblem problem) -> {
                            serverPlayer.displayClientMessage(problem.getComponent(), false);
                        });
                    });
        }
    }

    private Either<TeleportToDeathProblem, Unit> tryTeleportToDeath(ServerPlayer serverPlayer) {
        ServerConfig.TeleportRestriction teleportRestriction = DeathFinder.CONFIG.get(ServerConfig.class).components.allowTeleporting;
        if (teleportRestriction != ServerConfig.TeleportRestriction.NO_ONE) {
            if (serverPlayer.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                return Either.right(Unit.INSTANCE);
            } else if (teleportRestriction == ServerConfig.TeleportRestriction.EVERYONE) {
                DeathTracker deathTracker = ModRegistry.DEATH_TRACKER_ATTACHMENT_TYPE.get(serverPlayer);
                Either<TeleportToDeathProblem, Unit> problem = this.acceptsTracker(serverPlayer, deathTracker);
                problem.ifRight((Unit unit) -> ModRegistry.DEATH_TRACKER_ATTACHMENT_TYPE.set(serverPlayer, null));
                return problem;
            } else {
                return Either.left(TeleportToDeathProblem.MISSING_PERMISSIONS);
            }
        }

        return Either.left(TeleportToDeathProblem.OTHER_PROBLEM);
    }

    private Either<TeleportToDeathProblem, Unit> acceptsTracker(ServerPlayer serverPlayer, @Nullable DeathTracker deathTracker) {
        if (!serverPlayer.getUUID().equals(this.uuid)) {
            return Either.left(TeleportToDeathProblem.NOT_YOURS);
        } else if (deathTracker == null) {
            return Either.left(TeleportToDeathProblem.ALREADY_USED);
        } else {
            return deathTracker.isValid(this.position.dimension(),
                    this.position.pos(),
                    DeathFinder.CONFIG.get(ServerConfig.class).components.teleportInterval);
        }
    }
}
