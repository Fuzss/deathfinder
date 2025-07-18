package fuzs.deathfinder.neoforge;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.data.ModEntityTypeTagProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(DeathFinder.MOD_ID)
public class DeathFinderNeoForge {

    public DeathFinderNeoForge() {
        ModConstructor.construct(DeathFinder.MOD_ID, DeathFinder::new);
        DataProviderHelper.registerDataProviders(DeathFinder.MOD_ID, ModEntityTypeTagProvider::new);
    }
}
