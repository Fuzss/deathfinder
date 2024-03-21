package fuzs.deathfinder.network.chat;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.*;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CustomComponentSerializer {
    public static final MapCodec<Style> STYLE_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Style.Serializer.CODEC.fieldOf("style").forGetter(Function.identity()),
                ExtraCodecs.strictOptionalField(TeleportClickEvent.CODEC, "teleport_click_event")
                        .forGetter(style -> style.getClickEvent() instanceof TeleportClickEvent teleportClickEvent ?
                                Optional.of(teleportClickEvent) :
                                Optional.empty())
        ).apply(instance, (style, teleportClickEvent) -> {
            return teleportClickEvent.map(style::withClickEvent).orElse(style);
        });
    });
    public static final Codec<Component> COMPONENT_CODEC = ExtraCodecs.recursive("Component",
            CustomComponentSerializer::createCodec
    );

    /**
     * Same as {@link FriendlyByteBuf#readComponent()}, with our custom codec though.
     */
    public static Component readComponent(FriendlyByteBuf buf) {
        return buf.readWithCodec(NbtOps.INSTANCE, COMPONENT_CODEC, NbtAccounter.create(2097152L));
    }

    /**
     * Same as {@link FriendlyByteBuf#writeComponent(Component)}, with our custom codec though.
     */
    public static void writeComponent(FriendlyByteBuf buf, Component component) {
        buf.writeWithCodec(NbtOps.INSTANCE, COMPONENT_CODEC, component);
    }

    /**
     * Copied from {@link ComponentSerialization#createCodec(Codec)} with a different style codec.
     */
    private static Codec<Component> createCodec(Codec<Component> codec) {
        ComponentContents.Type<?>[] types = new ComponentContents.Type[]{
                PlainTextContents.TYPE,
                TranslatableContents.TYPE,
                KeybindContents.TYPE,
                ScoreContents.TYPE,
                SelectorContents.TYPE,
                NbtContents.TYPE
        };
        MapCodec<ComponentContents> mapCodec = ComponentSerialization.createLegacyComponentMatcher(types,
                ComponentContents.Type::codec,
                ComponentContents::type,
                "type"
        );
        Codec<Component> codec2 = RecordCodecBuilder.create(instance -> instance.group(mapCodec.forGetter(Component::getContents),
                ExtraCodecs.strictOptionalField(ExtraCodecs.nonEmptyList(codec.listOf()), "extra", List.of())
                        .forGetter(Component::getSiblings),
                // this line is changed to use our custom component codec
                STYLE_CODEC.forGetter(Component::getStyle)
        ).apply(instance, (componentContents, components, style) -> {
            MutableComponent mutableComponent = MutableComponent.create(componentContents);
            components.forEach(mutableComponent::append);
            mutableComponent.setStyle(style);
            return mutableComponent;
        }));
        return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(codec.listOf())), codec2)
                .xmap(either -> either.map(eitherx -> eitherx.map(Component::literal,
                        CustomComponentSerializer::createFromList
                ), content -> content), content -> {
                    String string = content.tryCollapseToString();
                    return string != null ? Either.left(Either.left(string)) : Either.right(content);
                });
    }

    /**
     * Copied from {@link ComponentSerialization#createFromList(List)}.
     */
    private static MutableComponent createFromList(List<Component> components) {
        MutableComponent mutableComponent = components.get(0).copy();

        for (int i = 1; i < components.size(); ++i) {
            mutableComponent.append(components.get(i));
        }

        return mutableComponent;
    }
}
