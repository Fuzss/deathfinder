package com.fuzs.deathfinder.handler;

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
    public void drawScreen(GuiScreenEvent.DrawScreenEvent evt) {

        if (!ConfigHandler.GENERAL_CONFIG.deathScreen.get()) {
            return;
        }

        if (evt.getGui() instanceof DeathScreen) {

            float x = Math.round(this.mc.player.posX * 10.0) / 10.0f;
            float y = Math.round(this.mc.player.getBoundingBox().minY * 10.0) / 10.0f;
            float z = Math.round(this.mc.player.posZ * 10.0) / 10.0f;

            ITextComponent textComponent = new TranslationTextComponent("deathScreen.coordinates", x, y, z);
            evt.getGui().drawCenteredString(this.mc.fontRenderer, textComponent.getFormattedText(), evt.getGui().width / 2, 115, 16777215);

        }

    }

}
