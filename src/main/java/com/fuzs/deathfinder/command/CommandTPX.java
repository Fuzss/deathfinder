package com.fuzs.deathfinder.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTPX extends CommandBase {

    private final String name;

    public CommandTPX(String s) {
        this.name = s;
    }

    /**
     * Gets the name of the command
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
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
        if (args.length != 2 && args.length != 4 && args.length != 5 && args.length != 7) {
            throw new WrongUsageException("commands.tpx.usage", this.name);
        }

        Entity entity = getEntity(server, sender, args[0]);

        if (args.length != 2) {

            Vec3d position = parseCoordinates(entity, args, 1);
            int dimension = args.length > 4 ? parseInt(args[4]) : entity.dimension;
            Vec3d rotation = parseRotation(entity, args, 5);

            teleportEntityToCoordinates(server, entity, dimension, position, rotation);

            notifyCommandListener(sender, this, "commands.tpx.success.coordinates", entity.getName(), position.x, position.y, position.z, entity.dimension);

        } else {

            Entity destination = getEntity(server, sender, args[args.length - 1]);

            teleportEntityToCoordinates(server, entity, destination.dimension, new Vec3d(destination.posX, destination.posY, destination.posZ), new Vec3d(destination.rotationYaw, destination.rotationPitch, 0.0));

            notifyCommandListener(sender, this, "commands.tpx.success.entity", entity.getName(), destination.getName(), entity.dimension);

        }
    }

    private static Vec3d parseCoordinates(Entity entity, String[] args, int startIndex) throws CommandException {

        CoordinateArg argX = parseCoordinate(entity.posX, args[startIndex], true);
        CoordinateArg argY = parseCoordinate(entity.posY, args[startIndex + 1], -4096, 4096, false);
        CoordinateArg argZ = parseCoordinate(entity.posZ, args[startIndex + 2], true);

        return new Vec3d(argX.getResult(), argY.getResult(), argZ.getResult());

    }

    private static Vec3d parseRotation(Entity entity, String[] args, int startIndex) throws CommandException {

        CommandBase.CoordinateArg argYaw = parseCoordinate((double)entity.rotationYaw, args.length > startIndex ? args[startIndex] : "~", false);
        CommandBase.CoordinateArg argPitch = parseCoordinate((double)entity.rotationPitch, args.length > startIndex + 1 ? args[startIndex + 1] : "~", false);

        float yaw = (float) MathHelper.wrapDegrees(argYaw.getResult());
        float pitch = (float) MathHelper.wrapDegrees(argPitch.getResult());
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        return new Vec3d(yaw, pitch, 0.0);

    }

    /**
     * Teleports an entity to the specified coordinates
     */
    private static void teleportEntityToCoordinates(MinecraftServer server, Entity entity, int dimension, Vec3d position, Vec3d rotation) throws CommandException {

        if (entity.world == null) {
            return;
        }

        entity.dismountRidingEntity();
        entity.setRotationYawHead((float) rotation.x);

        if (entity.dimension != dimension) {

            checkDimensionId(dimension);
            CommandTeleporter teleporter = new CommandTPX.CommandTeleporter(position, rotation);

            if (entity instanceof EntityPlayerMP) {
                server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, dimension, teleporter);
            } else {
                entity.changeDimension(dimension, teleporter);
            }

        } else {

            if (entity instanceof EntityPlayerMP) {
                ((EntityPlayerMP) entity).connection.setPlayerLocation(position.x, position.y, position.z, (float) rotation.x, (float) rotation.y);
            } else {
                entity.setLocationAndAngles(position.x, position.y, position.z, (float) rotation.x, (float) rotation.y);
            }

        }

        if (!(entity instanceof EntityLivingBase) || !((EntityLivingBase) entity).isElytraFlying()) {
            entity.motionY = 0.0;
            entity.onGround = true;
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

    private static void checkDimensionId(int id) throws NumberInvalidException
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

        private CommandTeleporter(Vec3d position, Vec3d rotation)
        {
            this.targetPos = new BlockPos(position);
            this.rotationYaw = (float) rotation.x;
            this.rotationPitch = (float) rotation.y;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw)
        {
            entity.moveToBlockPosAndAngles(this.targetPos, this.rotationYaw, this.rotationPitch);
        }
    }
}