package fuzs.deathfinder.network.chat;

import net.minecraft.network.chat.Component;

public enum TeleportToDeathProblem {
    MISSING_PERMISSIONS(Component.translatable("death.message.teleport.missing_permissions")),
    ALREADY_USED(Component.translatable("death.message.teleport.already_used")),
    TOO_LONG_AGO(Component.translatable("death.message.teleport.too_long_ago")),
    NOT_MOST_RECENT(Component.translatable("death.message.teleport.not_most_recent")),
    NOT_YOURS(Component.translatable("death.message.teleport.not_yours")),
    OTHER_PROBLEM(Component.translatable("death.message.teleport.other_problem"));

    private final Component component;

    TeleportToDeathProblem(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return this.component;
    }
}
