package com.fuzs.deathfinder;

import com.fuzs.deathfinder.command.CommandTPX;
import com.fuzs.deathfinder.handler.ConfigHandler;
import com.fuzs.deathfinder.network.NetworkHandler;
import com.fuzs.deathfinder.proxy.CommonProxy;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = DeathFinder.MODID,
        name = DeathFinder.NAME,
        version = DeathFinder.VERSION,
        acceptedMinecraftVersions = DeathFinder.RANGE,
        certificateFingerprint = DeathFinder.FINGERPRINT
)
@Mod.EventBusSubscriber(modid = DeathFinder.MODID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class DeathFinder
{
    public static final String MODID = "deathfinder";
    public static final String NAME = "Death Finder";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.12, 1.12.2]";
    public static final String CLIENT_PROXY_CLASS = "com.fuzs.deathfinder.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.fuzs.deathfinder.proxy.ServerProxy";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(DeathFinder.NAME);

    @SidedProxy(clientSide = DeathFinder.CLIENT_PROXY_CLASS, serverSide = DeathFinder.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        NetworkHandler.init();
        proxy.preInit();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent evt) {
        if (ConfigHandler.tpxEnable) {
            evt.registerServerCommand(new CommandTPX(ConfigHandler.tpxName.isEmpty() ? "tpx" : ConfigHandler.tpxName));
        }
    }

    @EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent evt) {
        LOGGER.warn("Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @SubscribeEvent
    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {

        if (evt.getModID().equals(DeathFinder.MODID)) {
            ConfigManager.sync(DeathFinder.MODID, Config.Type.INSTANCE);
        }

    }
}
