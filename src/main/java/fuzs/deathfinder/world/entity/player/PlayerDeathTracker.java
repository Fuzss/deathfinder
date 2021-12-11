package fuzs.deathfinder.world.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface PlayerDeathTracker {
    default boolean hasLastDeathData() {
        return this.getLastDeathPosition() != BlockPos.ZERO;
    }

    BlockPos getLastDeathPosition();

    ResourceKey<Level> getLastDeathDimension();

    long getLastDeathDate();

    void setLastDeathPosition(BlockPos lastDeathPosition);

    void setLastDeathDimension(ResourceKey<Level> lastDeathDimension);

    void setLastDeathDate(long lastDeathDate);

    static void saveLastDeathData(PlayerDeathTracker player, BlockPos pos, ResourceKey<Level> dimension) {
        player.setLastDeathPosition(pos);
        player.setLastDeathDimension(dimension);
        player.setLastDeathDate(System.currentTimeMillis());
    }

    static void clearLastDeathData(PlayerDeathTracker player) {
        player.setLastDeathPosition(BlockPos.ZERO);
    }
}
