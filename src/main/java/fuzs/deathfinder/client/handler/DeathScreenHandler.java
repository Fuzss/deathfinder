package fuzs.deathfinder.client.handler;

import fuzs.deathfinder.DeathFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;

public class DeathScreenHandler {
    private final Minecraft minecraft = Minecraft.getInstance();
    private BlockPos lastPlayerPosition = BlockPos.ZERO;

    public void onDrawScreen(final ScreenEvent.DrawScreenEvent evt) {
        if (!DeathFinder.CONFIG.client().deathScreenCoordinates) return;
        Screen deathScreen = evt.getScreen();
        if (deathScreen instanceof DeathScreen && this.lastPlayerPosition != BlockPos.ZERO) {
            Component component = new TranslatableComponent("death.screen.coordinates", this.lastPlayerPosition.getX(), this.lastPlayerPosition.getY(), this.lastPlayerPosition.getZ());
            GuiComponent.drawCenteredString(evt.getPoseStack(), this.minecraft.font, component, deathScreen.width / 2, 115, 16777215);
        }
    }

    public void onScreenOpen(final ScreenOpenEvent evt) {
        if (evt.getScreen() instanceof DeathScreen) {
            // when canceling death message on server, death screen package is still sent (arrives after ours though)
            // so we intercept it here and keep our screen
            if (this.minecraft.screen instanceof DeathScreen) {
                evt.setCanceled(true);
            } else {
                this.lastPlayerPosition = this.minecraft.player.blockPosition();
            }
        }
    }
}
