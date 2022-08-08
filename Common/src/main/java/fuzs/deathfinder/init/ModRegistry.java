package fuzs.deathfinder.init;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityKey;

public class ModRegistry {
    public static final CapabilityKey<PlayerDeathTracker> PLAYER_DEATH_TRACKER_CAPABILITY = CapabilityController.makeCapabilityKey(DeathFinder.MOD_ID, "death_tracker", PlayerDeathTracker.class);

    public static void touch() {

    }
}
