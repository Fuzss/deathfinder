package fuzs.deathfinder.network.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum TeleportToDeathProblem implements StringRepresentable {
    MISSING_PERMISSIONS("missing_permissions"),
    ALREADY_USED("already_used"),
    TOO_LONG_AGO("too_long_ago"),
    NOT_MOST_RECENT("not_most_recent"),
    NOT_YOURS("not_yours"),
    OTHER_PROBLEM("other_problem");

    public static final StringRepresentable.EnumCodec<TeleportToDeathProblem> CODEC = StringRepresentable.fromEnum(TeleportToDeathProblem::values);

    private final String name;

    TeleportToDeathProblem(String name) {
        this.name = name;
    }

    public Component getComponent() {
        return Component.translatable("death.message.teleport." + this.name);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
