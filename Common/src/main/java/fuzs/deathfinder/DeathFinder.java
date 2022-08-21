package fuzs.deathfinder;

import fuzs.deathfinder.config.ClientConfig;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.client.C2SDeathPointTeleportMessage;
import fuzs.deathfinder.network.S2CAdvancedSystemChatMessage;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathFinder implements ModConstructor {
    public static final String MOD_ID = "deathfinder";
    public static final String MOD_NAME = "Death Finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = CoreServices.FACTORIES.network(MOD_ID);
    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder CONFIG = CoreServices.FACTORIES
            .clientConfig(ClientConfig.class, () -> new ClientConfig())
            .serverConfig(ServerConfig.class, () -> new ServerConfig());

    @Override
    public void onConstructMod() {
        CONFIG.bakeConfigs(MOD_ID);
        ModRegistry.touch();
        AdvancedClickEvent.register(new ResourceLocation(MOD_ID, "teleport"), TeleportClickEvent.class, TeleportClickEvent::new);
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.register(S2CAdvancedSystemChatMessage.class, S2CAdvancedSystemChatMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(C2SDeathPointTeleportMessage.class, C2SDeathPointTeleportMessage::new, MessageDirection.TO_SERVER);
    }
}
