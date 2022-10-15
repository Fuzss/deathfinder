package fuzs.deathfinder.mixin.client;

import fuzs.deathfinder.api.client.event.AttemptScreenOpenCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void deathfinder$setScreen(Screen screen, CallbackInfo callbackInfo) {
        if (!AttemptScreenOpenCallback.EVENT.invoker().onAttemptScreenOpen(screen)) callbackInfo.cancel();
    }
}
