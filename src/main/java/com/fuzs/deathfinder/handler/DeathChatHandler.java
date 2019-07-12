package com.fuzs.deathfinder.handler;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathChatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent evt) {

        if (evt.getMessage() instanceof TranslationTextComponent) {

            TranslationTextComponent textcomponent = (TranslationTextComponent) evt.getMessage();
            String s = textcomponent.getKey();

            if (s.matches("death\\..*")) {

                evt.setCanceled(true);

            }

        }

    }

}
