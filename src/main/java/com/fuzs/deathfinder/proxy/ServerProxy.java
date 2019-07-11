package com.fuzs.deathfinder.proxy;

import com.fuzs.deathfinder.handler.DeathEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ServerProxy extends CommonProxy {

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new DeathEventHandler());
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return null;
    }

}
