package com.fuzs.deathfinder.network.message;

import com.fuzs.deathfinder.helper.DeathChatHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDeathCoords {

    private final MessageDeathCoordsData[] data;

    public MessageDeathCoords(MessageDeathCoordsData... data) {

        this.data = data;

    }

    public static void writePacketData(MessageDeathCoords message, PacketBuffer buf) {

        buf.writeVarInt(message.data.length);

        for (MessageDeathCoordsData data : message.data) {

            buf.writeTextComponent(data.getMessage());
            buf.writeInt(data.getPosition().getX());
            buf.writeInt(data.getPosition().getY());
            buf.writeInt(data.getPosition().getZ());
            buf.writeByte(data.getDimension());
            buf.writeByte(data.getType());

        }

    }

    public static MessageDeathCoords readPacketData(PacketBuffer buf) {

        int size = buf.readVarInt();
        MessageDeathCoordsData[] data = new MessageDeathCoordsData[size];

        for (int i = 0; i < size; i++) {

            ITextComponent message = buf.readTextComponent();
            Vec3i position = new Vec3i(buf.readInt(), buf.readInt(), buf.readInt());
            int dimension = buf.readUnsignedByte();
            int type = buf.readUnsignedByte();

            data[i] = new MessageDeathCoordsData(message, position, dimension, type);

        }

        return new MessageDeathCoords(data);

    }

    public static void processPacket(final MessageDeathCoords message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            for (MessageDeathCoordsData data : message.data) {

                if (!DeathChatHelper.DeathEntityType.values()[data.getType()].isEnabled()) {
                    continue;
                }

                ITextComponent componentCoordinate = DeathChatHelper.getCoordinateComponent(data.getPosition(), data.getDimension());
                ITextComponent componentDistance = new TranslationTextComponent("death.message.distance", DeathChatHelper.getDistanceComponent(data.getPosition(), data.getDimension()));
                ITextComponent component = data.getMessage().appendSibling(componentCoordinate).appendSibling(componentDistance);

                Minecraft.getInstance().ingameGUI.addChatMessage(ChatType.SYSTEM, component);

            }

        });

        ctx.get().setPacketHandled(true);

    }

    public static class MessageDeathCoordsData {

        private final ITextComponent message;
        private final Vec3i position;
        private final int dimension;
        private int type = 0;

        public MessageDeathCoordsData(ITextComponent component, LivingEntity entity) {

            this.message = component;
            this.position = new Vec3i(entity.posX, entity.posY, entity.posZ);
            this.dimension = entity.dimension.getId();

        }

        private MessageDeathCoordsData(ITextComponent component, Vec3i vec3i, int i, int j) {

            this.message = component;
            this.position = vec3i;
            this.dimension = i;
            this.type = j;

        }

        private ITextComponent getMessage() {
            return this.message;
        }

        private Vec3i getPosition() {
            return this.position;
        }

        private int getDimension() {
            return this.dimension;
        }

        private int getType() {
            return this.type;
        }

        public void setType(DeathChatHelper.DeathEntityType type) {
            this.type = type.ordinal();
        }

    }

}
