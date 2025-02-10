package fuzs.deathfinder.network.chat;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomComponentSerializer {
    /**
     * Similar to {@link ComponentSerialization#CODEC}.
     */
    public static final Codec<Component> CODEC = Codec.recursive("Component", CustomComponentSerializer::createCodec);
    /**
     * Similar to {@link ComponentSerialization#STREAM_CODEC}.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(
            CODEC);
    /**
     * Similar to {@link TranslatableContents#ARG_CODEC}.
     */
    private static final Codec<Object> TRANSLATABLE_CONTENTS_ARG_CODEC = Codec.either(TranslatableContents.PRIMITIVE_ARG_CODEC,
                    CODEC)
            .xmap(arg -> arg.map(argx -> argx, text -> Objects.requireNonNullElse(text.tryCollapseToString(), text)),
                    arg -> arg instanceof Component component ? Either.right(component) : Either.left(arg));
    /**
     * Similar to {@link TranslatableContents#CODEC}.
     */
    public static final MapCodec<TranslatableContents> TRANSLATABLE_CONTENTS_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codec.STRING.fieldOf("translate").forGetter(TranslatableContents::getKey),
                            Codec.STRING.optionalFieldOf("fallback")
                                    .forGetter(contents -> Optional.ofNullable(contents.getFallback())),
                            TRANSLATABLE_CONTENTS_ARG_CODEC.listOf()
                                    .optionalFieldOf("with")
                                    .forGetter(contents -> TranslatableContents.adjustArgs(contents.getArgs())))
                    .apply(instance, TranslatableContents::create));
    /**
     * Similar to {@link TranslatableContents#TYPE}.
     */
    public static final ComponentContents.Type<TranslatableContents> TRANSLATABLE_CONTENTS_TYPE = new ComponentContents.Type<>(
            TRANSLATABLE_CONTENTS_CODEC,
            "translatable");

    /**
     * Copied from {@link ComponentSerialization#createCodec(Codec)} with a different style codec.
     */
    private static Codec<Component> createCodec(Codec<Component> codec) {
        // use a custom translatable contents type with a custom codec that uses our custom component codec
        ComponentContents.Type<?>[] types = new ComponentContents.Type[]{
                PlainTextContents.TYPE,
                TRANSLATABLE_CONTENTS_TYPE,
                KeybindContents.TYPE,
                ScoreContents.TYPE,
                SelectorContents.TYPE,
                NbtContents.TYPE
        };
        // use a custom translatable contents type with a custom codec that uses our custom component codec
        MapCodec<ComponentContents> mapCodec = ComponentSerialization.createLegacyComponentMatcher(types,
                ComponentContents.Type::codec,
                contents -> contents.type() == TranslatableContents.TYPE ? TRANSLATABLE_CONTENTS_TYPE : contents.type(),
                "type");
        Codec<Component> codec2 = RecordCodecBuilder.create(instance -> instance.group(mapCodec.forGetter(Component::getContents),
                        ExtraCodecs.nonEmptyList(codec.listOf())
                                .optionalFieldOf("extra", List.of())
                                .forGetter(Component::getSiblings),
                        Style.Serializer.MAP_CODEC.forGetter(Component::getStyle),
                        // additional line to include our custom component codec
                        TeleportClickEvent.CODEC.optionalFieldOf("custom_data")
                                .forGetter(style ->
                                        style.getStyle().getClickEvent() instanceof TeleportClickEvent teleportClickEvent ?
                                                Optional.of(teleportClickEvent) : Optional.empty()))
                .apply(instance,
                        (ComponentContents componentContents, List<Component> components, Style style, Optional<TeleportClickEvent> optional) -> {
                            MutableComponent mutableComponent = MutableComponent.create(componentContents);
                            components.forEach(mutableComponent::append);
                            mutableComponent.setStyle(style);
                            optional.ifPresent(teleportClickEvent -> mutableComponent.withStyle(currentStyle -> currentStyle.withClickEvent(
                                    teleportClickEvent)));
                            return mutableComponent;
                        }));
        return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(codec.listOf())), codec2)
                .xmap(either -> either.map(eitherx -> eitherx.map(Component::literal,
                        ComponentSerialization::createFromList), content -> content), content -> {
                    String string = content.tryCollapseToString();
                    return string != null ? Either.left(Either.left(string)) : Either.right(content);
                });
    }
}
