package fuzs.deathfinder.client.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.client.C2SDeathPointTeleportMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;

public class DeathCommandHandler {

    public boolean onMouseClicked(Screen screen, double mouseX, double mouseY, int button) {
        Minecraft minecraft = Minecraft.getInstance();
        ChatComponent chatcomponent = minecraft.gui.getChat();
        Style style = chatcomponent.getClickedComponentStyleAt(mouseX, mouseY);
        if (this.handleComponentClicked(style)) {
            minecraft.setScreen(null);
            return false;
        }
        return true;
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
