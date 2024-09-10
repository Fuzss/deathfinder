package fuzs.deathfinder.neoforge;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.data.ModEntityTypeTagProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(DeathFinder.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeathFinderNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(DeathFinder.MOD_ID, DeathFinder::new);
        DataProviderHelper.registerDataProviders(DeathFinder.MOD_ID, ModEntityTypeTagProvider::new);
    }
}
