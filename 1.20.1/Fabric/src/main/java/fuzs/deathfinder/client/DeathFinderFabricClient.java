package fuzs.deathfinder.client;

import fuzs.deathfinder.DeathFinder;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class DeathFinderFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(DeathFinder.MOD_ID, DeathFinderClient::new);
    }
}
