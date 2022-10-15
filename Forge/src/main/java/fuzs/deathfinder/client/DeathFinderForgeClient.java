package fuzs.deathfinder.client;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.client.handler.DeathCommandHandler;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = DeathFinder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DeathFinderForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        final DeathScreenHandler deathScreenHandler = new DeathScreenHandler();
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.Render.Post evt) -> {
            if (evt.getScreen() instanceof DeathScreen) {
                deathScreenHandler.onDrawScreen(evt.getScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTick());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.Opening evt) -> {
            boolean result = deathScreenHandler.onScreenOpen(evt.getNewScreen());
            if (!result) evt.setCanceled(true);
        });
        final DeathCommandHandler deathCommandHandler = new DeathCommandHandler();
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.MouseButtonPressed.Pre evt) -> {
            if (evt.getScreen() instanceof ChatScreen) {
                boolean result = deathCommandHandler.onMouseClicked(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
                if (!result) evt.setCanceled(true);
            }
        });
    }
}
