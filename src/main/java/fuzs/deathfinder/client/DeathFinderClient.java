package fuzs.deathfinder.client;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.client.handler.DeathCommandHandler;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = DeathFinder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DeathFinderClient {
    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        final DeathScreenHandler deathScreenHandler = new DeathScreenHandler();
        MinecraftForge.EVENT_BUS.addListener(deathScreenHandler::onDrawScreen);
        MinecraftForge.EVENT_BUS.addListener(deathScreenHandler::onScreenOpen);
        final DeathCommandHandler deathCommandHandler = new DeathCommandHandler();
        MinecraftForge.EVENT_BUS.addListener(deathCommandHandler::onMouseClicked$Pre);
    }
}
