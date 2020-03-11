package com.fuzs.deathfinder.config;

import com.google.common.collect.Lists;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.BooleanValue DEATH_MESSAGE;
	public static final ForgeConfigSpec.BooleanValue DEATH_SCREEN;
	public static final ForgeConfigSpec.BooleanValue DEATH_MAP;
	public static final ForgeConfigSpec.BooleanValue PLAYERS;
	public static final ForgeConfigSpec.BooleanValue TAMED;
	public static final ForgeConfigSpec.BooleanValue NAMED;
	public static final ForgeConfigSpec.BooleanValue ALL;
	public static final ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;
	public static final ForgeConfigSpec.ConfigValue<List<String>> WHITELIST;
	public static final ForgeConfigSpec.EnumValue<MapDecoration.Type> MAP_DECORATION;
	public static final ForgeConfigSpec.BooleanValue IGNORE_KEEP_INVENTORY;

	static {

		BUILDER.push("general");
		DEATH_MESSAGE = ConfigBuildHandler.BUILDER.comment("Add coordinates to the end of ever death message.").define("Death Message Coordinates", true);
		DEATH_SCREEN = ConfigBuildHandler.BUILDER.comment("Show current player coordinates on the death screen.").define("Death Screen Coordinates", true);
		DEATH_MAP = ConfigBuildHandler.BUILDER.comment("Give the player an explorer map on respawn leading to their point of death.").define("Death Maps", false);
		BUILDER.pop();

		BUILDER.push("death_messages");
		PLAYERS = ConfigBuildHandler.BUILDER.comment("Show death message for player entities.").define("Player Deaths", true);
		TAMED = ConfigBuildHandler.BUILDER.comment("Show death message for tamed entities.").define("Pet Deaths", true);
		NAMED = ConfigBuildHandler.BUILDER.comment("Show death message for named entities.").define("Named Entity Deaths", true);
		ALL = ConfigBuildHandler.BUILDER.comment("Show death message for all entities.").define("All Deaths", false);
		BLACKLIST = ConfigBuildHandler.BUILDER.comment("Entities to be excluded when \"All Deaths\" is enabled. Format for every entry is \"<namespace>:<id>\".").define("Entity Blacklist", Lists.newArrayList("minecraft:bat"));
		WHITELIST = ConfigBuildHandler.BUILDER.comment("Only entities to be included when \"All Deaths\" is enabled. Format for every entry is \"<namespace>:<id>\".").define("Entity Whitelist", Lists.newArrayList());
		BUILDER.pop();

		BUILDER.push("death_maps");
		MAP_DECORATION = ConfigBuildHandler.BUILDER.comment("Icon to mark a death point with.").defineEnum("Death Marker", MapDecoration.Type.TARGET_X);
		IGNORE_KEEP_INVENTORY = ConfigBuildHandler.BUILDER.comment("Give maps even when the \"keepInventory\" gamerule is enabled.").define("Ignore Keep Inventory", true);
		BUILDER.pop();

	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
}
