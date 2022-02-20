package fuzs.deathfinder.network.chat;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceKey;
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
