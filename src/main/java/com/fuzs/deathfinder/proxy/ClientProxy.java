package com.fuzs.deathfinder.proxy;

import com.fuzs.deathfinder.handler.DeathChatHandler;
import com.fuzs.deathfinder.handler.DeathEventHandler;
import com.fuzs.deathfinder.handler.DeathScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new DeathEventHandler());
        MinecraftForge.EVENT_BUS.register(new DeathChatHandler());
        MinecraftForge.EVENT_BUS.register(new DeathScreenHandler());
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }
}
