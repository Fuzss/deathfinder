package fuzs.deathfinder.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface AttemptScreenOpenCallback {
    Event<AttemptScreenOpenCallback> EVENT = EventFactory.createArrayBacked(AttemptScreenOpenCallback.class, listeners -> (Screen newScreen) -> {
        for (AttemptScreenOpenCallback event : listeners) {
            if (!event.onAttemptScreenOpen(newScreen)) {
                return false;
            }
        }
        return true;
    });

    /**
     * called when a new screen is trying to be opened
     * returning false will discard the new screen, resulting in the old screen remaining open
     * @param newScreen the new screen being opened
     * @return is the <code>newScreen</code> allowed to be opened or should the old one be kept
     */
    boolean onAttemptScreenOpen(Screen newScreen);
}