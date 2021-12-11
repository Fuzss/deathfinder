package fuzs.deathfinder.client;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import fuzs.deathfinder.registry.ModRegistry;
import fuzs.deathfinder.world.item.DeathCompassItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import javax.annotation.Nullable;
import java.util.Optional;

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

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        ItemProperties.register(ModRegistry.DEATH_COMPASS_ITEM.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
            private final CompassWobble wobble = new CompassWobble();
            private final CompassWobble wobbleRandom = new CompassWobble();

            @Override
            public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, int p_174675_) {
                Entity entity = (Entity)(livingEntity != null ? livingEntity : stack.getEntityRepresentation());
                if (entity == null || !stack.hasTag()) {
                    return 0.0F;
                } else {
                    if (level == null && entity.level instanceof ClientLevel) {
                        level = (ClientLevel)entity.level;
                    }

                    BlockPos blockpos = this.getLastDeathPosition(level, stack.getOrCreateTag());
                    long gameTime = level.getGameTime();
                    if (blockpos != null && !(entity.position().distanceToSqr((double)blockpos.getX() + 0.5D, entity.position().y(), (double)blockpos.getZ() + 0.5D) < (double)1.0E-5F)) {
                        boolean flag = livingEntity instanceof Player && ((Player)livingEntity).isLocalPlayer();
                        double d1 = 0.0D;
                        if (flag) {
                            d1 = (double)livingEntity.getYRot();
                        } else if (entity instanceof ItemFrame) {
                            d1 = this.getFrameRotation((ItemFrame)entity);
                        } else if (entity instanceof ItemEntity) {
                            d1 = (double)(180.0F - ((ItemEntity)entity).getSpin(0.5F) / ((float)Math.PI * 2F) * 360.0F);
                        } else if (livingEntity != null) {
                            d1 = (double)livingEntity.yBodyRot;
                        }

                        d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
                        double d2 = this.getAngleTo(Vec3.atCenterOf(blockpos), entity) / (double)((float)Math.PI * 2F);
                        double d3;
                        if (flag) {
                            if (this.wobble.shouldUpdate(gameTime)) {
                                this.wobble.update(gameTime, 0.5D - (d1 - 0.25D));
                            }

                            d3 = d2 + this.wobble.rotation;
                        } else {
                            d3 = 0.5D - (d1 - 0.25D - d2);
                        }

                        return Mth.positiveModulo((float)d3, 1.0F);
                    } else {
                        if (this.wobbleRandom.shouldUpdate(gameTime)) {
                            this.wobbleRandom.update(gameTime, Math.random());
                        }

                        double d0 = this.wobbleRandom.rotation + (double)((float)this.hash(p_174675_) / 2.14748365E9F);
                        return Mth.positiveModulo((float)d0, 1.0F);
                    }
                }
            }

            private int hash(int p_174670_) {
                return p_174670_ * 1327217883;
            }

            @Nullable
            private BlockPos getLastDeathPosition(Level p_117916_, CompoundTag p_117917_) {
                boolean flag = p_117917_.contains("LastDeathPos");
                boolean flag1 = p_117917_.contains("LastDeathDimension");
                if (flag && flag1) {
                    Optional<ResourceKey<Level>> optional = DeathCompassItem.getLastDeathDimension(p_117917_);
                    if (optional.isPresent() && p_117916_.dimension() == optional.get()) {
                        return NbtUtils.readBlockPos(p_117917_.getCompound("LastDeathPos"));
                    }
                }
                return null;
            }

            private double getFrameRotation(ItemFrame p_117914_) {
                Direction direction = p_117914_.getDirection();
                int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
                return (double)Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + p_117914_.getRotation() * 45 + i);
            }

            private double getAngleTo(Vec3 p_117919_, Entity p_117920_) {
                return Math.atan2(p_117919_.z() - p_117920_.getZ(), p_117919_.x() - p_117920_.getX());
            }
        });
    }

    private static class CompassWobble {
        double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        public boolean shouldUpdate(long p_117934_) {
            return this.lastUpdateTick != p_117934_;
        }

        public void update(long p_117936_, double p_117937_) {
            this.lastUpdateTick = p_117936_;
            double d0 = p_117937_ - this.rotation;
            d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            this.deltaRotation += d0 * 0.1D;
            this.deltaRotation *= 0.8D;
            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
        }
    }
}
