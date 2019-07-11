package com.fuzs.deathfinder.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathScreenHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void drawScreen(GuiScreenEvent.DrawScreenEvent evt) {

        if (!ConfigHandler.deathScreen) {
            return;
        }

        if (evt.getGui() instanceof GuiGameOver) {

            float x = Math.round(this.mc.player.posX * 10.0) / 10.0f;
            float y = Math.round(this.mc.player.getEntityBoundingBox().minY * 10.0) / 10.0f;
            float z = Math.round(this.mc.player.posZ * 10.0) / 10.0f;

            ITextComponent textComponent = new TextComponentTranslation("deathScreen.coordinates", x, y, z);
            evt.getGui().drawCenteredString(this.mc.fontRenderer, textComponent.getFormattedText(), evt.getGui().width / 2, 115, 16777215);

        }

    }

}
