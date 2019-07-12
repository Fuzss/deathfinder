package com.fuzs.deathfinder;

import com.fuzs.deathfinder.handler.ConfigHandler;
import com.fuzs.deathfinder.network.NetworkHandler;
import com.fuzs.deathfinder.proxy.ClientProxy;
import com.fuzs.deathfinder.proxy.CommonProxy;
import com.fuzs.deathfinder.proxy.ServerProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DeathFinder.MODID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class DeathFinder
{
    public static final String MODID = "deathfinder";
    public static final String NAME = "Death Finder";
    public static final Logger LOGGER = LogManager.getLogger(DeathFinder.NAME);

    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public DeathFinder() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.SPEC, MODID + ".toml");

    }

    private void commonSetup(final FMLCommonSetupEvent evt) {
        NetworkHandler.init();
        proxy.preInit();
    }

}
