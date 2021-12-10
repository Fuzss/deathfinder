package fuzs.deathfinder.client;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = DeathFinder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DeathFinderClient {
    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        final DeathScreenHandler handler = new DeathScreenHandler();
        MinecraftForge.EVENT_BUS.addListener(handler::onDrawScreen);
        MinecraftForge.EVENT_BUS.addListener(handler::onScreenOpen);
    }

//    @SubscribeEvent
//    public static void onClientSetup(final FMLClientSetupEvent evt) {
//        // register custom texture overwrite for death compass
//        ItemProperties.register(Items.COMPASS, new ResourceLocation("death"), (p_239423_0_, p_239423_1_, p_239423_2_) -> {
//            CompoundTag compoundnbt = p_239423_0_.getOrCreateTag();
//            return p_239423_0_.hasDisplayName() && compoundnbt.contains("LodestoneTracked") && !compoundnbt.getBoolean("LodestoneTracked") ? 1.0F : 0.0F;
//        });
//    }
}
