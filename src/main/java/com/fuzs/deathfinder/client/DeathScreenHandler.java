package com.fuzs.deathfinder.client;

import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathScreenHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onDrawScreen(final GuiScreenEvent.DrawScreenEvent evt) {

        if (!ConfigBuildHandler.DEATH_SCREEN.get()) {

            return;
        }

        if (evt.getGui() instanceof DeathScreen && this.mc.player != null) {

            int x = (int) this.mc.player.getPosX(), y = (int) this.mc.player.getPosY(), z = (int) this.mc.player.getPosZ();
            ITextComponent textComponent = new TranslationTextComponent("death.screen.coordinates", x, y, z);
            evt.getGui().drawCenteredString(this.mc.fontRenderer, textComponent.getFormattedText(), evt.getGui().width / 2, 115, 16777215);
        }
    }

}
