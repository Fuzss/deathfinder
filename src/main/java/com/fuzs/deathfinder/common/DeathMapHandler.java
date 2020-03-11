package com.fuzs.deathfinder.common;

import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathMapHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {

        if(evt.isWasDeath()) {

            ServerPlayerEntity player = (ServerPlayerEntity) evt.getOriginal();
            if (ConfigBuildHandler.IGNORE_KEEP_INVENTORY.get() || !player.getServerWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {

                ItemStack itemstack = FilledMapItem.setupNewMap(player.getServerWorld(), (int) player.getPosX(), (int) player.getPosZ(), (byte) 2, true, true);
                FilledMapItem.func_226642_a_(player.getServerWorld(), itemstack);
                MapData.addTargetDecoration(itemstack, player.getPosition(), "+", ConfigBuildHandler.MAP_DECORATION.get());
                itemstack.setDisplayName(new TranslationTextComponent("filled_map.death"));
                if (!evt.getPlayer().inventory.addItemStackToInventory(itemstack)) {

                    evt.getPlayer().dropItem(itemstack, false);
                }
            }
        }
    }

}
