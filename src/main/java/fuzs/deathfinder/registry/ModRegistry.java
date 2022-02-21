package fuzs.deathfinder.registry;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import fuzs.deathfinder.capability.PlayerDeathTrackerImpl;
import fuzs.deathfinder.core.capability.CapabilityController;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModRegistry {
    private static final CapabilityController CAPABILITIES = CapabilityController.of(DeathFinder.MOD_ID);
    public static final Capability<PlayerDeathTracker> PLAYER_DEATH_TRACKER_CAPABILITY = CAPABILITIES.registerEntityCapability("death_tracker", PlayerDeathTracker.class, PlayerDeathTrackerImpl::new, entity -> entity instanceof Player, new CapabilityToken<PlayerDeathTracker>() {});

    public static void touch() {

    }
}
