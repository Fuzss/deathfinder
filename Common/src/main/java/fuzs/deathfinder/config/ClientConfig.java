package fuzs.deathfinder.config;

import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.annotation.Config;

public class ClientConfig implements ConfigCore {
    @Config(description = "Show player coordinates on the death screen.")
    public boolean deathScreenCoordinates = true;
}
