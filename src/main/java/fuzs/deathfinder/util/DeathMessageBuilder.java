package fuzs.deathfinder.util;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.registry.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeathMessageBuilder {
    private final LivingEntity deadEntity;
    private boolean withPosition;
    private boolean withDimension;
    private boolean withDistance;

    private DeathMessageBuilder(LivingEntity deadEntity) {
        this.deadEntity = deadEntity;
    }

    public Component build(@Nullable Player receiver) {
        MutableComponent component = new TextComponent("").append(this.getVanillaComponent());
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
        MutableComponent component = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", x, y, z));
        ServerConfig.TeleportRestriction allowTeleporting = DeathFinder.CONFIG.server().components.allowTeleporting;
        if (receiver != null) {
            if (allowTeleporting != ServerConfig.TeleportRestriction.NO_ONE && (receiver.hasPermissions(2) || allowTeleporting == ServerConfig.TeleportRestriction.EVERYONE)) {
                component.withStyle(style -> style.withColor(ChatFormatting.GREEN)
                        .withClickEvent(new TeleportClickEvent(this.deadEntity.level.dimension(), x, y, z))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));
            }
        }
        if (receiver == this.deadEntity) {
            receiver.getCapability(ModRegistry.PLAYER_DEATH_TRACKER_CAPABILITY).ifPresent(tracker -> {
                tracker.setLastDeathDimension(this.deadEntity.level.dimension());
                tracker.setLastDeathPosition(this.deadEntity.blockPosition());
                tracker.captureDeathDate();
            });
        }
        return new TranslatableComponent("death.message.position", component);
    }

    private Component getDimensionComponent() {
        String dimension = this.deadEntity.level.dimension().location().toString();
        return new TranslatableComponent("death.message.dimension", dimension);
    }

    private Component getDistanceComponent(@Nonnull Player receiver) {
        Component component;
        if (this.deadEntity.level.dimension() != receiver.level.dimension()) {
            component = new TranslatableComponent("death.message.distance.dimension");
        } else {
            double distance = this.deadEntity.position().distanceTo(receiver.position());
            if (distance < 3.0) {
                component = new TranslatableComponent("death.message.distance.close");
            } else {
                component = new TranslatableComponent("death.message.distance.blocks", (int) distance);
            }
        }
        return new TextComponent("(").append(component).append(")");
    }

    public static DeathMessageBuilder from(LivingEntity entity) {
        return new DeathMessageBuilder(entity);
    }
}
