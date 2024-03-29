package fuzs.deathfinder;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class DeathFinderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(DeathFinder.MOD_ID, DeathFinder::new);
    }
}
