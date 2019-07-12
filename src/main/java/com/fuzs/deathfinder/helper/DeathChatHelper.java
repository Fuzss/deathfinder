package com.fuzs.deathfinder.helper;

import com.fuzs.deathfinder.DeathFinder;
import com.fuzs.deathfinder.handler.ConfigHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ForgeConfigSpec;

public class DeathChatHelper {

    public static ITextComponent getCoordinateComponent(Vec3i position, int dimension) {

        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        // probably only works with vanilla dimensions
        DimensionType dimensionType1 = DimensionType.getById(dimension);
        String dimensionType = "unknown";
        if (dimensionType1 != null) {
            ResourceLocation dimensionResource = DimensionType.getKey(dimensionType1);
            if (dimensionResource != null) {
                dimensionType = dimensionResource.toString();
            }
        }

        String command = String.format(ConfigHandler.GENERAL_CONFIG.deathMessageCommand.get(), dimensionType, x, y, z);
        Style style = new Style().setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("death.message.tooltip")));
        ITextComponent componentCoordinates = new TranslationTextComponent("death.message.coordinates", x, y, z).setStyle(style);

        return new TranslationTextComponent("death.message.location", componentCoordinates, dimensionType);

    }

    public static ITextComponent getDistanceComponent(Vec3i position, int dimension) {

        PlayerEntity player = DeathFinder.proxy.getClientPlayer();

        if (dimension != player.dimension.getId()) {

            return new TranslationTextComponent("death.message.distance.dimension");

        } else {

            int distance = getDistance(position, player);

            if (distance < 2) {

                return new TranslationTextComponent("death.message.distance.close");

            } else {

                return new TranslationTextComponent("death.message.distance.amount", distance);

            }

        }

    }

    private static int getDistance(Vec3i position, PlayerEntity player) {

        double x = position.getX() - player.posX;
        double y = position.getY() - player.posY;
        double z = position.getZ() - player.posZ;

        return (int) MathHelper.sqrt(x * x + y * y + z * z);

    }

    public enum DeathEntityType {

        PLAYER(ConfigHandler.GENERAL_CONFIG.playerEntities),
        TAMED(ConfigHandler.GENERAL_CONFIG.tamedEntities),
        NAMED(ConfigHandler.GENERAL_CONFIG.namedEntities);

        private ForgeConfigSpec.BooleanValue enabled;

        DeathEntityType(ForgeConfigSpec.BooleanValue flag) {
            this.enabled = flag;
        }

        public boolean isEnabled() {
            return enabled.get();
        }
    }

}
