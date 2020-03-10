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

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent evt) {

        LivingEntity entity = evt.getEntityLiving();

        if (!ConfigBuildHandler.GENERAL_CONFIG.deathMessage.get() || entity.getServer() == null ||
                !entity.getEntityWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {

            return;
        }

        DeathMessage message = new DeathMessage(entity);
        MessageSender sender = new MessageSender(entity.getServer());
        if (ConfigBuildHandler.GENERAL_CONFIG.all.get() && this.helper.isAllowed(entity.getType())) {

            sender.sendMessage(message);
        } else if (ConfigBuildHandler.GENERAL_CONFIG.players.get() && entity instanceof ServerPlayerEntity && !entity.isSpectator()) {

            this.helper.handlePlayer((ServerPlayerEntity) entity, message, sender);
        } else if (ConfigBuildHandler.GENERAL_CONFIG.tamed.get() && entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() instanceof ServerPlayerEntity) {

            this.helper.handleTamed((TameableEntity) entity, message, (ServerPlayerEntity) ((TameableEntity) entity).getOwner());
        } else if (ConfigBuildHandler.GENERAL_CONFIG.named.get() && entity.hasCustomName()) {

            sender.sendMessage(message);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
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

            this.helper.syncBlacklist();
        }
    }

}
