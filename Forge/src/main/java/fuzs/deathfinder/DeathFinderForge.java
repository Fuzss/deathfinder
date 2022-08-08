package fuzs.deathfinder;

import fuzs.deathfinder.handler.DeathMessageHandler;
import fuzs.deathfinder.init.ForgeModRegistry;
import fuzs.puzzleslib.core.CoreServices;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(DeathFinder.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeathFinderForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CoreServices.FACTORIES.modConstructor(DeathFinder.MOD_ID).accept(new DeathFinder());
        registerHandlers();
        ForgeModRegistry.touch();
    }

    private static void registerHandlers() {
        final DeathMessageHandler deathMessageHandler = new DeathMessageHandler();
        MinecraftForge.EVENT_BUS.addListener((final LivingDeathEvent evt) -> {
            deathMessageHandler.onLivingDeath(evt.getEntity(), evt.getSource());
        });
    }
}
