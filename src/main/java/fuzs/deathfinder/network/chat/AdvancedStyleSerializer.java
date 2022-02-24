package fuzs.deathfinder.network.chat;

import com.google.gson.*;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class AdvancedStyleSerializer extends Style.Serializer {
    @Override
    public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Style style = super.deserialize(jsonElement, type, jsonDeserializationContext);
        if (style != null) {
            ClickEvent clickEvent = getAdvancedClickEvent(jsonElement.getAsJsonObject());
            if (clickEvent != null) {
                return style.withClickEvent(clickEvent);
            }
        }
        return style;
    }

    @Nullable
    private static ClickEvent getAdvancedClickEvent(JsonObject jsonObject) {
        if (jsonObject.has("advancedclickevent")) {
            JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "advancedclickevent");
            String string1 = GsonHelper.getAsString(jsonObject2, "id", null);
            ResourceLocation identifier = ResourceLocation.tryParse(string1);
            String string = GsonHelper.getAsString(jsonObject2, "action", null);
            ClickEvent.Action action = string == null ? null : ClickEvent.Action.getByName(string);
            String string2 = GsonHelper.getAsString(jsonObject2, "value", null);
            if (identifier != null && action != null && string2 != null && action.isAllowedFromServer()) {
                return AdvancedClickEvent.deserialize(identifier, action, string2, jsonObject2);
            }
        }
        return null;
    }

    @Override
    public JsonElement serialize(Style style, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonElement = super.serialize(style, type, jsonSerializationContext);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            if (style.getClickEvent() instanceof AdvancedClickEvent clickEvent) {
                JsonObject jsonObject = new JsonObject();
                clickEvent.serialize(jsonObject);
                ((JsonObject) jsonElement).add("advancedclickevent", jsonObject);
            }
        }
        return jsonElement;
    }
}
