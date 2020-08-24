package com.fuzs.deathfinder.common;

import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;

public class DeathMessage {

    private final LivingEntity entity;

    public DeathMessage(LivingEntity entity) {

        this.entity = entity;
    }

    public IFormattableTextComponent getMessage() {

        return ConfigBuildHandler.DEATH_MESSAGE.get() ? this.getCoordinateMessage(null) : this.getSimpleMessage();
    }

    public IFormattableTextComponent getMessage(PlayerEntity player) {

        return ConfigBuildHandler.DEATH_MESSAGE.get() ? this.getCoordinateMessage(player)
                .append(this.getDistanceComponent(player)) : this.getSimpleMessage();
    }

    private IFormattableTextComponent getSimpleMessage() {

        return (TranslationTextComponent) this.entity.getCombatTracker().getDeathMessage();
    }

    private IFormattableTextComponent getCoordinateMessage(@Nullable PlayerEntity player) {

        return this.getSimpleMessage().append(this.getCoordinateComponent(player));
    }

    private ITextComponent getCoordinateComponent(@Nullable PlayerEntity player) {

        String type = this.entity.world.getDimensionKey().func_240901_a_().toString();
        int x = (int) this.entity.getPosX(), y = (int) this.entity.getPosY(), z = (int) this.entity.getPosZ();
        IFormattableTextComponent itextcomponent = TextComponentUtils.wrapWithSquareBrackets(new TranslationTextComponent("chat.coordinates", x, y, z));
        if (player != null && player.hasPermissionLevel(2)) {

            itextcomponent.modifyStyle(style -> style.setFormatting(TextFormatting.GREEN).setClickEvent(new ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + type + " run tp @s " + x + " " + y + " " + z))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip"))));
        }

        return new TranslationTextComponent("death.message.location", itextcomponent, type);
    }

    private ITextComponent getDistanceComponent(PlayerEntity player) {

        return new TranslationTextComponent("death.message.distance", this.getDistance(player));
    }

    private ITextComponent getDistance(PlayerEntity player) {

        if (this.entity.world.getDimensionKey() != player.world.getDimensionKey()) {

            return new TranslationTextComponent("death.message.distance.dimension");
        } else {

            int distance = (int) this.entity.getPositionVec().distanceTo(player.getPositionVec());
            if (distance < 2) {

                return new TranslationTextComponent("death.message.distance.close");
            } else {

                return new TranslationTextComponent("death.message.distance.amount", distance);
            }
        }
    }

}
