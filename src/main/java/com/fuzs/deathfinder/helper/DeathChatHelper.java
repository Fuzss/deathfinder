package com.fuzs.deathfinder.helper;

import com.fuzs.deathfinder.DeathFinder;
import com.fuzs.deathfinder.handler.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class DeathChatHelper {

    public static ITextComponent getCoordinateComponent(Vec3i position, int dimension) {

        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        String command = String.format(ConfigHandler.deathMessageCommand, x, y, z, dimension);
        Style style = new Style().setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("death.message.tooltip")));
        ITextComponent componentCoordinates = new TextComponentTranslation("death.message.coordinates", x, y, z).setStyle(style);

        return new TextComponentTranslation("death.message.location", componentCoordinates, dimension);

    }

    public static ITextComponent getDistanceComponent(Vec3i position, int dimension) {

        EntityPlayer player = DeathFinder.proxy.getClientPlayer();

        if (dimension != player.dimension) {

            return new TextComponentTranslation("death.message.distance.dimension");

        } else {

            int distance = getDistance(position, player);

            if (distance < 2) {

                return new TextComponentTranslation("death.message.distance.close");

            } else {

                return new TextComponentTranslation("death.message.distance.amount", distance);

            }

        }

    }

    private static int getDistance(Vec3i position, EntityPlayer player) {

        double x = position.getX() - player.posX;
        double y = position.getY() - player.posY;
        double z = position.getZ() - player.posZ;

        return (int) MathHelper.sqrt(x * x + y * y + z * z);

    }

    public enum DeathEntityType {

        PLAYER(ConfigHandler.playerEntities),
        TAMED(ConfigHandler.tamedEntities),
        NAMED(ConfigHandler.namedEntities);

        private boolean enabled;

        DeathEntityType(boolean flag) {
            this.enabled = flag;
        }

        public boolean isEnabled() {
            return this.enabled;
        }
    }

}
