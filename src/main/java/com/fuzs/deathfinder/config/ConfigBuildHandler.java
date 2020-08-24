package com.fuzs.deathfinder.config;

import com.google.common.collect.Lists;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	// general
	public static final ForgeConfigSpec.BooleanValue DEATH_MESSAGE;
	public static final ForgeConfigSpec.BooleanValue DEATH_SCREEN;
	public static final ForgeConfigSpec.BooleanValue DEATH_MAP;
	public static final ForgeConfigSpec.BooleanValue DEATH_COMPASS;
	// death messages
	public static final ForgeConfigSpec.BooleanValue MESSAGES_PLAYERS;
	public static final ForgeConfigSpec.BooleanValue MESSAGES_TAMED;
	public static final ForgeConfigSpec.BooleanValue MESSAGES_NAMED;
	public static final ForgeConfigSpec.BooleanValue MESSAGES_ALL;
	public static final ForgeConfigSpec.ConfigValue<List<String>> MESSAGES_BLACKLIST;
	public static final ForgeConfigSpec.ConfigValue<List<String>> MESSAGES_WHITELIST;
	// death items
	public static final ForgeConfigSpec.EnumValue<MapDecoration.Type> MAP_DECORATION;
	public static final ForgeConfigSpec.BooleanValue IGNORE_KEEP_INVENTORY;
	public static final ForgeConfigSpec.BooleanValue ITEMS_ON_LOST;
	public static final ForgeConfigSpec.BooleanValue REMOVE_DEATH_TRACKERS;
	public static final ForgeConfigSpec.BooleanValue CUSTOM_COMPASS_TEXTURE;

	static {

		BUILDER.push("general");
		DEATH_MESSAGE = ConfigBuildHandler.BUILDER.comment("Add coordinates to the end of ever death message.").define("Death Message Coordinates", true);
		DEATH_SCREEN = ConfigBuildHandler.BUILDER.comment("Show current player coordinates on the death screen.").define("Death Screen Coordinates", true);
		DEATH_MAP = ConfigBuildHandler.BUILDER.comment("Give the player a death map on respawn leading to their point of death.").define("Death Map", false);
		DEATH_COMPASS = ConfigBuildHandler.BUILDER.comment("Give the player a death compass on respawn leading to their point of death.").define("Death Compass", false);
		BUILDER.pop();

		BUILDER.push("death_messages");
		MESSAGES_PLAYERS = ConfigBuildHandler.BUILDER.comment("Show death message for players.").define("Player Deaths", true);
		MESSAGES_TAMED = ConfigBuildHandler.BUILDER.comment("Show death message for tamed entities.").define("Pet Deaths", true);
		MESSAGES_NAMED = ConfigBuildHandler.BUILDER.comment("Show death message for named entities.").define("Named Entity Deaths", true);
		MESSAGES_ALL = ConfigBuildHandler.BUILDER.comment("Show death message for all entities.").define("All Deaths", false);
		MESSAGES_BLACKLIST = ConfigBuildHandler.BUILDER.comment("Entities to be excluded when \"All Deaths\" is enabled. Format for every entry is \"<namespace>:<id>\".").define("Entity Blacklist", Lists.newArrayList("minecraft:bat"));
		MESSAGES_WHITELIST = ConfigBuildHandler.BUILDER.comment("Only entities to be included when \"All Deaths\" is enabled. Format for every entry is \"<namespace>:<id>\".").define("Entity Whitelist", Lists.newArrayList());
		BUILDER.pop();

		BUILDER.push("death_items");
		MAP_DECORATION = ConfigBuildHandler.BUILDER.comment("Icon to mark death point on a map with.").defineEnum("Death Map Marker", MapDecoration.Type.TARGET_X);
		IGNORE_KEEP_INVENTORY = ConfigBuildHandler.BUILDER.comment("Give map and compass items even when the \"keepInventory\" gamerule is enabled.").define("Ignore Keep Inventory", false);
		ITEMS_ON_LOST = ConfigBuildHandler.BUILDER.comment("Give death items only to players that dropped items from their inventory upon dying.").define("No Empty Inventory ", false);
		REMOVE_DEATH_TRACKERS = ConfigBuildHandler.BUILDER.comment("Remove death tracking items from inventory when approaching death point.").define("Remove Death Trackers", true);
		CUSTOM_COMPASS_TEXTURE = ConfigBuildHandler.BUILDER.comment("Use a custom texture for the death compass.").define("Custom Compass Texture", true);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
}
