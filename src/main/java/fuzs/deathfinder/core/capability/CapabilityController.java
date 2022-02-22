package fuzs.deathfinder.core.capability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.deathfinder.core.capability.data.CapabilityComponent;
import fuzs.deathfinder.core.capability.data.CapabilityDispatcher;
import fuzs.deathfinder.core.capability.data.CapabilityFactory;
import fuzs.deathfinder.core.capability.data.PlayerRespawnStrategy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
    private final Multimap<Class<?>, CapabilityData<?>> typeToData = ArrayListMultimap.create();

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
        for (CapabilityData<?> data : this.typeToData.values()) {
            evt.register(data.capabilityType());
        }
    }

    @Deprecated
    @SubscribeEvent
    public void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {
        for (CapabilityData<?> data : this.typeToData.get((Class<?>) evt.getGenericType())) {
            if (data.filter().test(evt.getObject())) {
                evt.addCapability(data.capabilityKey(), data.capabilityFactory().create(evt.getObject()));
            }
        }
    }

    @Deprecated
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {
        if (this.typeToData.get(Entity.class).isEmpty()) return;
        // we have to revive caps and then invalidate them again since 1.17+
        evt.getOriginal().reviveCaps();
        for (CapabilityData<?> data : this.typeToData.get(Entity.class)) {
            evt.getOriginal().getCapability(data.capability()).ifPresent(oldCapability -> {
                evt.getPlayer().getCapability(data.capability()).ifPresent(newCapability -> {
                    ((EntityCapabilityData<?>) data).respawnStrategy().copy(oldCapability, newCapability, !evt.isWasDeath(), evt.getPlayer().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));
                });
            });
        }
        evt.getOriginal().invalidateCaps();
    }

    /**
     * register capabilities for a given object type
     * @param providerType type of object to attach to, only works for generic supertypes
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    private <T extends CapabilityComponent> Capability<T> registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Predicate<Object> filter, CapabilityToken<T> token) {
        final Capability<T> capability = CapabilityManager.get(token);
        this.typeToData.put(providerType, new DefaultCapabilityData<>(this.locate(capabilityKey), capability, capabilityType, provider -> new CapabilityDispatcher<>(capability, capabilityFactory.create(provider)), filter));
        return capability;
    }

    public <T extends CapabilityComponent> Capability<T> registerItemCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Item item, CapabilityToken<T> token) {
        return this.registerItemCapability(capabilityKey, capabilityType, capabilityFactory, o -> o == item, token);
    }

    /**
     * register capability to {@link ItemStack} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerItemCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, capabilityFactory, filter, token);
    }

    public <T extends CapabilityComponent> Capability<T> registerEntityCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Class<Entity> clazz, CapabilityToken<T> token) {
        return this.registerEntityCapability(capabilityKey, capabilityType, capabilityFactory, clazz::isInstance, PlayerRespawnStrategy.LOSSLESS, token);
    }

    public <T extends CapabilityComponent> Capability<T> registerPlayerCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, PlayerRespawnStrategy respawnStrategy, CapabilityToken<T> token) {
        return this.registerEntityCapability(capabilityKey, capabilityType, capabilityFactory, Player.class::isInstance, respawnStrategy, token);
    }

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerEntityCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Predicate<Object> filter, PlayerRespawnStrategy respawnStrategy, CapabilityToken<T> token) {
        final Capability<T> capability = CapabilityManager.get(token);
        this.typeToData.put(Entity.class, new EntityCapabilityData<>(this.locate(capabilityKey), capability, capabilityType, provider -> new CapabilityDispatcher<>(capability, capabilityFactory.create(provider)), filter, respawnStrategy));
        return capability;
    }

    public <T extends CapabilityComponent> Capability<T> registerBlockEntityCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Class<BlockEntity> clazz, CapabilityToken<T> token) {
        return this.registerBlockEntityCapability(capabilityKey, capabilityType, capabilityFactory, clazz::isInstance, token);
    }

    /**
     * register capability to {@link BlockEntity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerBlockEntityCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, capabilityFactory, filter, token);
    }

    /**
     * register capability to {@link Level} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerLevelCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(Level.class, capabilityKey, capabilityType, capabilityFactory, filter, token);
    }

    /**
     * register capability to {@link LevelChunk} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param filter filter for <code>objectType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <T> capability type
     * @return capability instance from capability manager
     */
    public <T extends CapabilityComponent> Capability<T> registerLevelChunkCapability(String capabilityKey, Class<T> capabilityType, CapabilityFactory<T> capabilityFactory, Predicate<Object> filter, CapabilityToken<T> token) {
        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, capabilityFactory, filter, token);
    }

    /**
     * @param path path for capabilityKey
     * @return resource capabilityKey for {@link #namespace}
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

    private interface CapabilityData<C extends CapabilityComponent> {
        ResourceLocation capabilityKey();

        Capability<C> capability();

        Class<C> capabilityType();

        CapabilityFactory<CapabilityDispatcher<C>> capabilityFactory();

        Predicate<Object> filter();
    }

    /**
     * just a data class for all the things we need when registering capabilities...
     */
    private static record DefaultCapabilityData<C extends CapabilityComponent>(ResourceLocation capabilityKey, Capability<C> capability, Class<C> capabilityType, CapabilityFactory<CapabilityDispatcher<C>> capabilityFactory, Predicate<Object> filter) implements CapabilityData<C> {

    }

    /**
     * just a data class for all the things we need when registering capabilities...
     */
    private static record EntityCapabilityData<C extends CapabilityComponent>(ResourceLocation capabilityKey, Capability<C> capability, Class<C> capabilityType, CapabilityFactory<CapabilityDispatcher<C>> capabilityFactory, Predicate<Object> filter, PlayerRespawnStrategy respawnStrategy) implements CapabilityData<C> {
        
    }
}
