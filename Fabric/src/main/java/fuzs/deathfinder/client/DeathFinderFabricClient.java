package fuzs.deathfinder.client;

import fuzs.deathfinder.api.client.event.AttemptScreenOpenCallback;
import fuzs.deathfinder.client.handler.DeathCommandHandler;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;

public class DeathFinderFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        onConstructMod();
    }

    public static void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        final DeathScreenHandler deathScreenHandler = new DeathScreenHandler();
        final DeathCommandHandler deathCommandHandler = new DeathCommandHandler();
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof DeathScreen) {
                ScreenEvents.afterRender(screen).register(deathScreenHandler::onDrawScreen);
            }
            if (screen instanceof ChatScreen) {
                ScreenMouseEvents.allowMouseClick(screen).register(deathCommandHandler::onMouseClicked);
            }
        });
        AttemptScreenOpenCallback.EVENT.register(deathScreenHandler::onScreenOpen);
    }
}
