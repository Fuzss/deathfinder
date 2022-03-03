package fuzs.deathfinder;

import fuzs.deathfinder.config.ClientConfig;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.handler.DeathMessageHandler;
import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.client.message.C2SDeathPointTeleportMessage;
import fuzs.deathfinder.network.message.S2CAdvancedChatMessage;
import fuzs.deathfinder.registry.ModRegistry;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ConfigHolderImpl;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DeathFinder.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeathFinder {
    public static final String MOD_ID = "deathfinder";
    public static final String MOD_NAME = "Death Finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.of(MOD_ID);
    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder<ClientConfig, ServerConfig> CONFIG = ConfigHolder.of(() -> new ClientConfig(), () -> new ServerConfig());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ((ConfigHolderImpl<?, ?>) CONFIG).addConfigs(MOD_ID);
        registerHandlers();
        registerMessages();
        ModRegistry.touch();
        AdvancedClickEvent.register(new ResourceLocation(MOD_ID, "teleport"), TeleportClickEvent.class, TeleportClickEvent::new);
    }

    private static void registerHandlers() {
        final DeathMessageHandler handler = new DeathMessageHandler();
        MinecraftForge.EVENT_BUS.addListener(handler::onLivingDeath);
    }

    private static void registerMessages() {
        NETWORK.register(S2CAdvancedChatMessage.class, S2CAdvancedChatMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(C2SDeathPointTeleportMessage.class, C2SDeathPointTeleportMessage::new, MessageDirection.TO_SERVER);
    }
}
