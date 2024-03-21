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
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_POSITION, "at %s");
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DIMENSION, "in dimension %s");
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DISTANCE_DIMENSION, "very far away");
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DISTANCE_CLOSE, "very close");
        builder.add(DeathMessageBuilder.KEY_DEATH_MESSAGE_DISTANCE_BLOCKS, "%s blocks away");
        builder.add(DeathScreenHandler.KEY_DEATH_SCREEN_POSITION, "X: %s Y: %s Z: %s");
        builder.add(CompassTooltipHandler.KEY_COMPASS_POSITION, "X: %s Y: %s Z: %s");
        builder.add(CompassTooltipHandler.KEY_COMPASS_DIMENSION, "Dimension: %s");
        builder.add(TeleportToDeathProblem.MISSING_PERMISSIONS.getComponent(), "You do not have the necessary permissions to teleport");
        builder.add(TeleportToDeathProblem.ALREADY_USED.getComponent(), "You have already teleported to a death point");
        builder.add(TeleportToDeathProblem.TOO_LONG_AGO.getComponent(), "This death occurred too long ago");
        builder.add(TeleportToDeathProblem.NOT_MOST_RECENT.getComponent(), "This is not your most recent death point");
        builder.add(TeleportToDeathProblem.NOT_YOURS.getComponent(), "This is not your death point");
        builder.add(TeleportToDeathProblem.OTHER_PROBLEM.getComponent(), "Teleporting to death points is not allowed on this server");
    }
}
