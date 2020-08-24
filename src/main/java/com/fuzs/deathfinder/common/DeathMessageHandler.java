package com.fuzs.deathfinder.common;

import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class DeathMessageHandler {

    private final DeathMessageHelper helper = new DeathMessageHelper();

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent evt) {

        LivingEntity entity = evt.getEntityLiving();

        if (!entity.getEntityWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) || entity.getServer() == null) {

            return;
        }

        DeathMessage message = new DeathMessage(entity);
        MessageSender sender = new MessageSender(entity.getServer());
        if (ConfigBuildHandler.MESSAGES_ALL.get() && this.helper.isAllowed(entity.getType())) {

            sender.sendMessage(message);
        } else if (entity instanceof ServerPlayerEntity && !entity.isSpectator()) {

            this.helper.handlePlayer((ServerPlayerEntity) entity, message, sender);
        } else if (entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() instanceof ServerPlayerEntity) {

            this.helper.handleTamed((ServerPlayerEntity) ((TameableEntity) entity).getOwner(), message);
        } else if (ConfigBuildHandler.MESSAGES_NAMED.get() && entity.hasCustomName()) {

            sender.sendMessage(message);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingDrops(final LivingDropsEvent evt) {

        // reset gamerule which has previously been disabled
        if (evt.getEntityLiving().getEntityWorld() instanceof ServerWorld && this.helper.getReset()) {

            evt.getEntityLiving().getEntityWorld().getGameRules().get(GameRules.SHOW_DEATH_MESSAGES).set(true, null);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (evt.getConfig().getSpec() == ConfigBuildHandler.SPEC) {

            this.helper.sync();
        }
    }

}
