package com.fuzs.deathfinder.proxy;

import net.minecraft.entity.player.PlayerEntity;

public abstract class CommonProxy{

    public abstract void preInit();

    public abstract PlayerEntity getClientPlayer();

}