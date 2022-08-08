package fuzs.deathfinder;

import fuzs.deathfinder.api.event.LivingDeathCallback;
import fuzs.deathfinder.handler.DeathMessageHandler;
import fuzs.deathfinder.init.FabricModRegistry;
import fuzs.puzzleslib.core.CoreServices;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class DeathFinderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(DeathFinder.MOD_ID).accept(new DeathFinder());
        registerHandlers();
        FabricModRegistry.touch();
    }

    private static void registerHandlers() {
        final DeathMessageHandler deathMessageHandler = new DeathMessageHandler();
        LivingDeathCallback.EVENT.register((LivingEntity entity, DamageSource source) -> {
            deathMessageHandler.onLivingDeath(entity, source);
            return true;
        });
    }
}
