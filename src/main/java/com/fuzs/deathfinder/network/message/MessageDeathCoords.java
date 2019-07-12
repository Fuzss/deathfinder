package com.fuzs.deathfinder.network.message;

import com.fuzs.deathfinder.helper.DeathChatHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

        }

    }

    public static MessageDeathCoords readPacketData(PacketBuffer buf) {

        int size = buf.readVarInt();
        MessageDeathCoordsData[] data = new MessageDeathCoordsData[size];

        for (int i = 0; i < size; i++) {

            ITextComponent message = buf.readTextComponent();
            Vec3i position = new Vec3i(buf.readInt(), buf.readInt(), buf.readInt());
            int dimension = buf.readUnsignedByte();

            data[i] = new MessageDeathCoordsData(message, position, dimension);

        }

        return new MessageDeathCoords(data);

    }

    public static void processPacket(final MessageDeathCoords message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            for (MessageDeathCoordsData data : message.data) {

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

        public MessageDeathCoordsData(ITextComponent component, LivingEntity entity) {

            message = component;
            position = new Vec3i(entity.posX, entity.posY, entity.posZ);
            dimension = entity.dimension.getId();

        }

        private MessageDeathCoordsData(ITextComponent component, Vec3i vec3i, int i) {

            message = component;
            position = vec3i;
            dimension = i;

        }

        @OnlyIn(Dist.CLIENT)
        private ITextComponent getMessage() {
            return message;
        }

        @OnlyIn(Dist.CLIENT)
        private Vec3i getPosition() {
            return position;
        }

        @OnlyIn(Dist.CLIENT)
        private int getDimension() {
            return dimension;
        }

    }

}
