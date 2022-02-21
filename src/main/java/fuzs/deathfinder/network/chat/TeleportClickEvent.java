package fuzs.deathfinder.network.chat;

import com.mojang.datafixers.util.Either;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.capability.PlayerDeathTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;

public class TeleportClickEvent extends ClickEvent {
    private final ResourceKey<Level> dimension;
    private final int x, y, z;

    public TeleportClickEvent(ResourceKey<Level> dimension, int x, int y, int z) {
        super(Action.SUGGEST_COMMAND, makeCommand(dimension, x, y, z));
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Either<TeleportToDeathProblem, Unit> acceptsTracker(PlayerDeathTracker tracker) {
        return tracker.isValid(this.dimension, new BlockPos(this.x, this.y, this.z), DeathFinder.CONFIG.server().components.teleportInterval);
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.dimension.location());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    public static TeleportClickEvent deserialize(FriendlyByteBuf buf) {
        return new TeleportClickEvent(ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation()), buf.readInt(), buf.readInt(), buf.readInt());
    }

    private static String makeCommand(ResourceKey<Level> dimension, int x, int y, int z) {
        return String.format("/execute in %s run tp @s %s %s %s", dimension.location(), x, y, z);
    }
}
