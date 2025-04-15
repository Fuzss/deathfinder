package fuzs.deathfinder.client.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CompassTooltipHandler {
    public static final String KEY_COMPASS_POSITION = Items.RECOVERY_COMPASS.getDescriptionId() + ".position";
    public static final String KEY_COMPASS_DIMENSION = Items.RECOVERY_COMPASS.getDescriptionId() + ".dimension";

    public static void onItemTooltip(ItemStack itemStack, List<Component> lines, Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag) {
        if (!DeathFinder.CONFIG.get(ClientConfig.class).recoveryCompassTooltip) return;
        if (itemStack.is(Items.RECOVERY_COMPASS) && player != null) {
            Optional<GlobalPos> lastDeathLocation = player.getLastDeathLocation();
            if (lastDeathLocation.isPresent()) {
                BlockPos pos = lastDeathLocation.map(GlobalPos::pos).orElseThrow();
                ResourceKey<Level> dimension = lastDeathLocation.map(GlobalPos::dimension).orElseThrow();
                lines.add(Component.translatable(KEY_COMPASS_POSITION, Component.literal(String.valueOf(pos.getX())).withStyle(ChatFormatting.GRAY), Component.literal(String.valueOf(pos.getY())).withStyle(ChatFormatting.GRAY), Component.literal(String.valueOf(pos.getZ())).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
                lines.add(Component.translatable(KEY_COMPASS_DIMENSION, Component.literal(dimension.location().toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
            }
        }
    }
}
