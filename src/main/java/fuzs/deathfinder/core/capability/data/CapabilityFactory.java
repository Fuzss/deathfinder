package fuzs.deathfinder.core.capability.data;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

@FunctionalInterface
public interface CapabilityFactory<C extends CapabilityComponent> {
    C create(Object t);
}
