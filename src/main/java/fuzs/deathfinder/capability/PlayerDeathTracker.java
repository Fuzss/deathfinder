package fuzs.deathfinder.capability;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.core.capability.data.CapabilityComponent;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;

import java.time.Duration;
import java.time.Instant;

public interface PlayerDeathTracker extends CapabilityComponent {
    BlockPos getLastDeathPosition();

    ResourceKey<Level> getLastDeathDimension();

    long getLastDeathDate();

    void setLastDeathPosition(BlockPos lastDeathPosition);

    void setLastDeathDimension(ResourceKey<Level> lastDeathDimension);

    void setLastDeathDate(long lastDeathDate);

    default void captureDeathDate() {
        this.setLastDeathDate(System.currentTimeMillis());
    }

    default Either<TeleportToDeathProblem, Unit> isValid(ResourceKey<Level> lastDeathDimension, BlockPos lastDeathPosition, int seconds) {
        if (this.isInvalid()) return Either.left(TeleportToDeathProblem.ALREADY_USED);
        if (!this.getLastDeathPosition().equals(lastDeathPosition) || !this.getLastDeathDimension().equals(lastDeathDimension)) return Either.left(TeleportToDeathProblem.NOT_MOST_RECENT);
        if (seconds == -1 || Duration.between(Instant.ofEpochMilli(this.getLastDeathDate()), Instant.now()).toSeconds() < seconds) {
            return Either.right(Unit.INSTANCE);
        }
        return Either.left(TeleportToDeathProblem.TOO_LONG_AGO);
    }

    default boolean isInvalid() {
        return this.getLastDeathPosition() == BlockPos.ZERO;
    }

    default void invalidate() {
        this.setLastDeathPosition(BlockPos.ZERO);
    }
}
