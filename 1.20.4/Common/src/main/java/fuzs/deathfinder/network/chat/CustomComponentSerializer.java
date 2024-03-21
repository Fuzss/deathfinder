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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CustomComponentSerializer {
    public static final MapCodec<Style> STYLE_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Style.Serializer.CODEC.fieldOf("style").forGetter(Function.identity()),
                ExtraCodecs.strictOptionalField(TeleportClickEvent.CODEC, "custom_click_event")
                        .forGetter(style -> style.getClickEvent() instanceof TeleportClickEvent teleportClickEvent ?
                                Optional.of(teleportClickEvent) :
                                Optional.empty())
        ).apply(instance, (style, teleportClickEvent) -> {
            return teleportClickEvent.map(style::withClickEvent).orElse(style);
        });
    });
    /**
     * Similar to from {@link ComponentSerialization#CODEC}.
     */
    public static final Codec<Component> COMPONENT_CODEC = ExtraCodecs.recursive("Component",
            CustomComponentSerializer::createCodec
    );
    /**
     * Similar to {@link TranslatableContents#ARG_CODEC}.
     */
    private static final Codec<Object> TRANSLATABLE_CONTENTS_ARG_CODEC = Codec.either(TranslatableContents.PRIMITIVE_ARG_CODEC,
                    COMPONENT_CODEC
            )
            .xmap(arg -> arg.map(argx -> argx, text -> Objects.requireNonNullElse(text.tryCollapseToString(), text)),
                    arg -> arg instanceof Component component ? Either.right(component) : Either.left(arg)
            );
    /**
     * Similar to {@link TranslatableContents#CODEC}.
     */
    public static final MapCodec<TranslatableContents> TRANSLATABLE_CONTENTS_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codec.STRING.fieldOf("translate").forGetter(TranslatableContents::getKey),
                    Codec.STRING.optionalFieldOf("fallback")
                            .forGetter(contents -> Optional.ofNullable(contents.getFallback())),
                    ExtraCodecs.strictOptionalField(TRANSLATABLE_CONTENTS_ARG_CODEC.listOf(), "with")
                            .forGetter(contents -> TranslatableContents.adjustArgs(contents.getArgs()))
            ).apply(instance, TranslatableContents::create));
    /**
     * Similar to {@link TranslatableContents#TYPE}.
     */
    public static final ComponentContents.Type<TranslatableContents> TRANSLATABLE_CONTENTS_TYPE = new ComponentContents.Type<>(
            TRANSLATABLE_CONTENTS_CODEC,
            "translatable"
    );

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
                        ComponentSerialization::createFromList
                ), content -> content), content -> {
                    String string = content.tryCollapseToString();
                    return string != null ? Either.left(Either.left(string)) : Either.right(content);
                });
    }

    /**
     * Same as {@link FriendlyByteBuf#readComponent()}, with our custom codec though.
     */
    public static Component readComponent(FriendlyByteBuf buf) {
        return buf.readWithCodec(NbtOps.INSTANCE, COMPONENT_CODEC,
                NbtAccounter.create(2097152L)
        );
    }

    /**
     * Same as {@link FriendlyByteBuf#writeComponent(Component)}, with our custom codec though.
     */
    public static void writeComponent(FriendlyByteBuf buf, Component component) {
        buf.writeWithCodec(NbtOps.INSTANCE, COMPONENT_CODEC, component);
    }
}
