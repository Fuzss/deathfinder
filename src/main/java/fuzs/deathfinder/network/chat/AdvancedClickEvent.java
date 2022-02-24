package fuzs.deathfinder.network.chat;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

import java.util.Map;
import java.util.function.BiFunction;

public abstract class AdvancedClickEvent extends ClickEvent {
    private static final BiMap<ResourceLocation, Class<? extends AdvancedClickEvent>> ADVANCED_EVENTS_REGISTRY = HashBiMap.create();
    private static final Map<ResourceLocation, BiFunction<Action, String, ? extends AdvancedClickEvent>> ADVANCED_EVENTS_FACTORY = Maps.newHashMap();

    public static final Gson GSON = Util.make(() -> {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
        // we use our own style serializer to be able to handle advanced click events
        gsonBuilder.registerTypeHierarchyAdapter(Style.class, new AdvancedStyleSerializer());
        gsonBuilder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
        return gsonBuilder.create();
    });

    public AdvancedClickEvent(Action action, String string) {
        super(action, string);
    }

    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty("id", ADVANCED_EVENTS_REGISTRY.inverse().get(this.getClass()).toString());
        jsonObject.addProperty("action", this.getAction().getName());
        jsonObject.addProperty("value", this.getValue());
    }

    public abstract void deserialize(JsonObject jsonObject);

    public static AdvancedClickEvent deserialize(ResourceLocation identifier, Action action, String string, JsonObject jsonObject) {
        BiFunction<Action, String, ? extends AdvancedClickEvent> factory = ADVANCED_EVENTS_FACTORY.get(identifier);
        if (factory == null) throw new RuntimeException("Unknown advanced click event identifier");
        AdvancedClickEvent clickEvent = factory.apply(action, string);
        clickEvent.deserialize(jsonObject);
        return clickEvent;
    }

    public static synchronized void register(ResourceLocation location, Class<? extends AdvancedClickEvent> clazz, BiFunction<Action, String, ? extends AdvancedClickEvent> factory) {
        ADVANCED_EVENTS_REGISTRY.put(location, clazz);
        ADVANCED_EVENTS_FACTORY.put(location, factory);
    }
}
