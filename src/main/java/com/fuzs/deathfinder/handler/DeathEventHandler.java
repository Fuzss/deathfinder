package com.fuzs.deathfinder.handler;

import com.fuzs.deathfinder.network.NetworkHandler;
import com.fuzs.deathfinder.network.messages.MessageDeathCoords;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void livingDeath(LivingDeathEvent evt) {

        if (!evt.getEntityLiving().world.getGameRules().getBoolean("showDeathMessages") || !ConfigHandler.deathMessage) {
            return;
        }

        if (evt.getEntityLiving() instanceof EntityPlayerMP || (evt.getEntityLiving() instanceof EntityTameable && ((EntityTameable) evt.getEntityLiving()).getOwner() instanceof EntityPlayerMP)) {

            NetworkHandler.sendToAll(new MessageDeathCoords(evt.getEntityLiving()));

        }

    }

}
