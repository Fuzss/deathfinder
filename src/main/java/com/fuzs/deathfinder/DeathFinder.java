package com.fuzs.deathfinder;

import com.fuzs.deathfinder.client.DeathScreenHandler;
import com.fuzs.deathfinder.common.DeathItemHandler;
import com.fuzs.deathfinder.common.DeathMessageHandler;
import com.fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
        MinecraftForge.EVENT_BUS.register(new DeathItemHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new DeathScreenHandler());
        // register custom texture overwrite for death compass
        ItemModelsProperties.func_239418_a_(Items.COMPASS, new ResourceLocation("death"), (p_239423_0_, p_239423_1_, p_239423_2_) -> {

            CompoundNBT compoundnbt = p_239423_0_.getOrCreateTag();
            return ConfigBuildHandler.CUSTOM_COMPASS_TEXTURE.get() && p_239423_0_.hasDisplayName() &&
                    compoundnbt.contains("LodestoneTracked") && !compoundnbt.getBoolean("LodestoneTracked") ? 1.0F : 0.0F;
        });
    }

}
