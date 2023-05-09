package fuzs.deathfinder.util;

import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeathMessageBuilder {
    private final LivingEntity deadEntity;
    private boolean withPosition;
    private boolean withDimension;
    private boolean withDistance;

    private DeathMessageBuilder(LivingEntity deadEntity) {
        this.deadEntity = deadEntity;
    }

    public Component build(@Nullable Player receiver) {
        MutableComponent component = Component.empty().append(this.getVanillaComponent());
        if (this.withPosition) component.append(" ").append(this.getPositionComponent(receiver));
        if (this.withDimension) component.append(" ").append(this.getDimensionComponent());
        if (this.withDistance && receiver != null) component.append(" ").append(this.getDistanceComponent(receiver));
        return component;
    }

    public DeathMessageBuilder withPosition(boolean withPosition) {
        this.withPosition = withPosition;
        return this;
    }

    public DeathMessageBuilder withDimension(boolean withDimension) {
        this.withDimension = withDimension;
        return this;
    }

    public DeathMessageBuilder withDistance(boolean withDistance) {
        this.withDistance = withDistance;
        return this;
    }

    private Component getVanillaComponent() {
        return this.deadEntity.getCombatTracker().getDeathMessage();
    }

    private Component getPositionComponent(@Nullable Player receiver) {
        int x = this.deadEntity.getBlockX(), y = this.deadEntity.getBlockY(), z = this.deadEntity.getBlockZ();
        MutableComponent component = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", x, y, z))
                .withStyle(style -> style.withColor(ChatFormatting.GREEN)
                .withClickEvent(new TeleportClickEvent(this.deadEntity.getUUID(), this.deadEntity.level.dimension(), x, y, z))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip"))));
        if (receiver == this.deadEntity) {
            ModRegistry.PLAYER_DEATH_TRACKER_CAPABILITY.maybeGet(receiver).ifPresent(tracker -> {
                tracker.setLastDeathDimension(this.deadEntity.level.dimension());
                tracker.setLastDeathPosition(this.deadEntity.blockPosition());
                tracker.captureDeathDate();
            });
        }
        return Component.translatable("death.message.position", component);
    }

    private Component getDimensionComponent() {
        String dimension = this.deadEntity.level.dimension().location().toString();
        return Component.translatable("death.message.dimension", dimension);
    }

    private Component getDistanceComponent(@NotNull Player receiver) {
        Component component;
        if (this.deadEntity.level.dimension() != receiver.level.dimension()) {
            component = Component.translatable("death.message.distance.dimension");
        } else {
            double distance = this.deadEntity.position().distanceTo(receiver.position());
            if (distance < 3.0) {
                component = Component.translatable("death.message.distance.close");
            } else {
                component = Component.translatable("death.message.distance.blocks", (int) distance);
            }
        }
        return Component.literal("(").append(component).append(")");
    }

    public static DeathMessageBuilder from(LivingEntity entity) {
        return new DeathMessageBuilder(entity);
    }
}
