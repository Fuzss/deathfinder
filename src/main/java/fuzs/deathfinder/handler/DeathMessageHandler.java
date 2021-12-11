package fuzs.deathfinder.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.util.DeathMessageBuilder;
import fuzs.deathfinder.util.DeathMessageSender;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class DeathMessageHandler {
    public void onLivingDeath(final LivingDeathEvent evt) {
        LivingEntity entity = evt.getEntityLiving();
        if (entity.level.isClientSide || !entity.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            return;
        }
        for (DeathMessageSource source : DeathMessageSource.values()) {
            if (source.test(entity)) {
                DeathMessageBuilder builder = DeathMessageBuilder.from(entity)
                        .withPosition(DeathFinder.CONFIG.server().components.positionComponent)
                        .withDimension(DeathFinder.CONFIG.server().components.dimensionComponent)
                        .withDistance(DeathFinder.CONFIG.server().components.distanceComponent);
                switch (source) {
                    case PLAYER -> this.handlePlayer((ServerPlayer) entity, builder, DeathMessageSender.from(entity.getServer()));
                    case PET -> {
                        Player player = (Player) ((TamableAnimal) entity).getOwner();
                        player.sendMessage(builder.build(player), Util.NIL_UUID);
                    }
                    case VILLAGER -> DeathMessageSender.from(entity.getServer()).sendToAll(builder, false);
                    default -> DeathMessageSender.from(entity.getServer()).sendToAll(builder);
                }
                break;
            }
        }
    }

    private void handlePlayer(ServerPlayer player, DeathMessageBuilder builder, DeathMessageSender sender) {
        Component component = player.getCombatTracker().getDeathMessage();
        player.connection.send(new ClientboundPlayerCombatKillPacket(player.getCombatTracker(), component), (p_9142_) -> {
            if (!p_9142_.isSuccess()) {
                String s = component.getString(256);
                Component component1 = new TranslatableComponent("death.attack.message_too_long", (new TextComponent(s)).withStyle(ChatFormatting.YELLOW));
                Component component2 = (new TranslatableComponent("death.attack.even_more_magic", player.getDisplayName())).withStyle((p_143420_) -> {
                    return p_143420_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component1));
                });
                player.connection.send(new ClientboundPlayerCombatKillPacket(player.getCombatTracker(), component2));
            }

        });
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

    private static boolean isAllowed(EntityType<?> type) {
        if (DeathFinder.CONFIG.server().messages.deathMessageWhitelist.contains(type)) return true;
        return !DeathFinder.CONFIG.server().messages.deathMessageBlacklist.contains(type);
    }

    private enum DeathMessageSource {
        // enum order matters
        // players should be handled differently (in regards to teams) even when allDeaths is active
        PLAYER(() -> DeathFinder.CONFIG.server().messages.allDeaths || DeathFinder.CONFIG.server().messages.playerDeaths, entity -> entity instanceof ServerPlayer && !entity.isSpectator()),
        ALL(() -> DeathFinder.CONFIG.server().messages.allDeaths, entity -> isAllowed(entity.getType())),
        NAMED(() -> DeathFinder.CONFIG.server().messages.namedEntityDeaths, Entity::hasCustomName),
        VILLAGER(() -> DeathFinder.CONFIG.server().messages.villagerDeaths, entity -> entity instanceof Villager),
        PET(() -> DeathFinder.CONFIG.server().messages.petDeaths, entity -> entity instanceof TamableAnimal animal && animal.getOwner() instanceof ServerPlayer);

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
