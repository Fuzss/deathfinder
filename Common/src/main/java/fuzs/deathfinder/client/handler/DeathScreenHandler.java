package fuzs.deathfinder.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class DeathScreenHandler {
    private final Minecraft minecraft = Minecraft.getInstance();
    private BlockPos lastPlayerPosition = BlockPos.ZERO;

    public void onDrawScreen(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
        if (!DeathFinder.CONFIG.get(ClientConfig.class).deathScreenCoordinates) return;
        if (this.lastPlayerPosition != BlockPos.ZERO) {
            Component component = Component.translatable("death.screen.position", Component.literal(String.valueOf(this.lastPlayerPosition.getX())).withStyle(ChatFormatting.WHITE), Component.literal(String.valueOf(this.lastPlayerPosition.getY())).withStyle(ChatFormatting.WHITE), Component.literal(String.valueOf(this.lastPlayerPosition.getZ())).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GOLD);
            GuiComponent.drawCenteredString(poseStack, this.minecraft.font, component, screen.width / 2, 115, 16777215);
        }
    }

    public boolean onScreenOpen(Screen newScreen) {
        if (newScreen instanceof DeathScreen) {
            // when canceling death message on server, death screen package is still sent (arrives after ours though)
            // so we intercept it here and keep our screen
            if (this.minecraft.screen instanceof DeathScreen) {
                return false;
            } else {
                this.lastPlayerPosition = this.minecraft.player.blockPosition();
            }
        }
        return true;
    }
}
