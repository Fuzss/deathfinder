package fuzs.deathfinder.capability;

import fuzs.deathfinder.DeathFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PlayerDeathTrackerImpl implements PlayerDeathTracker {
    private BlockPos lastDeathPosition = BlockPos.ZERO;
    private ResourceKey<Level> lastDeathDimension = Level.OVERWORLD;
    private long lastDeathDate;

    @Override
    public BlockPos getLastDeathPosition() {
        return this.lastDeathPosition;
    }

    @Override
    public ResourceKey<Level> getLastDeathDimension() {
        return this.lastDeathDimension;
    }

    @Override
    public long getLastDeathDate() {
        return this.lastDeathDate;
    }

    @Override
    public void setLastDeathPosition(BlockPos lastDeathPosition) {
        this.lastDeathPosition = lastDeathPosition;
    }

    @Override
    public void setLastDeathDimension(ResourceKey<Level> lastDeathDimension) {
        this.lastDeathDimension = lastDeathDimension;
    }

    @Override
    public void setLastDeathDate(long lastDeathDate) {
        this.lastDeathDate = lastDeathDate;
    }

    @Override
    public void write(CompoundTag tag) {
        if (!this.isInvalid()) {
            tag.putInt("LastDeathX", this.lastDeathPosition.getX());
            tag.putInt("LastDeathY", this.lastDeathPosition.getY());
            tag.putInt("LastDeathZ", this.lastDeathPosition.getZ());
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.lastDeathDimension.location()).resultOrPartial(DeathFinder.LOGGER::error).ifPresent((p_9134_) -> {
                tag.put("LastDeathDimension", p_9134_);
            });
            tag.putLong("LastDeathDate", this.lastDeathDate);
        }
    }

    @Override
    public void read(CompoundTag tag) {
        if (tag.contains("LastDeathX", 99) && tag.contains("LastDeathY", 99) && tag.contains("LastDeathZ", 99)) {
            this.lastDeathPosition = new BlockPos(tag.getInt("LastDeathX"), tag.getInt("LastDeathY"), tag.getInt("LastDeathZ"));
            if (tag.contains("LastDeathDimension")) {
                this.lastDeathDimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("LastDeathDimension")).resultOrPartial(DeathFinder.LOGGER::error).orElse(Level.OVERWORLD);
            }
            if (tag.contains("LastDeathDate")) {
                this.lastDeathDate = tag.getLong("LastDeathDate");
            }
        }
    }
}
