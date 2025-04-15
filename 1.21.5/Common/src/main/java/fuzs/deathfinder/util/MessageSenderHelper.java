package fuzs.deathfinder.util;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.ClientboundAdvancedSystemChatMessage;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MessageSenderHelper extends CapabilityComponent<ServerPlayer> {
    private boolean isVanillaClient = true;

    public void setModAvailableForClient() {
        if (this.isVanillaClient) {
            this.isVanillaClient = false;
            this.setChanged();
        }
    }

    public static void sendSystemMessage(ServerPlayer serverPlayer, Component component, boolean bypassHiddenChat) {
        if (ModRegistry.MESSAGE_SENDER_ATTACHMENT_TYPE.has(serverPlayer)) {
            DeathFinder.NETWORK.sendMessage(PlayerSet.ofPlayer(serverPlayer),
                    new ClientboundAdvancedSystemChatMessage(component, bypassHiddenChat));
        } else {
            serverPlayer.sendSystemMessage(component, bypassHiddenChat);
        }
    }
}
