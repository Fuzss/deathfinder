package fuzs.deathfinder.util;

import fuzs.deathfinder.attachment.DeathTracker;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeathMessageBuilder {
    public static final String KEY_DEATH_MESSAGE_POSITION = "death.message.position";
    public static final String FALLBACK_DEATH_MESSAGE_POSITION = "at %s";
    public static final String KEY_DEATH_MESSAGE_DIMENSION = "death.message.dimension";
    public static final String FALLBACK_DEATH_MESSAGE_DIMENSION = "in dimension %s";
    public static final String KEY_DEATH_MESSAGE_DISTANCE_DIMENSION = "death.message.distance.dimension";
    public static final String FALLBACK_DEATH_MESSAGE_DISTANCE_DIMENSION = "very far away";
    public static final String KEY_DEATH_MESSAGE_DISTANCE_CLOSE = "death.message.distance.close";
    public static final String FALLBACK_DEATH_MESSAGE_DISTANCE_CLOSE = "very close";
    public static final String KEY_DEATH_MESSAGE_DISTANCE_BLOCKS = "death.message.distance.blocks";
    public static final String FALLBACK_DEATH_MESSAGE_DISTANCE_BLOCKS = "%s blocks away";

    private final LivingEntity deadEntity;
    private boolean withPosition;
    private boolean withDimension;
    private boolean withDistance;

    public DeathMessageBuilder(LivingEntity deadEntity) {
        this.deadEntity = deadEntity;
    }

    public Component build(@Nullable ServerPlayer receiver) {
        MutableComponent component = Component.empty().append(this.getVanillaComponent());
        if (this.withPosition) {
            component.append(CommonComponents.SPACE).append(this.getPositionComponent(receiver));
        }
        if (this.withDimension) {
            component.append(CommonComponents.SPACE).append(this.getDimensionComponent());
        }
        if (this.withDistance && receiver != null) {
            component.append(CommonComponents.SPACE).append(this.getDistanceComponent(receiver));
        }

        return component;
    }

    private Component getVanillaComponent() {
        return this.deadEntity.getCombatTracker().getDeathMessage();
    }

    private Component getPositionComponent(@Nullable ServerPlayer receiver) {
        BlockPos position = this.deadEntity.blockPosition();
        MutableComponent component = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates",
                        position.getX(),
                        position.getY(),
                        position.getZ()))
                .withStyle((Style style) -> style.withColor(ChatFormatting.GREEN)
                        .withClickEvent(TeleportClickEvent.create(receiver,
                                this.deadEntity.getUUID(),
                                this.deadEntity.level().dimension(),
                                position))
                        .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip"))));
        if (receiver == this.deadEntity) {
            ModRegistry.DEATH_TRACKER_ATTACHMENT_TYPE.set(receiver, DeathTracker.of(this.deadEntity));
        }

        return Component.translatableWithFallback(KEY_DEATH_MESSAGE_POSITION,
                FALLBACK_DEATH_MESSAGE_POSITION,
                component);
    }

    private Component getDimensionComponent() {
        String dimension = this.deadEntity.level().dimension().location().toString();
        return Component.translatableWithFallback(KEY_DEATH_MESSAGE_DIMENSION,
                FALLBACK_DEATH_MESSAGE_DIMENSION,
                dimension);
    }

    private Component getDistanceComponent(@NotNull Player receiver) {
        Component component;
        if (this.deadEntity.level().dimension() != receiver.level().dimension()) {
            component = Component.translatableWithFallback(KEY_DEATH_MESSAGE_DISTANCE_DIMENSION,
                    FALLBACK_DEATH_MESSAGE_DISTANCE_DIMENSION);
        } else {
            double distance = this.deadEntity.position().distanceTo(receiver.position());
            if (distance < 3.0) {
                component = Component.translatableWithFallback(KEY_DEATH_MESSAGE_DISTANCE_CLOSE,
                        FALLBACK_DEATH_MESSAGE_DISTANCE_CLOSE);
            } else {
                component = Component.translatableWithFallback(KEY_DEATH_MESSAGE_DISTANCE_BLOCKS,
                        FALLBACK_DEATH_MESSAGE_DISTANCE_BLOCKS,
                        (int) distance);
            }
        }

        return Component.literal("(").append(component).append(")");
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
}
