package com.fuzs.deathfinder.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");

	public static class GeneralConfig {

		public final ForgeConfigSpec.BooleanValue deathMessage;
		public final ForgeConfigSpec.BooleanValue deathScreen;
		public final ForgeConfigSpec.BooleanValue players;
		public final ForgeConfigSpec.BooleanValue tamed;
		public final ForgeConfigSpec.BooleanValue named;
		public final ForgeConfigSpec.BooleanValue all;
		public final ForgeConfigSpec.ConfigValue<List<String>> entityBlacklist;

		private GeneralConfig(String name) {

			BUILDER.push(name);

			this.deathMessage = ConfigBuildHandler.BUILDER.comment("Add coordinates to the end of ever death message.").define("Death Message Coordinates", true);
			this.deathScreen = ConfigBuildHandler.BUILDER.comment("Show current player coordinates on the death screen.").define("Death Screen Coordinates", true);
			this.players = ConfigBuildHandler.BUILDER.comment("Show death message for player entities.").define("Player Deaths", true);
			this.tamed = ConfigBuildHandler.BUILDER.comment("Show death message for tamed entities.").define("Pet Deaths", true);
			this.named = ConfigBuildHandler.BUILDER.comment("Show death message for named entities.").define("Named Entity Deaths", true);
			this.all = ConfigBuildHandler.BUILDER.comment("Show death message for all entities.").define("All Deaths", false);
			this.entityBlacklist = ConfigBuildHandler.BUILDER.comment("Entities to be excluded when \"All Deaths\" is enabled. Format for every entry is \"<namespace>:<id>\".").define("Entity Blacklist", Lists.newArrayList("minecraft:bat"));

			BUILDER.pop();

		}

	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
}
