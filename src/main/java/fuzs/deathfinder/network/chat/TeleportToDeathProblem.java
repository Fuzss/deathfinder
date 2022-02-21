package fuzs.deathfinder.network.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum TeleportToDeathProblem {
    MISSING_PERMISSIONS(new TranslatableComponent("death.message.teleport.missing_permissions")),
    ALREADY_USED(new TranslatableComponent("death.message.teleport.already_used")),
    TOO_LONG_AGO(new TranslatableComponent("death.message.teleport.too_long_ago")),
    NOT_MOST_RECENT(new TranslatableComponent("death.message.teleport.not_most_recent")),
    OTHER_PROBLEM(new TranslatableComponent("death.message.teleport.other_problem"));

    private final Component component;

    TeleportToDeathProblem(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return this.component;
    }
}
