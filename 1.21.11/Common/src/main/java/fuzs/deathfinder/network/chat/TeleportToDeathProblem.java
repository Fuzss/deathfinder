package fuzs.deathfinder.network.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum TeleportToDeathProblem implements StringRepresentable {
    MISSING_PERMISSIONS("You do not have the necessary permissions to teleport"),
    ALREADY_USED("You have already teleported to a death point"),
    TOO_LONG_AGO("This death occurred too long ago"),
    NOT_MOST_RECENT("This is not your most recent death point"),
    NOT_YOURS("This is not your death point"),
    OTHER_PROBLEM("Teleporting to death points is not allowed on this server");

    private static final TeleportToDeathProblem[] VALUES = values();
    public static final StringRepresentable.EnumCodec<TeleportToDeathProblem> CODEC = StringRepresentable.fromEnum(
            TeleportToDeathProblem::values);

    private final String message;

    TeleportToDeathProblem(String message) {
        this.message = message;
    }

    public static void forEach(Consumer<TeleportToDeathProblem> consumer) {
        for (TeleportToDeathProblem teleportToDeathProblem : VALUES) {
            consumer.accept(teleportToDeathProblem);
        }
    }

    public Component getComponent() {
        return Component.translatableWithFallback("death.message.teleport." + this.getSerializedName(), this.message);
    }

    public void registerTranslation(BiConsumer<Component, String> translationConsumer) {
        translationConsumer.accept(this.getComponent(), this.message);
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
