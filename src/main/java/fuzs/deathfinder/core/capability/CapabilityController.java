package fuzs.deathfinder.core.capability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.deathfinder.core.capability.data.CapabilityComponent;
import fuzs.deathfinder.core.capability.data.CapabilityDispatcher;
import fuzs.deathfinder.core.capability.data.PlayerRespawnStrategy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * helper object for registering and attaching mod capabilities, needs to be extended by every mod individually
 * this basically is the same as {@link fuzs.puzzleslib.registry.RegistryManager}
 */
public class CapabilityController {
    /**
     * capability controllers are stored for each mod separately to avoid concurrency issues, might not be need though
     */
    private static final Map<String, CapabilityController> MOD_TO_CAPABILITIES = Maps.newConcurrentMap();

    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * internal storage for registering capability entries
     */
    private final Multimap<Class<?>, CapabilityData> typeToData = ArrayListMultimap.create();
    /**
     * copy data on respawn strategies for player capabilities
     */
    private final Map<Capability<? extends CapabilityComponent>, PlayerRespawnStrategy> respawnStrategies = Maps.newHashMap();

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private CapabilityController(String namespace) {
        this.namespace = namespace;
    }

    /**
     * forge event
     */
    private void onRegisterCapabilities(final RegisterCapabilitiesEvent evt) {
        for (CapabilityData data : this.typeToData.values()) {
            evt.register(data.capabilityType());
        }
    }

    @Deprecated
    @SubscribeEvent
    public void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {
        for (CapabilityData data : this.typeToData.get((Class<?>) evt.getGenericType())) {
            if (data.filter().test(evt.getObject())) {
                evt.addCapability(data.location(), data.capabilityFactory().get());
            }
        }
    }

    @Deprecated
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {
        if (this.respawnStrategies.isEmpty()) return;
        // we have to revive caps and then invalidate them again since 1.17+
        evt.getOriginal().reviveCaps();
        for (Map.Entry<Capability<? extends CapabilityComponent>, PlayerRespawnStrategy> entry : this.respawnStrategies.entrySet()) {
            evt.getOriginal().getCapability(entry.getKey()).ifPresent(oldCapability -> {
                evt.getPlayer().getCapability(entry.getKey()).ifPresent(newCapability -> {
                    entry.getValue().copy(oldCapability, newCapability, !evt.isWasDeath(), evt.getPlayer().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));
                });
            });
        }
        evt.getOriginal().invalidateCaps();
    }

    /**
     * register capabilities for a given object type
     * @param objectType type of object to attach to, only works for generic supertypes
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    @SuppressWarnings("unchecked")
    private <T extends CapabilityComponent> Capability<T> registerCapability(Class<? extends ICapabilityProvider> objectType, String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, CapabilityToken<T> token) {
        final Capability<T> capability = CapabilityManager.get(token);
        this.typeToData.put(objectType, new CapabilityData(this.locate(path), (Class<CapabilityComponent>) type, () -> new CapabilityDispatcher<>(capability, factory.get()), filter));
        return capability;
    }

    /**
     * register capability to {@link ItemStack} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerItemStackCapability(String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(ItemStack.class, path, type, factory, filter, token);
    }

    /**
     * register capability to {@link Entity} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerEntityCapability(String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(Entity.class, path, type, factory, filter, token);
    }

    /**
     * register capability to {@link Entity} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param respawnStrategy how data should be copied when the player object is recreated
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerPlayerCapability(String path, Class<T> type, Supplier<T> factory, PlayerRespawnStrategy respawnStrategy, CapabilityToken<T> token) {
        return this.registerPlayerCapability(path, type, factory, o -> o instanceof Player, respawnStrategy, token);
    }

    /**
     * register capability to {@link Entity} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param respawnStrategy how data should be copied when the player object is recreated
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerPlayerCapability(String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, PlayerRespawnStrategy respawnStrategy, CapabilityToken<T> token) {
        final Capability<T> capability = this.registerCapability(Entity.class, path, type, factory, filter, token);
        this.respawnStrategies.put(capability, respawnStrategy);
        return capability;
    }

    /**
     * register capability to {@link BlockEntity} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerBlockEntityCapability(String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(BlockEntity.class, path, type, factory, filter, token);
    }

    /**
     * register capability to {@link Level} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerLevelCapability(String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(Level.class, path, type, factory, filter, token);
    }

    /**
     * register capability to {@link LevelChunk} objects
     * @param path path for internal name of this capability, will be used for serialization
     * @param type interface for this capability
     * @param factory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerLevelChunkCapability(String path, Class<T> type, Supplier<T> factory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(LevelChunk.class, path, type, factory, filter, token);
    }

    /**
     * @param path path for location
     * @return resource location for {@link #namespace}
     */
    private ResourceLocation locate(String path) {
        if (path.isEmpty()) throw new IllegalArgumentException("Can't register object without name");
        return new ResourceLocation(this.namespace, path);
    }

    /**
     * creates a new capability controller for <code>namespace</code> or returns an existing one
     * @param namespace namespace used for registration
     * @return new mod specific capability controller
     */
    public static synchronized CapabilityController of(String namespace) {
        return MOD_TO_CAPABILITIES.computeIfAbsent(namespace, key -> {
            final CapabilityController manager = new CapabilityController(namespace);
            // for registering capabilities
            FMLJavaModLoadingContext.get().getModEventBus().addListener(manager::onRegisterCapabilities);
            // for attaching capabilities
            MinecraftForge.EVENT_BUS.register(manager);
            return manager;
        });
    }

    /**
     * just a data class for all the things we need when registering capabilities...
     */
    private static record CapabilityData(ResourceLocation location, Class<CapabilityComponent> capabilityType, Supplier<ICapabilityProvider> capabilityFactory, Predicate<Object> filter) {
    }
}
