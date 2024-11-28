package fuzs.deathfinder;

import fuzs.deathfinder.config.ClientConfig;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.handler.DeathMessageHandler;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.S2CAdvancedSystemChatMessage;
import fuzs.deathfinder.network.client.C2SDeathPointTeleportMessage;
import fuzs.deathfinder.network.client.ServerboundNotifyModPresentMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDeathCallback;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathFinder implements ModConstructor {
    public static final String MOD_ID = "deathfinder";
    public static final String MOD_NAME = "Death Finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerLegacyClientbound(S2CAdvancedSystemChatMessage.class, S2CAdvancedSystemChatMessage::new)
            .registerLegacyServerbound(C2SDeathPointTeleportMessage.class, C2SDeathPointTeleportMessage::new)
            .registerServerbound(ServerboundNotifyModPresentMessage.class);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID)
            .client(ClientConfig.class)
            .server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        LivingDeathCallback.EVENT.register(DeathMessageHandler::onLivingDeath);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
