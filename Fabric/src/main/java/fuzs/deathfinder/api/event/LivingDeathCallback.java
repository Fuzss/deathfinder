package fuzs.deathfinder.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingDeathCallback {
    Event<LivingDeathCallback> EVENT = EventFactory.createArrayBacked(LivingDeathCallback.class, listeners -> (LivingEntity entity, DamageSource source) -> {
        for (LivingDeathCallback event : listeners) {
            if (!event.onLivingDeath(entity, source)) {
                return false;
            }
        }
        return true;
    });

    /**
     * even is fired whenever a living entity dies,
     * in contrast to Forge this runs before {@link net.minecraft.world.level.gameevent.GameEvent#ENTITY_DIE} is fired off,
     * so the game event may be cancelled
     *
     * @param entity    the entity that has been killed
     * @param source    the {@link DamageSource} the <code>entity</code> has been killed by
     * @return          is this death allowed to happen
     */
    boolean onLivingDeath(LivingEntity entity, DamageSource source);
}
