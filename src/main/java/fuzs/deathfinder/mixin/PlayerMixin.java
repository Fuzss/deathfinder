package fuzs.deathfinder.mixin;

import fuzs.deathfinder.world.entity.player.PlayerDeathTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDeathTracker {
    @Unique
    private BlockPos lastDeathPosition = BlockPos.ZERO;
    @Unique
    private ResourceKey<Level> lastDeathDimension = Level.OVERWORLD;
    @Unique
    private long lastDeathDate;
    
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag p_36215_, CallbackInfo callbackInfo) {
        if (p_36215_.contains("LastDeathX", 99) && p_36215_.contains("LastDeathY", 99) && p_36215_.contains("LastDeathZ", 99)) {
            this.lastDeathPosition = new BlockPos(p_36215_.getInt("LastDeathX"), p_36215_.getInt("LastDeathY"), p_36215_.getInt("LastDeathZ"));
            if (p_36215_.contains("LastDeathDimension")) {
                this.lastDeathDimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_36215_.get("LastDeathDimension")).resultOrPartial(LOGGER::error).orElse(Level.OVERWORLD);
            }
            if (p_36215_.contains("LastDeathDate")) {
                this.lastDeathDate = p_36215_.getLong("LastDeathDate");
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag p_36265_, CallbackInfo callbackInfo) {
        if (this.lastDeathPosition != BlockPos.ZERO) {
            p_36265_.putInt("LastDeathX", this.lastDeathPosition.getX());
            p_36265_.putInt("LastDeathY", this.lastDeathPosition.getY());
            p_36265_.putInt("LastDeathZ", this.lastDeathPosition.getZ());
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.lastDeathDimension.location()).resultOrPartial(LOGGER::error).ifPresent((p_9134_) -> {
                p_36265_.put("LastDeathDimension", p_9134_);
            });
            p_36265_.putLong("LastDeathDate", this.lastDeathDate);
        }
    }

    @Override
    public BlockPos getLastDeathPosition() {
        return this.lastDeathPosition;
    }

    @Override
    public ResourceKey<Level> getLastDeathDimension() {
        return this.lastDeathDimension;
    }

    @Override
    public long getLastDeathDate() {
        return this.lastDeathDate;
    }

    @Override
    public void setLastDeathPosition(BlockPos lastDeathPosition) {
        this.lastDeathPosition = lastDeathPosition;
    }

    @Override
    public void setLastDeathDimension(ResourceKey<Level> lastDeathDimension) {
        this.lastDeathDimension = lastDeathDimension;
    }

    @Override
    public void setLastDeathDate(long lastDeathDate) {
        this.lastDeathDate = lastDeathDate;
    }
}
