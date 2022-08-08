package fuzs.deathfinder.init;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import fuzs.deathfinder.capability.PlayerDeathTrackerImpl;
import fuzs.puzzleslib.capability.FabricCapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;

public class FabricModRegistry {
    private static final FabricCapabilityController CAPABILITIES = FabricCapabilityController.of(DeathFinder.MOD_ID);
    public static final CapabilityKey<PlayerDeathTracker> PLAYER_DEATH_TRACKER_CAPABILITY = CAPABILITIES.registerPlayerCapability("death_tracker", PlayerDeathTracker.class, player -> new PlayerDeathTrackerImpl(), PlayerRespawnStrategy.ALWAYS_COPY);

    public static void touch() {

    }
}
