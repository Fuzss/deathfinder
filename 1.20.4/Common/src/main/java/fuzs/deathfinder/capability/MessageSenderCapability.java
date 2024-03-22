package fuzs.deathfinder.capability;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.network.S2CAdvancedSystemChatMessage;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MessageSenderCapability extends CapabilityComponent<ServerPlayer> {
    private boolean isVanillaClient = true;

    public void setVanillaClient() {
        if (!this.isVanillaClient) {
            this.isVanillaClient = true;
            this.setChanged();
        }
    }

    public void sendSystemMessage(Component component, boolean bypassHiddenChat) {
        if (this.isVanillaClient) {
            this.getHolder().sendSystemMessage(component, bypassHiddenChat);
        } else {
            DeathFinder.NETWORK.sendTo(new S2CAdvancedSystemChatMessage(component, bypassHiddenChat), this.getHolder());
        }
    }
}
