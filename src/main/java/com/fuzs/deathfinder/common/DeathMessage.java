package com.fuzs.deathfinder.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;

public class DeathMessage {

    private final LivingEntity entity;

    public DeathMessage(LivingEntity entity) {

        this.entity = entity;
    }

    public ITextComponent getMessage() {

        return this.getMessagePart(null);
    }

    public ITextComponent getMessage(PlayerEntity player) {

        return this.getMessagePart(player).appendSibling(this.getDistanceComponent(player));
    }

    private ITextComponent getMessagePart(@Nullable PlayerEntity player) {

        ITextComponent message = this.entity.getCombatTracker().getDeathMessage().shallowCopy();
        return message.appendSibling(this.getCoordinateComponent(player));
    }

    private ITextComponent getCoordinateComponent(@Nullable PlayerEntity player) {

        ResourceLocation dimension = DimensionType.getKey(this.entity.dimension);
        String type = dimension != null ? dimension.toString() : "unknown";
        int x = (int) this.entity.posX, y = (int) this.entity.posY, z = (int) this.entity.posZ;

        ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", x, y, z));
        if (player != null && player.hasPermissionLevel(2)) {

            itextcomponent.applyTextStyle(style -> style.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + type + " run tp @s " + x + " " + y + " " + z))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip"))));
        }

        return new TranslationTextComponent("death.message.location", itextcomponent, type);
    }

    private ITextComponent getDistanceComponent(PlayerEntity player) {

        return new TranslationTextComponent("death.message.distance", this.getDistance(player));
    }

    private ITextComponent getDistance(PlayerEntity player) {

        if (this.entity.dimension != player.dimension) {

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
