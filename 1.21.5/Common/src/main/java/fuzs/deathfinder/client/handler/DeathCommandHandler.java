package fuzs.deathfinder.client.handler;

import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.client.ServerboundDeathPointTeleportMessage;
import fuzs.deathfinder.network.client.ServerboundNotifyModPresentMessage;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Style;

public class DeathCommandHandler {

    public static EventResult onMouseClicked(ChatScreen screen, double mouseX, double mouseY, int button) {
        ChatComponent chat = screen.minecraft.gui.getChat();
        Style style = chat.getClickedComponentStyleAt(mouseX, mouseY);
        if (handleComponentClicked(style)) {
            screen.minecraft.setScreen(null);
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    private static boolean handleComponentClicked(Style style) {
        if (style == null || Screen.hasShiftDown()) {
            return false;
        } else if (style.getClickEvent() instanceof TeleportClickEvent event) {
            MessageSender.broadcast(new ServerboundDeathPointTeleportMessage(event));
            return true;
        } else {
            return false;
        }
    }

    public static void onLoggedIn(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        MessageSender.broadcast(new ServerboundNotifyModPresentMessage());
    }
}
