package fuzs.deathfinder.client.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.client.message.C2SDeathPointTeleportMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathCommandHandler {
    @SubscribeEvent
    public void onMouseClicked$Pre(final ScreenEvent.MouseClickedEvent.Pre evt) {
        if (evt.getScreen() instanceof ChatScreen) {
            Minecraft minecraft = Minecraft.getInstance();
            ChatComponent chatcomponent = minecraft.gui.getChat();
            Style style = chatcomponent.getClickedComponentStyleAt(evt.getMouseX(), evt.getMouseY());
            if (this.handleComponentClicked(style)) {
                evt.setCanceled(true);
                minecraft.setScreen(null);
            }
        }
    }

    private boolean handleComponentClicked(Style style) {
        if (style == null || Screen.hasShiftDown()) return false;
        if (style.getClickEvent() instanceof TeleportClickEvent event) {
            DeathFinder.NETWORK.sendToServer(new C2SDeathPointTeleportMessage(event));
            return true;
        }
        return false;
    }
}
