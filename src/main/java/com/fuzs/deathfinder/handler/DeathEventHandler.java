package com.fuzs.deathfinder.handler;

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

        MessageDeathCoords message = new MessageDeathCoords(new MessageDeathCoords.MessageDeathCoordsData(entity.getCombatTracker().getDeathMessage(), entity));

        if (entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() instanceof ServerPlayerEntity) {

            NetworkHandler.sendTo(message, (ServerPlayerEntity) ((TameableEntity) entity).getOwner());

        } else if (ConfigHandler.GENERAL_CONFIG.namedEntities.get() && entity.hasCustomName()) {

            NetworkHandler.sendToAll(message);

        } else if (entity instanceof ServerPlayerEntity) {

            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            Team team = player.getTeam();

            if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {

                if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {

                    NetworkHandler.sendToAllTeamMembers(message, (ServerPlayerEntity) entity);

                } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {

                    NetworkHandler.sendToTeamOrAllPlayers(message, (ServerPlayerEntity) entity);

                }

            } else {

                NetworkHandler.sendToAll(message);

            }

        }

    }

}
