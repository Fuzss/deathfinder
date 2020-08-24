package com.fuzs.deathfinder.common;

import com.fuzs.deathfinder.DeathFinder;
import com.fuzs.deathfinder.config.ConfigBuildHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DeathItemHandler {

    private final Set<ServerPlayerEntity> lostPlayers = new HashSet<>();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {

        if(evt.isWasDeath()) {

            ServerPlayerEntity player = (ServerPlayerEntity) evt.getOriginal();
            ServerWorld world = player.getServerWorld();
            // clean lostPlayers in case someone logged out
            this.lostPlayers.removeIf(lostPlayer -> lostPlayer.deathTime == 0);
            if ((ConfigBuildHandler.IGNORE_KEEP_INVENTORY.get() || !world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) &&
                    (!ConfigBuildHandler.ITEMS_ON_LOST.get() || this.lostPlayers.contains(player))) {

                if (ConfigBuildHandler.DEATH_MAP.get()) {

                    ItemStack itemstack = FilledMapItem.setupNewMap(world, (int) player.getPosX(), (int) player.getPosZ(), (byte) 2, true, true);
                    FilledMapItem.func_226642_a_(world, itemstack);
                    MapData.addTargetDecoration(itemstack, player.getPosition(), "death_" + (int) player.getPosY(), ConfigBuildHandler.MAP_DECORATION.get());
                    itemstack.setDisplayName(new TranslationTextComponent("filled_map.death", player.getDisplayName()));
                    if (!evt.getPlayer().inventory.addItemStackToInventory(itemstack)) {

                        evt.getPlayer().dropItem(itemstack, false);
                    }
                }

                if (ConfigBuildHandler.DEATH_COMPASS.get()) {

                    ItemStack itemstack1 = new ItemStack(Items.COMPASS, 1);
                    CompoundNBT compoundnbt = new CompoundNBT();
                    itemstack1.setTag(compoundnbt);
                    compoundnbt.put("LodestonePos", NBTUtil.writeBlockPos(player.getPosition()));
                    World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, world.getDimensionKey()).resultOrPartial(DeathFinder.LOGGER::error).ifPresent((p_234668_1_) -> compoundnbt.put("LodestoneDimension", p_234668_1_));
                    compoundnbt.putBoolean("LodestoneTracked", false);
                    itemstack1.setDisplayName(new TranslationTextComponent("lodestone_compass.death", player.getDisplayName()));
                    if (!evt.getPlayer().inventory.addItemStackToInventory(itemstack1)) {

                        evt.getPlayer().dropItem(itemstack1, false);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemPickup(final EntityItemPickupEvent evt) {

        if (!ConfigBuildHandler.REMOVE_DEATH_TRACKERS.get()) {

            return;
        }

        PlayerInventory inventory = evt.getPlayer().inventory;
        World world = evt.getPlayer().world;
        final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(inventory.mainInventory, inventory.armorInventory, inventory.offHandInventory);
        for(List<ItemStack> list : allInventories) {

            for(ItemStack itemstack : list) {

                if (!itemstack.isEmpty() && itemstack.hasDisplayName()) {

                    if (ConfigBuildHandler.DEATH_MAP.get() && itemstack.isItemEqual(new ItemStack(Items.FILLED_MAP))) {

                        CompoundNBT compoundnbt = itemstack.getOrCreateTag();
                        if (compoundnbt.contains("Decorations", 9)) {

                            ListNBT listnbt = compoundnbt.getList("Decorations", 10);
                            for (int j = 0; j < listnbt.size(); ++j) {

                                CompoundNBT compoundnbt1 = listnbt.getCompound(j);
                                String id = compoundnbt1.getString("id");
                                if (id.contains("death")) {

                                    double posX = compoundnbt1.getDouble("x");
                                    double posY = Integer.parseInt(id.replaceAll("\\D", ""));
                                    double posZ = compoundnbt1.getDouble("z");
                                    if (evt.getPlayer().getPosition().withinDistance(new BlockPos(posX, posY, posZ), 12.0)) {

                                        inventory.deleteStack(itemstack);
                                    }
                                }
                            }
                        }
                    } else if (ConfigBuildHandler.DEATH_COMPASS.get() && itemstack.isItemEqual(new ItemStack(Items.COMPASS))) {

                        CompoundNBT compoundnbt = itemstack.getOrCreateTag();
                        boolean flag = compoundnbt.contains("LodestonePos");
                        boolean flag1 = compoundnbt.contains("LodestoneDimension");
                        boolean flag2 = compoundnbt.contains("LodestoneTracked") && !compoundnbt.getBoolean("LodestoneTracked");
                        if (flag && flag1 && flag2) {

                            Optional<RegistryKey<World>> optional = CompassItem.func_234667_a_(compoundnbt);
                            if (optional.isPresent() && world.getDimensionKey() == optional.get()) {

                                if (NBTUtil.readBlockPos(compoundnbt.getCompound("LodestonePos")).withinDistance(evt.getPlayer().getPosition(), 12.0)) {

                                    inventory.deleteStack(itemstack);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemPickup(final LivingDropsEvent evt) {

        if (evt.getEntityLiving() instanceof ServerPlayerEntity && !evt.getDrops().isEmpty()) {

            this.lostPlayers.add((ServerPlayerEntity) evt.getEntityLiving());
        }
    }

}
