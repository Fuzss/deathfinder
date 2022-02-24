package fuzs.deathfinder.network.chat;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TeleportClickEvent extends AdvancedClickEvent {
    private UUID uuid;
    private ResourceKey<Level> dimension;
    private int x, y, z;

    public TeleportClickEvent(UUID uuid, ResourceKey<Level> dimension, int x, int y, int z) {
        super(Action.SUGGEST_COMMAND, makeCommand(dimension, x, y, z));
        this.uuid = uuid;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public TeleportClickEvent(Action action, String string) {
        super(action, string);
    }

    public Either<TeleportToDeathProblem, Unit> acceptsTracker(Player player, PlayerDeathTracker tracker) {
        if (!player.getUUID().equals(this.uuid)) return Either.left(TeleportToDeathProblem.NOT_YOURS);
        return tracker.isValid(this.dimension, new BlockPos(this.x, this.y, this.z), DeathFinder.CONFIG.server().components.teleportInterval);
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        super.serialize(jsonObject);
        jsonObject.addProperty("uuid", this.uuid.toString());
        jsonObject.addProperty("dimension", this.dimension.location().toString());
        jsonObject.addProperty("x", this.x);
        jsonObject.addProperty("y", this.y);
        jsonObject.addProperty("z", this.z);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        String string = GsonHelper.getAsString(jsonObject, "uuid", null);
        if (string != null) this.uuid = UUID.fromString(string);
        String string2 = GsonHelper.getAsString(jsonObject, "dimension", null);
        if (string2 != null) this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation.tryParse(string2));
        this.x = GsonHelper.getAsInt(jsonObject, "x", 0);
        this.y = GsonHelper.getAsInt(jsonObject, "y", 0);
        this.z = GsonHelper.getAsInt(jsonObject, "z", 0);
    }

    private static String makeCommand(ResourceKey<Level> dimension, int x, int y, int z) {
        return String.format("/execute in %s run tp @s %s %s %s", dimension.location(), x, y, z);
    }
}
