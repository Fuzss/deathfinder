package fuzs.deathfinder.handler;

import fuzs.deathfinder.config.ConfigBuildHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class ItemHandler {

    private final Set<ServerPlayer> lostPlayers = new HashSet<>();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {
        if (!evt.isWasDeath()) return;
        ServerPlayer player = (ServerPlayer) evt.getOriginal();
        ServerLevel world = player.getLevel();
        // clean lostPlayers in case someone logged out
        this.lostPlayers.removeIf(lostPlayer -> lostPlayer.deathTime == 0);
        if ((ConfigBuildHandler.IGNORE_KEEP_INVENTORY.get() || !world.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) &&
                (!ConfigBuildHandler.ITEMS_ON_LOST.get() || this.lostPlayers.contains(player))) {
            ItemStack itemStack = new ItemStack(Items.COMPASS, 1);
            CompoundTag tag = new CompoundTag();
            itemStack.setTag(tag);
//                tag.put("LodestonePos", NBTUtil.writeBlockPos(player.getPosition()));
//                Level.RESOURCE_KEY_CODEC.encodeStart(NBTDynamicOps.INSTANCE, world.getDimensionKey()).resultOrPartial(DeathFinder.LOGGER::error).ifPresent((p_234668_1_) -> tag.put("LodestoneDimension", p_234668_1_));
//                tag.putBoolean("LodestoneTracked", false);
//                itemStack.setDisplayName(new TranslatableComponent("lodestone_compass.death", player.getDisplayName()));
//                if (!evt.getPlayer().getInventory().add(itemStack)) {
//                    evt.getPlayer().drop(itemStack, false);
//                }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemPickup(final LivingDropsEvent evt) {

        if (evt.getEntityLiving() instanceof ServerPlayer && !evt.getDrops().isEmpty()) {

            this.lostPlayers.add((ServerPlayer) evt.getEntityLiving());
        }
    }

}
