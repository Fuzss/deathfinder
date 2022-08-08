package fuzs.deathfinder.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;

/**
 * an extension to {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents} to include more functionality found on Forge
 */
public class ExtraScreenEvents {
    public static final Event<Opening> OPENING = EventFactory.createArrayBacked(Opening.class, listeners -> (Screen oldScreen, Screen newScreen) -> {
        for (Opening event : listeners) {
            Screen screen = event.onScreenOpening(oldScreen, newScreen);
            if (screen != newScreen) {
                return screen;
            }
        }
        return newScreen;
    });
    public static final Event<Closing> CLOSING = EventFactory.createArrayBacked(Closing.class, listeners -> (Screen screen) -> {
        for (Closing event : listeners) {
            event.onScreenClosing(screen);
        }
    });

    @FunctionalInterface
    public interface Opening {

        /**
         * called just before a new screen is set to {@link net.minecraft.client.Minecraft#screen} in {@link net.minecraft.client.Minecraft#setScreen},
         * allows for replacing the new screen with a different one returned by this callback;
         * IMPORTANT: for cancelling a new screen from being set and to keep the old one, simply return <code>oldScreen</code>
         * this is equivalent to cancelling the event on Forge;
         * DO NOT use {@link net.minecraft.client.Minecraft#setScreen} inside of your event callback, there will be an infinite loop
         *
         * @param oldScreen     the screen that is being removed
         * @param newScreen     the new screen that is being set
         * @return              the screen that is actually going to be set, <code>newScreen</code> by default
         */
        Screen onScreenOpening(Screen oldScreen, Screen newScreen);
    }

    @FunctionalInterface
    public interface Closing {

        /**
         * called just before a screen is closed in {@link net.minecraft.client.Minecraft#setScreen}, {@link net.minecraft.client.Minecraft#screen} still has the old screen
         *
         * @param screen        the screen that has been closed
         */
        void onScreenClosing(Screen screen);
    }
}
