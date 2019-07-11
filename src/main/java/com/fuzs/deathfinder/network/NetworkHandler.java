package com.fuzs.deathfinder.network;

import com.fuzs.deathfinder.DeathFinder;
import com.fuzs.deathfinder.network.messages.MessageDeathCoords;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    private static SimpleNetworkWrapper INSTANCE;
    private static int discriminator;

    public static void init(){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DeathFinder.MODID);
        INSTANCE.registerMessage(MessageDeathCoords.class, MessageDeathCoords.class, nextDiscriminator(), Side.CLIENT);
    }

    public static void sendToServer(IMessage message){
        INSTANCE.sendToServer(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player){
        INSTANCE.sendTo(message, player);
    }

    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point){
        INSTANCE.sendToAllAround(message, point);
    }

    public static void sendToAll(IMessage message){
        INSTANCE.sendToAll(message);
    }

    public static void sendToDimension(IMessage message, int dimensionId){
        INSTANCE.sendToDimension(message, dimensionId);
    }

    private static int nextDiscriminator() {
        return discriminator++;
    }

}