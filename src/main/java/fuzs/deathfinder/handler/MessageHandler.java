package fuzs.deathfinder.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ConfigBuildHandler;
import fuzs.deathfinder.util.DeathMessageBuilder;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class MessageHandler {

    private final DeathMessageHelper helper = new DeathMessageHelper();

    public void onLivingDeath(final LivingDeathEvent evt) {
        LivingEntity entity = evt.getEntityLiving();
        if (entity.level.isClientSide || !entity.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            return;
        }
        DeathMessageBuilder builder = DeathMessageBuilder.from(entity)
                .withPosition(DeathFinder.CONFIG.server().components.positionComponent)
                .withDimension(DeathFinder.CONFIG.server().components.dimensionComponent)
                .withDistance(DeathFinder.CONFIG.server().components.distanceComponent);
        MessageSender sender = new MessageSender(entity.getServer());
        if (DeathFinder.CONFIG.server().messages.allDeaths && this.isAllowed(entity.getType())) {
            sender.sendMessage(builder);
        } else if (DeathFinder.CONFIG.server().messages.playerDeaths && entity instanceof ServerPlayer player && !entity.isSpectator()) {
            this.helper.handlePlayer(player, builder, sender);
        } else if (DeathFinder.CONFIG.server().messages.namedEntityDeaths && entity.hasCustomName()) {
            sender.sendMessage(builder);
        } else if (DeathFinder.CONFIG.server().messages.villagerDeaths && entity instanceof Villager) {
            sender.sendMessage(builder);
        } else if (DeathFinder.CONFIG.server().messages.petDeaths && entity instanceof TamableAnimal animal && animal.getOwner() instanceof ServerPlayer player) {
            player.sendMessage(builder.build(player), Util.NIL_UUID);
        }
    }

    private static boolean isAllowed(EntityType<?> type) {
        if (DeathFinder.CONFIG.server().messages.deathMessageWhitelist.contains(type)) return true;
        return !DeathFinder.CONFIG.server().messages.deathMessageBlacklist.contains(type);
    }

    private enum DeathMessageSource {
        ALL(() -> DeathFinder.CONFIG.server().messages.allDeaths, entity -> isAllowed(entity.getType())),
        PLAYER(() -> DeathFinder.CONFIG.server().messages.playerDeaths, entity -> entity instanceof ServerPlayer && !entity.isSpectator()),
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
