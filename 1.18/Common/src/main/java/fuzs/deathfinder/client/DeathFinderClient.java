package fuzs.deathfinder.client;

import fuzs.deathfinder.client.handler.DeathCommandHandler;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.ScreenOpeningCallback;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;

public class DeathFinderClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ScreenEvents.afterRender(DeathScreen.class).register(DeathScreenHandler::onDrawScreen);
        ScreenMouseEvents.beforeMouseClick(ChatScreen.class).register(DeathCommandHandler::onMouseClicked);
        ScreenOpeningCallback.EVENT.register(DeathScreenHandler::onScreenOpen);
//        ItemTooltipCallback.EVENT.register(CompassTooltipHandler::onItemTooltip);
    }
}
