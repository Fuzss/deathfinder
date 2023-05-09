package fuzs.deathfinder.init;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import fuzs.deathfinder.capability.PlayerDeathTrackerImpl;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import fuzs.puzzleslib.api.capability.v2.data.PlayerRespawnStrategy;

public class ModRegistry {
    private static final CapabilityController CAPABILITIES = CapabilityController.from(DeathFinder.MOD_ID);
    public static final CapabilityKey<PlayerDeathTracker> PLAYER_DEATH_TRACKER_CAPABILITY = CAPABILITIES.registerPlayerCapability("death_tracker", PlayerDeathTracker.class, player -> new PlayerDeathTrackerImpl(), PlayerRespawnStrategy.ALWAYS_COPY);

    public static void touch() {

    }
}
