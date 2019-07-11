package com.fuzs.deathfinder.handler;

import com.fuzs.deathfinder.network.NetworkHandler;
import com.fuzs.deathfinder.network.messages.MessageDeathCoords;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void livingDeath(LivingDeathEvent evt) {

        EntityLivingBase entity = evt.getEntityLiving();

        if (!ConfigHandler.deathMessage || evt.isCanceled() || entity.world.isRemote || !entity.world.getGameRules().getBoolean("showDeathMessages")) {
            return;
        }

        MessageDeathCoords message = new MessageDeathCoords(entity.getCombatTracker().getDeathMessage(), entity);

        if (entity instanceof EntityTameable && ((EntityTameable) entity).getOwner() instanceof EntityPlayerMP) {

            NetworkHandler.sendTo(message, (EntityPlayerMP) ((EntityTameable) entity).getOwner());

        } else if (ConfigHandler.namedEntities && entity.hasCustomName()) {

            NetworkHandler.sendToAll(message);

        } else if (entity instanceof EntityPlayerMP) {

            EntityPlayerMP player = (EntityPlayerMP) entity;
            Team team = player.getTeam();

            if (team != null && team.getDeathMessageVisibility() != Team.EnumVisible.ALWAYS) {

                if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {

                    NetworkHandler.sendToAllTeamMembers(message, (EntityPlayerMP) entity);

                } else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {

                    NetworkHandler.sendToTeamOrAllPlayers(message, (EntityPlayerMP) entity);

                }

            } else {

                NetworkHandler.sendToAll(message);

            }

        }

    }

}
