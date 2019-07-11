package com.fuzs.deathfinder.handler;

import com.fuzs.deathfinder.helper.DeathChatHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathChatHandler {

    public static ITextComponent name;
    public static int dimension;
    public static int x;
    public static int y;
    public static int z;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final DeathChatHelper helper = new DeathChatHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent evt) {

        if (evt.getMessage() instanceof TextComponentTranslation) {

            if (DeathChatHandler.name == null) {
                return;
            }

            TextComponentTranslation textcomponent = (TextComponentTranslation) evt.getMessage();
            String s = textcomponent.getKey();

            if (s.matches("death\\..*") && ((ITextComponent) textcomponent.getFormatArgs()[0]).getUnformattedText().equals(DeathChatHandler.name.getUnformattedText())) {

                String command = String.format("/tpx @s %d %d %d %d", DeathChatHandler.x, DeathChatHandler.y, DeathChatHandler.z, DeathChatHandler.dimension);
                Style style = new Style().setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("death.message.tooltip")));

                ITextComponent componentCoordinates = new TextComponentTranslation("death.message.coordinates", DeathChatHandler.x, DeathChatHandler.y, DeathChatHandler.z).setStyle(style);
                ITextComponent componentDistance = this.helper.getDistanceComponent(this.mc.player);
                ITextComponent componentMessage = new TextComponentTranslation("death.message.location", componentCoordinates, DeathChatHandler.dimension, componentDistance);

                textcomponent.appendSibling(new TextComponentString(" ")).appendSibling(componentMessage);

                DeathChatHandler.name = null;

            }

        }

    }

}
