package com.fuzs.deathfinder.proxy;

import net.minecraft.entity.player.EntityPlayer;

public abstract class CommonProxy{

    public abstract void preInit();

    public abstract EntityPlayer getClientPlayer();

}