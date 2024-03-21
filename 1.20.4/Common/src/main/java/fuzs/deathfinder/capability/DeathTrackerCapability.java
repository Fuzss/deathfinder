package fuzs.deathfinder.capability;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class DeathTrackerCapability extends CapabilityComponent<Player> {
    public static final String TAG_LAST_DEATH_X = DeathFinder.id("last_death_x").toString();
    public static final String TAG_LAST_DEATH_Y = DeathFinder.id("last_death_y").toString();
    public static final String TAG_LAST_DEATH_Z = DeathFinder.id("last_death_z").toString();
    public static final String TAG_LAST_DEATH_DIMENSION = DeathFinder.id("last_death_dimension").toString();
    public static final String TAG_LAST_DEATH_TIME = DeathFinder.id("last_death_time").toString();
    
    private BlockPos lastDeathPosition = BlockPos.ZERO;
    private ResourceKey<Level> lastDeathDimension = Level.OVERWORLD;
    private long lastDeathTime;

    public void setLastDeathPosition(BlockPos lastDeathPosition) {
        if (!Objects.equals(this.lastDeathPosition, lastDeathPosition)) {
            this.lastDeathPosition = lastDeathPosition;
            this.setChanged();
        }
    }

    public void setLastDeathDimension(ResourceKey<Level> lastDeathDimension) {
        if (!Objects.equals(this.lastDeathDimension, lastDeathDimension)) {
            this.lastDeathDimension = lastDeathDimension;
            this.setChanged();
        }
    }

    public void setLastDeathTime() {
        this.lastDeathTime = System.currentTimeMillis();
        this.setChanged();
    }

    public Either<TeleportToDeathProblem, Unit> isValid(ResourceKey<Level> lastDeathDimension, BlockPos lastDeathPosition, int seconds) {
        if (this.lastDeathPosition == BlockPos.ZERO) return Either.left(TeleportToDeathProblem.ALREADY_USED);
        if (!this.lastDeathPosition.equals(lastDeathPosition) || !this.lastDeathDimension.equals(lastDeathDimension)) return Either.left(TeleportToDeathProblem.NOT_MOST_RECENT);
        if (seconds == -1 || Duration.between(Instant.ofEpochMilli(this.lastDeathTime), Instant.now()).toSeconds() < seconds) {
            return Either.right(Unit.INSTANCE);
        }
        return Either.left(TeleportToDeathProblem.TOO_LONG_AGO);
    }

    public void clear() {
        this.setLastDeathPosition(BlockPos.ZERO);
    }

    @Override
    public void write(CompoundTag tag) {
        if (!(this.lastDeathPosition == BlockPos.ZERO)) {
            tag.putInt(TAG_LAST_DEATH_X, this.lastDeathPosition.getX());
            tag.putInt(TAG_LAST_DEATH_Y, this.lastDeathPosition.getY());
            tag.putInt(TAG_LAST_DEATH_Z, this.lastDeathPosition.getZ());
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.lastDeathDimension.location()).resultOrPartial(DeathFinder.LOGGER::error).ifPresent((p_9134_) -> {
                tag.put(TAG_LAST_DEATH_DIMENSION, p_9134_);
            });
            tag.putLong(TAG_LAST_DEATH_TIME, this.lastDeathTime);
        }
    }

    @Override
    public void read(CompoundTag tag) {
        if (tag.contains(TAG_LAST_DEATH_X, Tag.TAG_ANY_NUMERIC) && tag.contains(TAG_LAST_DEATH_Y, Tag.TAG_ANY_NUMERIC) && tag.contains(TAG_LAST_DEATH_Z, Tag.TAG_ANY_NUMERIC)) {
            this.lastDeathPosition = new BlockPos(tag.getInt(TAG_LAST_DEATH_X), tag.getInt(TAG_LAST_DEATH_Y), tag.getInt(TAG_LAST_DEATH_Z));
            if (tag.contains(TAG_LAST_DEATH_DIMENSION)) {
                this.lastDeathDimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_LAST_DEATH_DIMENSION)).resultOrPartial(DeathFinder.LOGGER::error).orElse(Level.OVERWORLD);
            }
            if (tag.contains(TAG_LAST_DEATH_TIME)) {
                this.lastDeathTime = tag.getLong(TAG_LAST_DEATH_TIME);
            }
        }
    }
}
