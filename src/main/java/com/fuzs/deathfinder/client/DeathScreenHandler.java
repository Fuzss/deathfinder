package com.fuzs.deathfinder.client;

import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class DeathScreenHandler {

    private final Minecraft mc = Minecraft.getInstance();

    // local storage for player position as they might be moved after dying
    private int posX;
    private int posY;
    private int posZ;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onDrawScreen(final GuiScreenEvent.DrawScreenEvent evt) {

        if (!ConfigBuildHandler.DEATH_SCREEN.get()) {

            return;
        }

        Screen deathScreen = evt.getGui();
        if (deathScreen instanceof DeathScreen && this.mc.player != null) {

            if (this.posX == 0 && this.posY == 0 && this.posZ == 0) {

                this.posX = (int) this.mc.player.getPosX();
                this.posY = (int) this.mc.player.getPosY();
                this.posZ = (int) this.mc.player.getPosZ();
            }

            IFormattableTextComponent textComponent = new TranslationTextComponent("death.screen.coordinates", this.posX, this.posY, this.posZ);
            AbstractGui.drawCenteredString(evt.getMatrixStack(), this.mc.fontRenderer, textComponent, deathScreen.width / 2, 115, 16777215);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiOpen(final GuiOpenEvent evt) {

        if (evt.getGui() instanceof DeathScreen) {

            // package for displaying death screen is sent twice, we only want the first one
            if (this.mc.currentScreen instanceof DeathScreen) {

                evt.setCanceled(true);
                return;
            }

            this.posX = this.posY = this.posZ = 0;
        }
    }

}
