package fuzs.deathfinder.network.chat;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.attachment.DeathTracker;
import fuzs.deathfinder.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeleportClickEvent extends ClickEvent {
    public static final Codec<TeleportClickEvent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(UUIDUtil.CODEC.fieldOf("uuid").forGetter((clickEvent) -> {
            return clickEvent.uuid;
        }), Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter((clickEvent) -> {
            return clickEvent.dimension;
        }), BlockPos.CODEC.fieldOf("position").forGetter((clickEvent) -> {
            return clickEvent.position;
        })).apply(instance, TeleportClickEvent::new);
    });

    private final UUID uuid;
    private final ResourceKey<Level> dimension;
    private final BlockPos position;

    public TeleportClickEvent(UUID uuid, ResourceKey<Level> dimension, BlockPos position) {
        super(Action.SUGGEST_COMMAND, makeCommand(dimension, position.getX(), position.getY(), position.getZ()));
        this.uuid = uuid;
        this.dimension = dimension;
        this.position = position;
    }

    private static String makeCommand(ResourceKey<Level> dimension, int x, int y, int z) {
        return String.format("/execute in %s run tp @s %s %s %s", dimension.location(), x, y, z);
    }

    public Either<TeleportToDeathProblem, Unit> acceptsTracker(Player player, @Nullable DeathTracker deathTracker) {
        if (!player.getUUID().equals(this.uuid)) {
            return Either.left(TeleportToDeathProblem.NOT_YOURS);
        } else if (deathTracker == null) {
            return Either.left(TeleportToDeathProblem.ALREADY_USED);
        } else {
            return deathTracker.isValid(this.dimension,
                    this.position,
                    DeathFinder.CONFIG.get(ServerConfig.class).components.teleportInterval);
        }
    }

    public static TeleportClickEvent readTeleportClickEvent(FriendlyByteBuf buf) {
        return buf.readWithCodec(NbtOps.INSTANCE, CODEC, NbtAccounter.create(2097152L));
    }

    public static void writeTeleportClickEvent(FriendlyByteBuf buf, TeleportClickEvent teleportClickEvent) {
        buf.writeWithCodec(NbtOps.INSTANCE, CODEC, teleportClickEvent);
    }
}
