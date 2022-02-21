package fuzs.deathfinder.core.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface CapabilityComponent extends INBTSerializable<CompoundTag> {
    void write(CompoundTag tag);

    void read(CompoundTag tag);

    @Override
    default CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag tag) {
        this.read(tag);
    }
}
