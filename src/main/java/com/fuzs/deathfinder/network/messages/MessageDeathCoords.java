package com.fuzs.deathfinder.network.messages;

import com.fuzs.deathfinder.helper.DeathChatHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDeathCoords extends MessageBase<MessageDeathCoords> {

    private ITextComponent message;
    private Vec3i position;
    private int dimension;

    public MessageDeathCoords() {
    }

    public MessageDeathCoords(ITextComponent component1, EntityLivingBase entity) {
        this.message = component1;
        this.position = new Vec3i(entity.posX, entity.posY, entity.posZ);
        this.dimension = entity.dimension;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.message = ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
        this.position = new Vec3i(buf.readInt(), buf.readInt(), buf.readInt());
        this.dimension = buf.readUnsignedByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(this.message));
        buf.writeInt(this.position.getX());
        buf.writeInt(this.position.getY());
        buf.writeInt(this.position.getZ());
        buf.writeByte(this.dimension);
    }

    @Override
    public void handleClientSide(MessageDeathCoords message, EntityPlayer player) {

        Minecraft gameController = Minecraft.getMinecraft();

        gameController.addScheduledTask(() -> {

            ITextComponent componentCoordinate = DeathChatHelper.getCoordinateComponent(message.getPosition(), message.getDimension());
            ITextComponent componentDistance = new TextComponentTranslation("death.message.distance", DeathChatHelper.getDistanceComponent(message.getPosition(), message.getDimension()));
            ITextComponent component = message.getMessage().appendSibling(componentCoordinate).appendSibling(componentDistance);

            gameController.ingameGUI.addChatMessage(ChatType.SYSTEM, component);

        });

    }

    @Override
    public void handleServerSide(MessageDeathCoords message, EntityPlayer player) {

    }

    @SideOnly(Side.CLIENT)
    private ITextComponent getMessage() {
        return this.message;
    }

    @SideOnly(Side.CLIENT)
    private Vec3i getPosition() {
        return this.position;
    }

    @SideOnly(Side.CLIENT)
    private int getDimension() {
        return this.dimension;
    }

}
