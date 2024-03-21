package fuzs.deathfinder;

import fuzs.deathfinder.config.ClientConfig;
import fuzs.deathfinder.config.ServerConfig;
import fuzs.deathfinder.handler.DeathMessageHandler;
import fuzs.deathfinder.init.ModRegistry;
import fuzs.deathfinder.network.S2CAdvancedSystemChatMessage;
import fuzs.deathfinder.network.chat.AdvancedClickEvent;
import fuzs.deathfinder.network.chat.TeleportClickEvent;
import fuzs.deathfinder.network.client.C2SDeathPointTeleportMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDeathCallback;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathFinder implements ModConstructor {
    public static final String MOD_ID = "deathfinder";
    public static final String MOD_NAME = "Death Finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        AdvancedClickEvent.register(new ResourceLocation(MOD_ID, "teleport"), TeleportClickEvent.class, TeleportClickEvent::new);
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        NETWORK.register(S2CAdvancedSystemChatMessage.class, S2CAdvancedSystemChatMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(C2SDeathPointTeleportMessage.class, C2SDeathPointTeleportMessage::new, MessageDirection.TO_SERVER);
    }

    public void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int left, int top, int right, int bottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        left = Math.min(left, width / 2);
        right = Math.min(right, width / 2);
        top = Math.min(top, height / 2);
        bottom = Math.min(bottom, height / 2);
        if (width == spriteWidth && height == spriteHeight) {
            guiGraphics.blit(resourceLocation, x, y, uOffset, vOffset, width, height);
        } else if (height == spriteHeight) {
            guiGraphics.blit(resourceLocation, x, y, uOffset, vOffset, left, height);
            guiGraphics.blitRepeating(resourceLocation, x + left, y, width - right - left, height, uOffset + left, vOffset, spriteWidth - right - left, spriteHeight);
            guiGraphics.blit(resourceLocation, x + width - right, y, uOffset + spriteWidth - right, vOffset, right, height);
        } else if (width == spriteWidth) {
            guiGraphics.blit(resourceLocation, x, y, uOffset, vOffset, width, top);
            guiGraphics.blitRepeating(resourceLocation, x, y + top, width, height - bottom - top, uOffset, vOffset + top, spriteWidth, spriteHeight - bottom - top);
            guiGraphics.blit(resourceLocation, x, y + height - bottom, uOffset, vOffset + spriteHeight - bottom, width, bottom);
        } else {
            guiGraphics.blit(resourceLocation, x, y, uOffset, vOffset, left, top);
            guiGraphics.blitRepeating(resourceLocation, x + left, y, width - right - left, top, uOffset + left, vOffset, spriteWidth - right - left, top);
            guiGraphics.blit(resourceLocation, x + width - right, y, uOffset + spriteWidth - right, vOffset, right, top);
            guiGraphics.blit(resourceLocation, x, y + height - bottom, uOffset, vOffset + spriteHeight - bottom, left, bottom);
            guiGraphics.blitRepeating(resourceLocation, x + left, y + height - bottom, width - right - left, bottom, uOffset + left, vOffset + spriteHeight - bottom, spriteWidth - right - left, bottom);
            guiGraphics.blit(resourceLocation, x + width - right, y + height - bottom, uOffset + spriteWidth - right, vOffset + spriteHeight - bottom, right, bottom);
            guiGraphics.blitRepeating(resourceLocation, x, y + top, left, height - bottom - top, uOffset, vOffset + top, left, spriteHeight - bottom - top);
            guiGraphics.blitRepeating(resourceLocation, x + left, y + top, width - right - left, height - bottom - top, uOffset + left, vOffset + top, spriteWidth - right - left, spriteHeight - bottom - top);
            guiGraphics.blitRepeating(resourceLocation, x + width - right, y + top, left, height - bottom - top, uOffset + spriteWidth - right, vOffset + top, right, spriteHeight - bottom - top);
        }
    }

    private static void registerHandlers() {
        LivingDeathCallback.EVENT.register(DeathMessageHandler::onLivingDeath);
    }
}
