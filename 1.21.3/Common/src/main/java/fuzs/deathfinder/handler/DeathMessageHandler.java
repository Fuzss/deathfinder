package fuzs.deathfinder.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.util.MessageSenderHelper;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.util.DeathMessageBuilder;
import fuzs.deathfinder.util.DeathMessageSender;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.Team;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class DeathMessageHandler {

    public static EventResult onLivingDeath(LivingEntity entity, DamageSource source) {
        if (!(entity.level() instanceof ServerLevel serverLevel) ||
                !serverLevel.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            return EventResult.PASS;
        }
        for (DeathMessageSource deathSource : DeathMessageSource.values()) {
            if (deathSource.test(entity)) {
                DeathMessageBuilder builder = DeathMessageBuilder.from(entity)
                        .withPosition(DeathFinder.CONFIG.get(ServerConfig.class).components.positionComponent)
                        .withDimension(DeathFinder.CONFIG.get(ServerConfig.class).components.dimensionComponent)
                        .withDistance(DeathFinder.CONFIG.get(ServerConfig.class).components.distanceComponent);
                switch (deathSource) {
                    case PLAYER ->
                            handlePlayer((ServerPlayer) entity, builder, DeathMessageSender.from(entity.getServer()));
                    case PET -> {
                        if (((TamableAnimal) entity).getOwner() instanceof ServerPlayer player) {
                            MessageSenderHelper.sendSystemMessage(player, builder.build(player), false);
                        }
                    }
                    case VILLAGER -> DeathMessageSender.from(entity.getServer()).sendToAll(builder, false);
                    default -> DeathMessageSender.from(entity.getServer()).sendToAll(builder);
                }
                break;
            }
        }

        return EventResult.PASS;
    }

    private static void handlePlayer(ServerPlayer player, DeathMessageBuilder builder, DeathMessageSender sender) {
        Component component = player.getCombatTracker().getDeathMessage();
        player.connection.send(new ClientboundPlayerCombatKillPacket(player.getId(), component),
                PacketSendListener.exceptionallySend(() -> {
                    String s = component.getString(256);
                    Component component1 = Component.translatable("death.attack.message_too_long",
                            Component.literal(s).withStyle(ChatFormatting.YELLOW));
                    Component component2 = (Component.translatable("death.attack.even_more_magic",
                            player.getDisplayName())).withStyle((style) -> {
                        return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component1));
                    });
                    return new ClientboundPlayerCombatKillPacket(player.getId(), component2);
                }));
        Team team = player.getTeam();
        if (team != null && team.getDeathMessageVisibility() != Team.Visibility.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                sender.sendMessageToAllTeamMembers(player, builder);
            } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                sender.sendMessageToTeamOrAllPlayers(player, builder);
            }
        } else {
            sender.sendToAll(builder);
        }
    }

    private enum DeathMessageSource {
        // enum order matters
        // players should be handled differently (in regard to teams) even when allDeaths is active
        PLAYER(() -> DeathFinder.CONFIG.get(ServerConfig.class).messages.allDeaths ||
                DeathFinder.CONFIG.get(ServerConfig.class).messages.playerDeaths,
                entity -> entity instanceof ServerPlayer && !entity.isSpectator()),
        ALL(() -> DeathFinder.CONFIG.get(ServerConfig.class).messages.allDeaths,
                entity -> !entity.getType().is(ModRegistry.SILENT_DEATHS_ENTITY_TYPE_TAG)),
        NAMED(() -> DeathFinder.CONFIG.get(ServerConfig.class).messages.namedEntityDeaths, Entity::hasCustomName),
        VILLAGER(() -> DeathFinder.CONFIG.get(ServerConfig.class).messages.villagerDeaths,
                entity -> entity instanceof Villager),
        PET(() -> DeathFinder.CONFIG.get(ServerConfig.class).messages.petDeaths,
                entity -> entity instanceof TamableAnimal animal && animal.getOwner() instanceof ServerPlayer);

        private final BooleanSupplier config;
        private final Predicate<LivingEntity> predicate;

        DeathMessageSource(BooleanSupplier config, Predicate<LivingEntity> predicate) {
            this.config = config;
            this.predicate = predicate;
        }

        public boolean test(LivingEntity entity) {
            return this.config.getAsBoolean() && this.predicate.test(entity);
        }
    }
}
