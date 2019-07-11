package com.fuzs.deathfinder.network.messages;

import com.fuzs.deathfinder.handler.DeathChatHandler;
import com.fuzs.deathfinder.helper.DeathChatHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDeathCoords extends MessageBase<MessageDeathCoords> {

    private ITextComponent name;
    private int dimension;
    private int x;
    private int y;
    private int z;

    public MessageDeathCoords() {
    }

    public MessageDeathCoords(EntityLivingBase playerMP) {
        this.name = playerMP.getDisplayName();
        this.dimension = playerMP.dimension;
        this.x = (int) playerMP.posX;
        this.y = (int) playerMP.posY;
        this.z = (int) playerMP.posZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.name = ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
        this.dimension = buf.readUnsignedByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(this.name));
        buf.writeByte(this.dimension);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    @Override
    public void handleClientSide(MessageDeathCoords message, EntityPlayer player) {

        Minecraft gameController = Minecraft.getMinecraft();

        gameController.addScheduledTask(() -> {

            DeathChatHelper.deathMessageBuffer.add(new DeathChatHelper.DeathMessageBuffer(message.getPosition(), message.getDimension(), message.getName()));

        });

    }

    @Override
    public void handleServerSide(MessageDeathCoords message, EntityPlayer player) {

    }

    @SideOnly(Side.CLIENT)
    private ITextComponent getName() {
        return this.name;
    }

    @SideOnly(Side.CLIENT)
    private int getDimension() {
        return this.dimension;
    }

    @SideOnly(Side.CLIENT)
    private Vec3i getPosition() {
        return new Vec3i(this.x, this.y, this.z);
    }

}
