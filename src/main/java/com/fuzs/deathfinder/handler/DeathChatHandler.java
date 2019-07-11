package com.fuzs.deathfinder.handler;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathChatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent evt) {

        if (evt.getMessage() instanceof TextComponentTranslation) {

            TextComponentTranslation textcomponent = (TextComponentTranslation) evt.getMessage();
            String s = textcomponent.getKey();

            if (s.matches("death\\..*")) {

                evt.setCanceled(true);

            }

        }

    }

}
