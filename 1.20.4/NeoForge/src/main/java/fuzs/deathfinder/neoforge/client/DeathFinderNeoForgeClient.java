package fuzs.deathfinder.neoforge.client;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.client.DeathFinderClient;
import fuzs.deathfinder.data.ModEntityTypeTagProvider;
import fuzs.deathfinder.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = DeathFinder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DeathFinderNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(DeathFinder.MOD_ID, DeathFinderClient::new);
        DataProviderHelper.registerDataProviders(DeathFinder.MOD_ID, ModLanguageProvider::new);
    }
}
