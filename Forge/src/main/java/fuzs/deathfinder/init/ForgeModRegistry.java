package fuzs.deathfinder.init;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import fuzs.deathfinder.capability.PlayerDeathTrackerImpl;
import fuzs.puzzleslib.capability.ForgeCapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ForgeModRegistry {
    private static final ForgeCapabilityController CAPABILITIES = ForgeCapabilityController.of(DeathFinder.MOD_ID);
    public static final CapabilityKey<PlayerDeathTracker> PLAYER_DEATH_TRACKER_CAPABILITY = CAPABILITIES.registerPlayerCapability("death_tracker", PlayerDeathTracker.class, player -> new PlayerDeathTrackerImpl(), PlayerRespawnStrategy.ALWAYS_COPY, new CapabilityToken<PlayerDeathTracker>() {});

    public static void touch() {

    }
}
