package fuzs.deathfinder.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;

public class ClientConfig extends AbstractConfig {
    @Config(description = "Show player coordinates on the death screen.")
    public boolean deathScreenCoordinates = true;

    public ClientConfig() {
        super("");
    }
}
