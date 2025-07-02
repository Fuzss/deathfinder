package fuzs.deathfinder.network.client;

import fuzs.deathfinder.init.ModRegistry;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public record ServerboundNotifyModPresentMessage() implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundNotifyModPresentMessage> STREAM_CODEC = StreamCodec.unit(new ServerboundNotifyModPresentMessage());

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                ModRegistry.MESSAGE_SENDER_ATTACHMENT_TYPE.set(context.player(), Unit.INSTANCE);
            }
        };
    }
}
