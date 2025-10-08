package fuzs.deathfinder.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Show player coordinates on the death screen.")
    public boolean deathScreenCoordinates = true;
    @Config(description = "Add information about the last death position to the recovery compass tooltip.")
    public boolean recoveryCompassTooltip = true;
}
