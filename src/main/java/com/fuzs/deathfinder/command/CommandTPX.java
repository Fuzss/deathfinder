package com.fuzs.deathfinder.command;

import com.sun.javafx.geom.Vec2d;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CommandTPX extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "tpx";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "commands.tpx.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.tpx.usage");
        }
        else
        {
            int i = 0;
            Entity entity;

            if (args.length != 2 && args.length != 4 && args.length != 5 && args.length != 7)
            {
                entity = getCommandSenderAsPlayer(sender);
            }
            else
            {
                entity = getEntity(server, sender, args[0]);
                i = 1;
            }

            if (args.length != 2)
            {
                if (entity.world != null)
                {
                    int k = i + 1;
                    CoordinateArg commandbase$coordinatearg = parseCoordinate(entity.posX, args[i], true);
                    CoordinateArg commandbase$coordinatearg1 = parseCoordinate(entity.posY, args[k++], -4096, 4096, false);
                    CoordinateArg commandbase$coordinatearg2 = parseCoordinate(entity.posZ, args[k++], true);

                    if (args.length > k) {

                        int commandbase$coordinatearg3 = parseInt(args[k++]);
                        CoordinateArg commandbase$coordinatearg4 = parseCoordinate((double) entity.rotationYaw, args.length > k ? args[k++] : "~", false);
                        CoordinateArg commandbase$coordinatearg5 = parseCoordinate((double) entity.rotationPitch, args.length > k ? args[k] : "~", false);

                        if (commandbase$coordinatearg3 != entity.dimension) {

                            BlockPos pos = parseBlockPos(sender, args, 1, false);
                            checkDimensionId(commandbase$coordinatearg3);
                            CommandTeleporter teleporter = new CommandTPX.CommandTeleporter(pos, (float) commandbase$coordinatearg4.getAmount(), (float) commandbase$coordinatearg5.getAmount());

                            if (entity instanceof EntityPlayerMP) {

                                server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, commandbase$coordinatearg3, teleporter);

                            } else {

                                entity.changeDimension(commandbase$coordinatearg3, teleporter);

                            }

                        } else {

                            this.teleportEntityToCoordinatesOld(entity, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg4, commandbase$coordinatearg5);

                        }

                    } else {

                        this.teleportEntityToCoordinatesOld(entity, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, parseCoordinate((double) entity.rotationYaw, "~", false), parseCoordinate((double) entity.rotationPitch, "~", false));

                    }

                    notifyCommandListener(sender, this, "commands.tpx.success.coordinates", entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult(), entity.dimension);

                }
            }
            else
            {
                Entity entity1 = getEntity(server, sender, args[args.length - 1]);
                entity.dismountRidingEntity();

//                if (entity1.world != entity.world)
//                {
//                    BlockPos pos = new BlockPos(entity1.posX, entity1.posY, entity1.posZ);
//                    int id = checkDimensionId(entity1.dimension);
//                    CommandTeleporter teleporter = new CommandTPX.CommandTeleporter(pos, entity1.rotationYaw, entity1.rotationPitch);
//
//                    if (entity instanceof EntityPlayerMP) {
//
//                        server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, id, teleporter);
//
//                    } else {
//
//                        entity.changeDimension(id, teleporter);
//
//                    }
//                }
//                else
//                {
//                    if (entity instanceof EntityPlayerMP)
//                    {
//                        ((EntityPlayerMP)entity).connection.setPlayerLocation(entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);
//                    }
//                    else
//                    {
//                        entity.setLocationAndAngles(entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);
//                    }
//                }

                this.teleportEntityToCoordinates(server, entity, entity1.dimension, new Vec3d(entity1.posX, entity1.posY, entity1.posZ), new Vec2f(entity1.rotationYaw, entity1.rotationPitch));

                notifyCommandListener(sender, this, "commands.tpx.success.entity", entity.getName(), entity1.getName(), entity.dimension);

            }
        }
    }

    private void teleportEntityToCoordinates(MinecraftServer server, Entity entity, int dimension, Vec3d position, Vec2f rotation) throws CommandException {

        if (entity.world == null) {
            return;
        }

        if (entity.dimension != dimension) {

            this.checkDimensionId(dimension);
            CommandTeleporter teleporter = new CommandTPX.CommandTeleporter(position, rotation);

            if (entity instanceof EntityPlayerMP) {
                server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, dimension, teleporter);
            } else {
                entity.changeDimension(dimension, teleporter);
            }

        } else {

            if (entity instanceof EntityPlayerMP) {
                ((EntityPlayerMP) entity).connection.setPlayerLocation(position.x, position.y, position.z, rotation.x, rotation.y);
            } else {
                entity.setLocationAndAngles(position.x, position.y, position.z, rotation.x, rotation.y);
            }

        }

        if (!(entity instanceof EntityLivingBase) || !((EntityLivingBase) entity).isElytraFlying()) {
            entity.motionY = 0.0;
            entity.onGround = true;
        }

    }

    /**
     * Teleports an entity to the specified coordinates
     */
    private void teleportEntityToCoordinatesOld(Entity teleportingEntity, CoordinateArg argX, CoordinateArg argY, CoordinateArg argZ, CoordinateArg argYaw, CoordinateArg argPitch)
    {
        if (teleportingEntity instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);

            if (argX.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.X);
            }

            if (argY.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Y);
            }

            if (argZ.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Z);
            }

            if (argPitch.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
            }

            if (argYaw.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            }

            float f = (float)argYaw.getAmount();

            if (!argYaw.isRelative())
            {
                f = MathHelper.wrapDegrees(f);
            }

            float f1 = (float)argPitch.getAmount();

            if (!argPitch.isRelative())
            {
                f1 = MathHelper.wrapDegrees(f1);
            }

            teleportingEntity.dismountRidingEntity();
            ((EntityPlayerMP)teleportingEntity).connection.setPlayerLocation(argX.getAmount(), argY.getAmount(), argZ.getAmount(), f, f1, set);
            teleportingEntity.setRotationYawHead(f);
        }
        else
        {
            float f2 = (float) MathHelper.wrapDegrees(argYaw.getResult());
            float f3 = (float) MathHelper.wrapDegrees(argPitch.getResult());
            f3 = MathHelper.clamp(f3, -90.0F, 90.0F);
            teleportingEntity.setLocationAndAngles(argX.getResult(), argY.getResult(), argZ.getResult(), f2, f3);
            teleportingEntity.setRotationYawHead(f2);
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {

        if (args.length == 1 || args.length == 2 && args[1].matches("\\w+")) {

            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());

        } else if (args.length > 1 && args.length <= 4) {

            return getTabCompletionCoordinate(args, 1, targetPos);

        } else if (args.length == 5) {

            return DimensionManager.getRegisteredDimensions().values().stream().map(it -> Integer.toString(it.firstInt())).collect(Collectors.toList());

        }

        return Collections.emptyList();

    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }

    private void checkDimensionId(int id) throws NumberInvalidException
    {
        if (!DimensionManager.isDimensionRegistered(id)) {
            throw new NumberInvalidException("commands.tpx.dimension.notFound", id);
        }
    }

    private static class CommandTeleporter implements ITeleporter
    {
        private final BlockPos targetPos;
        private final float rotationYaw;
        private final float rotationPitch;

        private CommandTeleporter(BlockPos targetPos, float yaw, float pitch)
        {
            this.targetPos = targetPos;
            this.rotationYaw = yaw;
            this.rotationPitch = pitch;
        }

        private CommandTeleporter(Vec3d position, Vec2f rotation)
        {
            this.targetPos = new BlockPos(position);
            this.rotationYaw = rotation.x;
            this.rotationPitch = rotation.y;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw)
        {
            entity.moveToBlockPosAndAngles(this.targetPos, this.rotationYaw, this.rotationPitch);
        }
    }
}