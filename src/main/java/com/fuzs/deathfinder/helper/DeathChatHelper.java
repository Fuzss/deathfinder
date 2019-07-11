package com.fuzs.deathfinder.helper;

import com.fuzs.deathfinder.handler.DeathChatHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class DeathChatHelper {

    public ITextComponent getDistanceComponent(EntityPlayerSP player) {

        if (DeathChatHandler.dimension != player.dimension) {

            return new TextComponentTranslation("death.message.dimension");

        } else {

            float distance = this.getDistance(player);

            if (distance == 0) {

                return new TextComponentTranslation("death.message.close");

            } else {

                return new TextComponentTranslation("death.message.distance", distance);

            }

        }

    }

    private int getDistance(EntityPlayerSP player) {

        double x = DeathChatHandler.x - player.posX;
        double y = DeathChatHandler.y - player.posY;
        double z = DeathChatHandler.z - player.posZ;
        return (int) MathHelper.sqrt(x * x + y * y + z * z);

    }

}
