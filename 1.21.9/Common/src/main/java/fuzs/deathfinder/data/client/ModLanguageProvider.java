package fuzs.deathfinder.data.client;

import fuzs.deathfinder.client.handler.CompassTooltipHandler;
import fuzs.deathfinder.client.handler.DeathScreenHandler;
import fuzs.deathfinder.network.chat.TeleportToDeathProblem;
import fuzs.deathfinder.util.DeathMessageBuilder;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_POSITION,
                DeathMessageBuilder.FALLBACK_DEATH_MESSAGE_POSITION);
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DIMENSION,
                DeathMessageBuilder.FALLBACK_DEATH_MESSAGE_DIMENSION);
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DISTANCE_DIMENSION,
                DeathMessageBuilder.FALLBACK_DEATH_MESSAGE_DISTANCE_DIMENSION);
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DISTANCE_CLOSE,
                DeathMessageBuilder.FALLBACK_DEATH_MESSAGE_DISTANCE_CLOSE);
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DISTANCE_BLOCKS,
                DeathMessageBuilder.FALLBACK_DEATH_MESSAGE_DISTANCE_BLOCKS);
        builder.add(DeathScreenHandler.KEY_DEATH_SCREEN_POSITION, "X: %s Y: %s Z: %s");
        builder.add(CompassTooltipHandler.KEY_COMPASS_POSITION, "X: %s Y: %s Z: %s");
        builder.add(CompassTooltipHandler.KEY_COMPASS_DIMENSION, "Dimension: %s");
        TeleportToDeathProblem.forEach((TeleportToDeathProblem teleportToDeathProblem) -> {
            teleportToDeathProblem.registerTranslation(builder::add);
        });
    }
}
