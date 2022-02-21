package fuzs.deathfinder.core.capability.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * dispatcher for this serializable capability
 * @param <T> capability class
 */
public class CapabilityDispatcher<T extends CapabilityComponent> implements ICapabilitySerializable<CompoundTag> {
    /**
     * capability wrapper for object
     */
    private final Capability<T> capability;
    /**
     * capability object
     */
    private final T storage;

    /**
     * @param storage object
     * @param capability wrapper
     */
    public CapabilityDispatcher(Capability<T> capability, T storage) {
        this.capability = capability;
        this.storage = storage;
    }

    @Nonnull
    @Override
    public <S> LazyOptional<S> getCapability(@Nonnull Capability<S> capability, @Nullable Direction facing) {
        return capability == this.capability  ? LazyOptional.of(() -> this.storage).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.storage.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.storage.deserializeNBT(tag);
    }
}
