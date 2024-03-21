package fuzs.deathfinder;

import fuzs.deathfinder.config.ClientConfig;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.handler.DeathMessageHandler;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.S2CAdvancedSystemChatMessage;
import fuzs.deathfinder.network.client.C2SDeathPointTeleportMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDeathCallback;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathFinder implements ModConstructor {
    public static final String MOD_ID = "deathfinder";
    public static final String MOD_NAME = "Death Finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID, false);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerMessages();
        registerEventHandlers();
    }

    private static void registerMessages() {
        NETWORK.registerClientbound(S2CAdvancedSystemChatMessage.class, S2CAdvancedSystemChatMessage::new);
        NETWORK.registerServerbound(C2SDeathPointTeleportMessage.class, C2SDeathPointTeleportMessage::new);
    }

    private static void registerEventHandlers() {
        LivingDeathCallback.EVENT.register(DeathMessageHandler::onLivingDeath);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
