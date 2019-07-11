package com.fuzs.deathfinder.handler;

import com.fuzs.deathfinder.helper.DeathChatHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathChatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent evt) {

        if (evt.getMessage() instanceof TextComponentTranslation) {

            TextComponentTranslation textcomponent = (TextComponentTranslation) evt.getMessage();
            String s = textcomponent.getKey();

            if (!s.matches("death\\..*")) {
                return;
            }

            DeathChatHelper.DeathMessageBuffer buffer = DeathChatHelper.findBuffer(((ITextComponent) textcomponent.getFormatArgs()[0]).getUnformattedText());

            if (buffer != null) {

                String command = String.format(ConfigHandler.deathMessageCommand, buffer.pos.getX(), buffer.pos.getY(), buffer.pos.getZ(), buffer.dim);
                Style style = new Style().setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("death.message.tooltip")));

                ITextComponent componentCoordinates = new TextComponentTranslation("death.message.coordinates", buffer.pos.getX(), buffer.pos.getY(), buffer.pos.getZ()).setStyle(style);
                ITextComponent componentDistance = DeathChatHelper.getDistanceComponent(buffer);
                ITextComponent componentMessage = new TextComponentTranslation("death.message.location", componentCoordinates, buffer.dim, componentDistance);

                textcomponent.appendSibling(new TextComponentString(" ")).appendSibling(componentMessage);

            } else {
                DeathChatHelper.deathMessageBuffer.add(new DeathChatHelper.DeathMessageBuffer(null, 0, null));
            }

        }

    }

}
