package fuzs.deathfinder.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ClientConfig;
import fuzs.puzzleslib.api.client.screen.v2.ScreenHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

public class DeathScreenHandler {
    private static BlockPos lastPlayerPosition = BlockPos.ZERO;

    public static void onDrawScreen(DeathScreen screen, PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
        if (!DeathFinder.CONFIG.get(ClientConfig.class).deathScreenCoordinates) return;
        if (lastPlayerPosition != BlockPos.ZERO) {
            Component component = new TranslatableComponent("death.screen.position", new TextComponent(String.valueOf(lastPlayerPosition.getX())).withStyle(ChatFormatting.WHITE), new TextComponent(String.valueOf(lastPlayerPosition.getY())).withStyle(ChatFormatting.WHITE), new TextComponent(String.valueOf(lastPlayerPosition.getZ())).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GOLD);
            Font font = ScreenHelper.INSTANCE.getFont(screen);
            GuiComponent.drawCenteredString(poseStack, font, component, screen.width / 2, 115, 16777215);
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
