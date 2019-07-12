package com.fuzs.deathfinder.handler;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("WeakerAccess")
public class ConfigHandler {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");

	public static class GeneralConfig {

		public final ForgeConfigSpec.BooleanValue deathMessage;
		public final ForgeConfigSpec.BooleanValue deathScreen;
		public final ForgeConfigSpec.BooleanValue playerEntities;
		public final ForgeConfigSpec.BooleanValue tamedEntities;
		public final ForgeConfigSpec.BooleanValue namedEntities;
		public final ForgeConfigSpec.ConfigValue<String> deathMessageCommand;

		private GeneralConfig(String name) {

			BUILDER.push(name);

			this.deathMessage = ConfigHandler.BUILDER.comment("Add coordinates to the end of ever death message.").define("Coordinates In Death Message", true);
			this.deathScreen = ConfigHandler.BUILDER.comment("Show current player coordinates on the death screen.").define("Coordinates On Death Screen", true);
			this.playerEntities = ConfigHandler.BUILDER.comment("Show death message for player entities.").define("Player Entity Deaths", true);
			this.tamedEntities = ConfigHandler.BUILDER.comment("Show death message for tamed entities.").define("Tamed Entity Deaths", true);
			this.namedEntities = ConfigHandler.BUILDER.comment("Show death message for named entities.").define("Named Entity Deaths", true);
			this.deathMessageCommand = ConfigHandler.BUILDER.comment("Command entered into chat when clicking the death message coordinates (%s is replaced by dimension, x-coordinate, y-coordinate and z-coordinate).").define("Death Message Command", "/execute in %s run tp @s %s %s %s");

			BUILDER.pop();

		}

	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
}
