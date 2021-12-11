package fuzs.deathfinder.world.item;

import fuzs.deathfinder.registry.ModRegistry;
import fuzs.deathfinder.world.entity.player.PlayerDeathTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DeathCompassItem extends Item implements Vanishable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();

   public DeathCompassItem(Properties p_40718_) {
      super(p_40718_);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (itemstack.hasTag()) {
         final Component component = this.getDistanceComponent(player, itemstack.getTag());
         player.displayClientMessage(component, true);
         return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
      }
      return super.use(level, player, hand);
   }

   private Component getDistanceComponent(Player source, CompoundTag tag) {
      final Optional<ResourceKey<Level>> lastDeathDimension = getLastDeathDimension(tag);
      if (lastDeathDimension.isPresent() && lastDeathDimension.get() != source.level.dimension()) {
         return new  TranslatableComponent("death.message.distance.dimension");
      } else {
         BlockPos blockpos = NbtUtils.readBlockPos(tag.getCompound("LastDeathPos"));
         double distance = source.position().distanceTo(new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ()));
         if (distance < 3.0) {
            return new TranslatableComponent("death.message.distance.close");
         } else {
            return new TranslatableComponent("death.message.distance.blocks", (int) distance);
         }
      }
   }

   public static Optional<ResourceKey<Level>> getLastDeathDimension(CompoundTag p_40728_) {
      return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get("LastDeathDimension")).result();
   }

   public static Optional<ItemStack> createDeathCompass(Player player) {
      PlayerDeathTracker tracker = (PlayerDeathTracker) player;
      if (tracker.hasLastDeathData()) {
         ItemStack itemstack = new ItemStack(ModRegistry.DEATH_COMPASS_ITEM.get(), 1);
         itemstack.setHoverName(new TranslatableComponent("item.deathfinder.death_compass").append(new TranslatableComponent("item.deathfinder.death_compass.player", player.getDisplayName())));
         CompoundTag compoundtag = itemstack.hasTag() ? itemstack.getTag() : new CompoundTag();
         itemstack.setTag(compoundtag);
         addLastDeathTags(tracker.getLastDeathDimension(), tracker.getLastDeathPosition(), tracker.getLastDeathDate(), compoundtag);
         return Optional.of(itemstack);
      }
      return Optional.empty();
   }

   private static void addLastDeathTags(ResourceKey<Level> lastDeathDimension, BlockPos lastDeathPosition, long lastDeathDate, CompoundTag p_40735_) {
      p_40735_.put("LastDeathPos", NbtUtils.writeBlockPos(lastDeathPosition));
      Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, lastDeathDimension).resultOrPartial(LOGGER::error).ifPresent((p_40731_) -> {
         p_40735_.put("LastDeathDimension", p_40731_);
      });
      p_40735_.putLong("LastDeathDate", lastDeathDate);
   }

   @Override
   public void appendHoverText(ItemStack p_4itemStack457_, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
      if (p_4itemStack457_.hasTag()) {
         CompoundTag tag = p_4itemStack457_.getTag();
         BlockPos blockpos = NbtUtils.readBlockPos(tag.getCompound("LastDeathPos"));
         tooltip.add(new TranslatableComponent("death_compass.tooltip.pos", new TextComponent(String.valueOf(blockpos.getX())).withStyle(ChatFormatting.GRAY), new TextComponent(String.valueOf(blockpos.getY())).withStyle(ChatFormatting.GRAY), new TextComponent(String.valueOf(blockpos.getZ())).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
         final Optional<ResourceKey<Level>> lastDeathDimension = getLastDeathDimension(tag);
         if (lastDeathDimension.isPresent()) {
            tooltip.add(new TranslatableComponent("death_compass.tooltip.dimension", new TextComponent(lastDeathDimension.get().location().toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
         }
         String date = DATE_FORMAT.format(new Date(tag.getLong("LastDeathDate")));
         tooltip.add(new TranslatableComponent("death_compass.tooltip.date", new TextComponent(date).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
      }
   }
}