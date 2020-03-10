package com.fuzs.deathfinder;

import com.fuzs.deathfinder.client.DeathScreenHandler;
import com.fuzs.deathfinder.common.DeathMessageHandler;
import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(DeathFinder.MODID)
public class DeathFinder {

    public static final String MODID = "deathfinder";
    public static final String NAME = "Death Finder";
    public static final Logger LOGGER = LogManager.getLogger(DeathFinder.NAME);

    public DeathFinder() {

        // general setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // config setup
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC, MODID + ".toml");
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new DeathMessageHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new DeathScreenHandler());
    }

}
