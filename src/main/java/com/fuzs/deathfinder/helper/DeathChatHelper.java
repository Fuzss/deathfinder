package com.fuzs.deathfinder.helper;

import com.fuzs.deathfinder.DeathFinder;
import com.fuzs.deathfinder.handler.DeathChatHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DeathChatHelper {

    public static List<DeathChatHelper.DeathMessageBuffer> deathMessageBuffer = new LinkedList<>();

    public static ITextComponent getDistanceComponent(DeathMessageBuffer buffer) {

        EntityPlayer player = DeathFinder.proxy.getClientPlayer();

        if (buffer.dim != player.dimension) {

            return new TextComponentTranslation("death.message.dimension");

        } else {

            int distance = getDistance(buffer, player);

            if (distance == 0) {

                return new TextComponentTranslation("death.message.close");

            } else {

                return new TextComponentTranslation("death.message.distance", distance);

            }

        }

    }

    private static int getDistance(DeathMessageBuffer buffer, EntityPlayer player) {

        double x = buffer.pos.getX() - player.posX;
        double y = buffer.pos.getY() - player.posY;
        double z = buffer.pos.getZ() - player.posZ;
        return (int) MathHelper.sqrt(x * x + y * y + z * z);

    }

    public static DeathMessageBuffer findBuffer(String name) {

        List<DeathMessageBuffer> list = DeathChatHelper.deathMessageBuffer.stream().filter(it -> it.name.getUnformattedText().equals(name)).collect(Collectors.toList());

        if (!list.isEmpty()) {

            DeathMessageBuffer buffer = list.get(0);
            int i = DeathChatHelper.deathMessageBuffer.indexOf(buffer);
            DeathChatHelper.deathMessageBuffer.subList(0, i + 1).clear();
            return buffer;

        }

        return null;

    }

    public static class DeathMessageBuffer {

        public final Vec3i pos;
        public final int dim;
        public final ITextComponent name;

        public DeathMessageBuffer(Vec3i vec3i, int i, ITextComponent component) {
            this.pos = vec3i;
            this.dim = i;
            this.name = component;
        }

    }

}
