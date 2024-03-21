package fuzs.deathfinder.client.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ClientConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class DeathScreenHandler {
    public static final String KEY_DEATH_SCREEN_POSITION = "death.screen.position";

    private static BlockPos lastPlayerPosition = BlockPos.ZERO;

    public static void onDrawScreen(DeathScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        if (!DeathFinder.CONFIG.get(ClientConfig.class).deathScreenCoordinates) return;
        if (lastPlayerPosition != BlockPos.ZERO) {
            Component component = Component.translatable(KEY_DEATH_SCREEN_POSITION, Component.literal(String.valueOf(lastPlayerPosition.getX())).withStyle(ChatFormatting.WHITE), Component.literal(String.valueOf(lastPlayerPosition.getY())).withStyle(ChatFormatting.WHITE), Component.literal(String.valueOf(lastPlayerPosition.getZ())).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GOLD);
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.drawCenteredString(minecraft.font, component, screen.width / 2, 115, 16777215);
        }
    }

    public static EventResult onScreenOpen(@Nullable Screen oldScreen, DefaultedValue<Screen> newScreen) {
        if (newScreen.get() instanceof DeathScreen) {
            // when canceling death message on server, death screen package is still sent (arrives after ours though)
            // so we intercept it here and keep our screen
            if (oldScreen instanceof DeathScreen) {
                return EventResult.INTERRUPT;
            } else {
                Minecraft minecraft = Minecraft.getInstance();
                lastPlayerPosition = minecraft.player.blockPosition();
            }
        }

        return EventResult.PASS;
    }
}
