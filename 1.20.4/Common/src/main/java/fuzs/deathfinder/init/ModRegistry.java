package fuzs.deathfinder.init;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.DeathTrackerCapability;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class ModRegistry {
    static final BoundTagFactory TAGS = BoundTagFactory.make(DeathFinder.MOD_ID);
    public static final TagKey<EntityType<?>> SILENT_DEATHS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("silent_deaths");

    static final CapabilityController CAPABILITIES = CapabilityController.from(DeathFinder.MOD_ID);
    public static final EntityCapabilityKey<Player, DeathTrackerCapability> PLAYER_DEATH_TRACKER_CAPABILITY = CAPABILITIES.registerEntityCapability(
            "death_tracker",
            DeathTrackerCapability.class,
            DeathTrackerCapability::new,
            Player.class
    ).setCopyStrategy(CopyStrategy.ALWAYS);

    public static void touch() {

    }
}
