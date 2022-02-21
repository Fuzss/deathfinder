package fuzs.deathfinder.capability;

import fuzs.deathfinder.core.capability.data.CapabilityComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
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

    default void copy(PlayerDeathTracker other) {
        this.setLastDeathDimension(other.getLastDeathDimension());
        this.setLastDeathPosition(other.getLastDeathPosition());
        this.setLastDeathDate(other.getLastDeathDate());
    }

    default boolean isValid(ResourceKey<Level> lastDeathDimension, BlockPos lastDeathPosition, int seconds) {
        if (!this.getLastDeathPosition().equals(lastDeathPosition) || !this.getLastDeathDimension().equals(lastDeathDimension)) return false;
        if (seconds == -1) return true;
        return Duration.between(Instant.ofEpochMilli(this.getLastDeathDate()), Instant.now()).toSeconds() < seconds;
    }

    default void invalidate() {
        this.setLastDeathPosition(BlockPos.ZERO);
    }
}
