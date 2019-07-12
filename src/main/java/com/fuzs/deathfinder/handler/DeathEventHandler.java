package com.fuzs.deathfinder.handler;

import com.fuzs.deathfinder.helper.DeathChatHelper;
import com.fuzs.deathfinder.network.NetworkHandler;
import com.fuzs.deathfinder.network.message.MessageDeathCoords;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void livingDeath(LivingDeathEvent evt) {

        LivingEntity entity = evt.getEntityLiving();

        if (!ConfigHandler.GENERAL_CONFIG.deathMessage.get() || evt.isCanceled() || entity.world.isRemote || !entity.world.getGameRules().func_223586_b(GameRules.field_223609_l)) {
            return;
        }

        MessageDeathCoords.MessageDeathCoordsData messageData = new MessageDeathCoords.MessageDeathCoordsData(entity.getCombatTracker().getDeathMessage(), entity);

        if (ConfigHandler.GENERAL_CONFIG.tamedEntities.get() && entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() instanceof ServerPlayerEntity) {

            messageData.setType(DeathChatHelper.DeathEntityType.TAMED);
            NetworkHandler.sendTo(new MessageDeathCoords(messageData), (ServerPlayerEntity) ((TameableEntity) entity).getOwner());

        } else if (ConfigHandler.GENERAL_CONFIG.namedEntities.get() && entity.hasCustomName()) {

            messageData.setType(DeathChatHelper.DeathEntityType.NAMED);
            NetworkHandler.sendToAll(new MessageDeathCoords(messageData));

        } else if (ConfigHandler.GENERAL_CONFIG.playerEntities.get() && entity instanceof ServerPlayerEntity) {

            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            Team team = player.getTeam();
            messageData.setType(DeathChatHelper.DeathEntityType.PLAYER);

            if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {

                if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {

                    NetworkHandler.sendToAllTeamMembers(new MessageDeathCoords(messageData), (ServerPlayerEntity) entity);

                } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {

                    NetworkHandler.sendToTeamOrAllPlayers(new MessageDeathCoords(messageData), (ServerPlayerEntity) entity);

                }

            } else {

                NetworkHandler.sendToAll(new MessageDeathCoords(messageData));

            }

        }

    }

}
