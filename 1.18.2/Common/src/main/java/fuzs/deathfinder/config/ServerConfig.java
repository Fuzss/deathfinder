package fuzs.deathfinder.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class ServerConfig implements ConfigCore {
    @Config(name = "death_message_components")
    public ComponentsConfig components = new ComponentsConfig();
    @Config(name = "death_messages")
    public MessagesConfig messages = new MessagesConfig();

    public enum TeleportRestriction {
        NO_ONE, OPERATORS_ONLY, EVERYONE
    }

    public static class ComponentsConfig implements ConfigCore {
        @Config(description = "Add position component to death messages.")
        public boolean positionComponent = true;
        @Config(description = "Add dimension component to death messages.")
        public boolean dimensionComponent = true;
        @Config(description = "Add distance component to death messages.")
        public boolean distanceComponent = true;
        @Config(description = {"Who should be allowed to click the position component to teleport there.", "Normal player can only teleport to their own death position once in a given time frame, if enabled.", "Operators can teleport to any death position without limitations, if enabled."})
        public TeleportRestriction allowTeleporting = TeleportRestriction.OPERATORS_ONLY;
        @Config(description = {"Amount of seconds in which teleporting to the last death point is possible.", "Set to -1 to remove time limit."})
        @Config.IntRange(min = -1)
        public int teleportInterval = 300;
    }

    public static class MessagesConfig implements ConfigCore {
        @Config(description = "Show death message for players.")
        public boolean playerDeaths = true;
        @Config(description = "Show death message for tamed entities.")
        public boolean petDeaths = true;
        @Config(description = "Show death message for villagers.")
        public boolean villagerDeaths = true;
        @Config(description = "Show death message for named entities.")
        public boolean namedEntityDeaths = true;
        @Config(description = "Show death message for all entities.")
        public boolean allDeaths = false;
        @Config(name = "death_message_blacklist", description = {"Entities to be excluded when \"all_deaths\" is enabled.", ConfigDataSet.CONFIG_DESCRIPTION})
        List<String> deathMessageBlacklistRaw = ConfigDataSet.toString(Registry.ENTITY_TYPE_REGISTRY, EntityType.BAT, EntityType.GLOW_SQUID);
        @Config(name = "death_message_whitelist", description = {"The only entities to be included when \"all_deaths\" is enabled. Takes precedence over blacklist.", ConfigDataSet.CONFIG_DESCRIPTION})
        List<String> deathMessageWhitelistRaw = ConfigDataSet.toString(Registry.ENTITY_TYPE_REGISTRY);

        public ConfigDataSet<EntityType<?>> deathMessageBlacklist;
        public ConfigDataSet<EntityType<?>> deathMessageWhitelist;

        @Override
        public void afterConfigReload() {
            this.deathMessageBlacklist = ConfigDataSet.from(Registry.ENTITY_TYPE_REGISTRY, this.deathMessageBlacklistRaw);
            this.deathMessageWhitelist = ConfigDataSet.from(Registry.ENTITY_TYPE_REGISTRY, this.deathMessageWhitelistRaw);
        }
    }
}
