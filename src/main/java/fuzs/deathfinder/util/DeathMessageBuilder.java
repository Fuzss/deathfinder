package fuzs.deathfinder.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DeathMessageBuilder {
    private final LivingEntity deadEntity;
    private boolean withPosition;
    private boolean withDimension;
    private boolean withDistance;

    private DeathMessageBuilder(LivingEntity deadEntity) {
        this.deadEntity = deadEntity;
    }

    public Component build(Player receiver) {
        MutableComponent component = new TextComponent("").append(this.getVanillaComponent());
        if (this.withPosition) component.append(this.getPositionComponent(receiver));
        if (this.withDimension) component.append(this.getDimensionComponent());
        if (this.withDistance) component.append(this.getDistanceComponent(receiver));
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

    private Component getPositionComponent(Player receiver) {
        String dimension = this.deadEntity.level.dimension().location().toString();
        int x = this.deadEntity.getBlockX(), y = this.deadEntity.getBlockY(), z = this.deadEntity.getBlockZ();
        MutableComponent component = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", x, y, z));
        if (receiver.hasPermissions(2)) {
            component.withStyle(style -> style.withColor(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/execute in %s run tp @s %s %s %s", dimension, x, y, z)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));
        }
        return new TranslatableComponent("death.message.position", component);
    }

    private Component getDimensionComponent() {
        String dimension = this.deadEntity.level.dimension().location().toString();
        return new TranslatableComponent("death.message.dimension", dimension);
    }

    private Component getDistanceComponent(Player receiver) {
        Component component;
        if (this.deadEntity.level.dimension() != receiver.level.dimension()) {
            component = new TranslatableComponent("death.message.distance.dimension");
        } else {
            double distance = this.deadEntity.position().distanceTo(receiver.position());
            if (distance < 3.0) {
                component =  new TranslatableComponent("death.message.distance.close");
            } else {
                component =  new TranslatableComponent("death.message.distance.blocks", (int) distance);
            }
        }
        return new TextComponent("(").append(component).append(")");
    }

    public static DeathMessageBuilder from(LivingEntity entity) {
        return new DeathMessageBuilder(entity);
    }
}
