package fuzs.deathfinder.core.capability.data;

import net.minecraft.nbt.CompoundTag;

public abstract class PlayerRespawnStrategy {
    public static final PlayerRespawnStrategy ALWAYS_COPY = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            this.actuallyCopy(oldCapability, newCapability);
        }
    };
    public static final PlayerRespawnStrategy INVENTORY = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            if (returningFromEnd || keepInventory)
            this.actuallyCopy(oldCapability, newCapability);
        }
    };
    public static final PlayerRespawnStrategy LOSSLESS = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            if (returningFromEnd)
            this.actuallyCopy(oldCapability, newCapability);
        }
    };
    public static final PlayerRespawnStrategy NEVER = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {

        }
    };

    void actuallyCopy(CapabilityComponent oldCapability, CapabilityComponent newCapability) {
        CompoundTag tag = new CompoundTag();
        oldCapability.write(tag);
        newCapability.read(tag);
    }

    public abstract void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory);
}
