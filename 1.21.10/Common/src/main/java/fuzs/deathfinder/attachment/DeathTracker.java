package fuzs.deathfinder.attachment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.time.Duration;
import java.time.Instant;

/**
 * TODO replace this with {@link Player#getLastDeathLocation()} where possible
 */
public record DeathTracker(BlockPos lastDeathPosition, ResourceKey<Level> lastDeathDimension, long lastDeathTime) {
    public static final Codec<DeathTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockPos.CODEC.fieldOf(
                    "position").forGetter(DeathTracker::lastDeathPosition),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DeathTracker::lastDeathDimension),
            Codec.LONG.fieldOf("time").forGetter(DeathTracker::lastDeathTime)).apply(instance, DeathTracker::new));

    public static DeathTracker of(Entity entity) {
        return new DeathTracker(entity.blockPosition(), entity.level().dimension(), System.currentTimeMillis());
    }

    public Either<TeleportToDeathProblem, Unit> isValid(ResourceKey<Level> lastDeathDimension, BlockPos lastDeathPosition, int timeInSeconds) {
        if (!this.lastDeathPosition.equals(lastDeathPosition) || !this.lastDeathDimension.equals(lastDeathDimension)) {
            return Either.left(TeleportToDeathProblem.NOT_MOST_RECENT);
        } else if (timeInSeconds == -1
                || Duration.between(Instant.ofEpochMilli(this.lastDeathTime), Instant.now()).toSeconds()
                < timeInSeconds) {
            return Either.right(Unit.INSTANCE);
        } else {
            return Either.left(TeleportToDeathProblem.TOO_LONG_AGO);
        }
    }
}
