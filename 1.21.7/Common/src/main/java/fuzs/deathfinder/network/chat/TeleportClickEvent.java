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
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record TeleportClickEvent(UUID uuid, ResourceKey<Level> dimension, BlockPos position) implements ClickEvent {
    public static final Codec<TeleportClickEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(UUIDUtil.CODEC.fieldOf(
                            "uuid").forGetter(TeleportClickEvent::uuid),
                    Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(TeleportClickEvent::dimension),
                    BlockPos.CODEC.fieldOf("position").forGetter(TeleportClickEvent::position))
            .apply(instance, TeleportClickEvent::new));
    public static final StreamCodec<ByteBuf, TeleportClickEvent> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC,
            TeleportClickEvent::uuid,
            ResourceKey.streamCodec(Registries.DIMENSION),
            TeleportClickEvent::dimension,
            BlockPos.STREAM_CODEC,
            TeleportClickEvent::position,
            TeleportClickEvent::new);

    public static ClickEvent create(ServerPlayer receiver, UUID uuid, ResourceKey<Level> dimension, BlockPos position) {
        if (receiver != null && ModRegistry.MESSAGE_SENDER_ATTACHMENT_TYPE.has(receiver)) {
            return new TeleportClickEvent(uuid, dimension, position);
        } else {
            return new ClickEvent.SuggestCommand(makeCommand(dimension, position));
        }
    }

    private static String makeCommand(ResourceKey<Level> dimension, BlockPos pos) {
        return String.format("/execute in %s run tp @s %s %s %s",
                dimension.location(),
                pos.getX(),
                pos.getY(),
                pos.getZ());
    }

    public String getValue() {
        return makeCommand(this.dimension, this.position);
    }

    @Override
    public Action action() {
        throw new UnsupportedOperationException();
    }

    public Either<TeleportToDeathProblem, Unit> acceptsTracker(Player player, @Nullable DeathTracker deathTracker) {
        if (!player.getUUID().equals(this.uuid)) {
            return Either.left(TeleportToDeathProblem.NOT_YOURS);
        } else if (deathTracker == null) {
            return Either.left(TeleportToDeathProblem.ALREADY_USED);
        } else {
            return deathTracker.isValid(this.dimension,
                    this.position,
                    DeathFinder.CONFIG.get(ServerConfig.class).components.teleportInterval);
        }
    }
}
